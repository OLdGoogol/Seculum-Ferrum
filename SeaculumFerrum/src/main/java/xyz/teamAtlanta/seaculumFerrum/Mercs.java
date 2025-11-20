package xyz.teamAtlanta.seaculumFerrum;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.util.FileMgmt;
import org.bukkit.entity.Player;
import oshi.jna.platform.mac.SystemB;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Mercs {
    private String ID;
    private String FormattedName;
    private int[] DetailNums;
    private int[] Mercs;
    private Territory LimitCulture;
    private Resident Employer;
    private int Price;
    private boolean isKnight;

    public Mercs(String id) {this.ID = id;}

    public void load() {
        String line = null;
        String path = Main.getInstance().getDataFolder().getPath() + File.separator + "OIKMCMercs" + File.separator + getID() + ".txt";
        File fileWar = new File(path);
        HashMap<String, String> keys = FileMgmt.loadFileIntoHashMap(fileWar);
        line = keys.get("formattedname");
        if (line != null) {
            FormattedName = line;
        }
        line = null;
        int sum = 0;
        line = keys.get("detailnums");
        if (line != null) {
            int[] n = new int[4];
            String[] s = Utils.transferStringAToArray(line);
            for(int i=0;i<4;i++) {
                n[i] = Integer.parseInt(s[i]);
                sum += n[i];
                //any other ways?
                DetailNums = n;
            }
        }
        line = null;
        line = keys.get("mercs");
        if (line != null && !line.equals("")) {
            int[] n1 = new int[sum];
            String[] s = Utils.transferStringAToArray(line.replace(" ","").replace("[","").replace("]",""));
            for(int i=0;i<sum;i++) {
                n1[i] = Integer.parseInt(s[i]);
                Mercs = n1;
            }
        }
        line = null;
        line = keys.get("limitculture");
        if (line != null) {
            LimitCulture = Main.getTerritory(line);

        }
        line = null;
        line = keys.get("employer");
        if (line != null) {
            Employer = TownyUniverse.getInstance().getResident(line);
        }
        line = null;
        line = keys.get("price");
        if (line != null) {
            Price = Integer.parseInt(line);
        }
        line = null;
        line = keys.get("isknight");
        if (line != null) {
            isKnight = Boolean.parseBoolean(line);
        }

    }

    public void save() {
        String path = Main.getInstance().getDataFolder().getPath() + File.separator + "OIKMCMercs" + File.separator + getID() + ".txt";
        File fileWar = new File(path);
        List<String> list = new ArrayList<>();
        HashMap<String, String> keys = FileMgmt.loadFileIntoHashMap(fileWar);

        FileMgmt.checkOrCreateFile(Main.getInstance().getDataFolder().getPath() + File.separator + "OIKMCMercs" + File.separator + getID() + ".txt");
        for(String k : keys.keySet()) {
            if(!k.equals("employer") && !k.equals("mercs"))
                list.add(k + "=" + keys.get(k));
        }
        if(Employer == null) {list.add("employer=");}
        else {
        list.add("employer=" + Employer.getUUID().toString());}

        if(Mercs == null) {list.add("mercs=");}
        else {
            list.add("mercs=" + Arrays.toString(Mercs));
        }


        FileMgmt.listToFile(list, Main.getInstance().getDataFolder().getPath() + File.separator + "OIKMCMercs" + File.separator + getID() + ".txt");
    }

    public String getID() {
        return ID;
    }


    public String getFormattedName() {
        return FormattedName;
    }

    public int[] getDetailNums() {
        return DetailNums;
    }

    public void setDetailNums(int[] detailNums) {
        DetailNums = detailNums;
    }

    public int getNum() {
        return DetailNums[0] + DetailNums[1] + DetailNums[2] + DetailNums[3];
    }

    public Territory getLimitCulture() {
        return LimitCulture;
    }

    public Resident getEmployer() {
        return Employer;
    }

    public void setEmployer(Resident employer) {
        Employer = employer;
    }

    public int[] getMercs() {
        return Mercs;
    }

    public void setMercs(int[] mercs) {
        Mercs = mercs;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public boolean isKnight() {
        return isKnight;
    }

    public void setKnight(boolean knight) {
        isKnight = knight;
    }
}
