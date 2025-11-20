package xyz.teamAtlanta.seaculumFerrum.listener;

import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.event.ClickEvent;
import com.palmergames.adventure.text.event.HoverEvent;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.statusscreen.NationStatusScreenEvent;
import com.palmergames.bukkit.towny.event.statusscreen.ResidentStatusScreenEvent;
import com.palmergames.bukkit.towny.event.statusscreen.StatusScreenEvent;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.teamAtlanta.seaculumFerrum.Main;
import xyz.teamAtlanta.seaculumFerrum.Territory;
import xyz.teamAtlanta.seaculumFerrum.Utils;
import xyz.teamAtlanta.seaculumFerrum.command.OIKMCTownCommandAddon;
import xyz.teamAtlanta.seaculumFerrum.meta.MetaDataUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.palmergames.bukkit.towny.command.BaseCommand.catchConsole;

public class StatusScreenListener implements Listener {
	public static final List<String> GivenNames = Arrays.asList("assyrian");

	@EventHandler(ignoreCancelled = true)
	public void onResidentStatusScreen(ResidentStatusScreenEvent event) {
		}
	
	/*
	 */
	@EventHandler(ignoreCancelled = true)
	public void onNationStatusScreen(NationStatusScreenEvent event) {
		List<String> ex1 = new ArrayList<>();List<String> ex2 = new ArrayList<>();
		for(Nation nation : Main.getNationVassals(event.getNation())) {
			if(Main.getNationFixedTitle(nation) == 3 || Main.getNationFixedTitle(nation) == 4) {
				ex1.add("§b" + nation.getFormattedName() + "§f");
			} else {
				ex1.add(nation.getFormattedName());
			}
		}
		event.getStatusScreen().addComponentOf("OIKMC_vassal",
				Component.empty()
						.append(Component.text(hoverFormat("§2vassal :§a[" + Main.getNationVassals(event.getNation()).size() + "]")))
						.hoverEvent(HoverEvent.showText(Component.text(String.join(",", ex1))

								)
						)

		);

		event.getStatusScreen().addComponentOf("OIKMC_newline", Component.newline());
		double gov = TownyEventListener.getGovUsed(event.getNation(), ex1);
		double govlimit = TownyEventListener.getGovLimited(event.getNation(), ex2);
		int a = (int)( 10 * gov / govlimit );String pro;
		if(a > 10) {pro = "§c+".repeat(10);} else {pro = "§f+".repeat(a) + "§a-".repeat(10 - a);}
		event.getStatusScreen().addComponentOf("OIKMC_gov",
				Component.empty()
				        .append(Component.text("§b[行政容量] " + pro + " ").hoverEvent(HoverEvent.showText((Component.text("*行政容量及其使用程度决定了一个国家是否因为事务过度繁重而效率低下，产生额外的维护费用"))))
								)
						.append(Component.text(String.valueOf(new DecimalFormat("0.0").format(gov))).
								hoverEvent(HoverEvent.showText(Component.text(String.join("\n", ex1)))))
						.append(Component.text("/"))
						.append(Component.text(String.valueOf(new DecimalFormat("0.0").format(govlimit))).
								hoverEvent(HoverEvent.showText(Component.text(String.join("\n", ex2)))))
		);

			//大苯氮甚至不知道jump怎麽写，，，
		if(
				//!GivenNames.contains(event.getNation().getName().toLowerCase(Locale.ROOT))
		(Main.getNationFixedTitle(event.getNation()) == 5)
		) {
			//Empires
			event.getStatusScreen().addComponentOf("OIKMC_empire",
					Component.empty()
							.append(Component.text(hoverFormat("§5[帝国制]"))
									.hoverEvent(HoverEvent.showText(Component.text("帝国制是建立在古典帝国的基础上发展而来的制度，推崇自由民参加公共事务")
											.append(Component.newline()).append(Component.text("帝国制将带来以下效果："))
											.append(Component.newline()).append(Component.text("*§b允许向城市派遣市长§f"))
											.append(Component.newline()).append(Component.text("*§b允许向军区派遣将军§f"))
											.append(Component.newline()).append(Component.text("*§b有限的附庸（军区）上限§f"))
									)))
							.append(Component.newline()));
		} else {
			event.getStatusScreen().addComponentOf("OIKMC_empire",
					Component.empty()
							.append(Component.text(hoverFormat("§b[封建制]"))
									.hoverEvent(HoverEvent.showText(Component.text("test")
											.append(Component.newline()).append(Component.text("封建制是建立在日耳曼人的部落等级传统之上发展而来的制度"))
											.append(Component.newline()).append(Component.text("封建制将带来以下效果："))
											.append(Component.newline()).append(Component.text("*§b可以向附庸（封臣）收取封臣税§f"))
											.append(Component.newline()).append(Component.text("*§b无限的附庸（封臣）上限§f"))
									)))
							.append(Component.newline()));

		}


	}

	@EventHandler
	public void onPlotStatusScreen(StatusScreenEvent event) {
		System.out.println("success?……");
		if(event.getStatusScreen().hasComponent("townblock_title")) {
            try {
				System.out.println("能 抓到嗎》？");
                Player player = catchConsole(event.getCommandSender());
				TownBlock townBlock = TownyUniverse.getInstance().getTownBlockOrNull(WorldCoord.parseWorldCoord(player.getLocation()));
				if(Main.getTownBlockValueofBuilding(townBlock) > 0) {
					event.getStatusScreen().addComponentOf("OIKMC_valueob",
							Component.empty()
									.append(Component.text("[§9建筑等级-(" + Main.getTownBlockValueofBuilding(townBlock) + ")]")
											.hoverEvent(HoverEvent.showText(Component.text("建筑等级表示你的区块因其上建造的奇观建筑而拥有的发展等级，通过建造并向服务器官方申请获得該提升")))));

				}
				if(Main.getTownBlockValue(townBlock) > 0) {
					event.getStatusScreen().addComponentOf("OIKMC_value",
							Component.empty()
									.append(Component.text("[§1投资等级-(" + Main.getTownBlockValue(townBlock) + ")]")
											.hoverEvent(HoverEvent.showText(Component.text("投资等级表示你的区块因投资金钱而拥有的发展等级，通过/plot invest获得該提升")))));
				}

			} catch (TownyException e) {
                throw new RuntimeException(e);
            }

		}
	}

