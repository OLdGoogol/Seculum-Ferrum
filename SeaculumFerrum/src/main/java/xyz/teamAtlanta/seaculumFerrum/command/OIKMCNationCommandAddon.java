package xyz.teamAtlanta.seaculumFerrum.command;

import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.event.HoverEvent;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.object.statusscreens.StatusScreen;
import com.palmergames.bukkit.util.ChatTools;
import io.github.townyadvanced.eventwar.instance.War;
import io.github.townyadvanced.eventwar.objects.DeclarationOfWar;
import io.github.townyadvanced.eventwar.objects.WarTypeEnum;
import io.github.townyadvanced.eventwar.settings.EventWarSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.teamAtlanta.seaculumFerrum.Main;
import xyz.teamAtlanta.seaculumFerrum.Territory;
import xyz.teamAtlanta.seaculumFerrum.Utils;
import xyz.teamAtlanta.seaculumFerrum.meta.MetaDataUtil;

import java.util.*;

import static com.palmergames.bukkit.towny.TownyMessaging.sendStatusScreen;

public class OIKMCNationCommandAddon extends BaseCommand implements TabExecutor{

    public OIKMCNationCommandAddon() {
        AddonCommand nationSetSiegeWarCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.NATION, "oik", this);
        TownyCommandAddonAPI.addSubCommand(nationSetSiegeWarCommand);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1)
            return Arrays.asList("subject","form","gather","claim","dispatch");

        else if(args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "subject":
                    return getTownyStartingWith(args[1], "n");
                case "form":
                    return Arrays.asList("assyria","babylonia","persia","graecia","rome","western_romania","east_romania");
                case "gather":
                    return Arrays.asList("feudaltax");
                case "claim":
                    return Main.outputTerritoriesList();
                case "dispatch":
                    return Arrays.asList("demarchus","strategos");
            }

        }
        //还是搞成更正式的把
        else if(args.length == 3) {
            switch (args[1].toLowerCase()) {
                case "feudaltax":
                case "strategos":
                    try {
                        System.out.println("+1");
                        Player player = catchConsole(sender);
                        Nation nation = getNationFromPlayerOrThrow(player);

                        List<String> s = new ArrayList<>();

                        for(Nation v : Main.getNationVassals(nation)) {
                            s.add(v.getName());
                            return s;
                        }
                    } catch (TownyException e) {throw new RuntimeException(e);}

                    break;
                case "demarchus":
                    try {
                        System.out.println("+1");
                        Player player = catchConsole(sender);
                        Nation nation = getNationFromPlayerOrThrow(player);

                        List<String> s = new ArrayList<>();

                        for(Town v : nation.getTowns()) {
                            s.add(v.getName());
                            return s;
                        }
                    } catch (TownyException e) {throw new RuntimeException(e);}

                    break;





            }

        }

        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("sf.nation")) {commandSender.sendMessage("你没有如此做的权限！");return true;}
        if (args.length == 1) {
            if(args[0].toLowerCase().equals("form")) {
                try {
                    System.out.println("+1");
                    Player player = catchConsole(commandSender);
                    Nation nation = getNationFromPlayerOrThrow(player);
                    parsePrintNationForm(nation, commandSender);
                } catch (TownyException e) {throw new RuntimeException(e);}
            }
        }
        else if(args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "subject":
                    try {
                        Player player = catchConsole(commandSender);
                        Nation nation = getNationFromPlayerOrThrow(player);
                        Nation suz = TownyUniverse.getInstance().getNation(args[1]);
                        if(suz == null) {TownyMessaging.sendMsg(player, "没有这個郭嘉");return true;
                        }
                        if(nation.getUUID() == suz.getUUID()) {TownyMessaging.sendMsg(player, "你不能成为自己郭嘉的附庸！()");return true;}
                        if(!(Main.getNationTitle(nation) < Main.getNationTitle(suz))) {TownyMessaging.sendMsg(player, "窝們的郭嘉的头衔等级" + Main.getNationTitle(nation) + "并不比他們更低，向他們称臣有损國格！");return true;}
                        for(Nation nations : TownyUniverse.getInstance().getNations()) {
                            if(Main.hasNationVassal(nations,nation)) {
                                if(nations.getUUID() == suz.getUUID()) {TownyMessaging.sendMsg(player, "你已经成为了该國一位荣耀的封臣！");}
                                TownyMessaging.sendMsg(player, "你已经拥有了一個宗主：" + nations.getName() + ",勿事二主，勿当三姓家奴");
                                return true;
                            };
                        }
                        if(MetaDataUtil.getNationFreeSubject(suz)) {
                            Main.addNewNationVassal(suz,nation);return true;

                        }
                        if(Main.getNationFixedTitle(suz) == 5) {
                            switch (Main.getNationTitle(nation)) {
                                case 3:
                                    Main.setNationFixedTitle(nation, 3);
                                    break;
                                case 4:
                                    Main.setNationFixedTitle(nation, 4);
                            }
                        }
                            TownyMessaging.sendMsg(player, "対方尚未开放宣誓效忠，请向該郭嘉的头衔所有者提交你的申请");
                        //this is where I probably make it opposite down.
                        //I will not let something like a [] stain anything that has possible sacredness.

                        return true;



                    } catch (TownyException e) {throw new RuntimeException(e);}

                case "form":
                parseNationFormEmpires(commandSender, args[1]);return true;

                case "claim":
                    try {
                        Player player = catchConsole(commandSender);
                        Nation nation = getNationFromPlayerOrThrow(player);
                        Territory t = Main.getTerritory(args[1]);
                        if(t == null) {TownyMessaging.sendMsg(player, "没有这個领地");return true;
                        }
                        if(t.getClaimed().contains(nation) || t.getStrongClaimed().contains(nation)) {
                            TownyMessaging.sendMsg(player, "你已经拥有了该地区的宣称");return true;
                        }
                        System.out.println("到这一步了嗎");
                        boolean has = false;
                        for(Territory n : t.getNearby()) {
                            System.out.println("检查出一个nearby" + n.getID());
                            if(n.getClaimed().contains(nation) || n.getStrongClaimed().contains(nation)) {
                                has=true;
                            }

                        }
                        if(!has) {TownyMessaging.sendMsg(player, "你必须宣称一个已有宣称地区的相邻地区");
                            return true;}
                        int value = t.getValue();
                        //9級市政
                        if(Main.hasTownCulture(nation.getCapital(), "UN09")) {value = (int)(0.5 * value);}
                        if(Main.getTownCulturesPoints(nation.getCapital()) < t.getValue()) {
                            TownyMessaging.sendMsg(player, "你缺少宣称该地区所需的" + t.getValue() + "文化点数");
                            return true;
                        }
                        System.out.println("不会已经到这了吧");
                        t.addClaim(nation, false);
                        //this is where I probably make it opposite down.
                        //I will not let something like a [] stain anything that has possible sacredness.
                        t.save();

                        return true;



                    } catch (TownyException e) {throw new RuntimeException(e);}
            }
        } else if(args.length == 4) {
            switch (args[0].toLowerCase()) {
                case "gather":
                    if(args[1].toLowerCase().equals("feudaltax")) {
                        try {
                            Player player = catchConsole(commandSender);
                            Nation nation = getNationFromPlayerOrThrow(player);
                            Nation state = TownyUniverse.getInstance().getNation(args[2]);
                            parseGatherFeudalTax(nation, state, commandSender);} catch (TownyException e) {
                            throw new RuntimeException(e);
                        }
                    }
                case "dispatch":
                    try {
                        Player player = catchConsole(commandSender);
                        Nation nation = getNationFromPlayerOrThrow(player);
                        if(
                            //!GivenNames.contains(event.getNation().getName().toLowerCase(Locale.ROOT))
                                !(Main.getNationFixedTitle(nation) == 5)
                        ) {
                            TownyMessaging.sendMsg(player, "你的国家制度并非 帝国制 ，无法更换城主或军区将军");
                        }
                            switch (args[1].toLowerCase()) {
                            case "demarchus":
                                Town town = TownyUniverse.getInstance().getTown(args[2]);
                                Resident resident = TownyUniverse.getInstance().getResident(args[3]);
                                if (town == null || !town.hasNation() || !(town.getNation() == nation)) {
                                    TownyMessaging.sendMsg(player, "你的国家没有这个城镇");

                                    return true;
                                }
                                if(!Main.hasNationCore(nation, town)) {TownyMessaging.sendMsg(player, "你只能任命你的核心城镇的城主");
                                return true;}
                                town.setMayor(resident);
                                TownyMessaging.sendMsg(player, "成功任命新城主");

                            case "strategos":
                                Nation nation1 = TownyUniverse.getInstance().getNation(args[2]);
                                Resident resident1 = TownyUniverse.getInstance().getResident(args[3]);
                                if (nation1 == null || !Main.getNationVassals(nation).contains(nation1)) {
                                    TownyMessaging.sendMsg(player, "你的国家没有这个军区");

                                    return true;
                                }
                                if(
                                    //!GivenNames.contains(event.getNation().getName().toLowerCase(Locale.ROOT))
                                        !(Main.getNationFixedTitle(nation1) == 3) && !(Main.getNationFixedTitle(nation1) == 4)
                                ) {
                                    TownyMessaging.sendMsg(player, "该国并非你的国家的一个总督辖地");
                                }
                                nation1.setKing(resident1);
                                TownyMessaging.sendMsg(player, "成功任命新将军");
                        }



                    } catch (TownyException e) {throw new RuntimeException(e);}

            }
        }/*
         //i am tired of do something about war...
         else if(args.length == 4) {
            switch (args[0].toLowerCase()) {
                case "declare":
                    switch (args[1].toLowerCase()) {
                        case "war":
                            try {
                                Player player = catchConsole(commandSender);
                                Nation nation = getNationFromPlayerOrThrow(player);
                                Nation state = TownyUniverse.getInstance().getNation(args[2]);
                                if(state == null) {return true;}
                                String type = args[3];
                                boolean allow = false;
                                List<Town> target = new ArrayList<>();
                                switch (type) {
                                    case "reclaim":
                                        String[] claims = Utils.transferStringAToArray(MetaDataUtil.getNationCores(nation));
                                        for(String claim : claims) {
                                            if(TownyUniverse.getInstance().getTown(claim).hasNation() && TownyUniverse.getInstance().getTown(claim).getNationOrNull().equals(nation)) {
                                                target.add(TownyUniverse.getInstance().getTown(claim));

                                            }
                                        }
                                        if(!target.isEmpty()) {
                                            List<Nation> nations = new ArrayList<>();
                                            List<Resident> residents = new ArrayList<>();
                                            //nation joins
                                            nations.add(nation);
                                            residents.addAll(nation.getResidents());
                                            //state joins
                                            nations.add(state);
                                            residents.addAll(state.getResidents());

                                            TownyMessaging.sendMsg(commandSender, Translatable.of("msg_beginning_x_war_in_x_seconds", new Object[]{WarTypeEnum.NATIONWAR.getName(), EventWarSettings.nationWarDelay()}));
                                            War war = new War(nations, (List)null, residents, WarTypeEnum.NATIONWAR.getType(), (DeclarationOfWar)null);

                                            war.setWarName(nation + "vs" + state.getName() + "的再征服战争");


                                        }
                                        //to begin a conquest war, you must try to obtain a War Reason.
                                    case "conquest":

                                        ItemStack itemStack = player.getInventory().getItemInMainHand();
                                        if(itemStack.getType() == Material.AIR)
                                        {return true;}

                                        //itemStack.getItemMeta().setCustomModelData(1066);
                                        if(!(itemStack.getItemMeta().getCustomModelData() == 1066)) {return true;}
                                        List<String> lore = itemStack.getItemMeta().getLore();
                                        if( lore == null ||
                                                !(lore.contains(state.getUUID().toString()))) {return true;}
                                        Town capital = nation.getCapital();
                                        if(Main.getTownCulturesPoints(capital) < 1000) {
                                            commandSender.sendMessage
                                                    ("你的文化程度太低(不足1000哲学学院点数)，无法解释一個合适的理由以正当化地宣蘸");return true;

                                        }
                                        Main.setTownCulturePoints(capital, Main.getTownCulturesPoints(capital) - 1000);
                                        target.addAll(state.getTowns());
                                        if(!target.isEmpty()) {
                                            List<Nation> nations = new ArrayList<>();
                                            List<Resident> residents = new ArrayList<>();

                                            //nation joins
                                            nations.add(nation);
                                            residents.addAll(nation.getResidents());

                                            //state joins
                                            nations.add(state);
                                            residents.addAll(state.getResidents());

                                            TownyMessaging.sendMsg(commandSender, Translatable.of("msg_beginning_x_war_in_x_seconds", new Object[]{WarTypeEnum.NATIONWAR.getName(), EventWarSettings.nationWarDelay()}));
                                            War war = new War(nations, (List)null, residents, WarTypeEnum.NATIONWAR.getType(), (DeclarationOfWar)null);

                                            war.setWarName(nation + "vs" + state.getName() + "的征服战争");


                                        }

                                    case "independence":
                                    case "claim_title":




                                }

                                } catch (TownyException e) {
                                throw new RuntimeException(e);
                            }
                    }

            }
        }
        */
        return true;
    }

    private void parseGatherFeudalTax(Nation nation, Nation state, CommandSender sender) throws TownyException {
        if(Main.getTownSciencePoints(nation.getCapital()) < 10) {
            sender.sendMessage("你需要至少10点文化点数来收取封建税");return;

        }
        double amount = (4 + state.getNumTowns()) * MetaDataUtil.getNationFeudalTax(nation);
        state.getAccount().withdraw(Math.min(amount, state.getAccount().getHoldingBalance()),"feudal_tax");
        state.getAccount().deposit(Math.min(amount, state.getAccount().getHoldingBalance()), "feudal_tax");

        sender.sendMessage("成功收取价值" + amount + "ducas的封建税！");

    }

    private void parsePrintNationForm(Nation nation, CommandSender commandSender) {
        StatusScreen statusScreen = new StatusScreen(commandSender);
        statusScreen.addComponentOf("nation_form_title", ChatTools.formatTitle(nation.getName() + "--成立帝國"));
        statusScreen.addComponentOf("new1", Component.newline());
        if(Main.getNationFixedTitle(nation) != -1) {


        }
        else {
            statusScreen.addComponentOf("nation_form_intro", "§5成立一個帝國可让你的國家固定拥有最高的头衔等级，增加城镇区块上限和其他能力》");
            statusScreen.addComponentOf("nation_form_assyria", "§e[]",
                    HoverEvent.showText(Component.text("[]")));
            statusScreen.addComponentOf("nation_form_babylonia", "§e[]",
                    HoverEvent.showText(Component.text("[]")));
            statusScreen.addComponentOf("nation_form_persia", "§e[]",
                    HoverEvent.showText(Component.text("[]")));
            statusScreen.addComponentOf("nation_form_graecia", "§e[]",
                    HoverEvent.showText(Component.text("[]")));
            statusScreen.addComponentOf("nation_form_rome", "§e[]",
                    HoverEvent.showText(Component.text("[]")));
            statusScreen.addComponentOf("nation_western_romania", "§e[]",
                    HoverEvent.showText(Component.text("[]")));
            statusScreen.addComponentOf("nation_eastern_romania", "§e[]",
                    HoverEvent.showText(Component.text("[]")));

        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> sendStatusScreen(commandSender, statusScreen));return;

    }

    private void parseNationFormEmpires(CommandSender sender, String arg) {
        boolean a = false;
        try {
            Player player = catchConsole(sender);
            Nation nation = getNationFromPlayerOrThrow(player);
            List<Town> core = new ArrayList<>();
            List<String> ex = new ArrayList<>();
            switch (arg) {
                case "assyria":
                if(checkValidTownBlock(new WorldCoord(player.getWorld(), 0, 0), "尼姆鲁德",nation, core, ex)
                || checkValidTownBlock(new WorldCoord(player.getWorld(), 0, 0), "亚述",nation, core, ex)
                || checkValidTownBlock(new WorldCoord(player.getWorld(), 0, 0), "尼尼微",nation, core, ex)) {
                    a = true;
                }
                //permission
                if(a && sender.hasPermission("townyadmin.oik")) {
                    MetaDataUtil.setNationTitleFixed(nation, 5);
                    for(Town town : core) {Main.addNewNationCore(nation, town);}
                    nation.setName("Assyrian_Empire");
                    nation.setMapColorHexCode("DAA520");
                } else {
                    TownyMessaging.sendMsg(sender, "你未能满足建立assyria帝國的条件：");
                    for(String e : ex) {
                        sender.sendMessage(e);
                    }
                }



                case "babylonia":

                case "persia":

                case "graecia":

                case "rome":

                case "western_romania":

                case "east_romania":
            }
        } catch (TownyException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkValidTownBlock(WorldCoord worldCoord, String name, Nation n, List<Town> core, List<String> ex) {
        TownBlock townBlock = worldCoord.getTownBlockOrNull();
        if(townBlock == null || townBlock.getTownOrNull() == null) {ex.add("§7拥有" + name + "(×)");return false;}
        core.add(townBlock.getTownOrNull());
        if(Objects.requireNonNull(townBlock.getTownOrNull()).hasNation()
                && Objects.requireNonNull(townBlock.getTownOrNull().getNationOrNull()).getUUID() == n.getUUID()) {
            ex.add("§7拥有" + name + "(√)");
            return true;
        }
        ex.add("§7拥有" + name + "(×)");
        return false;
    }
}
