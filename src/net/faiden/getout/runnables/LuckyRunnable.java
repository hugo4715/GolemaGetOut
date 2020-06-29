package net.faiden.getout.runnables;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.faiden.getout.GetOut;
import net.golema.database.support.GameStatus;
import net.golema.database.support.builder.items.heads.HeadBuilder;

public class LuckyRunnable extends BukkitRunnable {

	public static Integer luckyTimer = 45;

	public LuckyRunnable() {
	}

	@Override
	public void run() {

		// Sécurité pour bloquer le Timer.
		if (GameStatus.isStatus(GameStatus.FINISH)) {
			this.cancel();
			return;
		}

		// Envoie des Lucky Bonus.
		if (luckyTimer == 0) {

			// Mise en place du Lucky Bonus et Effets.
			ItemStack itemLucky = new HeadBuilder().setHead("TheCheeseBlob").setName(ChatColor.LIGHT_PURPLE + "Bonus")
					.build();
			Location locationLucky = GetOut.instance.spawnLocations
					.get(new Random().nextInt(GetOut.instance.spawnLocations.size()));
			Bukkit.getWorld("world").dropItem(locationLucky, itemLucky);
			Bukkit.getWorld("world").strikeLightningEffect(locationLucky);

			// Création de l'Hologramme.
			ArmorStand armorStand = (ArmorStand) Bukkit.getWorld("world").spawnEntity(locationLucky,
					EntityType.ARMOR_STAND);
			armorStand.setBasePlate(false);
			armorStand.setSmall(true);
			armorStand.setVisible(false);
			armorStand.setCustomNameVisible(true);
			armorStand.setCustomName(ChatColor.LIGHT_PURPLE + "Bonus");
			armorStand.setMaxHealth(1000.0d);
			armorStand.setHealth(1000.0d);

			// Message de l'arrivé du Lucky Bonus.
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "NOUVEAU" + ChatColor.WHITE + "│ "
					+ ChatColor.YELLOW + "Un " + ChatColor.BOLD + "✪ Lucky Bonus ✪ " + ChatColor.YELLOW
					+ "vient de faire son apparition !");
			Bukkit.broadcastMessage("");

			// Remise du Timer à niveau.
			luckyTimer = 45;
		}

		// Réduction du Timer des Lucky Bonus.
		for (Player playerOnline : Bukkit.getOnlinePlayers()) {
			if (!(GetOut.getGamePlayer(playerOnline).isSpectator())) {
				GetOut.getGamePlayer(playerOnline).getScoreboardSign().setLine(4,
						ChatColor.GRAY + "Nouveau dans: " + ChatColor.WHITE + LuckyRunnable.luckyTimer + "s");
			}
		}
		luckyTimer--;
	}
}