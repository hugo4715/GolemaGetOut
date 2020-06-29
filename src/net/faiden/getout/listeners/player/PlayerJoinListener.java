package net.faiden.getout.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.faiden.getout.GamePlayer;
import net.faiden.getout.GetOut;
import net.faiden.getout.runnables.LobbyRunnable;
import net.golema.api.builder.titles.ActionBarBuilder;
import net.golema.api.utils.PlayerUtils;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.support.GameStatus;
import net.golema.database.support.MessagesUtils;
import net.golema.database.support.builder.TitleBuilder;
import net.golema.database.support.builder.items.ItemBuilder;

public class PlayerJoinListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {

		// Initialisation des Variables.
		Player player = event.getPlayer();
		GamePlayer gamePlayer = GetOut.getGamePlayer(player);
		GolemaPlayer golemaPlayer = gamePlayer.getGolemaPlayer();
		event.setJoinMessage(null);

		// Vérifier si la partie n'a pas déjà démarrée.
		if (!(GameStatus.isStatus(GameStatus.LOBBY))) {
			gamePlayer.setSpectator();

			// Message pour un Spectateur qui rejoins.
			player.sendMessage("");
			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "ATTENTION" + ChatColor.GRAY + "│ "
					+ ChatColor.YELLOW + "Il vous est impossible de jouer !");
			player.sendMessage(ChatColor.AQUA + "Vous avez rejoint la partie en mode spectateur.");
			player.sendMessage("");
			return;
		}

		// Informations message sur les Statistiques.
		// player.sendMessage(GameMessages.getCustomGameLine(net.md_5.bungee.api.ChatColor.GRAY,
		// net.md_5.bungee.api.ChatColor.AQUA, net.md_5.bungee.api.ChatColor.GRAY));
		player.sendMessage(" ");
		golemaPlayer.sendCenteredMessage(
				ChatColor.WHITE + "[" + ChatColor.AQUA + "?" + ChatColor.WHITE + "] Informations sur vos statistiques");
		player.sendMessage(" ");
		// golemaPlayer.sendCenteredMessage("§eVous possèdez " + ChatColor.GOLD + "" +
		// ChatColor.BOLD +
		// golemaPlayer.getStarNumberAboutStarGame(golemaPlayer.getPointsGame(StarsGameType.ARCADE))
		// + " ✯"
		// + ChatColor.YELLOW + " sur les jeux Arcades.");
		golemaPlayer.sendCenteredMessage(ChatColor.GRAY + "Classement : " + ChatColor.AQUA
				+ "https://stats.golemamc.net/player/" + player.getName() + "/");
		player.sendMessage(" ");
		// player.sendMessage(GameMessages.getCustomGameLine(net.md_5.bungee.api.ChatColor.GRAY,
		// net.md_5.bungee.api.ChatColor.AQUA, net.md_5.bungee.api.ChatColor.GRAY));

		// Paramètres liés aux Designs de la partie.
		new TitleBuilder(ChatColor.YELLOW + "GetOut", ChatColor.RED + "Arcade Games").send(player);
		new ActionBarBuilder(ChatColor.GRAY + "Développé par : " + ChatColor.DARK_GREEN + "Faiden").withStay(4)
				.sendTo(player);
		Bukkit.broadcastMessage(GetOut.instance.prefixGame + " " + golemaPlayer.getRank().getChatColor()
				+ golemaPlayer.getRank().getPrefix() + MessagesUtils.getRankSpace(golemaPlayer.getRank()) + player.getName()
				+ ChatColor.YELLOW + " a rejoint la partie ! " + ChatColor.GREEN + "("
				+ Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");
		// TeamsTagsManager.setNameTag(player,
		// golemaPlayer.getStarNumberAboutStarGame(golemaPlayer.getPointsGame(StarsGameType.ARCADE))
		// + "_" + new Random().nextInt(48485415),
		// ChatColor.GRAY + "[" +
		// golemaPlayer.getStarNumberAboutStarGame(golemaPlayer.getPointsGame(StarsGameType.ARCADE))
		// + "✫] " + golemaPlayer.getRank().getChatColor());

		// Paramètres de connexions à la partie du Joueur.
		player.setMaxHealth(20.0d);
		player.setHealth(20.0d);
		player.setFoodLevel(20);
		player.setWalkSpeed(0.2f);
		player.setLevel(LobbyRunnable.lobbyTimer);
		player.setGameMode(GameMode.ADVENTURE);
		player.teleport(gamePlayer.getLobbyLocation());
		player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0f, 1.0f);

		// Paramètres liés au Joueur lui-même.
		PlayerUtils.removeAllPotionEffect(player);
		PlayerUtils.clearInventory(player);
		player.getInventory()
				.setItem(8,
						new ItemBuilder().type(Material.BED).name(ChatColor.RED + "" + ChatColor.BOLD
								+ "Retourner au hub " + ChatColor.DARK_GRAY + " ▏ " + ChatColor.GRAY + " Clic-droit")
								.build());

		// Lancement du Timer pour le démarrage du Jeu.
		if ((Bukkit.getOnlinePlayers().size() >= GetOut.instance.minPlayers) && (!LobbyRunnable.isStarted)) {
			new LobbyRunnable().runTaskTimer(GetOut.instance, 0L, 20L);
			LobbyRunnable.isStarted = true;
		}
	}
}