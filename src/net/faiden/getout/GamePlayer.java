package net.faiden.getout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.faiden.getout.listeners.player.PlayerMoveListener;
import net.faiden.getout.manager.WinManager;
import net.faiden.getout.runnables.LobbyRunnable;
import net.golema.api.builder.board.ScoreboardSign;
import net.golema.api.utils.PlayerUtils;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.support.GameStatus;
import net.golema.database.support.boards.TeamsTagsManager;
import net.golema.database.support.builder.JsonMessageBuilder;
import net.golema.database.support.builder.TitleBuilder;
import net.golema.database.support.builder.items.ItemBuilder;
import net.golema.database.support.utils.GolemaLogger;

public class GamePlayer {

	public Player player;
	private GolemaPlayer golemaPlayer;

	private Float coinsWin;
	private Float creditsWin;

	private int kills;
	private int expel;
	private int live;
	private int timePlayed;

	private boolean hasPlayedTheGame;
	private boolean isWinner;
	private boolean isSpectator;
	private ScoreboardSign scoreboardSign;

	public Location lobbyLocation;

	/**
	 * Constructeur du GamePlayer.
	 * 
	 * @param player
	 */
	public GamePlayer(Player player) {

		// Variables liés au Joueur.
		this.player = player;
		this.golemaPlayer = GolemaPlayer.getGolemaPlayer(player);

		// Statistiques de la partie.
		this.coinsWin = 0.0f;
		this.creditsWin = 0.0f;
		this.kills = 0;
		this.expel = 0;
		this.live = 10;
		this.timePlayed = 0;
		this.hasPlayedTheGame = false;
		this.isWinner = false;

		// Définir le mode spectateur d'un Joueur.
		if (GameStatus.isStatus(GameStatus.LOBBY)) {
			this.isSpectator = false;
		} else {
			this.isSpectator = true;
		}

		// Mise en place du Scoreboard.
		this.scoreboardSign = new ScoreboardSign(player, player.getName());
		this.makeScoreboard();

		// Définir la Location du Lobby.
		lobbyLocation = new Location(Bukkit.getWorld("world"), -2.5, 69, -133.5, (float) 90.0, (float) 0.0);

		// Log de création.
		GolemaLogger.logDebug("[GamePlayer] Created " + player.getName() + " succes.");
	}

