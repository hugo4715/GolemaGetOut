package net.faiden.getout.listeners.entity;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.faiden.getout.GetOut;
import net.golema.database.support.GameStatus;

public class EntityDamageListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		switch (GameStatus.getStatus()) {
		case LOBBY:
			event.setCancelled(true);
			break;
		case GAME:
			if (event.getCause().equals(DamageCause.FALL)) {
				event.setCancelled(true);
			}
			if (event.getCause().equals(DamageCause.FALLING_BLOCK)) {
				event.setCancelled(true);
			}
			if (event.getEntity() instanceof ArmorStand) {
				event.setCancelled(true);
			}
			event.setDamage(0.0d);
			break;
		case FINISH:
			event.setCancelled(true);
			break;
		default:
			break;
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {

			// Initialisation de la variable du Joueur.
			Player player = (Player) event.getEntity();

			// D�tection d'un coup avec la main.
			if (event.getDamager() instanceof Player) {
				Player playerKiller = (Player) event.getDamager();
				GetOut.instance.playerTargetMap.put(player, playerKiller);
			}

			// D�tection d'un coup avec un arc.
			if (event.getDamager() instanceof Arrow) {
				Arrow projectil = (Arrow) event.getDamager();
				Entity shooter = (Entity) projectil.getShooter();
				if (shooter instanceof Player) {
					Player shooterPlayer = (Player) shooter;
					GetOut.instance.playerTargetMap.put(player, shooterPlayer);
				}
			}
		}
	}
}