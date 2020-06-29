package net.faiden.getout.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.faiden.getout.GamePlayer;
import net.faiden.getout.GetOut;
import net.faiden.getout.runnables.LaunchRunnable;
import net.faiden.getout.runnables.LuckyRunnable;
import net.golema.api.utils.PlayerUtils;
import net.golema.database.support.GameStatus;
import net.golema.database.support.boards.TeamsTagsManager;
import net.golema.database.support.builder.TitleBuilder;

public class GameManager {

	private List<Location> stampLocation = new ArrayList<Location>();

	/**
	 * Lancement de la partie.
	 */
	public GameManager() {

		// Initialisation de la partie.
		GameStatus.setStatus(GameStatus.GAME);
		GetOut.instance.registerLocations();
		GetOut.instance.canMove = false;

		// Paramètres des Joueurs.
		for (Player playerOnline : Bukkit.getOnlinePlayers()) {
			if (!(GetOut.getGamePlayer(playerOnline).isSpectator())) {
				// Récupération du GamePlayer.
				GamePlayer gamePlayer = GetOut.getGamePlayer(playerOnline);
				gamePlayer.setHasPlayedTheGame(true);

				// Gestion de la téléportation du Joueur.
				Location locationTP = GetOut.instance.spawnLocations
						.get(new Random().nextInt(GetOut.instance.spawnLocations.size()));
				playerOnline.teleport(locationTP);
				this.stampLocation.add(locationTP);
				GetOut.instance.spawnLocations.remove(locationTP);

				// Initialisation du Joueur.
				playerOnline.playSound(playerOnline.getLocation(), Sound.NOTE_PLING, 2.0f, 2.0f);
				PlayerUtils.clearInventory(playerOnline);
				playerOnline.setGameMode(GameMode.ADVENTURE);
				playerOnline.setMaxHealth(20.0d);
				playerOnline.setHealth(20.0d);
				playerOnline.setFoodLevel(20);
				playerOnline.setWalkSpeed(0.0f);
				playerOnline.setLevel(0);
				playerOnline.setFlying(false);
				playerOnline.setAllowFlight(false);
				TeamsTagsManager.setNameTag(playerOnline, "§1§b" + new Random().nextInt(999999999), "§1§b");

				// Mise en place du Scoreboard de la partie.
				gamePlayer.getScoreboardSign().setLine(8,
						ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + GetOut.instance.countPlayerInGame());
				gamePlayer.getScoreboardSign().setLine(7,
						ChatColor.GRAY + "Kills: " + ChatColor.WHITE + gamePlayer.getKills());
				gamePlayer.getScoreboardSign().setLine(6, "§c");
				gamePlayer.getScoreboardSign().setLine(5, ChatColor.YELLOW + "" + ChatColor.BOLD + "✪ Lucky Bonus ✪");
				gamePlayer.getScoreboardSign().setLine(4,
						ChatColor.GRAY + "Nouveau dans: " + ChatColor.WHITE + LuckyRunnable.luckyTimer + "s");
				gamePlayer.getScoreboardSign().setLine(3, "§b");

				// Title informatif du démarrage.
				new TitleBuilder(ChatColor.YELLOW + "GetOut", ChatColor.AQUA + "Téléportation en cours...")
						.send(playerOnline);
			}
		}

		// Remise à zéro des Locations.
		for (Location locationStamp : stampLocation) {
			GetOut.instance.spawnLocations.add(locationStamp);
		}

		// Annonce du Lancement de la partie et démarrage du LaunchRunnable.
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "?" + ChatColor.WHITE
				+ "] La partie a désormais été démarrée. Vous serez en mesure de bouger dans " + ChatColor.LIGHT_PURPLE
				+ "5 secondes" + ChatColor.WHITE + ".");
		new LaunchRunnable().runTaskTimer(GetOut.instance, 0L, 20L);
	}
}