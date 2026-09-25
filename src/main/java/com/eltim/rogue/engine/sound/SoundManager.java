package com.eltim.rogue.engine.sound;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * Moteur audio autonome streaming & SFX.
 * Permet la lecture fluide en continu (streaming) de gros fichiers d'ambiance (ex: WAV de 450 Mo)
 * sans aucun ralentissement ni surconsommation de mémoire RAM.
 */
public class SoundManager {

    private static SoundManager instance;

    private static class MusicSession {
        final int id;
        final String trackName;
        volatile boolean running = true;
        volatile boolean fading = false;
        volatile SourceDataLine line;
        Thread thread;
        volatile Thread fadeThread;
        volatile float fadeMultiplier = 1.0f;

        MusicSession(int id, String trackName) {
            this.id = id;
            this.trackName = trackName;
        }
    }

    private final Object musicLock = new Object();
    private int sessionCounter = 0;
    private MusicSession currentSession = null;
    private String currentMusicTrack;
    private String savedPreviousTrack;

    private float masterVolume = 1.00f; // Son Total (100%)
    private float musicVolume = 0.50f;  // Musique & Ambiance (50%)
    private float sfxVolume = 0.80f;    // Effets Sonores / VFX (80%)
    private boolean muted = false;

    public SoundManager() {
        instance = this;
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public float getEffectiveMusicVolume() {
        return masterVolume * musicVolume;
    }

    public float getEffectiveSfxVolume() {
        return masterVolume * sfxVolume;
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public String getCurrentMusicTrack() {
        synchronized (musicLock) {
            return currentMusicTrack;
        }
    }

    public boolean isMusicRunning() {
        synchronized (musicLock) {
            return currentSession != null && currentSession.running;
        }
    }

    /**
     * Joue une musique d'ambiance en boucle continue avec fondu (fade-out de l'ancienne, puis fade-in de la nouvelle).
     * @param trackName Nom ou partie du nom du fichier audio
     */
    public void playMusic(String trackName) {
        playMusic(trackName, 800);
    }

    public void playMusic(String trackName, long fadeDurationMs) {
        if (trackName == null || trackName.trim().isEmpty()) {
            return;
        }

        synchronized (musicLock) {
            if (!trackName.equalsIgnoreCase("combat") && !trackName.equalsIgnoreCase("hurryup")) {
                savedPreviousTrack = trackName;
            }

            // Déjà en cours de lecture et pas en train de disparaître (fade out)
            if (currentSession != null && currentSession.running && !currentSession.fading
                    && trackName.equalsIgnoreCase(currentSession.trackName)) {
                return;
            }

            if (muted) {
                currentMusicTrack = trackName;
                return;
            }

            final int thisRequestId = ++sessionCounter;
            final MusicSession oldSession = currentSession;
            currentMusicTrack = trackName;

            Runnable startNew = () -> {
                synchronized (musicLock) {
                    if (thisRequestId != sessionCounter || muted) {
                        return;
                    }
                    MusicSession newSession = new MusicSession(thisRequestId, trackName);
                    currentSession = newSession;

                    newSession.thread = new Thread(() -> {
                        runStreamingLoop(newSession, 0.0f, 1.0f, fadeDurationMs);
                    }, "MusicStream-" + trackName);
                    newSession.thread.setDaemon(true);
                    newSession.thread.start();
                }
            };

            if (oldSession != null && oldSession.running) {
                fadeOutAndStop(oldSession, fadeDurationMs, startNew);
            } else {
                startNew.run();
            }
        }
    }

    private void runStreamingLoop(MusicSession session, float initialFade, float targetFade, long fadeDurationMs) {
        SourceDataLine line = null;
        AudioInputStream ais = null;
        try {
            ais = findAudioStream(session.trackName);
            if (ais == null) {
                System.out.println("[SoundManager] Musique / Ambiance '" + session.trackName + "' introuvable.");
                synchronized (musicLock) {
                    if (currentSession == session) {
                        currentSession = null;
                        currentMusicTrack = null;
                    }
                }
                return;
            }

            AudioFormat format = ais.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                AudioFormat pcmFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(),
                    16,
                    format.getChannels(),
                    format.getChannels() * 2,
                    format.getSampleRate(),
                    false
                );
                ais = AudioSystem.getAudioInputStream(pcmFormat, ais);
                format = pcmFormat;
                info = new DataLine.Info(SourceDataLine.class, format);
            }

            line = (SourceDataLine) AudioSystem.getLine(info);
            int bufferSize = (int) (format.getFrameSize() * Math.max(2048, format.getSampleRate() * 0.1));
            line.open(format, bufferSize);
            session.line = line;
            session.fadeMultiplier = initialFade;
            updateLineVolume(line, session.fadeMultiplier);
            line.start();

            if (fadeDurationMs > 0 && targetFade > initialFade) {
                startFade(session, initialFade, targetFade, fadeDurationMs, null);
            }

            byte[] buffer = new byte[4096];
            while (session.running && !Thread.currentThread().isInterrupted()) {
                int bytesRead = ais.read(buffer, 0, buffer.length);
                if (bytesRead == -1) {
                    try {
                        ais.close();
                    } catch (Exception ignored) {}
                    ais = findAudioStream(session.trackName);
                    if (ais == null) break;
                    continue;
                }
                if (!session.running) break;
                line.write(buffer, 0, bytesRead);
            }

        } catch (Exception ignored) {
        } finally {
            if (line != null) {
                try {
                    if (line.isRunning()) line.stop();
                    line.close();
                } catch (Exception ignored) {}
            }
            if (ais != null) {
                try {
                    ais.close();
                } catch (Exception ignored) {}
            }
            synchronized (musicLock) {
                if (currentSession == session) {
                    currentSession = null;
                    currentMusicTrack = null;
                }
            }
        }
    }