	private String hoverFormat(String hover) {
		return String.format(hover,
				Translation.of("status_format_hover_bracket_colour"),
				Translation.of("status_format_hover_key"),
				Translation.of("status_format_hover_bracket_colour"));
	}

	@EventHandler(ignoreCancelled = true)
	public void onTownStatusScreen(TownStatusScreenEvent event) {
		event.getStatusScreen().addComponentOf("OIKMC_newline_1", Component.newline());
		//康康能不能搞定地区
		try {
			Territory[] terrs = Territory.initializeCulture(event.getTown().getHomeBlock().getCoord());
			List<String> s = new ArrayList<>();

			for(Nation nation : terrs[1].getStrongClaimed()) {
				if(nation != null)
				{s.add("§b" + nation.getFormattedName() + "§f");}
			}
			for(Nation nation : terrs[1].getClaimed()) {
				if(nation != null)
				{s.add(nation.getFormattedName());}
			}
			event.getStatusScreen().addComponentOf("OIKMC_terr",
					Component.empty()
							.append(Component.text(hoverFormat("§2Area :§a") + terrs[1].getID()))
							.hoverEvent(HoverEvent.showText(Component.text("§f该地区属于王国领 §b" + terrs[0].getFormattedName())
									.append(Component.newline()).append(Component.text("以下国家宣称此地区：§a" + String.join(",", s)))
									)
							)

			);
			event.getStatusScreen().addComponentOf("claimsuggest",
					Component.empty()
							.append(Component.text(hoverFormat("§b[宣称地区]§f"))
									.hoverEvent(HoverEvent.showText(Component.text("§a宣称新的区域§f")
											.append(Component.newline()).append(Component.text("服务器的地图被划分为多个不同的法理公国领和王国领，各国可根据一定的规则宣称这些地区，你只能在战争中攻击在你已宣称地区建立城镇的国家")))
									)

									.clickEvent(ClickEvent.suggestCommand("/n oik claim " + terrs[1].getID()))));

		} catch (TownyException e) {
			throw new RuntimeException(e);
		}
		event.getStatusScreen().addComponentOf("OIKMC_newline", Component.newline());
		event.getStatusScreen().addComponentOf("OIKMC_academy",
				Component.empty()
						.append(Component.text(hoverFormat("§b[科学学院:+" + Main.getNewSciPoints(event.getTown()) + "]"))
								.hoverEvent(HoverEvent.showText(Component.text("输入/t oik sci info了解详情")
										.append(Component.newline()).append(Component.text("你可以建设科学学院用以提升科学点数，科学点数进行以下行动："))
										.append(Component.newline()).append(Component.text("*§b发展科技§f"))
										.append(Component.newline()).append(Component.text("*§b合成需要科技点数的资源和装备§f"))))
								.clickEvent(ClickEvent.suggestCommand("/t oik sci info"))));
		event.getStatusScreen().addComponentOf("OIKMC_hall",
				Component.empty()
						.append(Component.text(hoverFormat("§5[哲学学院:+" + Main.getNewCulPoints(event.getTown()) + "]"))
								.hoverEvent(HoverEvent.showText(Component.text("输入/t oik cu info了解详情")
										.append(Component.newline()).append(Component.text("你可以建设哲学学院用以提升文化点数，文化点数进行以下行动："))
										.append(Component.newline()).append(Component.text("*§b发展市政§f"))
										.append(Component.newline()).append(Component.text("*§b收取文化（火星）税§f"))
										.append(Component.newline()).append(Component.text("*§b伪造公国地区的宣称§f"))
								))
								.clickEvent(ClickEvent.suggestCommand("/t oik cu info"))));

		event.getStatusScreen().addComponentOf("OIKMC_tax",
				Component.empty()
						.append(Component.text(hoverFormat("§a可收取铸币税:" + OIKMCTownCommandAddon.getTownChurchTax(event.getTown())))
								.hoverEvent(HoverEvent.showText(Component.text("输入/t oik gathertax standard收取铸币税")))));
		int[] n = Main.getDetailedRecruitLimit(event.getTown());
		event.getStatusScreen().addComponentOf("OIKMC_recruit",
				Component.empty()
						.append(Component.text(hoverFormat(
										"§6征召兵["
												+ Utils.transferStringAToArray(MetaDataUtil.getRecruits(event.getTown())).length
												+ "/" + Main.getRecruitLimit(event.getTown()) + "]"
								        )
								)
								.hoverEvent(HoverEvent.showText(Component.text("这是你所辖城市的征召兵情况，城主或军事领袖可以通过输入/t oik recruit 動员征召兵")
										.append(Component.newline()).append(Component.text("§6轻步兵§f-来自农田(城市基础+1) §b" + n[3] / 1000))

										.append(Component.newline()).append(Component.text("§6轻骑兵§f-来自马厩 §b" + n[2] / 1000))
										.append(Component.newline()).append(Component.text("§6弩箭手§f-来自要塞 §b" + n[1] / 1000))
										.append(Component.newline()).append(Component.text("§6重步兵§f-来自兵营 §b" + n[0] / 1000))))));



	}


}
