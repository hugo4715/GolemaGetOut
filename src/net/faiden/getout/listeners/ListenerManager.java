package net.faiden.getout.listeners;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import net.faiden.getout.listeners.entity.EntityDamageListener;
import net.faiden.getout.listeners.player.PlayerChatListener;
import net.faiden.getout.listeners.player.PlayerDropItemListener;
import net.faiden.getout.listeners.player.PlayerFoodLevelListener;
import net.faiden.getout.listeners.player.PlayerInteractListener;
import net.faiden.getout.listeners.player.PlayerJoinListener;
import net.faiden.getout.listeners.player.PlayerMoveListener;
import net.faiden.getout.listeners.player.PlayerPickupItemListener;
import net.faiden.getout.listeners.player.PlayerQuitListener;
import net.faiden.getout.listeners.world.WeatherChangeListener;

public class ListenerManager {

	public Plugin plugin;
	public PluginManager pluginManager;

	/**
	 * Constructeur du ListenerManager.
	 * 
	 * @param plugin
	 */
	public ListenerManager(Plugin plugin) {
		this.plugin = plugin;
		this.pluginManager = Bukkit.getPluginManager();
	}

	// Liste des événements à recevoir.
	public void registerListeners() {

		// Listener concernant les Entity. 
		pluginManager.registerEvents(new EntityDamageListener(), plugin);

		// Listener concernant les Player. 
		pluginManager.registerEvents(new PlayerJoinListener(), plugin);
		pluginManager.registerEvents(new PlayerQuitListener(), plugin);
		pluginManager.registerEvents(new PlayerFoodLevelListener(), plugin);
		pluginManager.registerEvents(new PlayerDropItemListener(), plugin);
		pluginManager.registerEvents(new PlayerMoveListener(), plugin);
		pluginManager.registerEvents(new PlayerPickupItemListener(), plugin);
		pluginManager.registerEvents(new PlayerChatListener(), plugin);
		pluginManager.registerEvents(new PlayerInteractListener(), plugin);
		
		// Listener concernant les World.
		pluginManager.registerEvents(new WeatherChangeListener(), plugin);
	}
}