	/**
	 * Créer le scoreboard du Joueur.
	 */
	private void makeScoreboard() {
		this.scoreboardSign
				.setObjectiveName(ChatColor.AQUA + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "≡ GetOut ≡");
		this.scoreboardSign.create();

		// Spectator
		if (isSpectator) {
			this.scoreboardSign.setLine(9, "§d");
			this.scoreboardSign.setLine(8, ChatColor.GRAY + "Faites " + ChatColor.RED + "/hub" + ChatColor.GRAY + ".");
			this.scoreboardSign.setLine(7, "§c");
			this.scoreboardSign.setLine(6, ChatColor.GOLD + "Vous êtes en mode");
			this.scoreboardSign.setLine(5, ChatColor.GOLD + "spectateur...");
			this.scoreboardSign.setLine(4, "§b");
			this.scoreboardSign.setLine(3,
					ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
			this.scoreboardSign.setLine(2,
					ChatColor.GRAY + "Map: " + ChatColor.WHITE + GetOut.instance.mapGameInfos.getMapName());
			this.scoreboardSign.setLine(1, "§a");
			this.scoreboardSign.setLine(0, ChatColor.YELLOW + "play.golemamc.net");

			// Player
		} else {
			this.scoreboardSign.setLine(9, "§d");
			this.scoreboardSign.setLine(8, "§7Joueurs: §f" + Bukkit.getOnlinePlayers().size() + ChatColor.GRAY + "/"
					+ ChatColor.WHITE + Bukkit.getMaxPlayers());
			this.scoreboardSign.setLine(7, "§c");
			this.scoreboardSign.setLine(6, ChatColor.GOLD + "En attente de");
			this.scoreboardSign.setLine(5, ChatColor.GOLD + "joueurs...");
			this.scoreboardSign.setLine(4, "§b");
			this.scoreboardSign.setLine(3, ChatColor.GRAY + "Lancement: " + ChatColor.WHITE
					+ new SimpleDateFormat("mm:ss").format(new Date(LobbyRunnable.lobbyTimer * 1000)));
			this.scoreboardSign.setLine(2,
					ChatColor.GRAY + "Map: " + ChatColor.WHITE + GetOut.instance.mapGameInfos.getMapName());
			this.scoreboardSign.setLine(1, "§a");
			this.scoreboardSign.setLine(0, ChatColor.YELLOW + "play.golemamc.net");
		}
	}

	/**
	 * Définir le joueur au status de Spectateur.
	 */
	public void setSpectator() {
		this.isSpectator = true;
		TeamsTagsManager.setNameTag(player, "§z§7SPECTATOR", ChatColor.GRAY + "[SPEC] ");
		player.setGameMode(GameMode.SPECTATOR);
	}

	/**
	 * Définir le Respawn d'un joueur.
	 */
	public void makeRespawn() {

		// Réinitialisation de certains paramètres.
		live--;
		PlayerUtils.removeAllPotionEffect(player);
		PlayerMoveListener.jumpList.remove(player);

		// Détecter si le joueur est éliminé.
		if (live == 0) {

			// Envoie du message de l'éliminatation du Joueur.
			if (GetOut.instance.playerTargetMap.get(player) != null) {
				Player targetPlayer = GetOut.instance.playerTargetMap.get(player);
				GolemaPlayer targetGolemaPlayer = GolemaPlayer.getGolemaPlayer(targetPlayer);
				GamePlayer targetGamePlayer = GetOut.getGamePlayer(targetPlayer);
				targetGamePlayer.setKills(targetGamePlayer.getKills() + 1);
				Bukkit.broadcastMessage(golemaPlayer.getRank().getChatColor() + player.getName() + ChatColor.YELLOW
						+ " (" + ChatColor.RED + "✖" + ChatColor.RED + " ❤" + ChatColor.YELLOW
						+ ") s'est fait éliminer par " + targetGolemaPlayer.getRank().getChatColor()
						+ targetPlayer.getName() + ChatColor.YELLOW + " (" + ChatColor.RED
						+ GetOut.getGamePlayer(targetPlayer).live + " ❤" + ChatColor.YELLOW + ")");

				// Envoie des Coins au target.
				float coinsReward = ((float) (3.0f * targetGolemaPlayer.getRank().getCoinsMultiplicator()));
				targetPlayer.sendMessage(ChatColor.GRAY + "Gain: " + ChatColor.YELLOW + "+" + coinsReward + " ⛃ "
						+ ChatColor.GOLD + " (Elimination de " + player.getName() + ")");
				targetGamePlayer.setCoinsWin(targetGamePlayer.getCoinsWin() + coinsReward);
				// targetGolemaPlayer.addCoins(Currency.MAINCOINS, coinsReward);

				// Envoie d'une nouvelle flêche au Joueur.
				ItemStack arrowItem = new ItemBuilder().type(Material.ARROW).amount(1)
						.name(ChatColor.WHITE + "Projectile").build();
				Integer arrowNumber = 0;
				for (ItemStack itemInventory : targetPlayer.getInventory().getContents()) {
					if ((itemInventory != null) && (!(itemInventory.getType().equals(Material.AIR)))
							&& (itemInventory.getType().equals(Material.ARROW))) {
						arrowNumber++;
					}
				}
				if (arrowNumber == 0) {
					targetPlayer.getInventory().setItem(17, arrowItem);
				} else {
					targetPlayer.getInventory().addItem(arrowItem);
				}

				GetOut.instance.playerTargetMap.remove(player);
			} else {
				Bukkit.broadcastMessage(golemaPlayer.getRank().getChatColor() + player.getName() + ChatColor.YELLOW
						+ " (" + ChatColor.RED + "✖" + ChatColor.RED + " ❤" + ChatColor.YELLOW + ") a été éliminé.");
			}

			// Définir le nouveau Scoreboard.
			this.scoreboardSign.setLine(9, "§d");
			this.scoreboardSign.setLine(8, ChatColor.GRAY + "Faites " + ChatColor.RED + "/hub" + ChatColor.GRAY + ".");
			this.scoreboardSign.setLine(7, "§c");
			this.scoreboardSign.setLine(6, ChatColor.GOLD + "Vous êtes en mode");
			this.scoreboardSign.setLine(5, ChatColor.GOLD + "spectateur...");
			this.scoreboardSign.setLine(4, "§b");
			this.scoreboardSign.setLine(3,
					ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());

			// Message pour un Spectateur qui est éliminé.
			player.sendMessage("");
			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "✖" + ChatColor.GRAY + "│ " + ChatColor.YELLOW
					+ "Vous avez été éliminé !");
			JsonMessageBuilder jsonMessageBuilder = new JsonMessageBuilder();
			jsonMessageBuilder.newJComp(ChatColor.AQUA + "Souhaitez-vous rejouer ? ").build(jsonMessageBuilder);
			jsonMessageBuilder.newJComp(ChatColor.GRAY + "[" + ChatColor.GREEN + "Rejouer - ➲" + ChatColor.GRAY + "]")
					.addCommandExecutor("/lobby").addHoverText(ChatColor.YELLOW + "Rejoindre une nouvelle partie.")
					.build(jsonMessageBuilder);
			jsonMessageBuilder.send(player);
			player.sendMessage("");

			// Envoie d'un effet de mort à tous.
			for (Player playerOnline : Bukkit.getOnlinePlayers()) {
				playerOnline.playSound(playerOnline.getLocation(), Sound.WITHER_DEATH, 1.0f, 1.0f);
			}

			// Initialiser le Mode Spectateur et détection de la Win.
			this.setSpectator();
			new WinManager();
			return;
		}

		// Paramètres du Joueur à Update.
		player.setMaxHealth(live * 2);
		player.setHealth(live * 2);
		player.teleport(
				GetOut.instance.spawnLocations.get(new Random().nextInt(GetOut.instance.spawnLocations.size())));
		TeamsTagsManager.setNameTag(player, getColorLiveTab() + new Random().nextInt(999999999), getColorLiveTab());
		new TitleBuilder("", ChatColor.GOLD + "Respawn...").send(player);

		// Envoyer un message lors de la chute de Joueur et Update des Kills.
		if (GetOut.instance.playerTargetMap.get(player) != null) {
			Player targetPlayer = GetOut.instance.playerTargetMap.get(player);
			GolemaPlayer targetGolemaPlayer = GolemaPlayer.getGolemaPlayer(targetPlayer);
			GamePlayer targetGamePlayer = GetOut.getGamePlayer(targetPlayer);
			targetGamePlayer.setExpel(targetGamePlayer.getExpel() + 1);
			targetPlayer.setLevel(targetGamePlayer.getExpel());
			Bukkit.broadcastMessage(golemaPlayer.getRank().getChatColor() + player.getName() + ChatColor.GRAY + " ("
					+ ChatColor.RED + live + ChatColor.RED + " ❤" + ChatColor.GRAY + ") s'est fait expulser par "
					+ targetGolemaPlayer.getRank().getChatColor() + targetPlayer.getName() + ChatColor.GRAY + " ("
					+ ChatColor.RED + GetOut.getGamePlayer(targetPlayer).live + " ❤" + ChatColor.GRAY + ")");
			GetOut.instance.playerTargetMap.remove(player);

			// Envoie d'une nouvelle flêche au Joueur.
			ItemStack arrowItem = new ItemBuilder().type(Material.ARROW).amount(1).name(ChatColor.WHITE + "Projectile")
					.build();
			Integer arrowNumber = 0;
			for (ItemStack itemInventory : targetPlayer.getInventory().getContents()) {
				if ((itemInventory != null) && (!(itemInventory.getType().equals(Material.AIR)))
						&& (itemInventory.getType().equals(Material.ARROW))) {
					arrowNumber++;
				}
			}
			if (arrowNumber == 0) {
				targetPlayer.getInventory().setItem(17, arrowItem);
			} else {
				targetPlayer.getInventory().addItem(arrowItem);
			}
		}
	}

