package com.eltim.rogue.level;

import com.eltim.rogue.world.map;
import com.eltim.rogue.world.tile;
import com.eltim.rogue.entity.monster;
import com.eltim.rogue.entity.npc;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.environment.door;
import com.eltim.rogue.entity.environment.doorStateEnum;
import com.eltim.rogue.entity.environment.chest;
import com.eltim.rogue.entity.environment.DescriptionMarker;
import com.eltim.rogue.item.key;
import com.eltim.rogue.item.potion;
import com.eltim.rogue.item.enumerateur.chestTypeEnum;

public class Level1 implements level {

    @Override
    public map generate(player p) {
        // Légende du layout :
        // D = Porte normale (NORMAL)
        // X = Porte verrouillée (LOCKED)
        // O = Vieille porte (OLD)
        // @ = Joueur
        // M = Groupe de monstres
        // C = Compagnon Célestin
        // L = Coffre randome
        // $ = coffre de soldat
        // ^ = Sortie
        // P = Armure de capitaine fantomale

        // £ = dialogue description
        // w = interaction pour sauter dans une rivière soutérenne
        // ù = description bosse escargot

        // # = Mur
        // 6 = \
        // | = Mur (barres verticales de couloir)
        // _ = Mur (barres horizontal de couloir)
        // <tuto-> = Entrée venant du niveau tutoriel
        String[] layout = {



                "111#^^^^^^^#ssssssssssssss#######################sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#       #ssssssssssssss#  $               $  #sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#       #ssssssssssssss#     #    M    #     #sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#       #ssssssssssssss#          MM         #sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#       ################     #         #     #####################sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#                                                    X           #sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#                                                    X           #sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#       #########D##########D#############################       #sssss##########################sssss#########################",
                "111#       #ss1s#       #ss#       #ssssssssssssssssssssssss#       #sssss#                        #sssss#       (o)        (o)  #",
                "111#       #ss1s#L   P  #ss#       #ssssssssssssssssssssssss#       #sssss#  .[_].   .[_].   .[_]. #sssss#   (o)  (o) (o)        #",
                "111#       #ss1s#########ss#e#######sssssss##################       #sssss#      .[_].   .[_].     #sssss#                       #",
                "111#       #ss1sssssssssssss#ssssssssssssss#   #%%%%%%%%%%#         #sssss#  .[_].   .[_].   .[_]. #sssss#  (o)               (o)#",
                "111#       #######################ss11######L              M        #sssss#C     .[_].   .[_].     #sssss#           8=@     (o) #",
                "111#6               . ..         #s111#%%%%#   #%%%%%%%%%%# M       #sssss#  .[_].   .[_].   .[_]. #sssss#   (o)                 #",
                "111#o=               o.O-=     w/#s1ss#L   D                        #sssss#      .[_].   .[_].     #sssss# (o)        (o)     (o)#",
                "111#/      M      .. . .  .   w|.#s111#%%%%#   #%%%%%%%%%%#         #sssss#                        #sssss#    (o)            (o) #",
                "111#6      M       . .. .     w|.#1111######L                       ###################D##############################D###########",
                "111#o=           .  .[_].     w|.#s1111ssss#LL #%%%%%%%%%%#                                                           ù          #",
                "111#/                ..  .     w6#s1111ssss##################                      M                                             #",
                "111#                             #s1111sssssssssssssssssssss#       ##############################################################",
                "111#       #######################11111############sssssssss#       #sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#      L#111111111111111111111111111#   $$     #ssss11sss#       #sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#  M    #ssss###1###1###1###1###1111#         $#sss111sss#       #sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#       #ssss# #s# #1#M#1# #1# #ss11#L         #sss111sss#£££££££#sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#L      ######_###_###_###_###D##########XX###############       #sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#                                           £X   M   M           #sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111#                    M                      £X    M M MM         #sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "111######       #####################################    ############sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "11111111#       #11111111111111111111111111111111111#    #11111111111sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "11111111#       #11111111111111111111111111111111111#    #11111111111sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "11111111#L      #11111111111111111111111111111111111#^^^^#11111111111sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "11111111#       #1111111111111111111111111111111111111111111111111111sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss",
                "11111111#<tuto->#1111111111111111sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
        };

        int width = layout[0].length();
        int height = layout.length;
        map tutorialMap = new map(width, height);
        tutorialMap.setLevelName("Sous sol de la forteresse");

        for (int y = 0; y < height; y++) {
            String row = layout[y];
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);
                if (c == '#' || c == '|' || c == '_') {
                    tutorialMap.setTile(x, y, new tile(c, false));
                } else if (c == '1' || c == 's' || c == '%') {
                    tutorialMap.setTile(x, y, new tile(' ', true));
                } else if (c == '^') {
                    tutorialMap.setTile(x, y, new tile('^', true)); // Sortie vers le niveau suivant
                } else if (c == 'L') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    chest cEntity = new chest(x, y, true, chestTypeEnum.COMMON);
                    tutorialMap.addEntity(cEntity);
                } else if (c == '$') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    chest cEntity = new chest(x, y, true, chestTypeEnum.RARE);
                    tutorialMap.addEntity(cEntity);
                } else if (c == 'D') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    tutorialMap.addEntity(new door(x, y, 'D', doorStateEnum.NORMAL, 152));
                } else if (c == 'X') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    tutorialMap.addEntity(new door(x, y, 'D', doorStateEnum.LOCKED, 152));
                } else if (c == 'O') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    tutorialMap.addEntity(new door(x, y, 'D', doorStateEnum.OLD, 152));
                } else if (c == 'M') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    monster m1 = new monster(x, y, 'M');
                    m1.setName("Garde Squelette");
                    m1.setXpReward(25);
                    m1.setMaxLifePoint(15);
                    m1.setLifePoint(15);
                    m1.setAgilite(12);
                    tutorialMap.addEntity(m1);
                } else if (c == 'C') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    npc companion = new npc(x, y, 'C');
                    companion.setName("Célestin");
                    companion.setMaxLifePoint(20);
                    companion.setLifePoint(20);
                    companion.setForce(14);
                    companion.setAgilite(10);
                    tutorialMap.addEntity(companion);
                } else if (c == '£') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    tutorialMap.addEntity(new DescriptionMarker(x, y, "Des écrits anciens sont gravés sur la roche."));
                } else if (c == 'w') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    tutorialMap.addEntity(new DescriptionMarker(x, y, "Une rivière souterraine s'écoule rapidement dans les profondeurs."));
                } else if (c == 'ù') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    tutorialMap.addEntity(new DescriptionMarker(x, y, "Vous sentez une présence monstrueuse... Le boss Escargot Géant ne doit pas être loin !"));
                } else if (c == 'P') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    tutorialMap.addEntity(new DescriptionMarker(x, y, "Une armure de capitaine fantomatique repose ici."));
                } else if (c == '@') {
                    tutorialMap.setTile(x, y, new tile('.', true));
                    p.setX(x);
                    p.setY(y);
                    tutorialMap.addEntity(p);
                } else {
                    // Pour '<', 't', 'u', 'o', '-', '>', '.', ' ', etc.
                    tutorialMap.setTile(x, y, new tile(c, true));
                }
            }
        }

        // Si le joueur n'est pas encore sur la carte, on le positionne à l'entrée <tuto-> (x=12, y=31)
        if (p != null && !tutorialMap.getEntities().contains(p)) {
            p.setX(12);
            p.setY(31);
            tutorialMap.addEntity(p);
        }

        return tutorialMap;
    }

}
