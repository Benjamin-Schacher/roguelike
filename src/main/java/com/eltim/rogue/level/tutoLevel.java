package com.eltim.rogue.level;

import com.eltim.rogue.entity.monster;
import com.eltim.rogue.entity.npc;
import com.eltim.rogue.entity.environment.door;
import com.eltim.rogue.entity.environment.doorStateEnum;
import com.eltim.rogue.world.map;
import com.eltim.rogue.world.tile;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.environment.chest;
import com.eltim.rogue.item.key;
import com.eltim.rogue.item.enumerateur.chestTypeEnum;

public class tutoLevel implements level {
    
    @Override
    public map generate(player p) {
        // Légende du layout :
        // D = Porte normale (NORMAL)
        // X = Porte verrouillée (LOCKED)
        // O = Vieille porte (OLD)
        // @ = Joueur
        // M = Groupe de monstres
        // V = Compagnon Valentin
        // L = Coffre
        // ^ = Sortie vers lvl 1
        // # = Mur
        // | = Mur (barres verticales de couloir)
        // . = zone d'intéraction
        // £ = Mur autel de prière
        // ? = Point de description
        String[] layout = {
            "11111111###############^^^^^^^######################",
            "11111111#                             #           L#",
            "11111111#                             X            #",
            "11111111#                             X            #",
            "11111111#                             #            #",
            "################        ############################",
            "#              |        |              #111111111111",
            "#              D        D              #111111111111",
            "#              |        |              #111111111111",
            "#              |        |              #111111111111",
            "################  ?M    ################111111111111",
            "################        ################111111111111",
            "#   £          |        |              #111111111111",
            "#   . ?@      ?O        D              #111111111111",
            "#              |        |              #111111111111",
            "#              |        |              #111111111111",
            "################        ################111111111111",
            "################        ################111111111111",
            "#              |        |              #111111111111",
            "#              X        X              #111111111111",
            "#              |        |      V       #111111111111",
            "#              |        |              #111111111111",
            "################        ################111111111111",
            "11111111#                       #1111111111111111111",
            "11111111#                      L#1111111111111111111",
            "11111111#L                      #1111111111111111111",
            "11111111#      L               L#1111111111111111111",
            "11111111#########################1111111111111111111"
        };
        
        int width = layout[0].length();
        int height = layout.length;
        map tutorialMap = new map(width, height);
        tutorialMap.setLevelName("Prison");
        
        // Pour les multiples ?, on garde un compteur
        int descCounter = 0;

        for (int y = 0; y < height; y++) {
            String row = layout[y];
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);
                if (c == '#' || c == '|') {
                    tutorialMap.setTile(x, y, new tile(c, false));
                } else if (c == '£') {
                    tutorialMap.setTile(x, y, new tile('£', true));
                } else if (c == '1') {
                    tutorialMap.setTile(x, y, new tile(' ', true));
                } else if (c == '^') {
                    tutorialMap.setTile(x, y, new tile('^', true)); // Sortie
                } else if (c == 'L') {
                    tutorialMap.setTile(x, y, new tile('.', true)); // Sol franchissable sous le coffre
                    chest cEntity = new chest(x, y, true, chestTypeEnum.COMMON);
                    tutorialMap.addEntity(cEntity);
                } else if (c == 'D') {
                    // Porte normale
                    tutorialMap.setTile(x, y, new tile('.', true));
                    tutorialMap.addEntity(new door(x, y, 'D', doorStateEnum.NORMAL, 152));
                } else if (c == 'X') {
                    // Porte verrouillée
                    tutorialMap.setTile(x, y, new tile('.', true));
                    tutorialMap.addEntity(new door(x, y, 'D', doorStateEnum.LOCKED, 152));
                } else if (c == 'O') {
                    // Vieille porte
                    tutorialMap.setTile(x, y, new tile('.', true));
                    tutorialMap.addEntity(new door(x, y, 'D', doorStateEnum.OLD, 152));
                } else {
                    tutorialMap.setTile(x, y, new tile('.', true)); // Sol franchissable
                    
                    if (c == '@') {
                        p.setX(x);
                        p.setY(y);
                        tutorialMap.addEntity(p);
                    } else if (c == '?') {
                        String text = "Description introuvable.";
                        if (descCounter == 0) text = "\"Salot de barons ! Il ma tout pris, sauf ma vengeance ! Et je vais allez la lui foutre dans la geule... Tien quesque c'est que ce bruit... ?\" ";
                        else if (descCounter == 1) text = "Merde c'est quoi ce truc, on dirait que le garde a subit une malédiction. Cette porte semble vieille et fragile, je dois pouvoir la forcer.";
                        else if (descCounter == 2) text = "Le mort vivant sanble a peine conscient, il ne devrais pas faire long feu !";
                        
                        tutorialMap.addEntity(new com.eltim.rogue.entity.environment.DescriptionMarker(x, y, text));
                        descCounter++;
                    } else if (c == 'M') {
                        // Groupe de monstres
                        monster m1 = new monster(x, y, 'M');
                        m1.setName("Loup"); 
                        m1.setXpReward(15);
                        m1.setMaxLifePoint(10);
                        m1.setLifePoint(10);
                        m1.setAgilite(14);
                        m1.addLoot(new key("Clé rouillée", 152));
                        tutorialMap.addEntity(m1);
                    } else if (c == 'V') {
                        npc companion = new npc(x, y, 'P');
                        companion.setName("Valentin");
                        companion.setMaxLifePoint(15);
                        companion.setLifePoint(15);
                        companion.setForce(12);
                        companion.setAgilite(12);
                        tutorialMap.addEntity(companion);
                    }
                }
            }
        }
        
        return tutorialMap;
    }
}
