package com.eltim.rogue.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class inputHandler extends KeyAdapter {
    private static inputHandler instance;

    private final ConcurrentLinkedQueue<KeyEvent> keyQueue = new ConcurrentLinkedQueue<>();
    private final Set<Integer> heldKeys = new HashSet<>();
    private final Map<Integer, Long> lastPressTimeMap = new HashMap<>();

    // Délais pour le maintien des touches de déplacement (déplacement continu et fluide)
    private static final long INITIAL_REPEAT_DELAY = 180; // Délai avant le début du déplacement continu (ms)
    private static final long REPEAT_INTERVAL = 110;      // Intervalle entre deux pas en maintien (ms)

    public inputHandler() {
        instance = this;
    }

    public static void clearInput() {
        if (instance != null) {
            instance.clear();
        }
    }

    private boolean isMovementKey(int code) {
        return code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN ||
               code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT ||
               code == KeyEvent.VK_Z || code == KeyEvent.VK_Q ||
               code == KeyEvent.VK_S || code == KeyEvent.VK_D ||
               code == KeyEvent.VK_W || code == KeyEvent.VK_A;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        long now = System.currentTimeMillis();

        if (!heldKeys.contains(code)) {
            heldKeys.add(code);
            lastPressTimeMap.put(code, now);
            keyQueue.add(e);
        } else if (isMovementKey(code)) {
            // Répétition automatique uniquement pour les déplacements
            Long lastTime = lastPressTimeMap.get(code);
            if (lastTime != null) {
                long elapsed = now - lastTime;
                if (elapsed >= INITIAL_REPEAT_DELAY) {
                    keyQueue.add(e);
                    lastPressTimeMap.put(code, now - (INITIAL_REPEAT_DELAY - REPEAT_INTERVAL));
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        heldKeys.remove(code);
        lastPressTimeMap.remove(code);
    }

    public KeyEvent getInput() {
        return keyQueue.poll();
    }

    public void clear() {
        keyQueue.clear();
        heldKeys.clear();
        lastPressTimeMap.clear();
    }
}
