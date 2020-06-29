package net.faiden.getout.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.faiden.getout.GamePlayer;
import net.faiden.getout.GetOut;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.rank.Rank;
import net.golema.database.support.GameStatus;
import net.golema.database.support.builder.TitleBuilder;
import net.golema.database.support.servers.SwitchServer;

public class WinManager {

	/**
	 * Détection de la Victoire.
	 */
	public WinManager() {
		if (GameStatus.isStatus(GameStatus.GAME)) {

			// Les joueurs se sont tous déconnectés.
			if (Bukkit.getOnlinePlayers().size() == 0) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
				return;
			}

			// Compteur pour la détection de la Win.
			List<Player> playerInGame = new ArrayList<Player>();
			for (Player playerOnline : Bukkit.getOnlinePlayers()) {
				if (!GetOut.getGamePlayer(playerOnline).isSpectator()) {
					playerInGame.add(playerOnline);
				}
			}

			// Sécurité pour le serveur vide.
			if (playerInGame.isEmpty()) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
				return;
			}

			// Récupération du Gagnant.
			if (playerInGame.size() == 1) {

				GameStatus.setStatus(GameStatus.FINISH);

				// Détection de Gagnant.
				Player playerWinner = playerInGame.get(0);
				GolemaPlayer golemaPlayer = GolemaPlayer.getGolemaPlayer(playerWinner);
				Rank rank = golemaPlayer.getRank();
				if (rank != null) {
					GolemaPlayer golemaPlayerWinner = GolemaPlayer.getGolemaPlayer(playerWinner);
					GamePlayer gamePlayerWinner = GetOut.getGamePlayer(playerWinner);
					gamePlayerWinner.setWinner(true);
					Bukkit.broadcastMessage(GetOut.instance.prefixGame + ChatColor.GREEN
							+ " Félicitations, victoire de " + rank.getChatColor() + rank.getPrefix()
							+ getSpaceAllow(rank) + playerWinner.getName() + ChatColor.GREEN + " !");
					new TitleBuilder("", ChatColor.GOLD + "Victoire de " + rank.getChatColor() + rank.getPrefix()
							+ getSpaceAllow(rank) + playerWinner.getName() + ChatColor.GOLD + " !").broadcast();

					// Envoie des Coins au target.
					float coinsReward = ((float) (5.0f * golemaPlayerWinner.getRank().getCoinsMultiplicator()));
					playerWinner.sendMessage(ChatColor.GRAY + "Gain: " + ChatColor.YELLOW + "+" + coinsReward + " ⛃ "
							+ ChatColor.GOLD + " (Victoire)");
					gamePlayerWinner.setCoinsWin(gamePlayerWinner.getCoinsWin() + coinsReward);
					// golemaPlayerWinner.addCoins(Currency.MAINCOINS, coinsReward);

					// Envoie des Crédits au target.
					float creditsReward = ((float) (1.0f * golemaPlayerWinner.getRank().getCoinsMultiplicator()));
					playerWinner.sendMessage(ChatColor.GRAY + "Gain: " + ChatColor.AQUA + "+" + creditsReward + " ✆ "
							+ ChatColor.GOLD + " (Victoire)");
					gamePlayerWinner.setCreditsWin(gamePlayerWinner.getCreditsWin() + creditsReward);
					// golemaPlayerWinner.addCoins(Currency.MAINCREDITS, creditsReward);

					// Envoie de l'Experience au target.
					int experience = 10;
					playerWinner.sendMessage(
							ChatColor.GRAY + "Experience: " + ChatColor.LIGHT_PURPLE + "+" + experience + " Points");
					// golemaPlayerWinner.addPointsGame(StarsGameType.ARCADE, experience);
				}

				// Envoie du nouveau Scoreboard et message de Récompenses.
				for (Player playerOnline : Bukkit.getOnlinePlayers()) {

					// Jouer un Son
					playerOnline.playSound(playerOnline.getLocation(), Sound.LEVEL_UP, 2.0f, 1.0f);

					// Scoreboard
					GamePlayer gamePlayer = GetOut.getGamePlayer(playerOnline);
					if (gamePlayer != null) {
						gamePlayer.getScoreboardSign().setLine(9, "§d");
						gamePlayer.getScoreboardSign().setLine(8, ChatColor.GOLD + "Fin de la partie,");
						gamePlayer.getScoreboardSign().setLine(7, ChatColor.GOLD + "faites " + ChatColor.YELLOW + ""
								+ ChatColor.BOLD + "/hub" + ChatColor.GOLD + ".");
						gamePlayer.getScoreboardSign().setLine(6, "§c");
						gamePlayer.getScoreboardSign().setLine(5, ChatColor.GRAY + "Gagnant:");
						gamePlayer.getScoreboardSign().setLine(4, ChatColor.AQUA + playerWinner.getName());
						gamePlayer.getScoreboardSign().setLine(3, "§b");
					}

					// Message de Récompenses.
					GetOut.getGamePlayer(playerOnline).rewardMessage();
				}

				// Help pour retourner au Hub.
				Bukkit.broadcastMessage("");
				Bukkit.broadcastMessage(
						ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "?" + ChatColor.WHITE + "] " + ChatColor.GRAY
								+ "Faites " + ChatColor.GREEN + "/hub " + ChatColor.GRAY + "pour retourner au hub.");

				// Fin de Game.
				Bukkit.getScheduler().runTaskLater(GetOut.instance, new Runnable() {
					@Override
					public void run() {
						for (Player playerOnline : Bukkit.getOnlinePlayers()) {
							SwitchServer.sendPlayerToLobby(playerOnline, true);
						}
						Bukkit.getScheduler().runTaskLater(GetOut.instance, new Runnable() {
							@Override
							public void run() {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
							}
						}, 20 * 5L);

					}
				}, 20 * 10L);
			}
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