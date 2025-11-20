package xyz.teamAtlanta.seaculumFerrum;

import com.palmergames.util.FileMgmt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Science {
    private final String ID;
    private String FormattedName;
    private String intro;
    private String motto;
    private int Cost;
    private final List<Science> PreRequisiteSciences = new ArrayList<>();
    private final List<Science> AndPreRequisiteSciences = new ArrayList<>();
    private final List<Science> PostPositionSciences = new ArrayList<>();

    public Science(String id) {this.ID = id;}
    public String getID() {
        return ID;
    }

    public void load() {
        String line = null;
        String path = Main.getInstance().getDataFolder().getPath() + File.separator + "OIKMCSciences" + File.separator + getID() + ".txt";
        File fileWar = new File(path);
        HashMap<String, String> keys = FileMgmt.loadFileIntoHashMap(fileWar);
        line = keys.get("cost");if (line != null) {Cost = Integer.parseInt(line);}line = null;
        line = keys.get("formattedname");if (line != null) {FormattedName = line;}line = null;
        line = keys.get("intro");if (line != null) {intro = line;}line = null;
        line = keys.get("motto");if (line != null) {motto = line;}line = null;

        line = keys.get("prerequisites");if (line != null) {
            String[] scis = Utils.transferStringAToArray(line);
            if(scis.length > 0) {
                for(String s : scis) {
                    if(Main.hasScience(s)) {
                        PreRequisiteSciences.add(Main.getScience(s));
                    }
                }
            }
        }line = null;
        line = keys.get("andprerequisites");if (line != null) {
            String[] scis = Utils.transferStringAToArray(line);
            if(scis.length > 0) {
                for(String s : scis) {
                    if(Main.hasScience(s)) {
                        AndPreRequisiteSciences.add(Main.getScience(s));
                    }
                }
            }
        }line = null;

        line = keys.get("postpositions");if (line != null) {
            String[] scis = Utils.transferStringAToArray(line);
            if(scis.length > 0) {
                for(String s : scis) {
                    if(Main.hasScience(s)) {
                        PostPositionSciences.add(Main.getScience(s));
                    }
                }
            }
        }line = null;


        //应当Catch


    }

    public String getFormattedName() {
        return FormattedName;
    }

    public List<Science> getPreRequisiteSciences() {
        return PreRequisiteSciences;
    }

    public List<Science> getAndPreRequisiteSciences() {
        return AndPreRequisiteSciences;
    }

    public List<Science> getPostPositionSciences() {
        return PostPositionSciences;
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
