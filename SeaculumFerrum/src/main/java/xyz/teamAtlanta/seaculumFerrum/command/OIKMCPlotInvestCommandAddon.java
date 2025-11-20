package xyz.teamAtlanta.seaculumFerrum.command;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import io.github.townyadvanced.eventwar.events.scoring.EventWarTownBlockHPScoringEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.teamAtlanta.seaculumFerrum.Main;

import java.util.*;

public class OIKMCPlotInvestCommandAddon extends BaseCommand implements TabExecutor {
    public OIKMCPlotInvestCommandAddon() {
        AddonCommand townyAdminAddonCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.PLOT, "invest", this);
        TownyCommandAddonAPI.addSubCommand(townyAdminAddonCommand);
    }

    private final List<String> acceptedType = Arrays.asList("church", "gov", "base", "barrack","art", "sci", "farm", "stable");
    private CommandSender sender;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }



    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        this.sender = sender;
        try {
            Player player = catchConsole(sender);

            TownBlock townBlock = TownyUniverse.getInstance().getTownBlockOrNull(WorldCoord.parseWorldCoord(player.getLocation()));
            //confirm if the block is a wildness block
            if(townBlock == null || !townBlock.hasTown()) {
                TownyMessaging.sendMsg(player, "§c你并不处在一個城镇区块上");return true;
            }


            if(!acceptedType.contains(Objects.requireNonNull(townBlock).getTypeName().toLowerCase())) {TownyMessaging.sendMsg(player, "区块类型必须为[教堂][役所][要塞][农田][工坊][社区][艺术][科学]其中之一");return true;}
            int priceG = Main.getTownBlockValue(townBlock);
            if(priceG > 6) {TownyMessaging.sendMsg(player, "§c你対该区块的投资已到达上限！");return true;}
            if(priceG > 2 && !((townBlock.getType().getName().equalsIgnoreCase("base")) && Main.hasTownScience(townBlock.getTown(), "UN02"))) {TownyMessaging.sendMsg(player, "§c你対该区块的投资已到达上限！");return true;}
            double mod = 1.0;
            TownyMessaging.sendMsg(player, "§b这项対你的" + townBlock.getTypeName() + "的投资将花费" + (priceG * 20.0 + 10.0) * mod + "枚杜卡斯，是否继续？");

            Confirmation
                    .runOnAccept(()-> {
                        if(TownyUniverse.getInstance().getResident(player.getUniqueId()) == null) {return;}
                        if(!TownyUniverse.getInstance().getResident(player.getUniqueId()).getAccount().canPayFromHoldings((priceG * 20.0 + 10.0) * mod)) {
                            TownyMessaging.sendMsg(player, "§c你无法承担这笔花费");
                            //这return是対的嗎？
                            return;
                        }
                        TownyUniverse.getInstance().getResident(player.getUniqueId()).getAccount().withdraw((priceG * 20.0 + 10.0) *  mod,
                                "investment for his blocks");
                        Main.setTownBlockValue(townBlock, Main.getTownBlockValue(townBlock) + 1);
                        TownyMessaging.sendMsg(player, "§b成功进行本地投资！你的功能区块将获得：投资发展度+1");
                        if(!townBlock.getType().getName().toLowerCase(Locale.ROOT).equals("gov")) {return;}
                        townBlock.getTownOrNull().setBonusBlocks((townBlock.getTownOrNull().getBonusBlocks()) + 8);

                    })
                    .runOnCancel(()-> TownyMessaging.sendMsg(player, "§7已放弃該投资项目"))
                    .sendTo(player);

        } catch (TownyException e) {TownyMessaging.sendErrorMsg(sender, e.getMessage(sender));}
        return true;

    }
}
