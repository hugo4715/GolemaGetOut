package net.faiden.getout.runnables;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.faiden.getout.GetOut;
import net.faiden.getout.manager.GameManager;

public class LobbyRunnable extends BukkitRunnable {

	public static Integer lobbyTimer = 90;
	public static boolean isStarted = false;

	public LobbyRunnable() {
	}

	@Override
	public void run() {

		// S�curit� si le 'minPlayer' n'est plus respect�.
		if ((Bukkit.getOnlinePlayers().size() < GetOut.instance.minPlayers) && (!(GetOut.instance.forceStart))) {
			Bukkit.broadcastMessage(GetOut.instance.prefixGame + ChatColor.RED
					+ " Il n'y a pas assez de joueurs pour démarrer la partie!");
			lobbyTimer = 120;
			isStarted = false;
			this.cancel();
			return;
		}

		// Acc�l�ration du Timer si le serveur se remplit.
		if ((Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) && (lobbyTimer >= 20)) {
			lobbyTimer = 15;
		}

		// D�marrage de la partie.
		if (lobbyTimer == 0) {
			new GameManager();
			this.cancel();
			return;
		}

		// Envoie du Messsage d'arlerte sur le Temps restant.
		if ((lobbyTimer == 120) || (lobbyTimer == 90) || (lobbyTimer == 60) || (lobbyTimer == 30) || (lobbyTimer == 15)
				|| (lobbyTimer == 10) || ((lobbyTimer <= 5) && (lobbyTimer != 0))) {
			Bukkit.broadcastMessage(GetOut.instance.prefixGame + " " + ChatColor.YELLOW + "Début de la partie dans "
					+ ChatColor.GOLD + lobbyTimer + getSeconds(lobbyTimer) + ChatColor.YELLOW + ".");
			for (Player playerOnline : Bukkit.getOnlinePlayers()) {
				playerOnline.playSound(playerOnline.getLocation(), Sound.NOTE_PLING, 2.0f, 1.0f);
			}
		}

		// R�duction du Timer du LobbyRunnable.
		for (Player playerOnline : Bukkit.getOnlinePlayers()) {
			playerOnline.setLevel(lobbyTimer);
			GetOut.getGamePlayer(playerOnline).getScoreboardSign().setLine(3, "§7Lancement: §f"
					+ new SimpleDateFormat("mm:ss").format(new Date(LobbyRunnable.lobbyTimer * 1000)));
		}
		lobbyTimer--;
	}

	/**
	 * R�cup�rer la bonne String du temps.
	 * 
	 * @param timer
	 * @return
	 */
	private String getSeconds(Integer timer) {
		return timer == 1 ? " seconde" : " secondes";
	}
}