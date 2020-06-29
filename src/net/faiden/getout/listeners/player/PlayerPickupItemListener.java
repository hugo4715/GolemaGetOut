package net.faiden.getout.listeners.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.faiden.getout.GamePlayer;
import net.faiden.getout.GetOut;
import net.faiden.getout.manager.LuckyType;
import net.golema.database.support.builder.TitleBuilder;

public class PlayerPickupItemListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {

		// Variable concernant le Joueur.
		Player player = event.getPlayer();
		GamePlayer gamePlayer = GetOut.getGamePlayer(player);
		if (gamePlayer.isSpectator()) {
			event.setCancelled(true);
			return;
		}

		// D�tection d'un LuckyBonus.
		ItemStack itemPickup = event.getItem().getItemStack();
		if (itemPickup == null)
			return;
		if (itemPickup.getType().equals(Material.AIR))
			return;
		if (itemPickup.getType().equals(Material.ARROW)) {
			event.setCancelled(true);
			return;
		}
		if (itemPickup.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "Bonus")) {

			// Annulation de l'�v�nement.
			event.setCancelled(true);
			event.getItem().remove();

			// Randomisation sur le LuckyBonus.
			List<LuckyType> luckyTypesList = new ArrayList<LuckyType>();
			for (LuckyType luckyType : LuckyType.values()) {
				luckyTypesList.add(luckyType);
			}
			LuckyType luckyTypeRandom = luckyTypesList.get(new Random().nextInt(luckyTypesList.size()));

			// Suppression de l'ArmorStand
			Location location = event.getItem().getLocation();
			for (Entity entityArround : Bukkit.getWorld("world").getNearbyEntities(location, 4, 4, 4)) {
				if (entityArround instanceof ArmorStand) {
					entityArround.remove();
				}
			}

			// Envoie du LyckyBonus.
			switch (luckyTypeRandom) {
			case MOREONELIFE:
				gamePlayer.setLive(gamePlayer.getLive() + 1);
				player.setMaxHealth(gamePlayer.getLive() * 2);
				player.setHealth(gamePlayer.getLive() * 2);
				new TitleBuilder("", luckyTypeRandom.getTitle()).send(player);
				break;
			case SPEED:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (20 * 11), 0), true);
				new TitleBuilder("", luckyTypeRandom.getTitle()).send(player);
				break;
			case INVISIBILITY:
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (20 * 11), 1), true);
				new TitleBuilder("", luckyTypeRandom.getTitle()).send(player);
				break;
			case SWAPPLAYER:
				List<Player> playerInGameSwap = new ArrayList<Player>();
				for (Player playerOnline : Bukkit.getOnlinePlayers()) {
					if (!(GetOut.getGamePlayer(playerOnline)).isSpectator()
							&& (!(playerOnline.getName().equalsIgnoreCase(player.getName())))) {
						playerInGameSwap.add(playerOnline);
					}
				}
				Player playerTarget = playerInGameSwap.get(new Random().nextInt(playerInGameSwap.size()));
				Location playerLocation = player.getLocation();
				Location targetLocation = playerTarget.getLocation();
				player.teleport(targetLocation);
				playerTarget.teleport(playerLocation);
				new TitleBuilder("", luckyTypeRandom.getTitle().replace("%playerTarget%", playerTarget.getName()))
						.send(player);
				new TitleBuilder("", luckyTypeRandom.getTitle().replace("%playerTarget%", player.getName()))
						.send(playerTarget);
				break;
			case TPONPLAYER:
				List<Player> playerInGame = new ArrayList<Player>();
				for (Player playerOnline : Bukkit.getOnlinePlayers()) {
					if (!(GetOut.getGamePlayer(playerOnline)).isSpectator()
							&& (!(playerOnline.getName().equalsIgnoreCase(player.getName())))) {
						playerInGame.add(playerOnline);
					}
				}
				player.teleport(playerInGame.get(new Random().nextInt(playerInGame.size())).getLocation());
				new TitleBuilder("", luckyTypeRandom.getTitle()).send(player);
				break;
			case SLOWNESS:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (20 * 11), 0), true);
				new TitleBuilder("", luckyTypeRandom.getTitle()).send(player);
				break;
			case LESSONELIFE:
				gamePlayer.makeRespawn();
				break;
			default:
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		event.setCancelled(true);
	}
}