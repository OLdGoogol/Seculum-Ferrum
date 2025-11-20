package xyz.teamAtlanta.seaculumFerrum.listener;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.nation.NationPreAddAllyEvent;
import com.palmergames.bukkit.towny.event.nation.PreNewNationEvent;
import com.palmergames.bukkit.towny.event.time.NewShortTimeEvent;
import com.palmergames.bukkit.towny.object.*;
import io.github.townyadvanced.eventwar.EventWar;
import io.github.townyadvanced.eventwar.events.scoring.EventWarTownBlockHPScoringEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.mcmonkey.sentinel.SentinelTrait;
import xyz.teamAtlanta.seaculumFerrum.Main;
import xyz.teamAtlanta.seaculumFerrum.Utils;
import xyz.teamAtlanta.seaculumFerrum.command.OIKMCTownCommandAddon;
import xyz.teamAtlanta.seaculumFerrum.meta.MetaDataUtil;
import io.github.townyadvanced.eventwar.command.EventWarCommand;
import io.github.townyadvanced.eventwar.instance.WarParticipants;
import io.github.townyadvanced.eventwar.db.WarMetaDataLoader;
import io.github.townyadvanced.eventwar.WarUniverse;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 
 * @author FinosPalaiologos
 *
 */
public class TownyEventListener implements Listener {
    private static final List<String> acceptedType = Arrays.asList("church", "gov", "base", "barrack", "art", "sci", "farm", "stable");


    @EventHandler(ignoreCancelled = true)
    public void onUpkeep(TownUpkeepCalculationEvent e) {
        //维护费的解释絶対是能写的，但是本人不会写handler，遂暂时做罢；
        int Point = 0;
        double minus = 0;
        int extrabyWorkOr_= 0;
        for(TownBlock townBlock : e.getTown().getTownBlocks()) {
            if(!townBlock.getType().getName().toLowerCase(Locale.ROOT).equals("default")) {
                if(townBlock.hasMeta("townblockValue")) {
                    Point += Main.getTownBlockValue(townBlock);
                }
            }
            if(townBlock.getType().getName().toLowerCase(Locale.ROOT).equals("gov")) {
                minus += 0.01 * Main.getTownBlockValue(townBlock);
                minus += 0.01 * Main.getTownBlockValueofBuilding(townBlock);
            }
        }

        System.out.println("总价：" + Point);
        e.setUpkeep(Math.max((e.getUpkeep() + Point) * (1 - minus), 1.0));

    }

    @EventHandler(ignoreCancelled = true)
    public void onNationUpkeep(NationUpkeepCalculationEvent e) {
        double up = e.getUpkeep();
        up += 1.0 * e.getNation().getNumTowns();
        up = up * getGovUsed(e.getNation()) / getGovLimited(e.getNation());

        e.setUpkeep(up);


    }

    @EventHandler(ignoreCancelled = true)
    public void onPlotChangeType(PlotChangeTypeEvent e) {
        Town town = e.getTownBlock().getTownOrNull();
        if(town == null) {return;}
        if(e.getOldType().getName().equals(e.getNewType().getName())) {return;}
        if(!acceptedType.contains(e.getOldType().getName().toLowerCase(Locale.ROOT))) {return;}
        TownyMessaging.sendMsg(town.getMayor().getPlayer(), "改变土地类型将使你失去这片土地上的全部投资发展度（建筑发展度需要重新审核），是否继续？");
        Confirmation
                .runOnAccept(()-> {
                    //clean bonus blocks
                    e.getTownBlock().getTownOrNull().setBonusBlocks((town.getBonusBlocks()) - Main.getTownBlockValue(e.getTownBlock()) * 8);

                    if(!e.getOldType().getName().toLowerCase(Locale.ROOT).equals("gov")) {return;}
                    Main.setTownBlockValue(e.getTownBlock(), 0);
                    Main.setTownBlockValueofBuilding(e.getTownBlock(), 0);
                })
                .sendTo(town.getMayor().getPlayer());
    }



