package xyz.teamAtlanta.seaculumFerrum;

import com.palmergames.util.FileMgmt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Culture {
    private String ID;
    private String FormattedName;
    private String intro;
    private String motto;
    private int Cost;
    private final List<Culture> PreRequisiteCultures = new ArrayList<>();
    private final List<Culture> AndPreRequisiteCultures = new ArrayList<>();
    private final List<Culture> PostPositionCultures = new ArrayList<>();

    public Culture(String id) {this.ID = id;}
    public String getID() {
        return ID;
    }

    public void load() {
        String line = null;
        String path = Main.getInstance().getDataFolder().getPath() + File.separator + "OIKMCCultures" + File.separator + getID() + ".txt";
        File fileCu = new File(path);
        HashMap<String, String> keys = FileMgmt.loadFileIntoHashMap(fileCu);
        line = keys.get("cost");if (line != null) {Cost = Integer.parseInt(line);}line = null;
        line = keys.get("formattedname");if (line != null) {FormattedName = line;}line = null;
        line = keys.get("intro");if (line != null) {intro = line;}line = null;
        line = keys.get("motto");if (line != null) {motto = line;}line = null;

        line = keys.get("prerequisites");if (line != null) {
            String[] cus = Utils.transferStringAToArray(line);
            if(cus.length > 0) {
                for(String s : cus) {
                    if(Main.hasCulture(s)) {
                        PreRequisiteCultures.add(Main.getCulture(s));
                    }
                }
            }
        }line = null;

        line = keys.get("andprerequisites");if (line != null) {
            String[] cus = Utils.transferStringAToArray(line);
            if(cus.length > 0) {
                for(String s : cus) {
                    if(Main.hasCulture(s)) {
                        AndPreRequisiteCultures.add(Main.getCulture(s));
                    }
                }
            }
        }line = null;

        line = keys.get("postpositions");if (line != null) {
            String[] cus2 = Utils.transferStringAToArray(line);
            if(cus2.length > 0) {
                for(String s : cus2) {
                    if(Main.hasCulture(s)) {
                        PostPositionCultures.add(Main.getCulture(s));
                    }
                }
            }
        }line = null;


        //应当Catch


    }

    public String getFormattedName() {
        return FormattedName;
    }

    public List<Culture> getPreRequisiteCultures() {
        return PreRequisiteCultures;
    }

    public List<Culture> getAndPreRequisiteCultures() {
        return AndPreRequisiteCultures;
    }

    public List<Culture> getPostPositionCultures() {
        return PostPositionCultures;
    }

    public String getIntro() {
        return intro;
    }

    public String getMotto() {
        return motto;
    }

    public int getCost() {
        return Cost;
    }
}
