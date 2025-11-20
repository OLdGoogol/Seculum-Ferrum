package xyz.teamAtlanta.seaculumFerrum.meta;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.DecimalDataField;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;

public class MetaDataUtil {
    //Oikmc-CIV:FNAP Project
    private static IntegerDataField townblockValue = new IntegerDataField("townblockValue", 0);
    private static IntegerDataField townblockValueofBuilding = new IntegerDataField("townblockValueofBuilding", 0);

    private static IntegerDataField townSciencePoint = new IntegerDataField("townSciencePoint", 0);
    private static IntegerDataField townCulturePoint = new IntegerDataField("townCulturePoint", 0);
    private static StringDataField townSciences = new StringDataField("townSciences", "");
    private static StringDataField townCultures = new StringDataField("townCultures", "");
    private static StringDataField recruits = new StringDataField("recruits", ""  );
    private static BooleanDataField taxed = new BooleanDataField("taxed", false);

    private static StringDataField nationCores = new StringDataField("nationCores", "");
    private static StringDataField nationVassals = new StringDataField("nationVassals", "");
    private static IntegerDataField nationTitleFixed = new IntegerDataField("nationHasTitleFixed", -1);
    private static BooleanDataField nationFreeSubject = new BooleanDataField("nationFreeSubject", false);
    private static DecimalDataField nationFeudalTax = new DecimalDataField("nationFeudalTax", 0.0);
    private static IntegerDataField barbarian = new IntegerDataField("barbarian", -1);

    public static int getTownblockValue(TownBlock townBlock) {
        IntegerDataField idf = (IntegerDataField) townblockValue.clone();
        if (townBlock.hasMeta(idf.getKey()))
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getInt(townBlock, idf);
        return 0;
    }

