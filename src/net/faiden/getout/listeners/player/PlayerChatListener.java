package net.faiden.getout.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.faiden.getout.GetOut;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.rank.Rank;
import net.golema.database.support.GameStatus;

public class PlayerChatListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent event) {

		// Initialisation de variables.
		Player player = event.getPlayer();
		GolemaPlayer golemaPlayer = GolemaPlayer.getGolemaPlayer(player);
		String message = event.getMessage();
		event.setCancelled(true);

		// (t)Chat en fonction du Status.
		switch (GameStatus.getStatus()) {
		case LOBBY:
			Bukkit.broadcastMessage(golemaPlayer.getRank().getChatColor() + golemaPlayer.getRank().getPrefix()
					+ getSpaceAllow(golemaPlayer.getRank()) + player.getName() + ChatColor.WHITE + ": " + message);
			break;
		case GAME:

			// Détection si le Joueur est en mode Spectateur.
			if (GetOut.getGamePlayer(player).isSpectator()) {
				for (Player playerOnline : Bukkit.getOnlinePlayers()) {
					if (GetOut.getGamePlayer(playerOnline).isSpectator()) {
						playerOnline.sendMessage(ChatColor.GRAY + "[SPEC] " + player.getName() + ": " + message);
					}
				}
				return;
			}

			// Envoie du message d'un Joueur qui est en jeu.
			Bukkit.broadcastMessage(golemaPlayer.getRank().getChatColor() + golemaPlayer.getRank().getPrefix()
					+ getSpaceAllow(golemaPlayer.getRank()) + player.getName() + ChatColor.WHITE + ": " + message);
			break;
		case FINISH:
			Bukkit.broadcastMessage(golemaPlayer.getRank().getChatColor() + golemaPlayer.getRank().getPrefix()
					+ getSpaceAllow(golemaPlayer.getRank()) + player.getName() + ChatColor.WHITE + ": " + message);
			break;
		default:
			break;
		}
	}

	/**
	 * Espace en fonction du Rank.
	 * 
	 * @param rank
	 * @return
	 */
	private String getSpaceAllow(Rank rank) {
		return rank.getIdentificatorName().equals(Rank.PLAYER.getIdentificatorName()) ? "" : " ";
	}
}