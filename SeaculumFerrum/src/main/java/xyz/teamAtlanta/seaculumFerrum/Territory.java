package xyz.teamAtlanta.seaculumFerrum;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.util.FileMgmt;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Territory {
    private String ID;
    private String FormattedName;
    private Coord originCoord;
    private boolean isKingdom;
    private int value;
    private String intro;
    private List<Territory> DutchesWithin = new ArrayList<>();
    private List<Territory> Nearby = new ArrayList<>();
    private List<Nation> Claimed = new ArrayList<>();
    private List<Nation> StrongClaimed = new ArrayList<>();
    public static List<Territory> cultures = new ArrayList<>();
    //public static List<String> culturesAbbrs = Arrays.asList("thracian");

    public Territory(String id) {this.ID = id;}
    public String getID() {
        return ID;
    }

    public void load() {
        String line = null;
        String path = Main.getInstance().getDataFolder().getPath() + File.separator + "OIKMCTerritories" + File.separator + getID() + ".txt";
        System.out.println("load时候的path：" + path);
        File fileWar = new File(path);
        HashMap<String, String> keys = FileMgmt.loadFileIntoHashMap(fileWar);
        line = keys.get("formattedname");if (line != null) {FormattedName = line;}line = null;
        line = keys.get("origincoord");if (line != null)
        {   String[] s = Utils.transferStringAToArray(line);
            int x = Integer.parseInt(s[0]);
            int z = Integer.parseInt(s[1]);
            originCoord = Coord.parseCoord(x, z);
        }line = null;
        line = keys.get("value");
        if (line != null) {value = Integer.parseInt(line);}line = null;
        line = keys.get("nearby");if (line != null) {
            String[] d = Utils.transferStringAToArray(line);
            for (String d2 : d) {
                if (Main.hasTerritory(d2) && !Main.getTerritory(d2).isKingdom) {
                    Nearby.add(Main.getTerritory(d2));
                }
            }
        }line = null;
        //查看強，弱宣称
        line = keys.get("claimed");if (line != null) {
            System.out.println(line);
            String[] c = Utils.transferStringAToArray(line);
            System.out.println("找到一位宣称者UUID：" + c);
            for (String c1 : c) {
                Claimed.add(TownyUniverse.getInstance().getNation(UUID.fromString(c1)));

            }
        }line = null;
        line = keys.get("strongclaimed");if (line != null) {
            String[] c = Utils.transferStringAToArray(line);
            for (String c1 : c) {
                Claimed.add(TownyUniverse.getInstance().getNation(UUID.fromString(c1)));

            }
        }line = null;


        line = keys.get("iskingdom");
        if (line != null) {isKingdom = Boolean.parseBoolean(line);}line = null;
        if(!isKingdom) return;
        //加入文化序列
        cultures.add(this);
        line = keys.get("dutcheswithin");if (line != null) {
            String[] d = Utils.transferStringAToArray(line);
            for (String d1 : d) {
                if (Main.hasTerritory(d1) && !Main.getTerritory(d1).isKingdom) {
                    DutchesWithin.add(Main.getTerritory(d1));
                }
            }
        }line = null;






        


        //应当Catch



    }

    public void save() {
        String path = Main.getInstance().getDataFolder().getPath() + File.separator + "OIKMCTerritories" + File.separator + getID() + ".txt";
        File fileWar = new File(path);
        List<String> list = new ArrayList<>();
        HashMap<String, String> keys = FileMgmt.loadFileIntoHashMap(fileWar);

        FileMgmt.checkOrCreateFile(Main.getInstance().getDataFolder().getPath() + File.separator + "OIKMCTerritories" + File.separator + getID() + ".txt");
        for(String k : keys.keySet()) {
            if(!k.equals("claimed") && !k.equals("strongclaimed"))
            list.add(k + "=" + keys.get(k));
        }

        List<String> ln = new ArrayList<>();
        List<String> lns = new ArrayList<>();

        for(Nation nation : Claimed) {
            ln.add(nation.getUUID().toString());

        }
        for(Nation nation : StrongClaimed) {
            lns.add(nation.getUUID().toString());

        }

        list.add("claimed=" + String.join(",", ln));
        list.add("strongclaimed=" + String.join(",", lns));


        FileMgmt.listToFile(list, Main.getInstance().getDataFolder().getPath() + File.separator + "OIKMCTerritories" + File.separator + getID() + ".txt");
    }

    public void addClaim(Nation nation, boolean strong) {
        if(strong) StrongClaimed.add(nation);
        else Claimed.add(nation);
    }

    public String getFormattedName() {
        return FormattedName;
    }

    public Coord getOriginCoord() {
        return originCoord;
    }

    public void setOriginCoord(Coord originCoord) {
        this.originCoord = originCoord;
    }

    public boolean hasDutch(Territory d) {
        if(isKingdom) return DutchesWithin.contains(d);
        return false;
    }

    public List<Territory> getDutchesWithin() {
        if(isKingdom) return DutchesWithin;
        return null;
    }

    public static Territory[] initializeCulture(Coord coord) {
        Main.Instance.getLogger().info("开始地块初始化……");
        Territory[] terrs = new Territory[2];
        if(coord != null) {
            Map<Territory, Double> culturesDistanceMap = new ConcurrentHashMap<>();
            for(Territory culture : cultures) {
                culturesDistanceMap.put(culture,distanceCalculator(coord, culture.getOriginCoord()));
            }
            List<Territory> cultureSequence = new ArrayList<>(cultures);
            Territory temp = null;
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
            Territory culturesLarge = cultureSequence.get(0);
            terrs[0] = culturesLarge;

            if(!culturesLarge.getDutchesWithin().isEmpty()) {
                culturesDistanceMap.clear();
                for(Territory cultures : culturesLarge.getDutchesWithin()) {
                   culturesDistanceMap.put(cultures,distanceCalculator(coord, cultures.getOriginCoord()));
                }
                List<Territory> cultureSmallSequence = new ArrayList<>(culturesLarge.getDutchesWithin());
               Territory temp1 = null;
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
                terrs[1] = cultureSmallSequence.get(0);
                return terrs;


            }

            return null;


        }
        return null;
    }

    public static double distanceCalculator(Coord coord1, Coord coord2) {
        if (coord1 != null && coord2 != null) {
            System.out.println("坐标情形：" + coord1.getX() + "," + coord1.getZ() + ";" + coord2.getX() + "," + coord2.getZ());
            double distance = Math.sqrt(Math.pow(coord1.getX() - coord2.getX(), 2) + Math.pow(coord1.getZ() - coord2.getZ(), 2));
            Main.Instance.getLogger().info("已计算坐标之间的距离为" + String.valueOf(new DecimalFormat("0.00").format(distance) + "个区块（x16）"));
            return distance;
        } else {Main.Instance.getLogger().info("距离计算器出现了错误，因为其中一个坐标为null");}

        return -1;



    }

    public List<Nation> getClaimed() {
        return Claimed;
    }

    public List<Nation> getStrongClaimed() {
        return StrongClaimed;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public List<Territory> getNearby() {
        return Nearby;
    }

    public void setNearby(List<Territory> nearby) {
        Nearby = nearby;
    }
}