    public static double getGovUsed(Nation nation) {
        double count = nation.getNumTowns();
        double ally = nation.getAllies().size();
        double vassal = Main.getNationVassals(nation).size();

        double s = count + ally + vassal;
        return s;
    }

    public static double getGovUsed(Nation nation, List<String> ex) {
        double count = nation.getNumTowns();
        //3級市政
        if(Main.hasTownCulture(nation.getCapital(), "UN03")) {count = 0;}

        ex.add(nation.getNumTowns() + "§b来自直辖城镇 §c+" + String.valueOf(new DecimalFormat("0.0").format(count)));
        double ally = nation.getAllies().size();
        ex.add(nation.getAllies().size() + "§b来自盟友 §c+" + String.valueOf(new DecimalFormat("0.0").format(ally)));
        double vassal = Main.getNationVassals(nation).size();
        ex.add(nation.getAllies().size() + "§b来自附庸 §c+" + String.valueOf(new DecimalFormat("0.0").format(vassal)));

        double s = count + ally + vassal;
        return s;
    }

    public static double getGovLimited(Nation nation) {
        double minus = 1.0;
        for(TownBlock townBlock : nation.getCapital().getTownBlocks()) {
            if(townBlock.getType().getName().toLowerCase(Locale.ROOT).equals("gov")) {
                minus += 0.1 * Main.getTownBlockValue(townBlock);
                minus += 0.1 * Main.getTownBlockValueofBuilding(townBlock);
            }
        }
        return minus;
    }

    public static double getGovLimited(Nation nation, List<String> ex) {
        double minus = 1.0;int n = 0;
        for(TownBlock townBlock : nation.getCapital().getTownBlocks()) {
            if(townBlock.getType().getName().toLowerCase(Locale.ROOT).equals("gov")) {
                minus += 0.1 * Main.getTownBlockValue(townBlock);
                minus += 0.1 * Main.getTownBlockValueofBuilding(townBlock);
                n ++;
            }
        }
        ex.add(n + "§b個市政厅区块的发展度 -§a" + String.valueOf(new DecimalFormat("0.0").format(minus)));
        return minus;
    }

    @EventHandler(ignoreCancelled = true)
    public void onNewDay(NewDayEvent event) {
            //OIKMC settings
        {
            for(Town town : TownyUniverse.getInstance().getTowns()) {

                Main.setTownSciencePoints(town, Main.getTownSciencePoints(town)
                        + Main.getNewSciPoints(town));
                Main.setTownCulturePoints(town, Main.getTownCulturesPoints(town)
                        + Main.getNewCulPoints(town));

                //check sciences and cultures
                String[] scis = Utils.transferStringAToArray(MetaDataUtil.getTownScience(town));
                String[] cus = Utils.transferStringAToArray(MetaDataUtil.getTownCulture(town));

                if(MetaDataUtil.getTownCulture(town) != null
                        && !cus[0].isEmpty()
                        && !cus[0].equals("n")
                        && Main.getTownCulturesPoints(town) > OIKMCTownCommandAddon.getActualCost(town, cus))
                {MetaDataUtil.setTownCulture(town, MetaDataUtil.getTownCulture(town).replace(cus[0], "n"));
                    Main.addNewTownCulture(town, Objects.requireNonNull(Main.getCulture(cus[0])));}

                if(MetaDataUtil.getTownScience(town) != null
                        && !cus[0].isEmpty()
                        && !scis[0].equals("n")
                        && Main.getTownSciencePoints(town) > OIKMCTownCommandAddon.getActualCost(town, cus))
                {MetaDataUtil.setTownScience(town, MetaDataUtil.getTownCulture(town).replace(cus[0], "n"));
                    Main.addNewTownScience(town, Objects.requireNonNull(Main.getScience(cus[0])));}

               }
            }


    }

    @EventHandler(ignoreCancelled = true)
    public void onShortTime(NewShortTimeEvent event) {

    }

