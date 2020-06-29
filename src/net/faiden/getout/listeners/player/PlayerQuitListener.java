package net.faiden.getout.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.faiden.getout.GamePlayer;
import net.faiden.getout.GetOut;
import net.faiden.getout.manager.WinManager;
import net.golema.api.builder.titles.ActionBarBuilder;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.support.GameStatus;

public class PlayerQuitListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {

		// Initialisation de Variables.
		Player player = event.getPlayer();
		GamePlayer gamePlayer = GetOut.getGamePlayer(player);
		GolemaPlayer golemaPlayer = gamePlayer.getGolemaPlayer();

		// Param�tres du Left.
		gamePlayer.getScoreboardSign().destroy();
		GetOut.gamePlayersMap.remove(player.getName());
		event.setQuitMessage(null);

		// Param�tres suivant le Status.
		switch (GameStatus.getStatus()) {
		case LOBBY:
			Bukkit.getOnlinePlayers().forEach(playerOnline -> {
				new ActionBarBuilder(golemaPlayer.getRank().getChatColor() + golemaPlayer.getRank().getPrefix() + " "
						+ player.getName() + ChatColor.YELLOW + " a quitté la partie, " + ChatColor.AQUA
						+ (Bukkit.getOnlinePlayers().size() - 1) + " Joueur(s) " + ChatColor.YELLOW + "en partie.")
								.sendTo(playerOnline);
			});
			break;
		case GAME:
			new WinManager();
			break;
		default:
			break;
		}
	}
}