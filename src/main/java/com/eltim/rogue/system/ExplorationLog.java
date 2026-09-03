package com.eltim.rogue.system;

import java.util.ArrayList;
import java.util.List;

/**
 * Journal de bord des événements hors-combat (jets de dés, interactions, descriptions).
 * Affiché dans le terminal en bas à gauche de l'écran.
 */
public class ExplorationLog {

    private static final int MAX_ENTRIES = 8;
    private static List<String> log = new ArrayList<>();

    /**
     * Ajoute un message dans le terminal d'exploration.
     * Format recommandé : « Jet de perception : 14+2=16 vs DC12 — Succès »
     */
    public static void add(String message) {
        log.add(message);
        while (log.size() > MAX_ENTRIES) {
            log.remove(0);
        }
    }

    public static void addLog(String message) {
        add(message);
    }

    /**
     * Ajoute un jet de dé formaté automatiquement.
     * @param action   nom de l'action (ex: "Crochetage", "Perception")
     * @param roll     résultat brut du dé
     * @param modifier modificateur appliqué
     * @param dc       difficulté cible
     */
    public static void addRoll(String action, int roll, int modifier, int dc) {
        int total = roll + modifier;
        String modStr = (modifier >= 0) ? "+" + modifier : String.valueOf(modifier);
        String result = (total >= dc) ? "Succès" : "Échec";
        add("« " + action + " : " + roll + modStr + "=" + total + " vs DC" + dc + " — " + result + " »");
    }

    /**
     * Ajoute un message de description simple (pour les marqueurs '?').
     */
    public static void addDescription(String text) {
        add("[ " + text + " ]");
    }

    public static List<String> getLog() {
        return log;
    }

    public static void clear() {
        log.clear();
    }
}