    @EventHandler(ignoreCancelled = true)
    public void onNPCSpawn(NPCSpawnEvent event) {
        NPC npc = event.getNPC();
        Entity e = npc.getEntity();
        if (e instanceof Horse) {
            if(!npc.getName().contains("horse")) {
                return;
            }
            Entity eh = npc.getEntity();
            if (!(eh instanceof Horse)) {
                return;
            }
            ((Horse) eh).getInventory().setArmor(new ItemStack(Material.IRON_HORSE_ARMOR));

        }



        if (!(e instanceof Player)) {
            return;
        }
        if(npc.getName().contains("militia")) {
            ((Player) e).getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            ((Player) e).getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
            ((Player) e).getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
            ((Player) e).getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
            npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD));
        }
        if(npc.getName().contains("horseman")) {
            ((Player) e).getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
            ((Player) e).getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_HELMET));
            ((Player) e).getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_HELMET));
            ((Player) e).getInventory().setBoots(new ItemStack(Material.CHAINMAIL_HELMET));
            ((Player) e).getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));


            npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD));

        }
        if(npc.getName().contains("crossbowman")) {
            ((Player) e).getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
            ((Player) e).getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
            ((Player) e).getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
            ((Player) e).getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
            npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.CROSSBOW));





        }
        if(npc.getName().contains("warrior")) {
            ((Player) e).getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
            ((Player) e).getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            ((Player) e).getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            ((Player) e).getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
            ((Player) e).getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));

            npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.IRON_AXE));

        }


    }




    //farm plot
    @EventHandler(ignoreCancelled = true)
    public void onGrowth(BlockGrowEvent event) {
        if(event.getBlock().getType().toString().equals("")) {return;};
        WorldCoord worldCoord = new WorldCoord("world", Coord.parseCoord(
                Objects.requireNonNull(event.getBlock().getLocation())));
        TownBlock townBlock = worldCoord.getTownBlockOrNull();
        int gra = 1;
        if(townBlock != null) {gra += Main.getTownBlockValue(townBlock) + Main.getTownBlockValueofBuilding(townBlock);
        }
        double i = Math.random() * gra;
        //System.out.println(i);
        if(i < 0.9) {
            event.setCancelled(true);
            //System.out.println("取消了一個生长事件");
        }

    }

    //technology staff
    /*(not used now)
    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if(Objects.requireNonNull(event.getInventory().getResult()).getType().toString().toLowerCase(Locale.ROOT).equals("musketmod_musket")) {
            WorldCoord worldCoord = new WorldCoord("world", Coord.parseCoord(
                    Objects.requireNonNull(event.getInventory().getLocation())));
            TownBlock townBlock = worldCoord.getTownBlockOrNull();
            //check sci condition
            if(townBlock == null
                    || !townBlock.getType().getName().toLowerCase(Locale.ROOT).equals("sci")
                    || !Main.hasTownScience(townBlock.getTownOrNull(), "UN01"))
            {event.setCancelled(true);
                event.getWhoClicked().sendMessage("你需要在至少1科技等级的学院里合成此物品");;return;}

            //consume sci points
            int cost = 10;
            if(Main.getTownSciencePoints(townBlock.getTownOrNull()) < cost) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("你的城镇需要在至少10科技点数完成此合成");;return;


            }

            Main.setTownSciencePoints(townBlock.getTownOrNull(),
                    Main.getTownSciencePoints(townBlock.getTownOrNull()) -10);
        }
    }

     */

    //gov staff
    @EventHandler(ignoreCancelled = true)
    public void onNationNew(PreNewNationEvent event) {
        Town town = event.getTown();
        if(Main.getTownCulturesPoints(town) < 0) {
            event.setCancelled(true);
            event.setCancelMessage("你的城镇过于鄙陋，需要至少100文化建立新的共同体认同以创建国家");;return;

        }
        Main.setTownCulturePoints(town, Main.getTownCulturesPoints(town) - 100);

    }

    @EventHandler(ignoreCancelled = true)
    public void onNationAlly(NationPreAddAllyEvent event) {
        Town town = event.getNation().getCapital();
        if(Main.getTownCulturesPoints(town) < 10) {
            event.setCancelled(true);
            event.setCancelMessage("需要至少10点文化建立一個新的盟友关系");;return;

        }
        Main.setTownCulturePoints(town, Main.getTownCulturesPoints(town) - 10);

    }

    @EventHandler(ignoreCancelled = true)
    public void onNationEnemy(NationPreAddEnemyEvent event) {
        Town town = event.getNation().getCapital();
        if(Main.getTownCulturesPoints(town) < 10) {
            event.setCancelled(true);
            event.setCancelMessage("需要至少10点文化建立一個新的宿敌关系");;return;

        }
        Main.setTownCulturePoints(town, Main.getTownCulturesPoints(town) - 10);

    }

    @EventHandler(ignoreCancelled = true)
    public void onNationAddTown(NationPreAddTownEvent event) {
        Town town = event.getNation().getCapital();
        if(Main.getTownCulturesPoints(town) < 10) {
            event.setCancelled(true);
            event.setCancelMessage("需要至少1点文化以拥有一個新的直辖城市");;return;

        }
        Main.setTownCulturePoints(town, Main.getTownCulturesPoints(town) - 100);

    }

    @EventHandler(ignoreCancelled = true)
    public void onNationRename(NationPreRenameEvent event) {
        Town town = event.getNation().getCapital();
        if(Main.getTownCulturesPoints(town) < 10) {
            event.setCancelled(true);
            event.setCancelMessage("需要至少50点文化修改國名");;return;

        }
        Main.setTownCulturePoints(town, Main.getTownCulturesPoints(town) - 100);

    }

    @EventHandler(ignoreCancelled = true)
    public void onBookEmploy(PlayerInteractEvent e) {

        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack i = e.getItem();
            if(i == null) {return;}
            switch (i.getType().toString()) {
                case "ENCHANTED_BOOK":
                    System.out.println("找到书了");
                    if(!i.hasItemMeta()) {return;}
                    ItemMeta im = i.getItemMeta();
                    for(String str1 : Main.outputMercsList()) {
                        if (Objects.requireNonNull(im.getLore()).contains(str1))
                            Main.RecruitMercs(Main.getMercs(str1), e.getPlayer());
                        i.setType(Material.PAPER);
                    }
                    break;
                case "PAPER":
                    System.out.println("找到纸了");
                    if(!i.hasItemMeta()) {return;}
                    ItemMeta im1 = i.getItemMeta();
                    for(String s : Main.outputMercsList()) {
                        if (Objects.requireNonNull(im1.getLore()).contains(s))
                            Main.disRecruitMercs(Main.getMercs(s), e.getPlayer());
                    }
                    i.setType(Material.BOOK);
            }

        }


    }

    @EventHandler(ignoreCancelled = true)
    public void onGarrison(PlayerInteractEntityEvent e) {
        System.out.println("test-garrison");
        System.out.println(e.getHand().toString());
        System.out.println(e.getRightClicked().getName());

        if(e.getHand().toString().equals("OFF_HAND")) {
            System.out.println("是OFFHAND，不管");
            return;
        }
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(e.getRightClicked());
        SentinelTrait s = npc.getOrAddTrait(SentinelTrait.class);
        if(s.getGuarding().equals(e.getPlayer().getUniqueId())) {
            e.getRightClicked().setMetadata("employer:" + e.getPlayer().getUniqueId(), new FixedMetadataValue
                    (Main.getInstance(), e.getPlayer().getUniqueId().toString()));
            System.out.println("uuid字符是：" + String.valueOf(e.getPlayer().getUniqueId()));
            s.setGuarding(npc.getId());
            return;

            //s.getGuardZone().setX(e.getRightClicked().getLocation().getX());
            //s.getGuardZone().setZ(e.getRightClicked().getLocation().getZ());
        }

        System.out.println(String.valueOf(e.getPlayer().getUniqueId()));
        if(e.getRightClicked().hasMetadata("employer:" + e.getPlayer().getUniqueId()))

            System.out.println("确实是uu");
            s.setGuarding(e.getPlayer().getUniqueId());

            //s.getGuardZone().setX(e.getRightClicked().getLocation().getX());
            //s.getGuardZone().setZ(e.getRightClicked().getLocation().getZ());

    }










}
