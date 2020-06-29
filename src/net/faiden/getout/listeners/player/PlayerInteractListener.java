package net.faiden.getout.listeners.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.golema.database.support.GameStatus;
import net.golema.database.support.servers.SwitchServer;

public class PlayerInteractListener implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		// Initialisation de la variable du Joueur.
		Player player = event.getPlayer();

		// Gestion de l'Interaction.
		if ((event.getItem() == null) || (event.getItem().getType().equals(Material.AIR)))
			return;
		if ((event.getAction().equals(Action.RIGHT_CLICK_AIR))
				|| (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {

			// Interaction en fonction des Status.
			switch (GameStatus.getStatus()) {
			case LOBBY:
				switch (event.getItem().getType()) {
				case BED:
					SwitchServer.sendPlayerToLobby(player, false);
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
	}
}