    public static void setTownblockValue(TownBlock townBlock, int num) {
        IntegerDataField idf = (IntegerDataField) townblockValue.clone();
        if (townBlock.hasMeta(idf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setInt(townBlock, idf, num, true);
        else
            townBlock.addMetaData(new IntegerDataField("townblockValue", num));
    }
    public static int getTownblockValueofBuilding(TownBlock townBlock) {
        IntegerDataField idf = (IntegerDataField) townblockValueofBuilding.clone();
        if (townBlock.hasMeta(idf.getKey()))
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getInt(townBlock, idf);
        return 0;
    }

    public static void setTownblockValueofBuilding(TownBlock townBlock, int num) {
        IntegerDataField idf = (IntegerDataField) townblockValueofBuilding.clone();
        if (townBlock.hasMeta(idf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setInt(townBlock, idf, num, true);
        else
            townBlock.addMetaData(new IntegerDataField("townblockValueofBuilding", num));
    }

    public static int getTownSciencePoints(Town town) {
        IntegerDataField idf = (IntegerDataField) townSciencePoint.clone();
        if (town.hasMeta(idf.getKey()))
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getInt(town, idf);
        return 0;
    }

    public static void setTownSciencePoint(Town town, int num) {
        IntegerDataField idf = (IntegerDataField) townSciencePoint.clone();
        if (town.hasMeta(idf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setInt(town, idf, num, true);
        else
            town.addMetaData(new IntegerDataField("townSciencePoint", num));
    }

    public static int getTownCulturePoints(Town town) {
        IntegerDataField idf = (IntegerDataField) townCulturePoint.clone();
        if (town.hasMeta(idf.getKey()))
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getInt(town, idf);
        return 0;
    }

    public static void setTownCulturePoint(Town town, int num) {
        IntegerDataField idf = (IntegerDataField) townCulturePoint.clone();
        if (town.hasMeta(idf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setInt(town, idf, num, true);
        else
            town.addMetaData(new IntegerDataField("townCulturePoint", num));
    }

    public static String getTownScience(Town town) {
        StringDataField sdf = (StringDataField) townSciences.clone();
        if (town.hasMeta(sdf.getKey()))
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getString(town, sdf);
        return "";
    }

    public static void setTownScience(Town town, String status) {
        StringDataField sdf = (StringDataField) townSciences.clone();
        if (town.hasMeta(sdf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setString(town, sdf, status, true);
        else
            town.addMetaData(new StringDataField("townSciences", status));
    }

    public static String getTownCulture(Town town) {
        StringDataField sdf = (StringDataField) townCultures.clone();
        System.out.println("城镇的名字是" + town.getName() + ",窝是不信了。怎麽会没有捏？？？");
        System.out.println(sdf.getKey());
        if (town.hasMeta(sdf.getKey()))
        {
            System.out.println("麻了……");
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getString(town, sdf);}
        return "";
    }

    public static void setTownCulture(Town town, String status) {
        StringDataField sdf = (StringDataField) townCultures.clone();
        if (town.hasMeta(sdf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setString(town, sdf, status, true);
        else
            town.addMetaData(new StringDataField("townCultures", status));
    }

    public static String getRecruits(Town town) {
        StringDataField sdf = (StringDataField)recruits.clone();
        if (town.hasMeta(sdf.getKey()))
        {System.out.println("麻了……");
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getString(town, sdf);}
        return "";
    }

    public static void setRecruits(Town town, String status) {
        StringDataField sdf = (StringDataField)recruits.clone();
        if (town.hasMeta(sdf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setString(town, sdf, status, true);
        else
            town.addMetaData(new StringDataField("recruits", status));
    }

    public static int getBarbarian(Nation nation) {
        IntegerDataField idf = (IntegerDataField) barbarian.clone();
        if (nation.hasMeta(idf.getKey()))
        {
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getInt(nation, idf);}
        return 0;
    }

    public static void setBarbarian(Nation nation, int n) {
        IntegerDataField idf = (IntegerDataField) barbarian.clone();
        if (nation.hasMeta(idf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setInt(nation, idf, n, true);
        else
            nation.addMetaData(new IntegerDataField("barbarian", n));
    }

    public static String getNationCores(Nation nation) {
        StringDataField sdf = (StringDataField) nationCores.clone();
        if (nation.hasMeta(sdf.getKey()))
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getString(nation, sdf);
        return "";
    }

    public static void setNationCores(Nation nation, String status) {
        StringDataField sdf = (StringDataField) nationCores.clone();
        if (nation.hasMeta(sdf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setString(nation, sdf, status, true);
        else
            nation.addMetaData(new StringDataField("nationCores", status));
    }

    public static String getNationVassals(Nation nation) {
        StringDataField sdf = (StringDataField) nationVassals.clone();
        if (nation.hasMeta(sdf.getKey()))
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getString(nation, sdf);
        return "";
    }

    public static void setNationVassals(Nation nation, String status) {
        StringDataField sdf = (StringDataField) nationVassals.clone();
        if (nation.hasMeta(sdf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setString(nation, sdf, status, true);
        else
            nation.addMetaData(new StringDataField("nationVassals", status));
    }

    public static int getNationTitleFixed(Nation nation) {
        IntegerDataField idf = (IntegerDataField) nationTitleFixed.clone();
        if (nation.hasMeta(idf.getKey()))
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getInt(nation, idf);
        return -1;
    }

    public static void setNationTitleFixed(Nation nation, int num) {
        IntegerDataField idf = (IntegerDataField) nationTitleFixed.clone();
        if (nation.hasMeta(idf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setInt(nation, idf, num, true);
        else
            nation.addMetaData(new IntegerDataField("nationTitleFixed", num));
    }

    public static double getNationFeudalTax(Nation nation) {
        DecimalDataField ddf = (DecimalDataField) nationFeudalTax.clone();
        if (nation.hasMeta(ddf.getKey()))
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getDouble(nation, ddf);
        return 0;
    }

    public static void setNationFeudalTax(Nation nation, double num) {
        DecimalDataField ddf = (DecimalDataField) nationFeudalTax.clone();
        if (nation.hasMeta(ddf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setDouble(nation, ddf, num, true);
        else
            nation.addMetaData(new DecimalDataField("nationFeudalTax", num));
    }

    public static boolean getNationFreeSubject(Nation nation) {
        BooleanDataField bdf = (BooleanDataField) nationFreeSubject.clone();
        if (nation.hasMeta(bdf.getKey()))
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getBoolean(nation, bdf);
        return false;
    }

    public static void setNationFreeSubject(Nation nation, boolean status) {
        BooleanDataField bdf = (BooleanDataField) nationFreeSubject.clone();
        if (nation.hasMeta(bdf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setBoolean(nation, bdf, status, true);
        else
            nation.addMetaData(new BooleanDataField("nationFreeSubject", status));
    }

    public static boolean getTaxed(Town town) {
        BooleanDataField bdf = (BooleanDataField) taxed.clone();
        if (town.hasMeta(bdf.getKey()))
            return com.palmergames.bukkit.towny.utils.MetaDataUtil.getBoolean(town, bdf);
        return false;
    }

    public static void setTaxed(Town town, boolean status) {
        BooleanDataField bdf = (BooleanDataField) taxed.clone();
        if (town.hasMeta(bdf.getKey()))
            com.palmergames.bukkit.towny.utils.MetaDataUtil.setBoolean(town, bdf, status, true);
        else
            town.addMetaData(new BooleanDataField("taxed", status));
    }

}
