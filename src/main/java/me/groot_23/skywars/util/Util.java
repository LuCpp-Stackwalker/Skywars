package me.groot_23.skywars.util;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import me.groot_23.skywars.Main;

public class Util {

	public static String chat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static String getWorldPath(String world) {
		return Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world;
	}
	
	public static File getDataPackResources(String namespace) {
		return new File(Bukkit.getWorlds().get(0).getWorldFolder().getPath() + File.separator + "datapacks" +
				File.separator + "bukkit" + File.separator + "data" + File.separator + namespace);
	}
	
	public static String repeat(int count, String with) {
	    return new String(new char[count]).replace("\0", with);
	}
	
	public static String minuteSeconds(int seconds) {
		String minuteStr = Integer.toString(seconds / 60);
		int secondPart = seconds % 60;
		String secondStr = (secondPart < 10 ? "0" : "") + secondPart;
		return minuteStr + ":" + secondStr;
	}
	
	public static int secondsFromStr(String str) {
		int i = 0;
		for(char c : str.toCharArray()) {
			if(c == ':') break;
			i++;
		}
		int minutes = Integer.parseInt(str.substring(0, i));
		int seconds = Integer.parseInt(str.substring(i + 1));
		return 60 * minutes + seconds;
	}
	
	public static void resetPlayer(Player player) {
		BossBar bb = Bukkit.getBossBar(new NamespacedKey(Main.getInstance(), "deathMatch"));
		if(bb != null) bb.removePlayer(player);
		player.getInventory().clear();
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setLevel(0);
		for(PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}
	
	
//	public static void spectatorWithInventory(Player player) {
//		player.setGameMode(GameMode.ADVENTURE);
//		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 0xffff, 1, false, false));
//	}
}
