package net.faiden.getout.listeners.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import net.faiden.getout.GetOut;
import net.golema.api.builder.titles.ActionBarBuilder;
import net.golema.database.support.GameStatus;
import net.golema.database.support.builder.TitleBuilder;
import net.golema.database.support.particle.ParticleEffect;

public class PlayerMoveListener implements Listener {

	public static List<Player> jumpList = new ArrayList<Player>();

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		switch (GameStatus.getStatus()) {
		case LOBBY:
			// Téléportation au Hub si le Joueur s'éloigne.
			if ((player.getLocation().getBlock().getType().equals(Material.WATER))
					|| (player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER))
					|| (player.getLocation().getBlock().getType().equals(Material.WATER_LILY))) {
				player.teleport(GetOut.getGamePlayer(player).getLobbyLocation());
				new TitleBuilder("", ChatColor.AQUA + "Plop ! Ne t'éloignes pas trop de la Zone d'attente...")
						.send(player); 
				player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
			}
			break;
		case GAME:

			// Ignore les Joueurs en mode Spectateur.
			if (GetOut.getGamePlayer(player).isSpectator()) {
				return;
			}

			// Message concernant le Double Jump.
			if ((jumpList.contains(player)) && (GetOut.instance.canMove)) {
				new ActionBarBuilder(ChatColor.RED + "" + ChatColor.BOLD + "✖ DOUBLE-JUMP ✖").sendTo(player);
			} else {
				new ActionBarBuilder(ChatColor.AQUA + "" + ChatColor.BOLD + "✔ DOUBLE-JUMP ✔").sendTo(player);
			}

			// Plaque de pression en OR.
			if (player.getLocation().getBlock().getType().equals(Material.GOLD_PLATE)) {
				player.setVelocity(player.getLocation().getDirection().multiply(3.0).setY(5));
			}

			// Bloquer les joueurs lors de la téléportation
			if (!(GetOut.instance.canMove)) {
				if (event.getFrom().getX() != event.getTo().getX() || event.getFrom().getY() != event.getTo().getY()
						|| event.getFrom().getZ() != event.getTo().getZ()) {
					Location loc = event.getFrom();
					event.getPlayer().teleport(loc.setDirection(event.getTo().getDirection()));
				}
			}

			// Détection d'une expulsion.
			if ((player.getLocation().getBlock().getType().equals(Material.WATER))
					|| (player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER))
					|| (player.getLocation().getBlock().getType().equals(Material.WATER_LILY))) {
				if (!GetOut.getGamePlayer(player).isSpectator()) {
					GetOut.getGamePlayer(player).makeRespawn();
				}
			}

			// Gestion du DoubleJump.
			if (!(jumpList.contains(player)) && (GetOut.instance.canMove)) {
				if ((player.getGameMode() != GameMode.CREATIVE) && (!player.isFlying())) {
					player.setAllowFlight(true);
				}
			}
			break;
		default:
			break;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		if ((!(GetOut.getGamePlayer(player).isSpectator())) && (!(jumpList.contains(player)))
				&& (GetOut.instance.canMove)) {
			if (player.getGameMode() == GameMode.CREATIVE)
				return;
			event.setCancelled(true);
			player.setAllowFlight(false);
			player.setFlying(false);
			player.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(1));
			player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 1.0f, 1.0f);
			ParticleEffect.FLAME.display(player.getVelocity(), 1.0f, player.getLocation(), 200);
			jumpList.add(player);
			this.taskRemoveDoubleJump(player);
		}
	}

	/**
	 * Réactiver le DoubleJump d'un Joueur.
	 * 
	 * @param player
	 */
	public void taskRemoveDoubleJump(Player player) {
		Bukkit.getScheduler().runTaskLater(GetOut.instance, new Runnable() {
			@Override
			public void run() {
				if (jumpList.contains(player)) {
					jumpList.remove(player);
					player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0f, 1.0f);
				}
			}
		}, 20 * 4L);
	}
}