package xyz.teamAtlanta.seaculumFerrum;

public class Utils {
    public static String[] transferStringAToArray(String string) {
        if(string != null && !string.isEmpty()) {
            return string.split(",");
        }
        else {return new String[0];}
    }

    public static String[] transferStringBToArray(String string) {
        if(string != null && !string.isEmpty()) {
            return string.split("-");
        }
        else {return new String[0];}
    }


}