    private void startFade(MusicSession session, float fromVal, float toVal, long durationMs, Runnable onComplete) {
        if (session.fadeThread != null && session.fadeThread.isAlive()) {
            session.fadeThread.interrupt();
        }
        if (durationMs <= 0) {
            session.fadeMultiplier = toVal;
            updateLineVolume(session.line, toVal);
            if (onComplete != null) onComplete.run();
            return;
        }

        Thread fadeThread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            while (session.running && (System.currentTimeMillis() - startTime < durationMs)) {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, (float) elapsed / durationMs);
                session.fadeMultiplier = fromVal + (toVal - fromVal) * progress;
                updateLineVolume(session.line, session.fadeMultiplier);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    return;
                }
            }
            if (session.running) {
                session.fadeMultiplier = toVal;
                updateLineVolume(session.line, toVal);
            }
            if (onComplete != null) {
                onComplete.run();
            }
        }, "AudioFadeThread-" + session.id);
        session.fadeThread = fadeThread;
        fadeThread.setDaemon(true);
        fadeThread.start();
    }

    private void fadeOutAndStop(MusicSession session, long durationMs, Runnable onComplete) {
        if (session == null || !session.running) {
            if (onComplete != null) onComplete.run();
            return;
        }
        session.fading = true;
        float currentFade = session.fadeMultiplier;
        long actualDuration = (long) (durationMs * currentFade);
        if (actualDuration < 50) actualDuration = 0;

        startFade(session, currentFade, 0.0f, actualDuration, () -> {
            session.running = false;
            if (session.line != null) {
                try {
                    if (session.line.isRunning()) session.line.stop();
                    session.line.flush();
                    session.line.close();
                } catch (Exception ignored) {}
                session.line = null;
            }
            if (session.thread != null) {
                session.thread.interrupt();
            }
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    /**
     * Sélectionne la musique/ambiance de fond adaptée au niveau avec fondu.
     */
    public void playMusicForLevel(String levelName) {
        if (levelName == null) return;
        String clean = levelName.toLowerCase();

        String track = "Dark Tomb";
        if (clean.contains("tuto") || clean.contains("prison")) {
            track = "Dark Tomb";
        } else if (clean.contains("level1") || clean.contains("sous-sol") || clean.contains("sous sol") || clean.contains("forteresse")) {
            track = "short_adventure";
        }

        savedPreviousTrack = track;
        playMusic(track, 800);
    }

    public void startCombatMusic() {
        synchronized (musicLock) {
            if (currentMusicTrack != null && !currentMusicTrack.equalsIgnoreCase("combat")
                    && !currentMusicTrack.equalsIgnoreCase("hurryup")) {
                savedPreviousTrack = currentMusicTrack;
            }
        }
        playMusic("combat", 500);
    }

    public void fadeOutCombatMusic(long durationMs) {
        stopMusicWithFade(durationMs, null);
    }

    public void restorePreviousMusic() {
        restorePreviousMusic(1000);
    }

    public void restorePreviousMusic(long fadeDurationMs) {
        String track;
        synchronized (musicLock) {
            track = savedPreviousTrack;
        }
        if (track != null) {
            playMusic(track, fadeDurationMs);
        } else {
            stopMusicWithFade(fadeDurationMs, null);
        }
    }

    public void stopMusic() {
        synchronized (musicLock) {
            sessionCounter++;
            if (currentSession != null) {
                currentSession.running = false;
                currentSession.fading = true;
                if (currentSession.fadeThread != null) {
                    currentSession.fadeThread.interrupt();
                }
                if (currentSession.line != null) {
                    try {
                        if (currentSession.line.isRunning()) {
                            currentSession.line.stop();
                        }
                        currentSession.line.flush();
                        currentSession.line.close();
                    } catch (Exception ignored) {}
                    currentSession.line = null;
                }
                if (currentSession.thread != null) {
                    currentSession.thread.interrupt();
                }
                currentSession = null;
            }
            currentMusicTrack = null;
        }
    }

    public void stopMusicWithFade(long durationMs, Runnable onComplete) {
        synchronized (musicLock) {
            sessionCounter++;
            final MusicSession sessionToFade = currentSession;
            currentMusicTrack = null;

            if (sessionToFade != null && sessionToFade.running) {
                fadeOutAndStop(sessionToFade, durationMs, () -> {
                    synchronized (musicLock) {
                        if (currentSession == sessionToFade) {
                            currentSession = null;
                        }
                    }
                    if (onComplete != null) onComplete.run();
                });
            } else {
                currentSession = null;
                if (onComplete != null) onComplete.run();
            }
        }
    }

    /**
     * Joue un bruitage (SFX) ponctuel.
     */
    public void playSFX(String soundName) {
        if (muted || soundName == null) return;

        new Thread(() -> {
            try {
                AudioInputStream ais = findAudioStream(soundName);
                if (ais != null) {
                    Clip clip = AudioSystem.getClip();
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                    clip.open(ais);
                    setClipVolume(clip, getEffectiveSfxVolume());
                    clip.start();
                }
            } catch (Exception ignored) {}
        }, "SFXThread-" + soundName).start();
    }

    public AudioInputStream findAudioStream(String name) {
        if (name == null || name.trim().isEmpty()) return null;

        String trimmed = name.trim();
        AudioInputStream stream = resolveStream(trimmed);
        if (stream != null) return stream;

        // Fallbacks intelligents si le nom logique n'a pas de fichier nommé exactement ainsi
        String lower = trimmed.toLowerCase();
        if (lower.equals("combat") || lower.contains("battle") || lower.contains("fight")) {
            stream = resolveStream("hurryup");
            if (stream != null) return stream;
            stream = resolveStream("short_adventure");
            if (stream != null) return stream;
        } else if (lower.equals("level1") || lower.contains("sous-sol") || lower.contains("forteresse")) {
            stream = resolveStream("short_adventure");
            if (stream != null) return stream;
            stream = resolveStream("when_angels_cry");
            if (stream != null) return stream;
            stream = resolveStream("Dark Tomb");
            if (stream != null) return stream;
        } else if (lower.contains("tuto") || lower.contains("prison") || lower.contains("dark tomb") || lower.contains("cave")) {
            stream = resolveStream("Dark Tomb");
            if (stream != null) return stream;
        } else if (lower.equals("boss")) {
            stream = resolveStream("snd_music_electrictheme");
            if (stream != null) return stream;
            stream = resolveStream("hurryup");
            if (stream != null) return stream;
        } else if (lower.equals("menu") || lower.contains("creation")) {
            stream = resolveStream("when_angels_cry");
            if (stream != null) return stream;
            stream = resolveStream("oldskoolmix");
            if (stream != null) return stream;
        }

        return null;
    }

    private AudioInputStream resolveStream(String name) {
        if (name == null || name.trim().isEmpty()) return null;

        String cleanName = name.trim();
        if (!cleanName.endsWith(".wav") && !cleanName.endsWith(".mp3") && !cleanName.endsWith(".ogg")) {
            cleanName = cleanName + ".wav";
        }

        // 1. Recherche par Classpath Resource
        String[] relPaths = {
            "/audio/ambiance/" + cleanName,
            "/audio/music/" + cleanName,
            "/audio/sfx/" + cleanName,
            "/audio/sfx/" + name + ".ogg",
            "/audio/sfx/" + name + ".wav",
            "/audio/sfx/" + name,
            "/audio/" + cleanName,
            "/audio/ambiance/" + name,
            "/audio/music/" + name
        };

        for (String rel : relPaths) {
            try {
                InputStream is = getClass().getResourceAsStream(rel);
                if (is != null) {
                    return AudioSystem.getAudioInputStream(new BufferedInputStream(is));
                }
            } catch (Exception ignored) {}
        }

        // 2. Recherche par Système de Fichiers (avec recherche récursive dans sfx/music/ambiance)
        File[] searchDirs = {
            new File("src/main/resources/audio"),
            new File("target/classes/audio")
        };

        File found = searchAudioFileRecursive(searchDirs, name);
        if (found != null) {
            try {
                return AudioSystem.getAudioInputStream(found);
            } catch (Exception ignored) {}
        }

        return null;
    }

    private File searchAudioFileRecursive(File[] dirs, String queryName) {
        String target = queryName.toLowerCase().trim();

        // Passe 1 : correspondance exacte
        for (File dir : dirs) {
            if (dir.exists() && dir.isDirectory()) {
                File res = findInDirExact(dir, target);
                if (res != null) return res;
            }
        }

        // Passe 2 : correspondance partielle (ex: "Dark Tomb" trouve "- Dark Tomb - Cave Sounds 45 Minutes 🦇.wav")
        for (File dir : dirs) {
            if (dir.exists() && dir.isDirectory()) {
                File res = findInDirContains(dir, target);
                if (res != null) return res;
            }
        }

        return null;
    }

    private File findInDirExact(File dir, String query) {
        File[] files = dir.listFiles();
        if (files == null) return null;

        for (File f : files) {
            if (f.isDirectory()) {
                File sub = findInDirExact(f, query);
                if (sub != null) return sub;
            } else {
                String fullName = f.getName().toLowerCase();
                String nameNoExt = fullName;
                int dotIdx = fullName.lastIndexOf('.');
                if (dotIdx > 0) nameNoExt = fullName.substring(0, dotIdx);

                String relPath = f.getPath().replace('\\', '/').toLowerCase();

                if (fullName.equals(query) || nameNoExt.equals(query)
                        || relPath.endsWith("/" + query + ".ogg")
                        || relPath.endsWith("/" + query + ".wav")
                        || relPath.endsWith("/" + query + ".mp3")
                        || relPath.endsWith("/" + query)) {
                    return f;
                }
            }
        }
        return null;
    }

    private File findInDirContains(File dir, String query) {
        File[] files = dir.listFiles();
        if (files == null) return null;

        String cleanQuery = query.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        for (File f : files) {
            if (f.isDirectory()) {
                File sub = findInDirContains(f, query);
                if (sub != null) return sub;
            } else {
                String fullName = f.getName().toLowerCase();
                String nameNoExt = fullName;
                int dotIdx = fullName.lastIndexOf('.');
                if (dotIdx > 0) nameNoExt = fullName.substring(0, dotIdx);

                if (nameNoExt.contains(query) || fullName.contains(query)) {
                    return f;
                }

                String cleanName = nameNoExt.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                if (!cleanQuery.isEmpty() && cleanName.contains(cleanQuery)) {
                    return f;
                }
            }
        }
        return null;
    }

    private void setLineVolume(Line line, float volume) {
        if (line == null) return;
        try {
            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                float min = gainControl.getMinimum();
                float max = gainControl.getMaximum();
                float dB = (float) (Math.log(Math.max(0.0001f, volume)) / Math.log(10.0) * 20.0);
                if (dB < min) dB = min;
                if (dB > max) dB = max;
                gainControl.setValue(dB);
            }
        } catch (Exception ignored) {}
    }

    private void updateLineVolume(Line line, float fadeMultiplier) {
        setLineVolume(line, getEffectiveMusicVolume() * fadeMultiplier);
    }

    private void setClipVolume(Clip clip, float volume) {
        setLineVolume(clip, volume);
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        synchronized (musicLock) {
            if (currentSession != null && currentSession.line != null) {
                updateLineVolume(currentSession.line, currentSession.fadeMultiplier);
            }
        }
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        synchronized (musicLock) {
            if (currentSession != null && currentSession.line != null) {
                updateLineVolume(currentSession.line, currentSession.fadeMultiplier);
            }
        }
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public void toggleMute() {
        this.muted = !this.muted;
        if (muted) {
            stopMusic();
        } else if (savedPreviousTrack != null) {
            playMusic(savedPreviousTrack);
        }
    }

    public boolean isMuted() {
        return muted;
    }
}
