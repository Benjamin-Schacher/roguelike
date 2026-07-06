package com.eltim.rogue.entity.classe;

import java.util.ArrayList;
import java.util.List;

/**
 * Un arbre de compétences avec 5 tiers (un skill par tier).
 */
public class SkillTree {

    public final String name;
    public final List<Skill> skills; // Toujours 5 skills, un par tier

    public SkillTree(String name, List<Skill> skills) {
        this.name = name;
        this.skills = skills;
    }

    /** Retourne le prochain skill qui peut être débloqué (dernier tier débloqué + 1). */
    public Skill getNextUnlockable() {
        for (int i = 0; i < skills.size(); i++) {
            if (!skills.get(i).unlocked) {
                // Le premier non-débloqué n'est accessible que si le précédent est débloqué (ou c'est le T1)
                if (i == 0 || skills.get(i - 1).unlocked) {
                    return skills.get(i);
                }
                return null;
            }
        }
        return null; // Tout débloqué
    }

    /** Nombre de skills débloqués dans cet arbre (= nombre de "points investis"). */
    public int getPointsInvested() {
        int count = 0;
        for (Skill s : skills) {
            if (s.unlocked) count++;
        }
        return count;
    }
}
