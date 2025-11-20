package xyz.teamAtlanta.seaculumFerrum.command;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Translatable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.teamAtlanta.seaculumFerrum.Main;
import xyz.teamAtlanta.seaculumFerrum.meta.MetaDataUtil;

import java.util.Collections;
import java.util.List;

public class OIKMCNationSetCommandAddon  extends BaseCommand implements TabExecutor {

    public OIKMCNationSetCommandAddon() {
        AddonCommand nationSetSiegeWarCommand = new AddonCommand(TownyCommandAddonAPI.CommandType.NATION_SET, "feudaltax", this);
        TownyCommandAddonAPI.addSubCommand(nationSetSiegeWarCommand);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            try {
                Player player = catchConsole(commandSender);
                Nation nation = getNationFromPlayerOrThrow(player);
                //计算最大封臣挩
                double maxTaxPerCity = 1.0;
                //2級市政
                if(Main.hasTownCulture(nation.getCapital(), "UN02")) {maxTaxPerCity += 2.0;}
                double taxPerCity;
                if (args[0].equalsIgnoreCase("max")) {}
                else {
                    taxPerCity = getPositiveDoubleOrThrow(args[0]);
                    if (taxPerCity > maxTaxPerCity)
                        {
                            TownyMessaging.sendMsg(player, "你设置的封臣挩太高，贵族将叫苦连天，这被视为一种薄政，请设置一個低于" + maxTaxPerCity + "杜卡斯/城的价格");return true;}
                    taxPerCity = Math.min(maxTaxPerCity, taxPerCity);
                    MetaDataUtil.setNationFeudalTax(nation, taxPerCity);
                }
            } catch (TownyException e) {
                throw new RuntimeException(e);
            }

        }
        return true;
    }

    private double getPositiveDoubleOrThrow(String input) throws TownyException {
        try {
            double d = Double.parseDouble(input);
            if(d < 0) {
                throw new TownyException(Translatable.of("msg_err_negative"));
            }
            return d;
        } catch (NumberFormatException var3) {
            throw new TownyException(Translatable.of("msg_error_must_be_num"));
        }
    }


}

