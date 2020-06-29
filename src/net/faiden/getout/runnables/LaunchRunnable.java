package net.faiden.getout.runnables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.faiden.getout.GetOut;
import net.golema.api.utils.PlayerUtils;
import net.golema.database.support.builder.TitleBuilder;
import net.golema.database.support.builder.items.ItemBuilder;

public class LaunchRunnable extends BukkitRunnable {

	public static Integer timer = 5;

	@Override
	public void run() {

		// Si la partie vient d'être lancée.
		if (timer == 0) {

			// Settings sur les joueurs.
			for (Player playerOnline : Bukkit.getOnlinePlayers()) {
				if (!(GetOut.getGamePlayer(playerOnline).isSpectator())) {
					playerOnline.playSound(playerOnline.getLocation(), Sound.EXPLODE, 1.0f, 1.0f);
					playerOnline.setWalkSpeed(0.2f);
					PlayerUtils.clearInventory(playerOnline);

					// Gestion de l'Item de la Plume.
					ItemStack itemFeather = new ItemStack(Material.FEATHER);
					ItemMeta itemMetaFeather = itemFeather.getItemMeta();
					itemMetaFeather.setDisplayName(
							ChatColor.WHITE + "Plume " + ChatColor.GRAY + "■ " + ChatColor.LIGHT_PURPLE + "Ejecteur");
					itemMetaFeather.addEnchant(Enchantment.KNOCKBACK, 6, true);
					itemFeather.setItemMeta(itemMetaFeather);

					// Gestion de l'Item de l'Arc.
					ItemStack itemBow = new ItemStack(Material.BOW);
					ItemMeta itemMetaBow = itemBow.getItemMeta();
					itemMetaBow.setDisplayName(
							ChatColor.WHITE + "Arc " + ChatColor.GRAY + "■ " + ChatColor.LIGHT_PURPLE + "Shooter");
					itemMetaBow.addEnchant(Enchantment.ARROW_KNOCKBACK, 3, true);
					itemMetaBow.addEnchant(Enchantment.DURABILITY, 100, true);
					itemBow.setItemMeta(itemMetaBow);

					// Gestion de l'Item de la Flêche.
					ItemStack arrowItem = new ItemBuilder().type(Material.ARROW).amount(1)
							.name(ChatColor.WHITE + "Projectile").build();

					// Envoie des Items.
					playerOnline.getInventory().addItem(itemFeather);
					playerOnline.getInventory().addItem(itemBow);
					playerOnline.getInventory().setItem(17, arrowItem);
				}
			}

			// Alerts Messages de démarrage.
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage(GetOut.instance.prefixGame + ChatColor.WHITE
					+ " Vous pouvez désormais bouger, poussez les autres joueurs dans le vide pour les éliminer.");
			Bukkit.broadcastMessage("");
			new TitleBuilder(ChatColor.YELLOW + "GetOut",
					ChatColor.LIGHT_PURPLE + "Let's go ! Bonne chance à tous les joueurs...").broadcast();
			GetOut.instance.canMove = true;

			// Démarrage du système de Lucky Bonus.
			new LuckyRunnable().runTaskTimer(GetOut.instance, 0L, 20L);

			this.cancel();
			return;
		}

		// Alerts Messages Timer.
		new TitleBuilder(ChatColor.AQUA + "" + timer, "").broadcast();
		timer--;
	}
}