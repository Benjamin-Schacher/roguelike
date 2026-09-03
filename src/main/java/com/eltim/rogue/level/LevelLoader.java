package com.eltim.rogue.level;

import com.eltim.rogue.world.map;
import com.eltim.rogue.world.tile;
import com.eltim.rogue.entity.player;
import com.eltim.rogue.entity.monster;
import com.eltim.rogue.entity.npc;
import com.eltim.rogue.entity.environment.door;
import com.eltim.rogue.entity.environment.doorStateEnum;
import com.eltim.rogue.entity.environment.chest;
import com.eltim.rogue.entity.environment.DescriptionMarker;
import com.eltim.rogue.item.key;
import com.eltim.rogue.item.enumerateur.chestTypeEnum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LevelLoader {

    public static class LevelData {
        public String name = "Niveau";
        public String music = null;
        public int spawnX = 1;
        public int spawnY = 1;
        public List<String> layout = new ArrayList<>();
        public Map<Character, List<String>> descriptions = new HashMap<>();
        public Map<Character, String> monstersConfig = new HashMap<>();
        public Map<Character, String> npcsConfig = new HashMap<>();
        public Map<String, String> transitions = new HashMap<>();
        public Map<Character, Boolean> decorations = new HashMap<>();
        public Map<Character, chestTypeEnum> chestsConfig = new HashMap<>();
        public Map<Character, String> interactionsConfig = new HashMap<>();
        public List<String> globalDescriptions = new ArrayList<>();
    }

    public static LevelData parseFile(String filePath) {
        LevelData data = new LevelData();
        List<String> lines = readLines(filePath);

        String currentSection = "";
        Map<Character, Integer> descCounters = new HashMap<>();

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }

            if (line.startsWith("[") && line.endsWith("]")) {
                currentSection = line.substring(1, line.length() - 1).toUpperCase();
                continue;
            }

            switch (currentSection) {
                case "INFO":
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        String key = parts[0].trim().toLowerCase();
                        String val = parts[1].trim();
                        if (key.equals("name")) {
                            data.name = val;
                        } else if (key.equals("music") || key.equals("ambiance") || key.equals("son")) {
                            data.music = val;
                        } else if (key.equals("spawn")) {
                            String[] coords = val.split(",");
                            if (coords.length == 2) {
                                data.spawnX = Integer.parseInt(coords[0].trim());
                                data.spawnY = Integer.parseInt(coords[1].trim());
                            }
                        }
                    }
                    break;

                case "DESCRIPTIONS":
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        char sym = parts[0].trim().charAt(0);
                        String val = parts[1].trim();
                        // Support multi-descriptions séparées par |
                        String[] descs = val.split("\\|");
                        List<String> list = new ArrayList<>();
                        for (String d : descs) {
                            String cleaned = d.trim();
                            if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() >= 2) {
                                cleaned = cleaned.substring(1, cleaned.length() - 1);
                            }
                            list.add(cleaned);
                        }
                        data.descriptions.put(sym, list);
                    } else if (line.contains("\"")) {
                        String[] descs = line.split("\\|");
                        for (String d : descs) {
                            String cleaned = d.trim();
                            if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() >= 2) {
                                cleaned = cleaned.substring(1, cleaned.length() - 1);
                            }
                            if (!cleaned.isEmpty()) {
                                data.globalDescriptions.add(cleaned);
                            }
                        }
                    }
                    break;

                case "MONSTERS":
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        char sym = parts[0].trim().charAt(0);
                        data.monstersConfig.put(sym, parts[1].trim());
                    }
                    break;

                case "NPCS":
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        char sym = parts[0].trim().charAt(0);
                        data.npcsConfig.put(sym, parts[1].trim());
                    }
                    break;

                case "TRANSITIONS":
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        data.transitions.put(parts[0].trim(), parts[1].trim());
                    }
                    break;

                case "DECORATIONS":
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        char sym = parts[0].trim().charAt(0);
                        String val = parts[1].trim().toLowerCase();
                        boolean walkable = true;
                        if (val.contains("walkable:false") || val.contains("solid") || val.contains("false")) {
                            walkable = false;
                        }
                        data.decorations.put(sym, walkable);
                    }
                    break;

                case "CHESTS":
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        char sym = parts[0].trim().charAt(0);
                        String typeStr = parts[1].trim().toUpperCase();
                        try {
                            chestTypeEnum type = chestTypeEnum.valueOf(typeStr);
                            data.chestsConfig.put(sym, type);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;

                case "INTERACTIONS":
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        char sym = parts[0].trim().charAt(0);
                        data.interactionsConfig.put(sym, parts[1].trim());
                    }
                    break;

                case "MAP":
                    data.layout.add(rawLine); // keep exact trailing/leading spaces for map rows
                    break;

                default:
                    break;
            }
        }

        return data;
    }

    public static map generateMap(LevelData data, player p) {
        if (data.layout.isEmpty()) {
            return new map(10, 10);
        }

        int height = data.layout.size();
        int width = 0;
        for (String row : data.layout) {
            if (row.length() > width) width = row.length();
        }

        map m = new map(width, height);
        if (data.music != null && !data.music.isEmpty()) {
            com.eltim.rogue.engine.sound.SoundManager.getInstance().playMusic(data.music);
        } else {
            com.eltim.rogue.engine.sound.SoundManager.getInstance().playMusicForLevel(data.name);
        }

        Map<Character, Integer> descCounters = new HashMap<>();

        for (int y = 0; y < height; y++) {
            String row = data.layout.get(y);
            for (int x = 0; x < width; x++) {
                char c = (x < row.length()) ? row.charAt(x) : ' ';

                if (c == '#' || c == '|' || c == '_') {
                    m.setTile(x, y, new tile(c, false));
                } else if (c == '1' || c == 's' || c == '%') {
                    m.setTile(x, y, new tile(' ', true));
                } else if (data.interactionsConfig.containsKey(c)) {
                    m.setTile(x, y, new tile(' ', true));
                    String text = data.interactionsConfig.get(c);
                    m.addEntity(new com.eltim.rogue.entity.environment.InteractionTile(x, y, c, text));
                } else if (c == '^') {
                    m.setTile(x, y, new tile('^', true));
                } else if (c == 'L' || c == 'U' || c == '$' || c == 'E' || c == 'G' || c == 'T' || c == 'B' || c == 'A' || c == 'R' || c == 'Z' || c == 'S' || data.chestsConfig.containsKey(c)) {
                    m.setTile(x, y, new tile(' ', true));
                    chestTypeEnum type = chestTypeEnum.COMMON;
                    if (data.chestsConfig.containsKey(c)) {
                        type = data.chestsConfig.get(c);
                    } else if (c == 'U') type = chestTypeEnum.UNCOMMON;
                    else if (c == '$') type = chestTypeEnum.RARE;
                    else if (c == 'E') type = chestTypeEnum.EPIC;
                    else if (c == 'G') type = chestTypeEnum.LEGENDARY;
                    else if (c == 'T') type = chestTypeEnum.TRAPED;
                    else if (c == 'B') type = chestTypeEnum.CREST;
                    else if (c == 'A') type = chestTypeEnum.ARMORYCHEST;
                    else if (c == 'R') type = chestTypeEnum.MERCHANT;
                    else if (c == 'Z') type = chestTypeEnum.ALCHEMIST;
                    else if (c == 'S') type = chestTypeEnum.SPECIAL;
                    m.addEntity(new chest(x, y, c, true, type));
                } else if (c == 'D') {
                    m.setTile(x, y, new tile(' ', true));
                    m.addEntity(new door(x, y, 'D', doorStateEnum.NORMAL, 152));
                } else if (c == 'X') {
                    m.setTile(x, y, new tile(' ', true));
                    m.addEntity(new door(x, y, 'D', doorStateEnum.LOCKED, 152));
                } else if (c == 'O') {
                    m.setTile(x, y, new tile(' ', true));
                    m.addEntity(new door(x, y, 'D', doorStateEnum.OLD, 152));
                } else if (data.descriptions.containsKey(c)) {
                    m.setTile(x, y, new tile(' ', true));
                    List<String> list = data.descriptions.get(c);
                    int idx = descCounters.getOrDefault(c, 0);
                    String text = list.get(Math.min(idx, list.size() - 1));
                    descCounters.put(c, idx + 1);
                    m.addEntity(new DescriptionMarker(x, y, text));
                } else if (c == '?' && !data.globalDescriptions.isEmpty()) {
                    m.setTile(x, y, new tile(' ', true));
                    int idx = descCounters.getOrDefault('?', 0);
                    String text = data.globalDescriptions.get(Math.min(idx, data.globalDescriptions.size() - 1));
                    descCounters.put('?', idx + 1);
                    m.addEntity(new DescriptionMarker(x, y, text));
                } else if (data.decorations.containsKey(c)) {
                    m.setTile(x, y, new tile(c, data.decorations.get(c)));
                } else if (data.monstersConfig.containsKey(c)) {
                    m.setTile(x, y, new tile(' ', true));
                    monster mon = createMonster(x, y, c, data.monstersConfig.get(c));
                    m.addEntity(mon);
                } else if (data.npcsConfig.containsKey(c)) {
                    m.setTile(x, y, new tile(' ', true));
                    npc companion = createNPC(x, y, c, data.npcsConfig.get(c));
                    m.addEntity(companion);
                } else if (c == '@') {
                    m.setTile(x, y, new tile(' ', true));
                    if (p != null) {
                        p.setX(x);
                        p.setY(y);
                        m.addEntity(p);
                    }
                } else {
                    m.setTile(x, y, new tile(c, true));
                }
            }
        }

        // Place le joueur s'il n'est pas encore sur la carte
        if (p != null && !m.getEntities().contains(p)) {
            p.setX(data.spawnX);
            p.setY(data.spawnY);
            m.addEntity(p);
        }

        return m;
    }

    private static monster createMonster(int x, int y, char sym, String config) {
        monster m1 = new monster(x, y, sym);
        // Format : Nom | HP:15 | XP:25 | AGI:12 | LOOT:Clé rouillée:152
        String[] parts = config.split("\\|");
        m1.setName(parts[0].trim());

        for (int i = 1; i < parts.length; i++) {
            String p = parts[i].trim();
            if (p.startsWith("HP:")) {
                int hp = Integer.parseInt(p.substring(3).trim());
                m1.setMaxLifePoint(hp);
                m1.setLifePoint(hp);
            } else if (p.startsWith("XP:")) {
                m1.setXpReward(Integer.parseInt(p.substring(3).trim()));
            } else if (p.startsWith("STR:")) {
                m1.setForce(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("AGI:")) {
                m1.setAgilite(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("SAG:")) {
                m1.setSagesse(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("INT:")) {
                m1.setIntelligence(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("CHR:")) {
                m1.setCharisme(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("CON:")) {
                m1.setConstitution(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("LOOT:")) {
                String[] lootInfo = p.substring(5).trim().split(":");
                if (lootInfo.length >= 2) {
                    m1.addLoot(new key(lootInfo[0].trim(), Integer.parseInt(lootInfo[1].trim())));
                }
            } else if (p.startsWith("SON:") || p.startsWith("SOUND:") || p.startsWith("BRUITAGE:")) {
                int idx = p.indexOf(":");
                m1.setSoundName(p.substring(idx + 1).trim());
            }
        }
        return m1;
    }

    private static npc createNPC(int x, int y, char sym, String config) {
        npc companion = new npc(x, y, sym);
        String[] parts = config.split("\\|");
        companion.setName(parts[0].trim());

        for (int i = 1; i < parts.length; i++) {
            String p = parts[i].trim();
            if (p.startsWith("HP:")) {
                int hp = Integer.parseInt(p.substring(3).trim());
                companion.setMaxLifePoint(hp);
                companion.setLifePoint(hp);
            } else if (p.startsWith("STR:")) {
                companion.setForce(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("AGI:")) {
                companion.setAgilite(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("SAG:")) {
                companion.setSagesse(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("INT:")) {
                companion.setIntelligence(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("CHR:")) {
                companion.setCharisme(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("CON:")) {
                companion.setConstitution(Integer.parseInt(p.substring(4).trim()));
            } else if (p.startsWith("SON:") || p.startsWith("SOUND:") || p.startsWith("BRUITAGE:")) {
                int idx = p.indexOf(":");
                companion.setSoundName(p.substring(idx + 1).trim());
            }
        }
        return companion;
    }

    private static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
                return lines;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Try via classloader resource
        try (InputStream is = LevelLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (is != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }
}