	/*
	 * Getter du GamePlayer.
	 */
	public GolemaPlayer getGolemaPlayer() {
		return golemaPlayer;
	}

	public ScoreboardSign getScoreboardSign() {
		return scoreboardSign;
	}

	public Float getCoinsWin() {
		return coinsWin;
	}

	public Float getCreditsWin() {
		return creditsWin;
	}

	public int getKills() {
		return kills;
	}

	public int getExpel() {
		return expel;
	}

	public int getLive() {
		return live;
	}

	public int getTimePlayed() {
		return timePlayed;
	}

	public boolean isHasPlayedTheGame() {
		return hasPlayedTheGame;
	}

	public boolean isWinner() {
		return isWinner;
	}

	public boolean isSpectator() {
		return isSpectator;
	}

	public Location getLobbyLocation() {
		return lobbyLocation;
	}

	/**
	 * Setter du GamePlayer.
	 */
	public void setGolemaPlayer(GolemaPlayer golemaPlayer) {
		this.golemaPlayer = golemaPlayer;
	}

	public void setScoreboardSign(ScoreboardSign scoreboardSign) {
		this.scoreboardSign = scoreboardSign;
	}

	public void setCoinsWin(Float coinsWin) {
		this.coinsWin = coinsWin;
	}

	public void setCreditsWin(Float creditsWin) {
		this.creditsWin = creditsWin;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public void setExpel(int expel) {
		this.expel = expel;
	}

	public void setLive(int live) {
		this.live = live;
	}

	public void setTimePlayed(int timePlayed) {
		this.timePlayed = timePlayed;
	}

	public void setHasPlayedTheGame(boolean hasPlayedTheGame) {
		this.hasPlayedTheGame = hasPlayedTheGame;
	}

	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}

