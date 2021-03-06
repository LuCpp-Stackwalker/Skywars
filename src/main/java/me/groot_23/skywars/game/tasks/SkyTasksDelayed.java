package me.groot_23.skywars.game.tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.display.BossBarApi;
import me.groot_23.pixel.game.Game;
import me.groot_23.pixel.game.task.PixelTaskDelayed;
import me.groot_23.pixel.kits.Kit;
import me.groot_23.pixel.kits.KitApi;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.PixelLangKeys;
import me.groot_23.pixel.player.team.GameTeam;
import me.groot_23.skywars.Main;
import me.groot_23.skywars.game.SkyGame;
import me.groot_23.skywars.language.LanguageKeys;
import me.groot_23.skywars.scoreboard.SkywarsScoreboard;
import me.groot_23.skywars.util.SWconstants;
import me.groot_23.skywars.world.SkyArena;

public class SkyTasksDelayed {

	public static abstract class Base extends PixelTaskDelayed {
		// shortcuts
		public SkyGame game;
		public SkyArena arena;
		public World world;

		public Base(Game game) {
			this.game = (SkyGame) game;
			this.arena = this.game.skyArena;
			this.world = arena.getWorld();
		}
	}

	public static class StartGame extends Base {
		public static final String id = "startGame";

		public StartGame(Game game) {
			super(game);
		}

		@Override
		public void run() {
			arena.removeGlassSpawns();
			game.arena.getWorld().setPVP(true);
			for (Player player : world.getPlayers()) {
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				// remove fall damage
				player.setFallDistance(-1000);
				Kit kit = KitApi.getSelectedKit(player, "skywars");
				if (kit == null) {
					kit = KitApi.getKits("skywars").get(0);
				}
				kit.applyToPlayer(player);

				String started = ChatColor.GREEN + LanguageApi.getTranslation(player, LanguageKeys.STARTED);
				player.sendMessage(Main.chatPrefix + started);
				player.sendTitle(started,
						ChatColor.LIGHT_PURPLE + LanguageApi.getTranslation(player, LanguageKeys.FIGHT_BEGINS), 3, 20,
						3);
			}

			game.taskManager.addTask(new Draw(game), SWconstants.LENGTH_OF_GAME * 20, Draw.id);
			game.taskManager.addTask(new Refill(game, game.refillTime * 20), game.refillTime * 20, Refill.id);
			game.taskManager.addTask(new DeathMatch(game), game.deathMatchBegin * 20, DeathMatch.id);
		}
	}

	public static class DeathMatch extends Base {
		public static final String id = "deathMatch";

		public DeathMatch(Game game) {
			super(game);
		}

		@Override
		public void run() {
			arena.shrinkBorder(game.deathMatchBorderShrinkTime);
			BossBar newbb = Bukkit.createBossBar(
					ChatColor.YELLOW + "Überlebe das " + ChatColor.DARK_RED + "Death Match" + ChatColor.GRAY + "!",
					BarColor.RED, BarStyle.SOLID);
			for (Player p : game.players) {
				p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Death Match!",
						ChatColor.RED + "" + ChatColor.BOLD + LanguageApi.getTranslation(p, LanguageKeys.GO_TO_MID), 3,
						100, 3);
				BossBarApi.removePlayer(p);
				BossBarApi.addPlayer(newbb, p);
			}
			game.taskManager.getRepeated("game1").stop();
		}
	}

	public static class Refill extends Base {
		public static final String id = "refill";

		long delay;
		public Refill(Game game, long delay) {
			super(game);
			this.delay = delay;
		}

		@Override
		public void run() {
			arena.refillChests();
			game.taskManager.addTask(new Refill(game, delay), delay  + game.refillTimeChange * 20, id);
			for (Player player : game.players) {
				player.sendMessage(Main.chatPrefix + LanguageApi.getTranslation(player, LanguageKeys.CHESTS_REFILLED));
			}
		}
	}

	public static class Draw extends Base {
		public static final String id = "draw";

		public Draw(Game game) {
			super(game);
		}

		@Override
		public void run() {
			for (Player p : game.players) {
				p.sendTitle("UNENTSCHIEDEN", "", 8, 30, 8);
			}
			game.taskManager.addTask(new EndGame(game), 200, EndGame.id);
		}
	}

	public static class EndGame extends Base {
		public static final String id = "endGame";

		public EndGame(Game game) {
			super(game);
		}

		@Override
		public void run() {
			game.endGame();
		}

	}
}
