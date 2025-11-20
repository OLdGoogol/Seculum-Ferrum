package xyz.teamAtlanta.seaculumFerrum;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import org.apache.http.impl.io.IdentityOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.quickshop.listener.PlayerListener;
import org.mcmonkey.sentinel.SentinelTrait;
import xyz.teamAtlanta.seaculumFerrum.command.*;
import xyz.teamAtlanta.seaculumFerrum.listener.StatusScreenListener;
import xyz.teamAtlanta.seaculumFerrum.listener.TechnologyListener;
import xyz.teamAtlanta.seaculumFerrum.listener.TownyEventListener;
import xyz.teamAtlanta.seaculumFerrum.meta.MetaDataUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends JavaPlugin{
    public static JavaPlugin Instance;
    private static Map<String, Culture> IDCultureMap = new ConcurrentHashMap<>();
    private static Map<String, Science> IDScienceMap = new ConcurrentHashMap<>();
    private static Map<String, Territory> TerritoriesMap = new ConcurrentHashMap<>();
    private static Map<String, Mercs> MercsMap = new ConcurrentHashMap<>();


    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }

    }

    public static int getNewSciPoints(Town town) {
        double mod = 1.0;
        int sciPoint = 0;int sciPoint_b = 0;
        double moc = 1.0;
        int cuPoint = 0;int cuPoint_b = 0;
        for(TownBlock townBlock : town.getTownBlocks()) {
            if(townBlock.getType().getName().toLowerCase(Locale.ROOT).equals("sci")) {
                if(townBlock.hasMeta("townblockValueofBuilding")) {
                    sciPoint_b += Main.getTownBlockValueofBuilding(townBlock);
                }
                if(townBlock.hasMeta("townblockValue")) {
                    sciPoint += Main.getTownBlockValue(townBlock);
                }
            }
            if(townBlock.getType().getName().toLowerCase(Locale.ROOT).equals("art")) {
                if(townBlock.hasMeta("townblockValueofBuilding")) {
                    cuPoint_b += Main.getTownBlockValueofBuilding(townBlock);
                }
                if(townBlock.hasMeta("townblockValue")) {
                    cuPoint += Main.getTownBlockValue(townBlock);
                }
            }
        }
        double mod2 = 1.0;
        if(town.hasNation()) {mod2 -= 0.01 * MetaDataUtil.getBarbarian(
                Objects.requireNonNull(town.getNationOrNull()));}
        return (int)(mod * (sciPoint + sciPoint_b) * 1.0 * mod2);
    }

    public static int getNewCulPoints(Town town) {
        double mod = 1.0;
        int sciPoint = 0;int sciPoint_b = 0;
        double moc = 1.0;
        int cuPoint = 0;int cuPoint_b = 0;
        for(TownBlock townBlock : town.getTownBlocks()) {
            if(townBlock.getType().getName().toLowerCase(Locale.ROOT).equals("sci")) {
                if(townBlock.hasMeta("townblockValueofBuilding")) {
                    sciPoint_b += Main.getTownBlockValueofBuilding(townBlock);
                }
                if(townBlock.hasMeta("townblockValue")) {
                    sciPoint += Main.getTownBlockValue(townBlock);
                }
            }
            if(townBlock.getType().getName().toLowerCase(Locale.ROOT).equals("art")) {
                if(townBlock.hasMeta("townblockValueofBuilding")) {
                    cuPoint_b += Main.getTownBlockValueofBuilding(townBlock);
                }
                if(townBlock.hasMeta("townblockValue")) {
                    cuPoint += Main.getTownBlockValue(townBlock);
                }
            }
        }
        double mod2 = 1.0;
        if(town.hasNation()) {mod2 -= 0.01 * MetaDataUtil.getBarbarian(
                Objects.requireNonNull(town.getNationOrNull()));}
        return (int)(moc * (cuPoint + cuPoint_b) * 1.0 * mod2);
    }

    @Override
    public void onEnable() {

        Instance = this;
        //need to make some decorates
        System.out.println("test1");
        loadAll();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new StatusScreenListener(), this);
        pm.registerEvents(new TownyEventListener(), this);
        pm.registerEvents(new TechnologyListener(), this);

        new OIKMCPlotInvestCommandAddon();
        new OIKMCTownCommandAddon();
        new OIKMCNationCommandAddon();
        new OIKMCNationToggleCommandAddon();
        new OIKMCNationSetCommandAddon();
        new OIKMCtaCommandAddon();
        new OIKMCResidentCommandAddon();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            System.out.println("确实有啊……");
            new SomeExpansion(this).register();
        }

    }



    public static JavaPlugin getInstance() {
        return Instance;
    }

    public static boolean loadAll() {
        try {
         //OIKMC stuffs
            loadScienceList();
            getInstance().getLogger().info("Loading OIKMCScience's List...");
            loadSciences();
            getInstance().getLogger().info("Loading OIKMCScience Data...");
            loadCultureList();
            getInstance().getLogger().info("Loading OIKMCCulture's List...");
            loadCultures();
            getInstance().getLogger().info("Loading OIKMCCulture Data...");
            loadTerritoriesList();
            getInstance().getLogger().info("Loading OIKMCTerritories' List...");
            loadTerritories();
            getInstance().getLogger().info("Loading OIKMCTerritories Data...");
            loadMercsList();
            getInstance().getLogger().info("Loading OIKMCMercs' List...");
            loadMercs();
            getInstance().getLogger().info("Loading OIKMCMercs Data...");

            return true;
        } catch (Exception e) {
            getInstance().getLogger().severe("Problem Loading Siege Data...");
            e.printStackTrace();
            return false;
        }
    }

    public static void loadScienceList() {
        File[] sciFiles = new File(getInstance().getDataFolder().getPath() + File.separator + "OIKMCSciences").listFiles(file -> file.getName().toLowerCase().endsWith(".txt"));
        if (sciFiles != null) {
            for (File dec : sciFiles) {
                String id = dec.getName().replace(".txt", "");
                Science science = new Science(id);
                initializeScience(science);
            }
        }
    }

    public static void loadSciences() {
        for (Science science : IDScienceMap.values()) {
            science.load();
        }
    }

    public static List<String> outputSciencesList() {
        return new ArrayList<String>(IDScienceMap.keySet());
    }

    public static List<String> outputTerritoriesList() {
        return new ArrayList<String>(TerritoriesMap.keySet());
    }

    public static List<String> outputMercsList() {
        return new ArrayList<String>(MercsMap.keySet());
    }

    public static Mercs getMercs(String s) {
        return MercsMap.get(s);
    }

    public static void loadTerritoriesList() {
        File[] terrFiles = new File(getInstance().getDataFolder().getPath() + File.separator + "OIKMCTerritories").listFiles(file -> file.getName().toLowerCase().endsWith(".txt"));
        if (terrFiles != null) {
            for (File dec : terrFiles) {
                String id = dec.getName().replace(".txt", "");
                System.out.println("找到地块id" + id);
                Territory terr = new Territory(id);
                System.out.println("建立新terr，名称为" +  terr.getID());
                initializeTerritory(terr);
            }
        }
    }

    public static void loadTerritories() {
        for (Territory t : TerritoriesMap.values()) {
            System.out.println("load时候map里有这个" + t.getID());
            t.load();
        }
    }

    public static void loadMercsList() {
        File[] terrFiles = new File(getInstance().getDataFolder().getPath() + File.separator + "OIKMCMercs").listFiles(file -> file.getName().toLowerCase().endsWith(".txt"));
        if (terrFiles != null) {
            for (File dec : terrFiles) {
                String id = dec.getName().replace(".txt", "");
                System.out.println("找到雇佣兵id" + id);
                Mercs terr = new Mercs(id);
                System.out.println("建立新mercs，名称为" +  terr.getID());
                initializeMercs(terr);
            }
        }
    }

    private static void initializeMercs(Mercs terr) {
        System.out.println("初始化" + terr.getID());
        MercsMap.put(terr.getID(), terr);
    }

    public static void loadMercs() {
        for (Mercs t : MercsMap.values()) {
            System.out.println("load时候map里有这个" + t.getID());
            t.load();
        }
    }

    public static List<String> outputCulturesList() {
        return new ArrayList<String>(IDCultureMap.keySet()) {
        };
    }

    public static void loadCultureList() {
        File[] culFiles = new File(getInstance().getDataFolder().getPath() + File.separator + "OIKMCCultures").listFiles(file -> file.getName().toLowerCase().endsWith(".txt"));
        if (culFiles != null) {
            for (File dec : culFiles) {
                String id = dec.getName().replace(".txt", "");
                Culture culture = new Culture(id);
                initializeCulture(culture);
            }
        }
    }

    public static int getRecruitLimit(Town town) {
        int re = 0;int[] l = getDetailedRecruitLimit(town);
        for(int a : l) {re += a / 1000;}
        return re;
    }

    public static int[] getDetailedRecruitLimit(Town town) {
        int[] re = new int[4];
        re[3] = 1000;
        //1級市政
        if(Main.hasTownCulture(town, "UN01") && town.isCapital()) {re[3]+=3000;}

        for(TownBlock block : town.getTownBlocks()) {
            if(block.getTypeName().toLowerCase(Locale.ROOT).equals("farm")) {
                re[3] += (Main.getTownBlockValue(block) + Main.getTownBlockValueofBuilding(block)) * 100;
            }
            if(block.getTypeName().toLowerCase(Locale.ROOT).equals("stable")) {
                re[2] += (Main.getTownBlockValue(block) + Main.getTownBlockValueofBuilding(block)) * 25;
            }
            if(block.getTypeName().toLowerCase(Locale.ROOT).equals("base")) {
                re[1] += (Main.getTownBlockValue(block) + Main.getTownBlockValueofBuilding(block)) * 50;
            }
            if(block.getTypeName().toLowerCase(Locale.ROOT).equals("barrack")) {
                re[0] += (Main.getTownBlockValue(block) + Main.getTownBlockValueofBuilding(block)) * 50;
            }
        }

        return re;
    }

    public static void disRecruit(Town town, CommandSender commandSender) {
        System.out.println(MetaDataUtil.getRecruits(town));
        String[] recruits = Utils.transferStringAToArray(MetaDataUtil.getRecruits(town));
        System.out.println("征召兵数量：" + recruits.length);

        for(String rec : recruits) {
            System.out.println(rec);
            NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(rec));
            if(npc == null) {continue;}
            System.out.println("test --disrecruit");
            npc.despawn();
            npc.destroy();
        }
        MetaDataUtil.setRecruits(town, "");

    }

    public static void disRecruitMercs(Mercs mercs, CommandSender commandSender) {
        int[] m = mercs.getMercs();
        if(m == null) {return;}
        System.out.println("雇佣兵数量：" + m.length);

        for(int merc : m) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(merc);
            if(npc == null) {continue;}
            System.out.println("test --disrecruit");
            npc.despawn();
            npc.destroy();
        }
        mercs.setEmployer(null);
        mercs.setMercs(null);
        mercs.save();
    }

    public static void RecruitMercs(Mercs mercs, CommandSender sender) {
        if(mercs.getEmployer() != null) {
            TownyMessaging.sendMsg(sender, "该雇佣兵团已经" + mercs.getEmployer().getName() + "被雇佣了");
            return;
        }

        int n = 0;
        int[] limitD = mercs.getDetailNums();
        int[] newColume = new int[mercs.getNum()];
        System.out.println(limitD[3]);
        for (int i = 0; i < limitD[3]; i++) {
            System.out.println("i在检测之前是" + i);
            System.out.println(limitD[3]-1);
            System.out.println("检测的结果“" + (i < limitD[3]-1));
            System.out.println("所以到底是进了哪个的坑-m");

            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");System.out.println(npc.getId());
            if (!(sender instanceof Player)) {
                return;
            }
            System.out.println("i的数目是" + i);
            newColume[n] = (npc.getId());
            npc.setName("militia_" + npc.getId());
            npc.spawn(((Player) sender).getLocation());
            n++;

            SentinelTrait s = npc.getOrAddTrait(SentinelTrait.class);

            //numbers
            s.health = 25;
            s.damage = 5;

            //protect the player
            s.setGuarding(((Player) sender).getUniqueId());

            s.addTarget("monsters");
        }

        for (int i = 0; i < limitD[2]; i++) {
            System.out.println("所以到底是进了哪个的坑-h");
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
            NPC npchorse = CitizensAPI.getNPCRegistry().createNPC(EntityType.HORSE, "");


            if (!(sender instanceof Player)) {
                return;
            }



            System.out.println("i的数目是" + i);

            newColume[n] = (npc.getId());
            npc.setName("horseman_" + npc.getId());
            npc.spawn(((Player) sender).getLocation());
            npchorse.setName("horse" + npc.getId());
            npchorse.spawn(((Player) sender).getLocation());

            ConsoleCommandSender c = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(c, "npc sel " + npchorse.getId());
            Bukkit.dispatchCommand(c, "npc controllable");
            Bukkit.dispatchCommand(c, "npc sel " + npc.getId());
            Bukkit.dispatchCommand(c, "npc mount --onnpc " + npchorse.getId());

            n++;
            SentinelTrait s = npc.getOrAddTrait(SentinelTrait.class);

            //numbers
            s.health = 40;
            s.damage = 20;

            //protect the player
            s.setGuarding(((Player) sender).getUniqueId());
            s.addTarget("monsters");

        }
        for (int i = 0; i < limitD[1]; i++) {
            System.out.println("所以到底是进了哪个的坑-c");

            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
            if (!(sender instanceof Player)) {
                return;
            }
            System.out.println("i的数目是" + i);
            newColume[n] = (npc.getId());
            npc.setName("crossbowman_" + npc.getId());
            npc.spawn(((Player) sender).getLocation());
            n++;
            SentinelTrait s = npc.getOrAddTrait(SentinelTrait.class);

            //numbers
            s.health = 25;
            s.damage = 10;

            //protect the player
            s.setGuarding(((Player) sender).getUniqueId());
            s.addTarget("monsters");

        }

        for (int i = 0; i < limitD[0]; i++) {
            System.out.println("所以到底是进了哪个的坑-w");

            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
            if (!(sender instanceof Player)) {
                return;
            }

            System.out.println("i的数目是" + i);
            ItemStack weapon = new ItemStack(Material.CROSSBOW);
            /*((Player) sender).getInventory().setItemInOffHand(weapon);
            ((Player) sender).getInventory().setItemInMainHand(weapon);
            ((Player) sender).getInventory().setItem(1, weapon);

             */

            newColume[n] = (npc.getId());
            npc.setName("warrior_" + npc.getId());
            npc.spawn(((Player) sender).getLocation());
            n++;
            SentinelTrait s = npc.getOrAddTrait(SentinelTrait.class);

            //numbers
            s.health = 60;
            s.damage = 15;

            //protect the player
            s.setGuarding(((Player) sender).getUniqueId());
            s.addTarget("monsters");

            mercs.setMercs(newColume);
            mercs.setEmployer(TownyUniverse.getInstance().getResident(((Player) sender).getUniqueId()));
            mercs.save();
        }

    }

    public static void Recruit(Town town, CommandSender sender) {
        String[] recruits = Utils.transferStringAToArray(MetaDataUtil.getRecruits(town));
        System.out.println("召集征召兵");

        int limit = getRecruitLimit(town);
        int[] limitD = getDetailedRecruitLimit(town);
        for(int a=0; a<limitD.length; a++) {limitD[a]=limitD[a]/1000;System.out.println("a的数:" + limitD[a]);}
        int n = recruits.length;

        for(String rec : recruits) {
            if(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(rec)) == null) {continue;}
            if(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(rec)).getName().contains("militia")) {limitD[3]--;}
            if(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(rec)).getName().contains("horseman")) {limitD[2]--;}
            if(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(rec)).getName().contains("crossbowman")) {limitD[1]--;}
            if(CitizensAPI.getNPCRegistry().getById(Integer.parseInt(rec)).getName().contains("warrior")) {limitD[0]--;}


        }

        String[] newColume = new String[recruits.length + limit - recruits.length];
        System.arraycopy(recruits, 0, newColume, 0, recruits.length);
        System.out.println(limitD[3]);
        for (int i = 0; i < limitD[3]; i++) {
            System.out.println("i在检测之前是" + i);
            System.out.println(limitD[3]-1);
            System.out.println("检测的结果“" + (i < limitD[3]-1));
            System.out.println("所以到底是进了哪个的坑-m");

            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");System.out.println(npc.getId());
            if (!(sender instanceof Player)) {
                return;
            }
            System.out.println("i的数目是" + i);
            newColume[n] = String.valueOf(npc.getId());
            npc.setName("militia_" + npc.getId());
            npc.spawn(((Player) sender).getLocation());
            n++;

            SentinelTrait s = npc.getOrAddTrait(SentinelTrait.class);

            //numbers
            s.health = 25;
            s.damage = 5;

            //protect the player
            s.setGuarding(((Player) sender).getUniqueId());

            s.addTarget("monsters");
        }

        for (int i = 0; i < limitD[2]; i++) {
            System.out.println("所以到底是进了哪个的坑-h");
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
            NPC npchorse = CitizensAPI.getNPCRegistry().createNPC(EntityType.HORSE, "");


            if (!(sender instanceof Player)) {
                return;
            }



            System.out.println("i的数目是" + i);

            newColume[n] = String.valueOf(npc.getId());
            npc.setName("horseman_" + npc.getId());
            npc.spawn(((Player) sender).getLocation());
            npchorse.setName("horse" + npc.getId());
            npchorse.spawn(((Player) sender).getLocation());

            ConsoleCommandSender c = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(c, "npc sel " + npchorse.getId());
            Bukkit.dispatchCommand(c, "npc controllable");
            Bukkit.dispatchCommand(c, "npc sel " + npc.getId());
            Bukkit.dispatchCommand(c, "npc mount --onnpc " + npchorse.getId());

            n++;
            SentinelTrait s = npc.getOrAddTrait(SentinelTrait.class);

            //numbers
            s.health = 40;
            s.damage = 20;

            //protect the player
            s.setGuarding(((Player) sender).getUniqueId());
            s.addTarget("monsters");

        }
        for (int i = 0; i < limitD[1]; i++) {
            System.out.println("所以到底是进了哪个的坑-c");

            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
            if (!(sender instanceof Player)) {
                return;
            }
            System.out.println("i的数目是" + i);
            newColume[n] = String.valueOf(npc.getId());
            npc.setName("crossbowman_" + npc.getId());
            npc.spawn(((Player) sender).getLocation());
            n++;
            SentinelTrait s = npc.getOrAddTrait(SentinelTrait.class);

            //numbers
            s.health = 25;
            s.damage = 10;

            //protect the player
            s.setGuarding(((Player) sender).getUniqueId());
            s.addTarget("monsters");

        }

        for (int i = 0; i < limitD[0]; i++) {
            System.out.println("所以到底是进了哪个的坑-w");

            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
            if (!(sender instanceof Player)) {
                return;
            }

            System.out.println("i的数目是" + i);
            ItemStack weapon = new ItemStack(Material.CROSSBOW);
            ((Player) sender).getInventory().setItemInOffHand(weapon);
            ((Player) sender).getInventory().setItemInMainHand(weapon);
            ((Player) sender).getInventory().setItem(1, weapon);

            newColume[n] = String.valueOf(npc.getId());
            npc.setName("warrior_" + npc.getId());
            npc.spawn(((Player) sender).getLocation());
            n++;
            SentinelTrait s = npc.getOrAddTrait(SentinelTrait.class);

            //numbers
            s.health = 60;
            s.damage = 15;

            //protect the player
            s.setGuarding(((Player) sender).getUniqueId());
            s.addTarget("monsters");

        }
        System.out.println(newColume.length);
        MetaDataUtil.setRecruits(town, String.join(",", newColume));
        System.out.println(String.join(",", newColume));
    }



    public static void loadCultures() {
        for (Culture culture : IDCultureMap.values()) {
            culture.load();
        }
    }

    public static int getNationTitle(Nation nation) {
        return Math.max(MetaDataUtil.getNationTitleFixed(nation), nation.getLevel());
    }

    public static boolean hasScience(String ID) {
        return IDScienceMap.containsKey(ID);
    }

    public static Science getScience(String ID) {
        if (hasScience(ID))
            return IDScienceMap.get(ID);
        return null;
    }

    public static boolean hasTerritory(String ID) {return TerritoriesMap.containsKey(ID);
    }

    public static Territory getTerritory(String ID) {
        if (hasTerritory(ID))
            return TerritoriesMap.get(ID);
        return null;
    }

    public static void initializeScience(Science science) {
        IDScienceMap.put(science.getID(), science);

    }

    public static boolean hasCulture(String ID) {
        return IDCultureMap.containsKey(ID);
    }

    public static Culture getCulture(String ID) {
        System.out.println("文化映射里的情况" + IDCultureMap.keySet().toString());
        System.out.println(hasCulture(ID));
        if (hasCulture(ID))
            return IDCultureMap.get(ID);
        return null;
    }

    public static void initializeCulture(Culture culture) {
        IDCultureMap.put(culture.getID(), culture);

    }

    public static void initializeTerritory(Territory t) {
        System.out.println("初始化" + t.getID());
        TerritoriesMap.put(t.getID(), t);

    }

    //OIKMC main functions
    public static int getTownBlockValue(TownBlock townBlock) {
        return Math.max(MetaDataUtil.getTownblockValue(townBlock), 0);
    }

    //6級市政
    public static int getTownBlockValueofBuilding(TownBlock townBlock) {
        try {
            if(Main.hasTownCulture(townBlock.getTown(), "UN06")) {
                return Math.max(MetaDataUtil.getTownblockValueofBuilding(townBlock)+1, 0);
            }
        } catch (NotRegisteredException e) {
            throw new RuntimeException(e);
        }

        return Math.max(MetaDataUtil.getTownblockValueofBuilding(townBlock), 0);
    }

    public static void setTownBlockValue(TownBlock townBlock, int value) {
        MetaDataUtil.setTownblockValue(townBlock, value);
    }

    public static void setTownBlockValueofBuilding(TownBlock townBlock, int value) {
        MetaDataUtil.setTownblockValueofBuilding(townBlock, value);
    }

    public static int getTownSciencePoints(Town town) {
        return Math.max(MetaDataUtil.getTownSciencePoints(town), 0);
    }

    public static int getTownCulturesPoints(Town town) {
        return Math.max(MetaDataUtil.getTownCulturePoints(town), 0);
    }

    public static void setTownSciencePoints(Town town, int value) {
        MetaDataUtil.setTownSciencePoint(town, value);
    }

    public static void setTownCulturePoints(Town town, int value) {
        MetaDataUtil.setTownCulturePoint(town, value);
    }

    public static boolean hasTownScience(Town town, Science science) {
        return (Objects.requireNonNull(MetaDataUtil.getTownScience(town)).contains(science.getID()) && !Utils.transferStringAToArray(Objects.requireNonNull(MetaDataUtil.getTownScience(town)))[0].equalsIgnoreCase(science.getID()));
    }

    public static boolean hasTownScience(Town town, String ID) {
        return (Objects.requireNonNull(MetaDataUtil.getTownScience(town)).contains(ID) && !Utils.transferStringAToArray(Objects.requireNonNull(MetaDataUtil.getTownScience(town)))[0].equalsIgnoreCase(ID));
    }

    public static void addNewTownScience(Town town, Science science) {
        MetaDataUtil.setTownScience(town, String.join(",", MetaDataUtil.getTownScience(town), science.getID()));
        System.out.println(String.join(",", MetaDataUtil.getTownScience(town)));;
    }

    public static void addFutureTownScience(Town town, Science science, boolean replace) {
        if(MetaDataUtil.getTownCulture(town) == null) {MetaDataUtil.setTownScience(town, science.getID());}
        String[] scis = Utils.transferStringAToArray(MetaDataUtil.getTownScience(town));
        if(scis.length == 0) {
            MetaDataUtil.setTownScience(town, science.getID());
        }
        else if(scis[0].toLowerCase(Locale.ROOT).equals("null")) {
            MetaDataUtil.setTownScience(town, MetaDataUtil.getTownScience(town).replace("null", science.getID()));
        }
        else if(scis[0].equals("n")) {
            MetaDataUtil.setTownScience(town, MetaDataUtil.getTownScience(town).replace("n", science.getID()));
        }
        else if(replace) {
            MetaDataUtil.setTownScience(town, MetaDataUtil.getTownScience(town).replace(scis[0], science.getID()));
        }


    }

    public static void removeInvalidTownScience(Town town, Science sci) {
        String[] scis = Utils.transferStringAToArray(Objects.requireNonNull(MetaDataUtil.getTownCulture(town)));
        String newscis= scis[0];
        for (String s : scis) {
            if (!s.equalsIgnoreCase(scis[0]) && !s.equalsIgnoreCase(sci.getID())) {
                newscis = String.join(",", newscis, s);
            }
        }
        MetaDataUtil.setTownScience(town, newscis);
    }

    public static void addNewTownCulture(Town town, Culture culture) {
        MetaDataUtil.setTownCulture(town, String.join(",", MetaDataUtil.getTownCulture(town), culture.getID()));

    }

    public static void addFutureTownCulture(Town town, Culture culture, boolean replace) {
        if(MetaDataUtil.getTownCulture(town) == null) {MetaDataUtil.setTownCulture(town, culture.getID());}
        String[] cus = Utils.transferStringAToArray(MetaDataUtil.getTownCulture(town));
        if(cus.length == 0) {
            MetaDataUtil.setTownCulture(town, culture.getID());
        }
        else if(cus[0].toLowerCase(Locale.ROOT).equals("null")) {
            MetaDataUtil.setTownCulture(town, MetaDataUtil.getTownCulture(town).replace("null", culture.getID()));
        }
        else if(cus[0].equals("n")) {
            MetaDataUtil.setTownCulture(town, MetaDataUtil.getTownCulture(town).replace("n", culture.getID()));
        }
        else if(replace) {
            MetaDataUtil.setTownCulture(town, MetaDataUtil.getTownCulture(town).replace(cus[0], culture.getID()));
        }


    }

    public static void removeInvalidTownCulture(Town town, Culture cu) {
        System.out.println("这個时候就有了？？？");
        String[] cus = Utils.transferStringAToArray(Objects.requireNonNull(MetaDataUtil.getTownCulture(town)));
        String newcus= cus[0];
        for (String s : cus) {
            if (!s.equalsIgnoreCase(cus[0]) && !s.equalsIgnoreCase(cu.getID())) {
                newcus = String.join(",", newcus, s);
            }
        }
        MetaDataUtil.setTownCulture(town, newcus);
    }

    public static boolean hasTownCulture(Town town, Culture culture) {
        return (Objects.requireNonNull(MetaDataUtil.getTownCulture(town)).contains(culture.getID()) && !Utils.transferStringAToArray(MetaDataUtil.getTownCulture(town))[0].equalsIgnoreCase(culture.getID()));
    }

    public static boolean hasTownCulture(Town town, String ID) {
        return (Objects.requireNonNull(MetaDataUtil.getTownCulture(town)).contains(ID) && !Utils.transferStringAToArray(MetaDataUtil.getTownCulture(town))[0].equalsIgnoreCase(ID));
    }

    public static boolean hasNationCore(Nation nation, Town town) {
        return Objects.requireNonNull(MetaDataUtil.getNationCores(nation)).contains(town.getUUID().toString());
    }

    public static boolean hasNationVassal(Nation nation, Nation state) {
        return Objects.requireNonNull(MetaDataUtil.getNationVassals(nation)).contains(nation.getUUID().toString());
    }

    public static void addNewNationCore(Nation nation, Town town) {
        MetaDataUtil.setNationCores(nation, String.join(",", MetaDataUtil.getNationCores(nation), town.getUUID().toString()));
    }

    public static void removeNationCore(Nation nation, Town town) {
        String[] c = Utils.transferStringAToArray(Objects.requireNonNull(MetaDataUtil.getNationCores(nation)));
        String newc= c[0];
        for (String s : c) {
            if (!s.equalsIgnoreCase(c[0]) && !s.equalsIgnoreCase(town.getUUID().toString())) {
                newc = String.join(",", newc, s);
            }
        }
        MetaDataUtil.setNationCores(nation, newc);
    }

    public static void addNewNationVassal(Nation nation, Nation state) {
        if(nation.getUUID() == state.getUUID()) {return;}
        MetaDataUtil.setNationVassals(nation, String.join(",", MetaDataUtil.getNationVassals(nation), state.getUUID().toString()));
    }

    public static void removeNationVassal(Nation nation, Nation state) {
        if(nation.getUUID() == state.getUUID()) {return;}
        String[] v = Utils.transferStringAToArray(Objects.requireNonNull(MetaDataUtil.getNationVassals(nation)));
        String newv= v[0];
        for (String s : v) {
            if (!s.equalsIgnoreCase(v[0]) && !s.equalsIgnoreCase(state.getUUID().toString())) {
                newv = String.join(",", newv, s);
            }
        }
        MetaDataUtil.setNationVassals(nation,newv);
    }

    public static List<Nation> getNationVassals(Nation nation) {
        List<Nation> a = new ArrayList<>();
        if(MetaDataUtil.getNationVassals(nation) == null) {return a;}
        String[] v = Utils.transferStringAToArray(Objects.requireNonNull(MetaDataUtil.getNationVassals(nation)));
        for (String s : v) {
            if(TownyUniverse.getInstance().hasNation(UUID.fromString(s))) {
                a.add(TownyUniverse.getInstance().getNation(UUID.fromString(s)));
            }
        }
        return a;
    }

    public static int getNationFixedTitle(Nation nation) {
        return Math.max(MetaDataUtil.getNationTitleFixed(nation), -1);
    }

    public static void setNationFixedTitle(Nation nation, int title) {
        MetaDataUtil.setNationTitleFixed(nation, title);
    }


}