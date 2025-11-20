package xyz.teamAtlanta.seaculumFerrum.command;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.teamAtlanta.seaculumFerrum.meta.MetaDataUtil;

import java.util.Collections;
import java.util.List;

public class OIKMCNationToggleCommandAddon extends BaseCommand implements TabExecutor {

    public OIKMCNationToggleCommandAddon() {
        AddonCommand nationSetSiegeWarCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.NATION_TOGGLE, "freesubject", this);
        TownyCommandAddonAPI.addSubCommand(nationSetSiegeWarCommand);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 0) {
            try {
                Player player = catchConsole(commandSender);
                Nation nation = getNationFromPlayerOrThrow(player);

                MetaDataUtil.setNationFreeSubject(nation, !MetaDataUtil.getNationFreeSubject(nation));

            } catch (TownyException e) {throw new RuntimeException(e);}

        }
        return true;
    }
}

