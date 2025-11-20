package xyz.teamAtlanta.seaculumFerrum.command;

import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.event.ClickEvent;
import com.palmergames.adventure.text.event.HoverEvent;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.statusscreens.StatusScreen;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.collections4.EnumerationUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.teamAtlanta.seaculumFerrum.Main;
import xyz.teamAtlanta.seaculumFerrum.Mercs;
import xyz.teamAtlanta.seaculumFerrum.Territory;

import java.util.*;

public class OIKMCResidentCommandAddon extends BaseCommand implements TabExecutor{
    public OIKMCResidentCommandAddon() {
        AddonCommand townyResidentAddonCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.RESIDENT, "oik", this);
        TownyCommandAddonAPI.addSubCommand(townyResidentAddonCommand);

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) {return Arrays.asList("merc");}
        if(args.length == 2) {if(args[0].equals("merc")) return Arrays.asList("purchase","list");}
        if(args.length == 3) {if(args[0].equals("merc")) {
            if(args[1].toLowerCase().equals("purchase")) {
                return Main.outputMercsList();
                }
            }
        }


    return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 2) {
            if(strings[0].equalsIgnoreCase("merc")&& strings[1].equals("list")){
                StatusScreen screen = new StatusScreen(commandSender);
                screen.addComponentOf("title", "§6.o0o.---------.[Mercs List].---------.o0o.");
                Component component = Component.empty();
                for(String string : Main.outputMercsList()) {
                    Mercs mercs = Main.getMercs(string);
                    if(mercs == null) {continue;}
                    screen.addComponentOf("newline-" + string, Component.newline());
                    screen.addComponentOf(string, Component.text("§a[" + string + "]")
                                    .hoverEvent(HoverEvent.showText(Component.text("§f是骑士团：§b" + mercs.isKnight() + "§f ")
                                            .append(Component.text("驻扎地" + (mercs.getLimitCulture() == null ? "§b本地" : "§b" + mercs.getLimitCulture().getFormattedName())))))
                                    .clickEvent(ClickEvent.suggestCommand("/resident oik purchase " + string))
                    );

                }
                screen.addComponentOf("mercs", component);
                TownyMessaging.sendStatusScreen(commandSender, screen);

            }
        }
        if(strings.length == 3) {
            if(strings[0].equalsIgnoreCase("merc") && strings[1].equals("purchase")) {
                Mercs mercs = Main.getMercs(strings[2]);
                if(mercs == null) {
                    return true;
                }
                if(!(commandSender instanceof Player)) {
                    return true;
                }
                Player player = (Player) commandSender;
                double mod = 1.0;
                if(TownyUniverse.getInstance().getResident(player.getUniqueId()) == null) {return true;}
                if(!TownyUniverse.getInstance().getResident(player.getUniqueId()).getAccount().canPayFromHoldings((mercs.getPrice()) * mod)) {
                    TownyMessaging.sendMsg(player, "§c你无法承担雇佣该佣兵团的花费");
                    //这return是対的嗎？
                    return true;
                }
                System.out.println("test1");
                Town town = TownyUniverse.getInstance().getResident(player.getUniqueId()).getTownOrNull();
                try {
                    System.out.println("test2");
                    if(mercs.getLimitCulture() != null &&
                            (town == null ||
                                    Territory.initializeCulture(town.getHomeBlock().getCoord()).equals(mercs.getLimitCulture()))) {
                        System.out.println("test3");
                        TownyMessaging.sendMsg(player, "§c你所在城镇的地区距离佣兵召集点太远，他们无法到达");
                        return true;
                    }
                } catch (TownyException e) {
                    throw new RuntimeException(e);
                }
                //8級市政
                if(mercs.isKnight() && ( town ==null ||
                        !Main.hasTownCulture(town, "UN08")))
                {
                    System.out.println("test5");
                    TownyMessaging.sendMsg(player, "§c你无法招募骑士团，需要至少解锁市政 §f08§b §c骑士团");
                    return true;
                }



                ItemStack En = new ItemStack(Material.ENCHANTED_BOOK);
                List<String> lore1 = new ArrayList<String>();

                ItemMeta en = En.getItemMeta();
                lore1.add(mercs.getID());
                lore1.add("§f" + mercs.getFormattedName());
                lore1.add("§f这是一本佣兵附魔书，你可以用该书招募尚未被招募的佣兵团");

                en.setLore(lore1);
                En.setItemMeta(en);

                player.getInventory().addItem(En);
                TownyUniverse.getInstance().getResident(player.getUniqueId()).getAccount().withdraw(mercs.getPrice() *  mod,
                        "pay the mercs");



            }
        }
        return false;
    }
}
