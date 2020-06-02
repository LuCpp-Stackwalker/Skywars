package me.groot_23.skywars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import me.groot_23.skywars.util.Util;

public class SkywarsScoreboard {
	
	private Main plugin;
	
	private Scoreboard board;
	private Objective objective;
	
	public SkywarsScoreboard(Main plugin) {
		this.plugin = plugin;
		init();
	}
	
	public void init() {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		
		objective = board.registerNewObjective("skywars", "dummy", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "SKYWARS");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void resetObjective() {
		objective.unregister();
		
		objective = board.registerNewObjective("skywars", "dummy", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "SKYWARS");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void resetKills(Player player) {
		player.setMetadata("Skywars_kills", new FixedMetadataValue(plugin, 0));
	}
	
	public void addKill(Player player) {
		int kills = player.getMetadata("Skywars_kills").get(0).asInt();
		player.setMetadata("Skywars_kills", new FixedMetadataValue(plugin, kills + 1));
	}
	
	public void updatePreGame(World world, int maxPlayers, int timeLeft) {
		int playerCount = world.getPlayers().size();
		for(Player player : world.getPlayers()) {
			resetObjective();
			
			objective.getScore(Util.repeat(20, " ")).setScore(6);
			objective.getScore(Integer.toString(playerCount) + "/" + maxPlayers + " Spieler").setScore(5);
			objective.getScore(ChatColor.GREEN + Util.repeat(20, " ")).setScore(4);
			objective.getScore("Zeit bis zum Start: " + timeLeft).setScore(3);
			objective.getScore(ChatColor.RED + Util.repeat(20, " ")).setScore(2);
			objective.getScore(ChatColor.YELLOW + "Groot23.mcserv.me").setScore(1);
			
			//board.resetScores(player);
			player.setScoreboard(board);
		}
	}
	
	public void updateGame(World world, String nextEvent, int timeTillEvent) {
		int playersLeft = 0;
		for(Player p : world.getPlayers()) {
			if(p.getGameMode() == GameMode.SURVIVAL) {
				playersLeft++;
			}
		}
		
		for(Player player : world.getPlayers()) {
			resetObjective();
			int kills = player.getMetadata("Skywars_kills").get(0).asInt();
			
			objective.getScore(Util.repeat(20, " ")).setScore(9);
			objective.getScore(ChatColor.GREEN + "N�chstes Event:").setScore(8);
			objective.getScore(nextEvent + ": " + Util.minuteSeconds(timeTillEvent)).setScore(7);
			objective.getScore(ChatColor.BLUE + Util.repeat(20, " ")).setScore(6);
			objective.getScore("Players left: " + playersLeft).setScore(5);
			objective.getScore(ChatColor.GREEN + Util.repeat(20, " ")).setScore(4);
			objective.getScore(ChatColor.RED + "Kills: " + kills).setScore(3);
			objective.getScore(ChatColor.RED + Util.repeat(20, " ")).setScore(2);
			objective.getScore(ChatColor.YELLOW + "Groot23.mcserv.me").setScore(1);
			
			//board.resetScores(player);
			player.setScoreboard(board);
		}
	}
		
}