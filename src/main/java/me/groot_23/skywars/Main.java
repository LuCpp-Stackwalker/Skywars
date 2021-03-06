package me.groot_23.skywars;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import me.groot_23.pixel.Pixel;
import me.groot_23.pixel.commands.KitCommands;
import me.groot_23.pixel.commands.LangCommand;
import me.groot_23.pixel.commands.MarkerCommand;
import me.groot_23.pixel.game.Lobby;
import me.groot_23.pixel.game.LobbyCreator;
import me.groot_23.pixel.kits.KitApi;
import me.groot_23.pixel.language.LanguageApi;
import me.groot_23.pixel.language.LanguageFolder;
import me.groot_23.pixel.player.DataManager;
import me.groot_23.pixel.util.ResourceExtractor;
import me.groot_23.pixel.util.Utf8Config;
import me.groot_23.skywars.commands.KitShop;
import me.groot_23.skywars.commands.SWchest;
import me.groot_23.skywars.commands.SWedit;
import me.groot_23.skywars.commands.SWleave;
import me.groot_23.skywars.commands.SWmaps;
import me.groot_23.skywars.commands.SWset;
import me.groot_23.skywars.commands.SWstart;
import me.groot_23.skywars.events.ChestEvents;
import me.groot_23.skywars.events.StopLobbyLeave;
import me.groot_23.skywars.game.SkyLobby;
import me.groot_23.skywars.util.Util;

public class Main extends JavaPlugin {

	public static String chatPrefix;
	
	public static LanguageFolder langFolder;

	private static Main instance;

	@Override
	public void onEnable() {
		firstStart();
		instance = this;

		Pixel.registerGame("skywars-solo", new LobbyCreator() {
			@Override
			public Lobby createGame(String map) {
				return new SkyLobby("skywars-solo", map, 1, 8, 1, 30*20);
			}
		});
		Pixel.registerGame("skywars-duo", new LobbyCreator() {
			@Override
			public Lobby createGame(String map) {
				return new SkyLobby("skywars-duo", map, 2, 8, 2, 30*20);
			}
		});
		Utf8Config cfg = new Utf8Config();
		Utf8Config cfg2 = new Utf8Config();
		try {
			cfg.load(new File(this.getDataFolder(), "groups.yml"));
			cfg2.load(new File(this.getDataFolder(), "worlds.yml"));
			for (String s : cfg.getStringList("default")) {
				Pixel.registerGameMap("skywars-solo", s, cfg2.getInt(s + ".maxPlayers", 8));
				Pixel.registerGameMap("skywars-duo", s, cfg2.getInt(s + ".maxPlayers", 8));
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		langFolder = LanguageApi.addLanguageFolder(new File(getDataFolder(), "lang"));

		File kitFile = new File(getDataFolder(), "kits.yml");
		KitApi.loadKits(kitFile, "skywars");
		
		File kitUnlockFile = new File(getDataFolder(), "kit_unlock.yml");
		try {
			kitUnlockFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		DataManager.loadFile(kitUnlockFile, "skywars_kit_unlock");
		KitApi.setUnlockedDataId("skywars", "skywars_kit_unlock");

		new SWleave(this, "swleave");
		new SWedit(this);
		new SWmaps(this);
		new SWchest(this);
		new SWset(this);
		new SWstart(this);

		new KitCommands(this, langFolder, kitFile, "skywars", "swkits", "skywars.kits");
		new LangCommand(this, langFolder, "swlang", "skywars.lang");
		new MarkerCommand(this, "swspawns", "skywars.spawns", "sky_spawn");

		new StopLobbyLeave(this);
		new ChestEvents(this);
		
		new KitShop(this, "shop");
		
		chatPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("chat_prefix"));

	}

	public void firstStart() {
		File resources = Util.getDataPackResources("skywars");
		File lootTables = new File(resources, "loot_tables");
		if (!resources.exists()) {
			lootTables.mkdirs();
			getLogger().info("[Skywars] Extracting datapack-");
			ResourceExtractor.extractResources("resources/loot_tables", lootTables.toPath(), false, this.getClass());
		}
		saveDefaultConfig();

		ResourceExtractor.extractResources("resources/data", getDataFolder().toPath(), false, this.getClass());
	}

	public static Main getInstance() {
		return instance;
	}
}
