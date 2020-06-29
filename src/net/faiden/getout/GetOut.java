package net.faiden.getout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.faiden.getout.listeners.ListenerManager;
import net.faiden.getout.runnables.LobbyRunnable;
import net.faiden.getout.runnables.ScoreboardRunnable;
import net.golema.api.builder.HologramBuilder;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.rank.Rank;
import net.golema.database.support.GameStatus;
import net.golema.database.support.configs.FileManager;
import net.golema.database.support.configs.FileManager.Config;
import net.golema.database.support.world.WorldManager;
import tk.hugo4715.golema.timesaver.TimeSaverAPI;
import tk.hugo4715.golema.timesaver.server.GameInfos;
import tk.hugo4715.golema.timesaver.server.ServerStatus;
import tk.hugo4715.golema.timesaver.server.ServerType;

public class GetOut extends JavaPlugin {

	public List<Location> spawnLocations = new ArrayList<Location>();
	public List<MapGameInfos> mapInfosList = new ArrayList<MapGameInfos>();

	public Map<Player, Player> playerTargetMap = new HashMap<Player, Player>();
	public Map<Location, HologramBuilder> armorStandLuckyMap = new HashMap<Location, HologramBuilder>();
	public static Map<String, GamePlayer> gamePlayersMap = new HashMap<String, GamePlayer>();

	public String prefixGame = ChatColor.WHITE + "[" + ChatColor.AQUA + "GetOut" + ChatColor.WHITE + "]";
	public Integer minPlayers = 8;
	public boolean canMove = true;
	public boolean forceStart = false;

	public FileManager fileManager;
	public Config mapLocationsConfig;
	public MapGameInfos mapGameInfos;

	public static GetOut instance;

	@Override
	public void onLoad() {

		// D�finition de l'instance du Plugin.
		instance = this;

		// Gestion de la Map et des Configurations.
		Bukkit.unloadWorld("world", false);
		for (MapGameInfos mapInfos : MapGameInfos.values()) {
			mapInfosList.add(mapInfos);
		}
		this.mapGameInfos = mapInfosList.get(new Random().nextInt(mapInfosList.size()));
		fileManager = new FileManager(instance);
		mapLocationsConfig = fileManager.getConfig("maps/" + mapGameInfos.getConfigName());
		mapLocationsConfig.copyDefaults(true).save();
		WorldManager.deleteWorld(new File("world"));
		File from = new File("maps/" + mapGameInfos.getMapName());
		File to = new File("world");
		try {
			WorldManager.copyFolder(from, to);
		} catch (Exception e) {
			System.err.println("Erreur: Le serveur n'arrive pas � copier la Map : " + mapGameInfos.getMapName());
		}

		super.onLoad();
	}

	@Override
	public void onEnable() {

		// Initialisation des param�tres de la partie.
		new ListenerManager(instance).registerListeners();
		new ScoreboardRunnable().runTaskTimer(instance, 0L, 10L);
		GameStatus.setStatus(GameStatus.LOBBY);

		// Gestions des Times Server.
		Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
			@Override
			public void run() {
				// Initialisation du GolemaServer.
				TimeSaverAPI.setServerMap(mapGameInfos.getMapName());
				TimeSaverAPI.setServerStatus(ServerStatus.ALLOW);
				TimeSaverAPI.setServerGame(GameInfos.GETOUT);
				TimeSaverAPI.setServerType(ServerType.GAME);
				TimeSaverAPI.setJoinable(true);
			}
		}, 40L);

		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	/**
	 * Enregister les Locations.
	 */
	public void registerLocations() {
		for (int i = 1; i <= 20; i++) {
			spawnLocations.add(new Location(Bukkit.getWorld("world"),
					mapLocationsConfig.get().getDouble("LocationSpawn." + i + ".x"),
					mapLocationsConfig.get().getDouble("LocationSpawn." + i + ".y"),
					mapLocationsConfig.get().getDouble("LocationSpawn." + i + ".z")));
		}
	}

	/**
	 * Compter le nombre de Joueurs en vie dans la partie.
	 * 
	 * @return
	 */
	public Integer countPlayerInGame() {
		List<Player> playerInGame = new ArrayList<Player>();
		for (Player playerOnline : Bukkit.getOnlinePlayers()) {
			if ((GetOut.getGamePlayer(playerOnline) != null) && (!(GetOut.getGamePlayer(playerOnline).isSpectator()))) {
				playerInGame.add(playerOnline);
			}
		}
		return playerInGame.size();
	}

	/**
	 * R�cup�rer un GamePlayer.
	 * 
	 * @param player
	 * @return
	 */
	public static GamePlayer getGamePlayer(Player player) {
		if (gamePlayersMap.get(player.getName()) == null) {
			gamePlayersMap.put(player.getName(), new GamePlayer(player));
		}
		return gamePlayersMap.get(player.getName());
	}

	/**
	 * R�cup�rer la map des GamePlyers.
	 * 
	 * @return
	 */
	public static Map<String, GamePlayer> getGamePlayersMap() {
		return gamePlayersMap;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// V�rifiez qui effectu� la commande.
		if (!(sender instanceof Player)) {
			System.out.println("Vous devez être un joueur pour utiliser cette commande.");
			return false;
		}

		// Mise en oeuvre de la commande '/start'.
		if (label.equalsIgnoreCase("start")) {
			Player player = (Player) sender;
			GolemaPlayer golemaPlayer = GolemaPlayer.getGolemaPlayer(player);
			if (golemaPlayer.getRankPower() == Rank.ADMINISTRATOR.getPower()) {
				if (!(LobbyRunnable.isStarted)) {
					if (Bukkit.getOnlinePlayers().size() == 1) {
						player.sendMessage(ChatColor.RED + "Erreur: Vous ne pouvez pas jouer seul.");
						return false;
					}
					this.forceStart = true;
					new LobbyRunnable().runTaskTimer(GetOut.instance, 0L, 20L);
					LobbyRunnable.isStarted = true;
					Bukkit.broadcastMessage(prefixGame + ChatColor.YELLOW + " La partie a été lancée par "
							+ ChatColor.GOLD + player.getName() + ChatColor.YELLOW + ".");
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "Erreur: La partie à déjà été lancée.");
					return false;
				}
			} else {
				golemaPlayer.sendMessageNoPermission();
				return false;
			}
		}
		return false;
	}
}