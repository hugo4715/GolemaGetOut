package net.faiden.getout.runnables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.faiden.getout.GamePlayer;
import net.faiden.getout.GetOut;
import net.golema.database.support.GameStatus;

public class ScoreboardRunnable extends BukkitRunnable {

	public ScoreboardRunnable() {
	}

	@Override
	public void run() {
		for (Player playerOnline : Bukkit.getOnlinePlayers()) {
			if (GetOut.getGamePlayer(playerOnline) != null) {
				GamePlayer gamePlayer = GetOut.getGamePlayer(playerOnline);
				switch (GameStatus.getStatus()) {
				case LOBBY:
					gamePlayer.getScoreboardSign().setLine(8, "§7Joueurs: §f" + Bukkit.getOnlinePlayers().size()
							+ ChatColor.GRAY + "/" + ChatColor.WHITE + Bukkit.getMaxPlayers());
					break;
				case GAME:

					// Si le Joueur est en partie.
					if (!(gamePlayer.isSpectator())) {
						gamePlayer.getScoreboardSign().setLine(8,
								ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + GetOut.instance.countPlayerInGame());
						gamePlayer.getScoreboardSign().setLine(7,
								ChatColor.GRAY + "Kills: " + ChatColor.WHITE + gamePlayer.getKills());

						// Si le Joueur est en Spectateur.
					} else {
						gamePlayer.getScoreboardSign().setLine(3,
								ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + GetOut.instance.countPlayerInGame());
					}
					break;
				case FINISH:

					// Feu d'artifice de Victoire.
					/*
					 * if(gamePlayer.isWinner()) { List<Type> crList = new ArrayList<Type>();
					 * /*for(Type typeEffect : Type.values()) { crList.add(typeEffect); } Firework f
					 * = (Firework) playerOnline.getWorld().spawn(playerOnline.getLocation(),
					 * Firework.class); FireworkMeta fm = f.getFireworkMeta(); fm.setPower(1);
					 * fm.addEffect(FireworkEffect.builder() .flicker(false) .trail(true)
					 * .with(crList.get(new Random().nextInt(crList.size()))) .withColor(Color.RED)
					 * .withFade(Color.AQUA) .build()); fm.setPower(3); f.setFireworkMeta(fm); }
					 */

					break;
				default:
					break;
				}
			}
		}
	}
}