	public void setLobbyLocation(Location lobbyLocation) {
		this.lobbyLocation = lobbyLocation;
	}

	/**
	 * Récupérer la couleur de la vie du Joueur.
	 * 
	 * @return
	 */
	public String getColorLiveTab() {
		if ((live == 10) || (live == 9) | (live == 8))
			return "§1§b";
		if ((live == 7) || (live == 6) | (live == 6))
			return "§2§a";
		if ((live == 5) || (live == 4))
			return "§3§e";
		if ((live == 3) || (live == 2))
			return "§4§6";
		return "§5§c";
	}

	/**
	 * Envoyer le message de récompenses.
	 */
	public void rewardMessage() {
		if (hasPlayedTheGame) {
			player.sendMessage(ChatColor.GOLD + "-----------------------------------");
			golemaPlayer.sendCenteredMessage(ChatColor.WHITE + "● Récompenses ●");
			player.sendMessage("");
			golemaPlayer.sendCenteredMessage(ChatColor.YELLOW + "Vous avez gagné " + this.getCoinsWin() + " GCoins !");
			golemaPlayer
					.sendCenteredMessage(ChatColor.AQUA + "Vous avez gagné " + this.getCreditsWin() + " GCrédits !");
			player.sendMessage("");
			player.sendMessage(ChatColor.GOLD + "-----------------------------------");
		}
	}
}