package com.eltim.rogue.system;

import com.eltim.rogue.entity.base.Belief;
import com.eltim.rogue.entity.base.Gender;
import com.eltim.rogue.entity.base.Race;
import com.eltim.rogue.entity.classe.classe;
import com.eltim.rogue.entity.classe.warriorClasse;

import java.awt.event.KeyEvent;

public class CharacterCreationSystem {

    private static String name = "Heros";
    private static Race race = Race.HUMAIN;
    private static Gender gender = Gender.MASCULIN;
    private static Belief belief = Belief.SANS_RELIGION;
    private static char symbol = '@';
    // Stats de base (sans bonus de race)
    private static int force = 8, agilite = 8, intelligence = 8, charisme = 8, constitution = 8, sagesse = 8;
    private static int availablePoints = 15;
    
    private static classe characterClass = new warriorClasse();

    public enum Field {
        NAME("Nom"), 
        RACE("Race"), 
        GENDER("Sexe"), 
        BELIEF("Croyance"), 
        SYMBOL("Symbole"), 
        FORCE("Force"), 
        AGILITE("Agilité"), 
        INTELLIGENCE("Intelligence"), 
        CHARISME("Charisme"), 
        CONSTITUTION("Constitution"), 
        SAGESSE("Sagesse"),
        CONFIRM("Confirmer et commencer");

        private final String label;
        Field(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private static Field currentField = Field.NAME;
    private static boolean isEditingName = false;
    private static boolean isEditingSymbol = false;
    private static boolean isDone = false;

    public static void init() {
        name = "Heros";
        race = Race.HUMAIN;
        gender = Gender.MASCULIN;
        belief = Belief.SANS_RELIGION;
        symbol = '@';
        force = 10; agilite = 10; intelligence = 10; charisme = 10; constitution = 10; sagesse = 10;
        availablePoints = 10;
        currentField = Field.NAME;
        isEditingName = false;
        isEditingSymbol = false;
        isDone = false;
    }

    public static void handleInput(KeyEvent key) {
        int code = key.getKeyCode();
        char c = key.getKeyChar();

        if (isEditingName) {
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_ESCAPE) {
                isEditingName = false;
            } else if (code == KeyEvent.VK_BACK_SPACE && name.length() > 0) {
                name = name.substring(0, name.length() - 1);
            } else if (Character.isLetter(c) || c == ' ') {
                if (name.length() < 15) name += c;
            }
            return;
        }

        if (isEditingSymbol) {
            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_ESCAPE) {
                isEditingSymbol = false;
            } else if (c >= 33 && c <= 126) { // Caractères imprimables
                symbol = c;
                isEditingSymbol = false;
            }
            return;
        }

        switch (code) {
            case KeyEvent.VK_UP:
                previousField();
                break;
            case KeyEvent.VK_DOWN:
                nextField();
                break;
            case KeyEvent.VK_LEFT:
                adjustField(-1);
                break;
            case KeyEvent.VK_RIGHT:
                adjustField(1);
                break;
            case KeyEvent.VK_ENTER:
                if (currentField == Field.NAME) isEditingName = true;
                else if (currentField == Field.SYMBOL) isEditingSymbol = true;
                else if (currentField == Field.CONFIRM) {
                    isDone = true;
                }
                break;
        }
    }

    private static void previousField() {
        Field[] fields = Field.values();
        int ord = currentField.ordinal() - 1;
        if (ord < 0) ord = fields.length - 1;
        currentField = fields[ord];
    }

    private static void nextField() {
        Field[] fields = Field.values();
        int ord = currentField.ordinal() + 1;
        if (ord >= fields.length) ord = 0;
        currentField = fields[ord];
    }

