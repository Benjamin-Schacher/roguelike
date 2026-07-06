package com.eltim.rogue.level;

import com.eltim.rogue.world.map;
import com.eltim.rogue.world.tile;
import com.eltim.rogue.entity.monster;
import com.eltim.rogue.entity.npc;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.environment.door;
import com.eltim.rogue.item.key;
import com.eltim.rogue.item.potion;

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
                "11111111#^^^^^^^#1111111111111111sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"
        };

        int width = layout[0].length();
        int height = layout.length;
        map tutorialMap = new map(width, height);
        tutorialMap.setLevelName("Sous sol de la forteresse");

        return tutorialMap;
    }

}
