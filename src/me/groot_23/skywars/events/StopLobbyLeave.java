package me.groot_23.skywars.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.MetadataValue;

import me.groot_23.skywars.Main;
import me.groot_23.skywars.util.SWconstants;

public class StopLobbyLeave implements Listener {

	Main plugin;

	public StopLobbyLeave(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}

	private void stopWorld(World w) {
		// if world is lobby
		if (w.getName().startsWith(SWconstants.SW_GAME_WORLD_PREFIX)) {
			plugin.arenaProvider.stopLobby(w);
			System.out.println("[Skywars] lobby stopped: " + w.getName());
		}
		// if world is edited
		else {
			List<MetadataValue> meta = w.getMetadata("skywars_edit_world");
			if(!meta.isEmpty()) {
				if(meta.get(0).asBoolean()) {
					Bukkit.unloadWorld(w, true);
					System.out.println("[Skywars] unloaded world after edit: " + w.getName());
				}
			}
		}
		// else we don't know if it is supposed to be stopped
		// -> don't stop it, maybe another plugin wants it to keep loaded
	}


	@EventHandler
	public void playerLeave(PlayerChangedWorldEvent e) {
		World w = e.getFrom();
		if (w.getPlayers().size() == 0) {
			stopWorld(w);
		}

	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		World w = e.getPlayer().getWorld();
		// quit event seems to be fired before the player disconnects
		if (w.getPlayers().size() == 1) {
			stopWorld(w);
		}
	}
}