    private static void adjustField(int direction) {
        switch (currentField) {
            case RACE:
                race = cycleEnum(Race.values(), race, direction);
                validateStats();
                break;
            case GENDER:
                gender = cycleEnum(Gender.values(), gender, direction);
                break;
            case BELIEF:
                belief = cycleEnum(Belief.values(), belief, direction);
                break;
            case FORCE: adjustStat(1, direction); break;
            case AGILITE: adjustStat(2, direction); break;
            case INTELLIGENCE: adjustStat(3, direction); break;
            case CHARISME: adjustStat(4, direction); break;
            case CONSTITUTION: adjustStat(5, direction); break;
            case SAGESSE: adjustStat(6, direction); break;
            default: break; 
        }
    }

    private static <T extends Enum<T>> T cycleEnum(T[] values, T current, int direction) {
        int idx = current.ordinal() + direction;
        if (idx < 0) idx = values.length - 1;
        if (idx >= values.length) idx = 0;
        return values[idx];
    }

    private static void validateStats() {
        // Quand on change de race, on recalcule les stats pour respecter les bornes
        // AVEC le nouveau bonus racial, sans dépasser 18 ni descendre sous 6 en valeur finale
        int[] bases = { force, agilite, intelligence, charisme, constitution, sagesse };
        int[] bonuses = {
            race.getBonusForce(), race.getBonusAgilite(), race.getBonusIntelligence(),
            race.getBonusCharisme(), race.getBonusConstitution(), race.getBonusSagesse()
        };

        for (int i = 0; i < bases.length; i++) {
            int finalVal = bases[i] + bonuses[i];
            if (finalVal > 18) {
                // Rembourser les points excédentaires
                int excess = finalVal - 18;
                bases[i] -= excess;
                availablePoints += excess;
            } else if (finalVal < 6) {
                // Forcer le minimum à 6 : on PREND des points (peut aller négatif si le joueur n'en a pas)
                int deficit = 6 - finalVal;
                bases[i] += deficit;
                availablePoints -= deficit;
                if (availablePoints < 0) availablePoints = 0; // Sécurité anti-négatif
            }
        }

        force = bases[0];
        agilite = bases[1];
        intelligence = bases[2];
        charisme = bases[3];
        constitution = bases[4];
        sagesse = bases[5];
    }

    private static void adjustStat(int statId, int direction) {
        int currentVal = 0;
        int bonus = 0;
        switch(statId) {
            case 1: currentVal = force; bonus = race.getBonusForce(); break;
            case 2: currentVal = agilite; bonus = race.getBonusAgilite(); break;
            case 3: currentVal = intelligence; bonus = race.getBonusIntelligence(); break;
            case 4: currentVal = charisme; bonus = race.getBonusCharisme(); break;
            case 5: currentVal = constitution; bonus = race.getBonusConstitution(); break;
            case 6: currentVal = sagesse; bonus = race.getBonusSagesse(); break;
        }

        if (direction > 0 && availablePoints > 0 && (currentVal + bonus) < 18) {
            availablePoints--;
            currentVal++;
        } else if (direction < 0 && (currentVal + bonus) > 6) {
            availablePoints++;
            currentVal--;
        }

        switch(statId) {
            case 1: force = currentVal; break;
            case 2: agilite = currentVal; break;
            case 3: intelligence = currentVal; break;
            case 4: charisme = currentVal; break;
            case 5: constitution = currentVal; break;
            case 6: sagesse = currentVal; break;
        }
    }

    // --- Getters ---
    public static String getName() { return name; }
    public static Race getRace() { return race; }
    public static Gender getGender() { return gender; }
    public static Belief getBelief() { return belief; }
    public static char getSymbol() { return symbol; }
    public static int getForce() { return force; }
    public static int getAgilite() { return agilite; }
    public static int getIntelligence() { return intelligence; }
    public static int getCharisme() { return charisme; }
    public static int getConstitution() { return constitution; }
    public static int getSagesse() { return sagesse; }
    public static int getAvailablePoints() { return availablePoints; }
    public static Field getCurrentField() { return currentField; }
    public static boolean isEditingName() { return isEditingName; }
    public static boolean isEditingSymbol() { return isEditingSymbol; }
    public static boolean isDone() { return isDone; }
    public static classe getCharacterClass() { return characterClass; }
}
