package xyz.teamAtlanta.seaculumFerrum.listener;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import xyz.teamAtlanta.seaculumFerrum.Main;

import java.util.Locale;
import java.util.Objects;

public class TechnologyListener implements Listener{
    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (checkScience(event, 1452, "UN10")) {event.setCancelled(true);
        event.getWhoClicked().sendMessage("§c你國的科技水平太低了，无法合成此装备！至少需要解锁 10 §f板甲§b");
        }
        if (checkScience(event, 1449, "UN01")) {event.setCancelled(true);
            event.getWhoClicked().sendMessage("§c你國的科技水平太低了，无法合成此装备！至少需要解锁 §f01§b 晚期罗马鳞甲");
        }
        if (checkScience(event, 1448, "UN01")) {event.setCancelled(true);
            event.getWhoClicked().sendMessage("§c你國的科技水平太低了，无法合成此装备！至少需要解锁 §f01§b 晚期罗马鳞甲");
        }
        if (checkScience(event, 1004, "UN03")) {event.setCancelled(true);
            event.getWhoClicked().sendMessage("§c你國的科技水平太低了，无法合成此装备！至少需要解锁 §f03§b 骑枪");
        }
        if (checkScience(event, 1444, "UN06")) {event.setCancelled(true);
            event.getWhoClicked().sendMessage("§c你國的科技水平太低了，无法合成此装备！至少需要解锁 §f06§b钢盔");
        }

        if (checkScience(event, 1440, "UN07")) {event.setCancelled(true);
            event.getWhoClicked().sendMessage("§c你國的科技水平太低了，无法合成此装备！至少需要解锁 §f07§b 重甲骑士");
        }
        if (checkScience(event, 1402, "UN08")) {event.setCancelled(true);
            event.getWhoClicked().sendMessage("§c你國的科技水平太低了，无法合成此装备！至少需要解锁 §f08§b 棉甲");
        }
    }

    public boolean checkScience(CraftItemEvent event, int data, String id) {
        if(Objects.requireNonNull(event.getInventory().getResult()).hasItemMeta()
                && event.getInventory().getResult().getItemMeta().hasCustomModelData()
                && event.getInventory().getResult().getItemMeta().getCustomModelData() == data) {
            TownBlock townBlock = TownyUniverse.getInstance().getTownBlockOrNull(
                    WorldCoord.parseWorldCoord(
                            Objects.requireNonNull(event.getInventory().getLocation())));
            try {
                return (townBlock == null
                        || !townBlock.hasTown()
                        || Main.hasTownScience(townBlock.getTown(), id));
            } catch (NotRegisteredException e) {
                throw new RuntimeException(e);
            }

        }
        return false;
    }
}
