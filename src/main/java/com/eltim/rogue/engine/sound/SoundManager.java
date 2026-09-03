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

    private Thread musicThread;
    private SourceDataLine musicLine;
    private volatile boolean musicRunning = false;

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

    /**
     * Joue une musique d'ambiance en boucle continue via streaming audio (SourceDataLine).
     * @param trackName Nom ou partie du nom du fichier audio
     */
    public synchronized void playMusic(String trackName) {
        if (trackName == null || trackName.trim().isEmpty() || trackName.equalsIgnoreCase(currentMusicTrack)) {
            return; // Déjà en cours de lecture
        }

        stopMusic();

        currentMusicTrack = trackName;
        if (muted) return;

        musicRunning = true;
        musicThread = new Thread(() -> {
            while (musicRunning) {
                try {
                    AudioInputStream ais = findAudioStream(currentMusicTrack);
                    if (ais == null) {
                        System.out.println("[SoundManager] Musique / Ambiance '" + currentMusicTrack + "' introuvable.");
                        break;
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

                    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                    line.open(format);

                    synchronized (SoundManager.this) {
                        musicLine = line;
                        setLineVolume(musicLine, getEffectiveMusicVolume());
                    }
                    line.start();

                    byte[] buffer = new byte[16384];
                    int bytesRead = -1;
                    while (musicRunning && (bytesRead = ais.read(buffer, 0, buffer.length)) != -1) {
                        line.write(buffer, 0, bytesRead);
                    }

                    line.drain();
                    line.close();
                    ais.close();
                } catch (Exception e) {
                    System.err.println("[SoundManager] Erreur streaming ambiance '" + currentMusicTrack + "' : " + e.getMessage());
                    break;
                }
            }
        }, "AudioAmbianceThread");

        musicThread.setDaemon(true);
        musicThread.start();
    }

    /**
     * Sélectionne la musique/ambiance de fond adaptée au niveau.
     */
    public void playMusicForLevel(String levelName) {
        if (levelName == null) return;
        String clean = levelName.toLowerCase();

        String track = "Dark Tomb";
        if (clean.contains("tuto") || clean.contains("prison")) {
            track = "Dark Tomb";
        } else if (clean.contains("level1") || clean.contains("sous-sol") || clean.contains("forteresse")) {
            track = "level1";
        }

        savedPreviousTrack = track;
        playMusic(track);
    }

    public void startCombatMusic() {
        if (currentMusicTrack != null && !currentMusicTrack.equalsIgnoreCase("combat")) {
            savedPreviousTrack = currentMusicTrack;
        }
        playMusic("combat");
    }

    public void restorePreviousMusic() {
        if (savedPreviousTrack != null) {
            playMusic(savedPreviousTrack);
        }
    }

    public synchronized void stopMusic() {
        musicRunning = false;
        if (musicLine != null) {
            try {
                if (musicLine.isRunning()) {
                    musicLine.stop();
                }
                musicLine.close();
            } catch (Exception ignored) {}
            musicLine = null;
        }
        if (musicThread != null) {
            musicThread.interrupt();
            musicThread = null;
        }
        currentMusicTrack = null;
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
                    clip.open(ais);
                    setClipVolume(clip, getEffectiveSfxVolume());
                    clip.start();
                }
            } catch (Exception ignored) {}
        }).start();
    }

    private AudioInputStream findAudioStream(String name) {
        if (name == null || name.isEmpty()) return null;

        String cleanName = name;
        if (!name.endsWith(".wav") && !name.endsWith(".mp3") && !name.endsWith(".ogg")) {
            cleanName = name + ".wav";
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
        for (File dir : dirs) {
            if (dir.exists() && dir.isDirectory()) {
                File res = findInDir(dir, target);
                if (res != null) return res;
            }
        }
        return null;
    }

    private File findInDir(File dir, String query) {
        File[] files = dir.listFiles();
        if (files == null) return null;

        for (File f : files) {
            if (f.isDirectory()) {
                File sub = findInDir(f, query);
                if (sub != null) return sub;
            } else {
                String fullName = f.getName().toLowerCase();
                String nameNoExt = fullName;
                int dotIdx = fullName.lastIndexOf('.');
                if (dotIdx > 0) nameNoExt = fullName.substring(0, dotIdx);

                String relPath = f.getPath().replace('\\', '/').toLowerCase();

                if (fullName.equals(query) || nameNoExt.equals(query) || relPath.endsWith("/" + query + ".ogg") || relPath.endsWith("/" + query + ".wav") || relPath.endsWith("/" + query)) {
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
                float dB = (float) (Math.log(Math.max(0.0001f, volume)) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }
        } catch (Exception ignored) {}
    }

    private void setClipVolume(Clip clip, float volume) {
        setLineVolume(clip, volume);
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        synchronized (this) {
            setLineVolume(musicLine, getEffectiveMusicVolume());
        }
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        synchronized (this) {
            setLineVolume(musicLine, getEffectiveMusicVolume());
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
