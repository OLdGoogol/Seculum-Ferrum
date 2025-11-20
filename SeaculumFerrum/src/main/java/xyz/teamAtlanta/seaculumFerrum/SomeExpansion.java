package xyz.teamAtlanta.seaculumFerrum;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.plugin.java.JavaPlugin;

public class SomeExpansion extends PlaceholderExpansion {

    private JavaPlugin plugin; // This instance is assigned in canRegister()

    public SomeExpansion(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getAuthor() {
        return "Palaiologos_F";
    }

    @Override
    public String getIdentifier() {
        return "fixtitle";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getRequiredPlugin() {
        return "SeaculumFerrum";
    }

    @Override
    public boolean canRegister() {
        return (plugin = (Main) Bukkit.getPluginManager().getPlugin(getRequiredPlugin())) != null;
    }

    public boolean persist() {
        return true; // 插件重载时是否保留
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) {
            return "";
        }

        System.out.println("我们刚刚得到一个" + params);
        // 示例：处理 %fixednationlevel_nation_level% 占位符
        if (params.equalsIgnoreCase("townyadvanced_nation_formatted")) {
            Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
            if (resident != null && resident.hasNation()) {
                Nation nation = null;
                try {
                    nation = resident.getNation();
                } catch (TownyException e) {
                    throw new RuntimeException(e);
                }
                if (!(Main.getNationFixedTitle(nation) == -1)) {
                    return String.valueOf(Main.getNationFixedTitle(nation));
                    // 为波斯帝国返回固定等级
                } else {
                    // 对于其他国家，返回 Towny 实际计算的等级
                    return String.valueOf(nation.getLevel());
                }
            }
        }
        // 您可以添加更多自定义占位符
        return null;
    }

    // Placeholder is unknown by the expansion
}
