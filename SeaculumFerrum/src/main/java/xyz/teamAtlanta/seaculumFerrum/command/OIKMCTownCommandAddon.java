package xyz.teamAtlanta.seaculumFerrum.command;

import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.event.HoverEvent;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.statusscreens.StatusScreen;
import com.palmergames.bukkit.util.ChatTools;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.teamAtlanta.seaculumFerrum.Culture;
import xyz.teamAtlanta.seaculumFerrum.Main;
import xyz.teamAtlanta.seaculumFerrum.Science;
import xyz.teamAtlanta.seaculumFerrum.Utils;
import xyz.teamAtlanta.seaculumFerrum.meta.MetaDataUtil;

import java.text.DecimalFormat;
import java.util.*;

import static com.palmergames.bukkit.towny.TownyMessaging.sendStatusScreen;

public class OIKMCTownCommandAddon extends BaseCommand implements TabExecutor {
    public OIKMCTownCommandAddon() {
        AddonCommand nationSetSiegeWarCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.TOWN, "oik", this);
        TownyCommandAddonAPI.addSubCommand(nationSetSiegeWarCommand);
    }

    private static final List<String> primarySci = Arrays.asList("UN01");
    private static final List<String> primaryCu = Arrays.asList("UN01");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1)
            return Arrays.asList("sci","cu","gathertax","recruit","dissolve");
        else if(args.length == 2) {
            if(args[0].toLowerCase(Locale.ROOT).equals("sci") || args[0].toLowerCase(Locale.ROOT).equals("cu")) {
                return Arrays.asList("dev","info");
            }
            else if(args[0].toLowerCase(Locale.ROOT).equals("gathertax")) {
                return Arrays.asList("standard");
            }
        }
        else if(args.length == 3) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "cu":
                    switch (args[1].toLowerCase(Locale.ROOT)) {
                        case "dev":
                            return Main.outputCulturesList();
                        case "info":
                            return getTownyStartingWith(args[2], "t");
                    }

                case "sci":
                    switch (args[1].toLowerCase(Locale.ROOT)) {
                        case "dev":
                            return Main.outputSciencesList();
                        case "info":
                            return getTownyStartingWith(args[2], "t");
                    }

            }


        }
        return Collections.emptyList();

    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("sf.town")) {commandSender.sendMessage("你没有如此做的权限！");return true;}
        if (args.length == 1) {
            if(args[0].toLowerCase(Locale.ROOT).equals("recruit")) {
                try {
                    Player player = catchConsole(commandSender);
                    Town town = getTownFromPlayerOrThrow(player);
                    String[] re = Utils.transferStringAToArray(MetaDataUtil.getRecruits(town));
                    if(re.length >= Main.getRecruitLimit(town)) {
                        commandSender.sendMessage("§c你招募的征召兵数量已抵达上限！");
                        return true;
                    }
                    Main.Recruit(town, commandSender);
                    commandSender.sendMessage("§a已募集新的征召兵！");

                } catch (TownyException e) {throw new RuntimeException(e);}

            } else if(args[0].toLowerCase(Locale.ROOT).equals("dissolve")) {
                try {
                    System.out.println("test-command");
                    Player player = catchConsole(commandSender);
                    Town town = getTownFromPlayerOrThrow(player);

                    Main.disRecruit(town, commandSender);
                    commandSender.sendMessage("§b已解散全部征召兵！");

                } catch (TownyException e) {throw new RuntimeException(e);}

            }

        }
        else if(args.length == 2) {
            if(args[0].toLowerCase(Locale.ROOT).equals("sci") || args[0].toLowerCase(Locale.ROOT).equals("cu")) {
                if(args[1].toLowerCase(Locale.ROOT).equals("info")) {
                    try {
                        Player player = catchConsole(commandSender);
                        Town town = getTownFromPlayerOrThrow(player);
                        switch (args[0].toLowerCase()) {
                            case "sci":
                                parsePrintAcademy(commandSender, town);return true;
                            case "cu":
                                parsePrintHall(commandSender, town);return true;
                        }
                    }  catch (TownyException e) {throw new RuntimeException(e);}
                }
            }
            else if(args[0].toLowerCase(Locale.ROOT).equals("gathertax")) {
                try {
                    Player player = catchConsole(commandSender);
                    Town town = getTownFromPlayerOrThrow(player);
                    switch (args[1].toLowerCase()) {
                        case "standard":
                            parseGatherTaxofReligion(commandSender, town);
                    }
                }  catch (TownyException e) {throw new RuntimeException(e);}

            }
        }
        else if(args.length == 3) {
            System.out.println("soudesune");
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "cu":
                    System.out.println("soudesune2");
                    switch (args[1].toLowerCase(Locale.ROOT)) {
                        case "dev":
                            try {
                                System.out.println("soudesune3");
                                Player player = catchConsole(commandSender);
                                Town town = getTownFromPlayerOrThrow(player);
                                String[] cus = Utils.transferStringAToArray(MetaDataUtil.getTownCulture(town));
                                System.out.println("A1");
                                /*if(Main.getCulture(cus[cus.length-1]) == null) {
                                    System.out.println(cus);
                                    System.out.println("？？？");
                                    System.out.println("哎你不要闗注了，窝看妹什麽问题…");

                                }*/
                                Culture cu = Main.getCulture(args[2]);
                                if(cu == null) {TownyMessaging.sendMsg(commandSender, "未发现这项市政");return true;}
                                System.out.println("A2");
                                boolean allowed = false;
                                if(cu.getPreRequisiteCultures().size() == 0) {allowed = true;}
                                List<String> preName = new ArrayList<>();
                                for(Culture pre : cu.getPreRequisiteCultures()) {
                                    preName.add(pre.getID());
                                    if(Main.hasTownCulture(town, pre.getID())) {
                                        allowed = true;
                                    }
                                }
                                System.out.println("A3");
                                if(!allowed) {TownyMessaging.sendMsg(commandSender, "你所要研究的这项市政不满足其必要的前置条件，必须满足至少以下任意一项：" + String.join(",", preName));return true;}

                                boolean allowedAnd = true;
                                List<String> andPreName = new ArrayList<>();
                                for(Culture pre : cu.getAndPreRequisiteCultures()) {
                                    andPreName.add(pre.getID());
                                    if(!Main.hasTownCulture(town, pre.getID())) {
                                        allowedAnd = false;
                                    }
                                }
                                System.out.println("A4");
                                if(!allowedAnd) {TownyMessaging.sendMsg(commandSender, "你所要研究的这项市政不满足其必要的前置条件，以下所有必须全部拥有：" + String.join(",", andPreName));return true;}

                                if(cus.length == 0 || cus[0].toLowerCase(Locale.ROOT).equals("null") || cus[0].equals("n")) {
                                    Main.addFutureTownCulture(town, cu, false);return true;
                                }
                                System.out.println("A5");

                                TownyMessaging.sendMsg(player, "重新开始这项鈣鎘将放弃你的议事厅目前所有正在促进的市政进展：(" + cus[0] + "),你确定嗎？");

                                Confirmation
                                        .runOnAccept(()-> {
                                            Main.addFutureTownCulture(town, cu, true);
                                            Main.setTownCulturePoints(town, 0);
                                        })
                                        .runOnCancel(()-> TownyMessaging.sendMsg(player, "已转向新的市政鈣鎘！"))
                                        .sendTo(player);
                                System.out.println("A6");
                                return true;

                            } catch (TownyException e) {throw new RuntimeException(e);}
                        case "info":
                            try {
                                parsePrintHall(commandSender, getTownOrThrow(args[2]));return true;
                            } catch (TownyException e) {throw new RuntimeException(e);}

                    }

                case "sci":
                    switch (args[1].toLowerCase(Locale.ROOT)) {
                        case "dev":
                            try {
                                Player player = catchConsole(commandSender);
                                Town town = getTownFromPlayerOrThrow(player);
                                String[] scis = Utils.transferStringAToArray(MetaDataUtil.getTownScience(town));
                                Science sci = Main.getScience(args[2]);
                                if(sci == null) {TownyMessaging.sendMsg(commandSender, "未发现这项科学");return true;}

                                boolean allowed = false;
                                if(sci.getPreRequisiteSciences().size() == 0) {allowed = true;}
                                List<String> preName = new ArrayList<>();
                                for(Science pre : sci.getPreRequisiteSciences()) {
                                    preName.add(pre.getID());
                                    if(Main.hasTownScience(town, pre.getID())) {
                                        allowed = true;
                                    }
                                }
                                if(!allowed) {TownyMessaging.sendMsg(commandSender, "你所要研究的这项科学不满足其必要的前置条件，必须满足至少以下任意一项：" + String.join(",", preName));return true;}

                                boolean allowedAnd = true;
                                List<String> andPreName = new ArrayList<>();
                                for(Science pre : sci.getAndPreRequisiteSciences()) {
                                    andPreName.add(pre.getID());
                                    if(!Main.hasTownScience(town, pre.getID())) {
                                        allowedAnd = false;
                                    }
                                }
                                if(!allowedAnd) {TownyMessaging.sendMsg(commandSender, "你所要研究的这项科学不满足其必要的前置条件，以下所有必须全部拥有：" + String.join(",", andPreName));return true;}


                                if(scis.length == 0 || scis[0].toLowerCase(Locale.ROOT).equals("null") || scis[0].equals("n")) {
                                    Main.addFutureTownScience(town, sci, false);return true;
                                }

                                TownyMessaging.sendMsg(player, "重新开始这项研究将放弃你的学院目前所有正在钻研的科学进展：" + scis[0] + ",你确定嗎？");

                                Confirmation
                                        .runOnAccept(()-> {
                                            Main.addFutureTownScience(town, sci, true);
                                            Main.setTownSciencePoints(town, 0);
                                        })
                                        .runOnCancel(()-> TownyMessaging.sendMsg(player, "已转向新的科学研究！"))
                                        .sendTo(player);
                                return true;
                            } catch (TownyException e) {throw new RuntimeException(e);}
                        case "info":
                            try {
                                parsePrintAcademy(commandSender, getTownOrThrow(args[2]));return true;
                            } catch (TownyException e) {throw new RuntimeException(e);}


                    }


            }


        }
        return true;
    }

    private void parseGatherTaxofReligion(CommandSender commandSender, Town town) {
        //4級市政
        if(!Main.hasTownCulture(town, "UN04")) {commandSender.sendMessage("§c你的城镇需要解锁市政 §f04§b 铸币税 §c来收取");return;}
        if(MetaDataUtil.getTaxed(town)) {
            commandSender.sendMessage("§c已经从該城镇收取了铸币税，你不能无限制地攫取民间财富，请至少等待至：下一個towny日");return;

        }
        int value = 10;
        if(Main.hasTownCulture(town, "UN09")) {value = (int)(0.5 * value);}
        if(Main.getTownCulturesPoints(town) < 10) {
            commandSender.sendMessage("§c你需要至少10点文化点数来收取铸币税");return;

        }
        Main.setTownCulturePoints(town, Main.getTownCulturesPoints(town) - 10);
        double amount = getTownChurchTax(town);
        town.getAccount().deposit(amount, "religion_tax");
        commandSender.sendMessage("§a成功收取价值" + amount + "ducas的铸币税！");
        MetaDataUtil.setTaxed(town, true);


    }

    //抄行政容量计算的
    public static double getTownChurchTax(Town town) {
        double minus = 0.0;
        int mod = 0;
        for(TownBlock townBlock : town.getTownBlocks()) {
            switch (townBlock.getType().getName().toLowerCase(Locale.ROOT)) {
                case "church":
                    mod=10;break;
                case "farm":
                    mod=1;break;
                case "barrack":
                    mod=2;break;
                case "stable":
                    mod=2;break;
                case "sci":
                    mod=5;break;
                case "art":
                    mod=5;break;



            }
            minus += mod * 0.01 * Main.getTownBlockValue(townBlock);
            minus += mod * 0.01 * Main.getTownBlockValueofBuilding(townBlock);

        }
        return minus;
    }

    private void parsePrintHall(CommandSender commandSender, Town town) {
        StatusScreen statusScreen = new StatusScreen(commandSender);
        statusScreen.addComponentOf("town_cu_title", ChatTools.formatTitle("§5" + town.getName() + "市政厅"));
        statusScreen.addComponentOf("new1", Component.newline());
        System.out.println("1");
        String[] cus = Utils.transferStringAToArray(MetaDataUtil.getTownCulture(town));
        //get human-readable names of all sciences the town have
        List<String> cuNames = new ArrayList<>();
        List<String> explanations = new ArrayList<>();
        System.out.println("2");
        System.out.println("是这個出了问题嗎？？？");
        System.out.println(cus.length);
        for(String s : cus) {
            if(Main.hasCulture(s)) {cuNames.add("[" + Objects.requireNonNull(Main.getCulture(s)).getFormattedName() + "]");
            }
        }
        statusScreen.addComponentOf("cus", "§5[已解锁市政]§f[" + String.valueOf(Math.max(0, cus.length - 1))+ "/10]", HoverEvent.showText(Component.text(String.join(",", cuNames))));
        System.out.println("3");
        System.out.println("是这個出了问题嗎？？？");
        System.out.println(cus.length);
        System.out.println("真的嗎？");
        //intros to guide to new sci development
        if(cus.length == 0 || cus[0].equals("n") || cus[0].toLowerCase(Locale.ROOT).equals("null")) {statusScreen.addComponentOf("town_none_current_cu", "§a选择一项新的市政来进行鈣鎘》");
            System.out.println("是这個出了问题嗎×2？？？");
            System.out.println(cus.length);
            System.out.println("真的嗎？");
            if(cus.length > 1 && cus.length < 12) {
                for(Culture post : Main.getCulture(cus[cus.length-1]).getPostPositionCultures()) {
                    statusScreen.addComponentOf("new_cu_" + post.getID(), "§e[" + post.getFormattedName() + "]",
                            HoverEvent.showText(Component.text("基础花费：" + post.getCost()
                                    + "\n" + post.getIntro())));
                }
            }
            else {
                for(String pri : primaryCu) {
                    Culture pric = Main.getCulture(pri);
                    statusScreen.addComponentOf("new_cu_" + pri, "§e[" + pric.getFormattedName() + "]",
                            HoverEvent.showText(Component.text("基础花费：" + pric.getCost()
                                    + "\n" + pric.getIntro())));
                }
            }

            System.out.println("4");
        }


        else  {
            int cost = getActualCost(town, cus, explanations);
            System.out.println(Main.getTownCulturesPoints(town));
            statusScreen.addComponentOf("town_cu_cost", "§5当前市政项目：" + Main.getCulture(cus[0]).getFormattedName() + ", " + Main.getTownCulturesPoints(town) + "/" + cost,
                    HoverEvent.showText(Component.text(String.join("\n", explanations))));

        }
        statusScreen.addComponentOf("new2", Component.newline());
        if (cus.length < 2) {
            statusScreen.addComponentOf("town_cu_intro", "(§7你的城镇当前尚无已解锁市政，建立文化与艺术设施地块(/plot set art)积攒文化点数以发展新市政！)");
            System.out.println("5");
        }
        else {
            Culture currentCu = Main.getCulture(cus[cus.length-1]);
            if(currentCu != null) {}
            statusScreen.addComponentOf("town_cu_intro", "§5" + Objects.requireNonNull(currentCu).getIntro());

            System.out.println("6");

        }
        statusScreen.addComponentOf("new3", Component.newline());
        System.out.println("anyway…");
        if (cus.length < 2) { System.out.println("7");
        }

        else {
            System.out.println("窝柑橘这其实不太可能有问题……");

            Culture currentCu = Main.getCulture(cus[cus.length-1]);
            System.out.println("i");
            if(currentCu == null) {}
            System.out.println("ii");
            statusScreen.addComponentOf("town_cu_motto", Objects.requireNonNull(currentCu).getMotto());

            System.out.println("8");

        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> sendStatusScreen(commandSender, statusScreen));return;

    }

    private void parsePrintAcademy(CommandSender commandSender, Town town) {
        StatusScreen statusScreen = new StatusScreen(commandSender);
        statusScreen.addComponentOf("town_sci_title", ChatTools.formatTitle("§b" + town.getName() + "学院"));
        statusScreen.addComponentOf("new1", Component.newline());
        System.out.println("1");
        String[] scis = Utils.transferStringAToArray(MetaDataUtil.getTownScience(town));
        //get human-readable names of all sciences the town have
        List<String> sciNames = new ArrayList<>();
        List<String> explanations = new ArrayList<>();
        System.out.println("2");
        System.out.println("是这個出了问题嗎？？？");
        System.out.println(scis.length);
        for(String s : scis) {
            if(Main.hasScience(s)) {sciNames.add("[" + Objects.requireNonNull(Main.getScience(s)).getFormattedName() + "]");
            }
          }
        statusScreen.addComponentOf("scis", "§b[已解锁科技]§f[" + String.valueOf(Math.max(0, scis.length - 1))+ "/10]", HoverEvent.showText(Component.text(String.join(",", sciNames))));
        System.out.println("3");
        System.out.println("是这個出了问题嗎？？？");
        System.out.println(scis.length);
        System.out.println("真的嗎？");
        //intros to guide to new sci development
        if(scis.length == 0 || scis[0].equals("n") || scis[0].toLowerCase(Locale.ROOT).equals("null")) {statusScreen.addComponentOf("town_none_current_sci", "§a选择一项新的科学来进行研究》");
            System.out.println("是这個出了问题嗎×2？？？");
            System.out.println(scis.length);
            System.out.println("真的嗎？");
            if(scis.length > 2 && scis.length < 12) {
                for(Science post : Main.getScience(scis[scis.length-1]).getPostPositionSciences()) {
                    statusScreen.addComponentOf("new_sci_" + post.getID(), "[§e" + post.getFormattedName() + "]",
                            HoverEvent.showText(Component.text("基础花费：" + post.getCost()
                                    + "\n" + post.getIntro())));
                }
            }
            else {
                for(String pri : primarySci) {
                    Science prisc = Main.getScience(pri);
                    statusScreen.addComponentOf("new_sci_" + pri, "[§e" + prisc.getFormattedName() + "]",
                            HoverEvent.showText(Component.text("基础花费：" + prisc.getCost()
                                    + "\n" + prisc.getIntro())));
                }
            }

            System.out.println("4");
        }


        else  {
            int cost = 1000;
            //int cost = Main.getActualCost(town, scis, explanations);
            statusScreen.addComponentOf("town_sci_cost", "§b当前科研项目：" + Main.getCulture(scis[0]).getFormattedName() + "," + Main.getTownSciencePoints(town) + "/" + cost,
                    HoverEvent.showText(Component.text(String.join("\n", explanations))));

        }
        statusScreen.addComponentOf("new2", Component.newline());
        if(scis.length < 2 ){
                //|| (scis.length == 1 && scis[0].equals("n")) || (scis.length == 1 && scis[0].toLowerCase(Locale.ROOT).equals("null"))
            statusScreen.addComponentOf("town_sci_intro", "(§7你的城镇当前尚无已解锁科学，建立科学设施地块(/plot set sci)积攒科学点数以研发新科学！)");
            System.out.println("5");
        }
        else {
            Science currentSci = Main.getScience(scis[scis.length-1]);
            if(currentSci != null) {}
            statusScreen.addComponentOf("town_sci_intro", "§b" + Objects.requireNonNull(currentSci).getIntro());

            System.out.println("6");

        }
        statusScreen.addComponentOf("new3", Component.newline());
        System.out.println("anyway…");
        if(scis.length < 2) {

                //|| (scis.length == 1 && scis[0].equals("n")) || (scis.length == 1 && scis[0].toLowerCase(Locale.ROOT).equals("null"))
            System.out.println("7");
        }

        else {
            System.out.println("窝柑橘这其实不太可能有问题……");
            System.out.println(scis.length-1);
            System.out.println(scis[scis.length-1]);
            Science currentSci = Main.getScience(scis[scis.length-1]);
            System.out.println("i");
            if(currentSci == null) {}
            System.out.println("ii");
            statusScreen.addComponentOf("town_sci_motto", Objects.requireNonNull(currentSci).getMotto());

            System.out.println("8");

        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> sendStatusScreen(commandSender, statusScreen));return;

    }

    public static int getActualCost(Town town, String[] scis, List<String> explanations) {
        Science science = Main.getScience(scis[0]);
        int ocost = science.getCost();
        int discount = 0;
        explanations.add("基础花费：" + ocost);
        String series = science.getID().substring(0,2);
        for(int i = scis.length-1; i > 0; i--) {
            if(scis[i].substring(0,2).equalsIgnoreCase(series)) {
                System.out.println(scis[i].substring(0,2));
                discount ++;
                series = scis[i].substring(0,2);
            } else {break;}
        }
        double mod = 1.0 - 0.05 * discount;
        explanations.add("连续发展了" + discount + "個同系列研究：-"
                + String.valueOf(new DecimalFormat("0.0").format(0.05 * discount * 100)) + "%");
        //if(Main.hasTownCulture(town, "UN10")) {
        //	mod -= 0.10;
        //	explanations.add("[]市政提供：-10.0%");
        //}

        return (int) mod * ocost;
    }

    public static int getActualCost(Town town, String[] scis) {
        Science science = Main.getScience(scis[0]);
        int ocost = science.getCost();
        int discount = 0;
        String series = science.getID().substring(0,2);
        for(int i = scis.length-1; i > 0; i--) {
            if(scis[i].substring(0,2).equalsIgnoreCase(series)) {
                System.out.println(scis[i].substring(0,2));
                discount ++;
                series = scis[i].substring(0,2);
            } else {break;}
        }
        double mod = 1.0 - 0.05 * discount;
        //if(Main.hasTownCulture(town, "UN10")) {
        //	mod -= 0.10;
        //	explanations.add("[]市政提供：-10.0%");
        //}

        return (int) mod * ocost;
    }
}
