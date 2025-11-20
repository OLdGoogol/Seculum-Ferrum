package xyz.teamAtlanta.seaculumFerrum.command;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.utils.NameUtil;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import xyz.teamAtlanta.seaculumFerrum.Culture;
import xyz.teamAtlanta.seaculumFerrum.Main;
import xyz.teamAtlanta.seaculumFerrum.Science;
import xyz.teamAtlanta.seaculumFerrum.Utils;
import xyz.teamAtlanta.seaculumFerrum.meta.MetaDataUtil;

import java.util.*;

public class OIKMCtaCommandAddon extends BaseCommand implements CommandExecutor, TabCompleter {
    public OIKMCtaCommandAddon() {
        AddonCommand townyAdminResourcesCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.TOWNYADMIN, "oik", this);
        TownyCommandAddonAPI.addSubCommand(townyAdminResourcesCommand);
    }

    private static final List<String> acceptedType = Arrays.asList("church", "gov", "base", "barrack", "art", "sci", "farm", "stable");


    private static final List<String> tabCompletes = Arrays.asList("plot", "nation", "town");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1)
            return NameUtil.filterByStart(tabCompletes, args[0]);
        else if(args.length == 2) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "town":
                    return getTownyStartingWith(args[1], "t");
                case "nation":
                    return getTownyStartingWith(args[1], "n");
                case "plot":
                    return Arrays.asList("value","valueofbuilding");
            }
        }
        else if(args.length == 3) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "nation":
                    return Arrays.asList("core","vassal");
                case "town":
                    return Arrays.asList("sci","cu");
                case "plot":
                    if (args[1].toLowerCase(Locale.ROOT).equals("value") || args[1].toLowerCase(Locale.ROOT).equals("valueofbuilding")) {return Arrays.asList("set");}
            }

        }
        else if(args.length == 4) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "nation":
                    if(args[2].toLowerCase(Locale.ROOT).equals("core") || args[2].toLowerCase(Locale.ROOT).equals("vassal")) {return Arrays.asList("add","remove");}
                case "town":
                    if(args[2].toLowerCase(Locale.ROOT).equals("sci") || args[2].toLowerCase(Locale.ROOT).equals("cu")) {return Arrays.asList("add","remove");}
                case "plot":
                    if ((args[1].toLowerCase(Locale.ROOT).equals("value") || args[1].toLowerCase(Locale.ROOT).equals("valueofbuilding")) && args[2].toLowerCase(Locale.ROOT).equals("set")) {return Arrays.asList("1","2","3");}

            }
        }
        else if(args.length == 5) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "nation":
                    switch (args[2].toLowerCase(Locale.ROOT)) {
                        case "core":
                            switch (args[3].toLowerCase(Locale.ROOT)) {
                                case "add":
                                    return getTownyStartingWith(args[4], "t");
                                case "remove":
                                    if(MetaDataUtil.getNationCores(TownyUniverse.getInstance().getNation(args[1])).equalsIgnoreCase("")) {return Collections.emptyList();}
                                    String[] c = Utils.transferStringAToArray(Objects.requireNonNull(MetaDataUtil.getNationCores(TownyUniverse.getInstance().getNation(args[1]))));
                                    List<Town> cores = new ArrayList<>();
                                    for (String s : c) {
                                        if(TownyUniverse.getInstance().hasTown(s)) {cores.add(TownyUniverse.getInstance().getTown(s));}
                                    }
                                    return NameUtil.filterByStart(NameUtil.getNames(cores), args[4]);
                            }

                        case "vassal":
                            switch (args[3].toLowerCase(Locale.ROOT)) {
                                case "add":
                                    return getTownyStartingWith(args[4], "n");
                                case "remove":
                                    if(MetaDataUtil.getNationVassals(TownyUniverse.getInstance().getNation(args[1])).equalsIgnoreCase("")) {return Collections.emptyList();}
                                    String[] c = Utils.transferStringAToArray(Objects.requireNonNull(MetaDataUtil.getNationVassals(TownyUniverse.getInstance().getNation(args[1]))));
                                    List<Nation> vassals = new ArrayList<>();
                                    for (String s : c) {
                                        if(TownyUniverse.getInstance().hasNation(s)) {vassals.add(TownyUniverse.getInstance().getNation(s));}
                                    }
                                    return NameUtil.filterByStart(NameUtil.getNames(vassals), args[4]);
                            }

                    }
                case "town":
                    switch (args[2].toLowerCase(Locale.ROOT)) {
                        case "cu":
                            switch (args[3].toLowerCase(Locale.ROOT)) {
                                case "add":
                                    return Main.outputCulturesList();
                                case "remove":
                                    return Arrays.asList(Utils.transferStringAToArray(Objects.requireNonNull(MetaDataUtil.getTownCulture(TownyUniverse.getInstance().getTown(args[1])))));
                            }

                        case "sci":
                            switch (args[3].toLowerCase(Locale.ROOT)) {
                                case "add":
                                    return Main.outputSciencesList();
                                case "remove":
                                    return Arrays.asList(Utils.transferStringAToArray(Objects.requireNonNull(MetaDataUtil.getTownScience(TownyUniverse.getInstance().getTown(args[1])))));
                            }

                    }
                    case "plot":
                    if (args[1].toLowerCase(Locale.ROOT).equals("value") && args[2].toLowerCase(Locale.ROOT).equals("set")) {return Arrays.asList("1","2","3");}

            }
        }
        return Collections.emptyList();
        }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("sf.ta")) {commandSender.sendMessage("你没有如此做的权限！");}
        if (args.length == 1){
            //test...give tree materials
            if (args[0].toLowerCase(Locale.ROOT).equals("givetreematerials")) {
                try {
                    Player player = catchConsole(commandSender);
                    //laurel
                    /*ItemStack item = new ItemStack(Material.OAK_LEAVES);
                    ItemMeta meta = item.getItemMeta();

                    meta.setDisplayName(ChatColor.DARK_GREEN + "Laurel Leaves");
                    meta.setCustomModelData(1452);
                    item.setItemMeta(meta);

                    item.setAmount(64);
                    player.getInventory().addItem(item);

                     */

                    //is 高端局 not using 'for' at all?
                    //obviously not, how did you learn how to use [加強for]?
                    //but, these things which limDi made actually cannot to be count as 高端玩意.
                    for(int i=-1; i<2; i++) {
                        for(int j=-1; j<2; j++) {
                            for(int k=0; k>-5; k--) {
                                {
                                    System.out.println("x为" + i);
                                    System.out.println("y为" + k);
                                    System.out.println("z为" + j);
                                }
                                Location loc = player.getLocation().add(i, k, j);
                                {
                                    System.out.println("x为" + loc.getX());
                                    System.out.println("y为" + loc.getY());
                                    System.out.println("z为" + loc.getZ());
                                }
                                loc.getBlock().setMetadata("custom_leave_data", new FixedMetadataValue(Main.getInstance(), "1452"));
                                //好勒,写好了
                                //(但是能跑就有鬼了
                                System.out.println("success!");

                            }
                        }
                    }




                } catch (TownyException e) {throw new RuntimeException(e);}

                return true;
            }
        }

        else if(args.length == 2) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "town":
                case "nation":
                //help
                case "plot":
            }
        }
        else if(args.length == 3) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "town":
                case "nation":
                case "plot":

            }

        }
        else if(args.length == 4) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "town":
                case "nation":

                case "plot":
                    if (args[1].toLowerCase(Locale.ROOT).equals("value") && args[2].toLowerCase(Locale.ROOT).equals("set"))
                    {parsetaPlotSetValue(commandSender, args[3]);}
                    else if (args[1].toLowerCase(Locale.ROOT).equals("valueofbuilding") && args[2].toLowerCase(Locale.ROOT).equals("set"))
                    {parsetaPlotSetValueofBuilding(commandSender, args[3]);}
                    
                    

            }
        }
        else if(args.length == 5) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "town":
                    if(args[3].toLowerCase(Locale.ROOT).equals("add") || args[3].toLowerCase(Locale.ROOT).equals("remove"))
                    {parsetaTownRemoveOrAddScienceOrCulture(commandSender,args[1],args[2],args[3],args[4]);}
                case "nation":
                    if(args[3].toLowerCase(Locale.ROOT).equals("add") || args[3].toLowerCase(Locale.ROOT).equals("remove"))
                    {parsetaNationRemoveOrAddCoreOrVassal(commandSender,args[1],args[2],args[3],args[4]);}

            }
        }
        return false;
    }

    private void parsetaNationRemoveOrAddCoreOrVassal(CommandSender commandSender, String arg, String arg1, String arg2, String arg3) {
        Nation nation = TownyUniverse.getInstance().getNation(arg);
        if(nation == null) {return;}
        switch (arg1.toLowerCase(Locale.ROOT)) {
            case "core":
                Town town = TownyUniverse.getInstance().getTown(arg3);
                if(town == null) {TownyMessaging.sendMsg(commandSender, "没有这個城镇");return;}
                switch (arg2.toLowerCase(Locale.ROOT)) {
                    case "add":
                        Main.addNewNationCore(nation, town);
                        return;
                    case "remove":
                        if(!Main.hasNationCore(nation, town)) {TownyMessaging.sendMsg(commandSender, "你所指定的并非該郭嘉的核心城镇");return;}
                        Main.removeNationCore(nation, town);
                        return;
                }
            case "vassal":
                Nation state = TownyUniverse.getInstance().getNation(arg3);
                if(state == null) {TownyMessaging.sendMsg(commandSender, "没有这個郭嘉");return;}
                switch (arg2.toLowerCase(Locale.ROOT)) {
                    case "add":
                        if(nation.getUUID() == state.getUUID()) {TownyMessaging.sendMsg(commandSender, "郭嘉不可能为自己的附庸");return;}
                        Main.addNewNationVassal(nation, state);
                        return;
                    case "remove":
                        if(nation.getUUID() == state.getUUID()) {TownyMessaging.sendMsg(commandSender, "郭嘉不可能为自己的附庸");}
                        if(!Main.hasNationVassal(nation, state)) {TownyMessaging.sendMsg(commandSender, "你所指定的并非該郭嘉的一個附庸");return;}
                        Main.removeNationVassal(nation, state);
                        return;
                }
        }
    }

    private void parsetaTownRemoveOrAddScienceOrCulture(CommandSender commandSender, String arg, String arg1, String arg2, String arg3) {
        Town town = TownyUniverse.getInstance().getTown(arg);
        if(town == null) {return;}
        System.out.println("这下不该没有(1)……");
        switch (arg1.toLowerCase(Locale.ROOT)) {
            case "cu":
                System.out.println("这下不该没有(2)……");
                Culture cu = Main.getCulture(arg3);
                System.out.println("这下不该没有(3)……");
                if(cu == null) {
                    TownyMessaging.sendMsg(commandSender, "未发现該项市政");return;}
                System.out.println("……");
                switch (arg2.toLowerCase(Locale.ROOT)) {
                    case "add":
                        if(Main.hasTownCulture(town, cu)) {TownyMessaging.sendMsg(commandSender, "你不能增加一個已有的市政");return;}
                        if(MetaDataUtil.getTownCulture(town) != null
                                && Utils.transferStringAToArray(MetaDataUtil.getTownCulture(town))[0].equals(cu.getID()))
                        {MetaDataUtil.setTownCulture(town, MetaDataUtil.getTownCulture(town).replace(cu.getID(), "n"));}

                        Main.addNewTownCulture(town, cu);return;
                    case "remove":
                        System.out.println("到这了？");
                        if(!Main.hasTownCulture(town, cu)) {TownyMessaging.sendMsg(commandSender, "你所指定的城镇尚未解锁此市政");}
                        Main.removeInvalidTownCulture(town, cu);return;
                }
            case "sci":
                Science science = Main.getScience(arg3);
                if(science == null) {TownyMessaging.sendMsg(commandSender, "未发现这项科学");return;}
                switch (arg2.toLowerCase(Locale.ROOT)) {
                    case "add":
                        if(Main.hasTownScience(town, science)) {TownyMessaging.sendMsg(commandSender, "你不能增加一個已有的科学研究");return;}
                        if(MetaDataUtil.getTownScience(town) != null
                                && Utils.transferStringAToArray(MetaDataUtil.getTownScience(town))[0].equals(science.getID()))
                        {MetaDataUtil.setTownScience(town, MetaDataUtil.getTownScience(town).replace(science.getID(), "n"));}
                        Main.addNewTownScience(town, science);return;
                    case "remove":
                        if(!Main.hasTownScience(town, science)) {TownyMessaging.sendMsg(commandSender, "你所指定的城镇尚未解锁此科学研究");return;}
                        Main.removeInvalidTownScience(town, science);
                }
        }

    }

    private void parsetaPlotSetValueofBuilding(CommandSender commandSender, String arg) {
        try {
            Player player = catchConsole(commandSender);
            if (Integer.parseInt(arg) > 0) {
                TownBlock townBlock = TownyUniverse.getInstance().getTownBlockOrNull(WorldCoord.parseWorldCoord(player.getLocation()));
                //confirm if the block is a wildness block
                if(townBlock == null || !townBlock.hasTown()) {
                    TownyMessaging.sendMsg(player, "你并不处在一個城镇区块上");return;
                }
                if(!acceptedType.contains(Objects.requireNonNull(townBlock).getTypeName().toLowerCase())) {TownyMessaging.sendMsg(player, "区块类型必须为[教堂][役所][要塞][农田][工坊][社区][艺术][科学]其中之一");return;}
                Main.setTownBlockValueofBuilding(townBlock, Integer.parseInt(arg));
            } else {TownyMessaging.sendMsg(player, "你赋予的等级必须是一個数字，如1,2,3");return;
            }
        } catch (TownyException e) {TownyMessaging.sendErrorMsg(commandSender, e.getMessage(commandSender));}
        return;
    }

    private void parsetaPlotSetValue(CommandSender commandSender, String arg) {
        try {
            Player player = catchConsole(commandSender);
            if (Integer.parseInt(arg) > 0) {
                TownBlock townBlock = TownyUniverse.getInstance().getTownBlockOrNull(WorldCoord.parseWorldCoord(player.getLocation()));
                //confirm if the block is a wildness block
                if(townBlock == null || !townBlock.hasTown()) {
                    TownyMessaging.sendMsg(player, "你并不处在一個城镇区块上");return;
                }
                if(!acceptedType.contains(Objects.requireNonNull(townBlock).getTypeName())) {TownyMessaging.sendMsg(player, "区块类型必须为[教堂][役所][要塞][农田][工坊][社区][艺术][科学]其中之一");return;}
                Main.setTownBlockValue(townBlock, Integer.parseInt(arg));
            } else {TownyMessaging.sendMsg(player, "你赋予的等级必须是一個数字，如1,2,3");return;
            }
        } catch (TownyException e) {TownyMessaging.sendErrorMsg(commandSender, e.getMessage(commandSender));}
        return;
    }
}
