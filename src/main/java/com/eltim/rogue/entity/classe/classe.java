package com.eltim.rogue.entity.classe;

import java.util.ArrayList;
import java.util.List;

public abstract class classe {

    public String name;
    public String description;

    /** Points de compétence disponibles à dépenser. Le joueur démarre avec 2 points de base et en gagne 2 par niveau. */
    public int skillPoints = 2;

    /** Les 4 arbres de compétences de la classe. */
    public List<SkillTree> trees = new ArrayList<>();

    /**
     * Calcule le coût d'un skill selon son tier.
     * Tier 1-2 : 1 point. Tier 3-5 : 2 points.
     */
    public int getSkillCost(Skill skill) {
        return skill.tier <= 2 ? 1 : 2;
    }

    /**
     * Tente de débloquer un skill dans un arbre donné.
     * @return true si l'achat est réussi
     */
    public boolean unlockSkill(SkillTree tree, Skill skill) {
        if (skill.unlocked) return false; // Déjà débloqué
        int cost = getSkillCost(skill);
        if (skillPoints < cost) return false; // Pas assez de points

        // Vérifier que le skill précédent (tier - 1) est bien débloqué
        int tierIndex = skill.tier - 1; // tier est 1-indexé, liste est 0-indexée
        if (tierIndex > 0 && !tree.skills.get(tierIndex - 1).unlocked) return false;

        skill.unlocked = true;
        skillPoints -= cost;
        return true;
    }

    /**
     * Ajoute des points de compétence (appelé à chaque montée de niveau).
     */
    public void gainLevelPoints() {
        skillPoints += 2;
    }

    /**
     * Vérifie si un skill est débloqué dans n'importe quel arbre.
     */
    public boolean hasSkill(String skillId) {
        for (SkillTree tree : trees) {
            for (Skill s : tree.skills) {
                if (s.id.equals(skillId) && s.unlocked) return true;
            }
        }
        return false;
    }
}
