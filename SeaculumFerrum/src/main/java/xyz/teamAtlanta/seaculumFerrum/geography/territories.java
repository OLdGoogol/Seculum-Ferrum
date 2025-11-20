package xyz.teamAtlanta.seaculumFerrum.geography;

import com.palmergames.bukkit.towny.object.Coord;
import xyz.teamAtlanta.seaculumFerrum.Main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum territories {
    /*LEON(new Coord(-66,-543),"Western","莱昂", false, Arrays.asList()),
    CASTILE(new Coord(-48,-512),"Western","卡斯蒂利亚", false, Arrays.asList()),
    ARAGON(new Coord(-6,-504),"Western","阿拉贡", false, Arrays.asList()),
    CATALONIA(new Coord(11,-531),"Western","加泰罗尼亚", false, Arrays.asList()),
    GRANADA(new Coord(-46,-475),"Western","格拉纳达", false, Arrays.asList()),

     */
    PRIMITIVE(new Coord(0,0),"","原始文化（无）", false, new ArrayList<>()),
    ;

    private Coord originCoord;
    private String DefaultStandardCategory;
    private String Name;
    private boolean isKingdom;
    private List<territories> DutchesWithin;
    private static List<territories> cultures = Arrays.asList();
    public static List<String> culturesAbbrs = Arrays.asList("thracian");

    territories(Coord originCoord, String defaultStandardCategory, String name, boolean isKingdom, List<territories> cultures) {this.originCoord = originCoord; this.DefaultStandardCategory = defaultStandardCategory; this.Name = name; this.isKingdom = isKingdom; this.DutchesWithin = cultures;}

    public static territories parseString(String line) {
        /*switch (line) {
            case "PRIMITIVE":
                return PRIMITIVE;
            case "HELLENIC":
                return HELLENIC;
            case "ANATOLIA":
                return ANATOLIA;
            case "MESOPOTAMIA":
                return MESOPOTAMIA;
            case "EGYPT":
                return EGYPT;
            case "PERSIA":
                return PERSIA;
            case "ARMENIAN":
                return ARMENIAN;
            case "ARABIAN":
                return ARABIAN;
            case "BERBERIAN":
                return BERBERIAN;
            case "SYRIA":
                return SYRIA;
            case "ESPANA":
                return ESPANA;
            case "LATIN":
                return LATIN;
            case "POLISH":
                return POLISH;
            case "RUSSIAN":
                return RUSSIAN;
            case "ETHIOPIA":
                return ETHIOPIA;
            case "GERMAN":
                return GERMAN;
            case "FRANK":
                return FRANK;
            case "HINDU":
                return HINDU;
            case "RAJASTHAN":
                return RAJASTHAN;
            case "MARATHA":
                return MARATHA;
            case "BENGAL":
                return BENGAL;
            case "MALAYA":
                return MALAYA;
            case "SERES":
                return SERES;
            case "NIPPON":
                return NIPPON;
            case "IROQUOIS":
                return IROQUOIS;
            case "MAYA":
                return MAYA;
            case "INCA":
                return INCA;
            case "BLACK_AFRICAN":
                return BLACK_AFRICAN;
            case "GHANA":
                return GHANA;
            case "CARPATHIANS":
                return CARPATHIANS;
                case ""


        }

         */
        for(territories culture : territories.values()) {
            if(culture.toString().equals(line)) {return culture;}
        }
        return PRIMITIVE;
    }

    public static territories parseAbbrString(String line) {
        switch (line) {
            case "thracian":
        }
        return PRIMITIVE;
    }

    public String getFormattedName() {
        return Name;
    }

    public String getDefaultStandardCategory() {
        return DefaultStandardCategory;
    }

    public Coord getOriginCoord() {
        return originCoord;
    }

    public static territories initializeCulture(Coord coord) {
        Main.Instance.getLogger().info("开始地块初始化……");
        if(coord != null) {
            Map<territories, Double> culturesDistanceMap = new ConcurrentHashMap<>();
            for(territories cultures : cultures) {
                culturesDistanceMap.put(cultures,distanceCalculator(coord, cultures.getOriginCoord()));
            }
            List<territories> cultureSequence = new ArrayList<>(cultures);
            territories temp = null;
            for(int i=0 ;i<cultureSequence.size()-1; i++) {
                for(int j=0; j<cultureSequence.size()-1-i; j++) {
                     if((culturesDistanceMap.get(cultureSequence.get(j))) > (culturesDistanceMap.get(cultureSequence.get(j+1)))) {
                        temp = cultureSequence.get(j);
                        cultureSequence.set(j, cultureSequence.get(j+1));
                        cultureSequence.set(j+1, temp);
                    }
                }
            }

            //拒绝随机了
            /*double first = 1/culturesDistanceMap.get(cultureSequence.get(0));
            double second = 1/culturesDistanceMap.get(cultureSequence.get(1));
            double third = 1/culturesDistanceMap.get(cultureSequence.get(2));

            double r = Math.random() * (first + second + third);
            if(r <= first) {return cultureSequence.get(0);}
            else if(r <= first + second) {return cultureSequence.get(1);}
            else {return cultureSequence.get(2);}

             */
            territories culturesLarge = cultureSequence.get(0);
            if(culturesLarge.getDutchesWithin().size() > 0) {
                culturesDistanceMap.clear();
                for(territories cultures : culturesLarge.getDutchesWithin()) {
                    culturesDistanceMap.put(cultures,distanceCalculator(coord, cultures.getOriginCoord()));
                }
                List<territories> cultureSmallSequence = new ArrayList<>(culturesLarge.getDutchesWithin());
                territories temp1 = null;
                for(int i=0 ;i<cultureSmallSequence.size()-1; i++) {
                    for(int j=0; j<cultureSmallSequence.size()-1-i; j++) {
                        System.out.println(cultureSmallSequence);
                        if((culturesDistanceMap.get(cultureSmallSequence.get(j))) > (culturesDistanceMap.get(cultureSmallSequence.get(j+1)))) {
                            temp = cultureSmallSequence.get(j);
                            cultureSmallSequence.set(j, cultureSmallSequence.get(j+1));
                            cultureSmallSequence.set(j+1, temp);
                        }
                    }
                }
                return cultureSmallSequence.get(0);


            }

            return culturesLarge;


        }
        return PRIMITIVE;
    }

    public boolean isKingdom() {
        return isKingdom;
    }

    public List<territories> getDutchesWithin() {
        return DutchesWithin;
    }

    public static double distanceCalculator(Coord coord1, Coord coord2) {
        if (coord1 != null && coord2 != null) {
            double distance = Math.sqrt(Math.pow(coord1.getX() - coord2.getX(), 2) + Math.pow(coord1.getZ() - coord2.getZ(), 2));
            Main.Instance.getLogger().info("已计算坐标之间的距离为" + String.valueOf(new DecimalFormat("0.00").format(distance) + "个区块（x16）"));
            return distance;
        } else {Main.Instance.getLogger().info("距离计算器出现了错误，因为其中一个坐标为null");}

        return -1;



    }

}

