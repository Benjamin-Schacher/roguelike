package com.eltim.rogue.entity.classe;

import java.util.ArrayList;
import java.util.List;

public abstract class classe {

    public String name;
    public String description;

    /** Points de compétence disponibles à dépenser. Le joueur démarre avec 2 points de base. */
    public int skillPoints = 2;

    /** Les 4 emplacements d'arbres choisis par le joueur (initialement vides). */
    public SkillTree[] activeSlots = new SkillTree[4];

    /** Ensemble des arbres disponibles parmi lesquels le joueur peut choisir. */
    public List<SkillTree> availableTrees = new ArrayList<>();

    /** Liste synchronisée des arbres actifs (non-nuls), pour rétrocompatibilité avec combatSysteme et hasSkill. */
    public List<SkillTree> trees = new ArrayList<>();

    /** Nom de la sous-classe (bi-classe) choisie au niveau 3 (ex: "Mage-lame"), ou null. */
    public String subclass = null;

    /** Indique si le joueur a refusé la bi-classe pour rester en classe pure. */
    public boolean hasRefusedSubclass = false;

    public boolean hasSubclass() {
        return subclass != null && !subclass.trim().isEmpty();
    }

    /**
     * Définit la sous-classe choisie et ajoute ses 2 arbres de talents exclusifs au catalogue disponible.
     */
    public void setSubclass(String subclassName, List<SkillTree> subclassTrees) {
        this.subclass = subclassName;
        if (subclassTrees != null) {
            for (SkillTree st : subclassTrees) {
                if (!availableTrees.contains(st)) {
                    availableTrees.add(st);
                }
            }
        }
    }

    /**
     * Calcule le coût d'un skill selon son tier.
     * Tier 1-2 : 1 point. Tier 3-5 : 2 points.
     */
    public int getSkillCost(Skill skill) {
        return skill.tier <= 2 ? 1 : 2;
    }

    /**
     * Assigne un arbre de talents à un des 4 emplacements vides.
     * Dépense 1 point de compétence et débloque immédiatement son Tier 1.
     * @return true si l'assignation a réussi
     */
    public boolean assignTreeToSlot(int slotIndex, SkillTree tree) {
        if (slotIndex < 0 || slotIndex >= 4) return false;
        if (activeSlots[slotIndex] != null) return false; // Emplacement déjà pris
        if (tree == null) return false;
        if (skillPoints < 1) return false; // 1 point requis pour Tier 1
        if (isTreeSlotted(tree)) return false; // Arbre déjà placé dans un autre slot

        skillPoints--;
        if (!tree.skills.isEmpty()) {
            tree.skills.get(0).unlocked = true;
        }
        activeSlots[slotIndex] = tree;
        syncTreesList();
        return true;
    }

    /**
     * Vérifie si un arbre est déjà assigné dans l'un des 4 slots actifs.
     */
    public boolean isTreeSlotted(SkillTree tree) {
        if (tree == null) return false;
        for (SkillTree slotted : activeSlots) {
            if (slotted != null && slotted.name.equalsIgnoreCase(tree.name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne la liste des arbres disponibles qui n'ont pas encore été assignés dans un slot.
     */
    public List<SkillTree> getUnslottedAvailableTrees() {
        List<SkillTree> unslotted = new ArrayList<>();
        for (SkillTree t : availableTrees) {
            if (!isTreeSlotted(t)) {
                unslotted.add(t);
            }
        }
        return unslotted;
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
     * Ajoute des points de compétence à la montée de niveau.
     * Règle : 1 point par niveau si bi-classé, 2 points par niveau si classe pure.
     */
    public void gainLevelPoints() {
        if (hasSubclass()) {
            skillPoints += 1;
        } else {
            skillPoints += 2;
        }
    }

    /**
     * Met à jour la liste publique `trees` avec tous les arbres non-nuls de `activeSlots`.
     */
    public void syncTreesList() {
        trees.clear();
        for (SkillTree st : activeSlots) {
            if (st != null) {
                trees.add(st);
            }
        }
    }

    /**
     * Vérifie si un skill est débloqué dans n'importe quel arbre actif.
     */
    public boolean hasSkill(String skillId) {
        syncTreesList();
        for (SkillTree tree : trees) {
            for (Skill s : tree.skills) {
                if (s.id.equals(skillId) && s.unlocked) return true;
            }
        }
        return false;
    }
}
