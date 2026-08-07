package com.eltim.rogue.engine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class inputHandler extends KeyAdapter {
    private final ConcurrentLinkedQueue<KeyEvent> keyQueue = new ConcurrentLinkedQueue<>();
    // Touches actuellement physiquement appuyées (pour filtrer la répétition OS)
    private final Set<Integer> heldKeys = new HashSet<>();

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        // Ajouter à la queue seulement si la touche n'était pas déjà appuyée
        if (!heldKeys.contains(code)) {
            heldKeys.add(code);
            keyQueue.add(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        heldKeys.remove(e.getKeyCode());
    }

    public KeyEvent getInput() {
        return keyQueue.poll();
    }

    public void clear() {
        keyQueue.clear();
        heldKeys.clear();
    }
}

