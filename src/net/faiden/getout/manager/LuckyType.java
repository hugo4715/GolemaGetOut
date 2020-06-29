package net.faiden.getout.manager;

import org.bukkit.ChatColor;

public enum LuckyType {

	MOREONELIFE(true, ChatColor.RED + "+1 ❤"),
	SPEED(true, ChatColor.DARK_AQUA + "* " + ChatColor.AQUA + "Speed" + ChatColor.DARK_AQUA + " *"),
	INVISIBILITY(true, ChatColor.GOLD + "* " + ChatColor.YELLOW + "Invisibilité (10 sec)" + ChatColor.GOLD + " *"),
	SWAPPLAYER(false, ChatColor.DARK_AQUA + "* " + ChatColor.AQUA + "Place échangée avec %playerTarget% " + ChatColor.DARK_AQUA + " *"),
	TPONPLAYER(false, ChatColor.DARK_PURPLE + "* " + ChatColor.LIGHT_PURPLE + "Téléportation sur un joueur" + ChatColor.DARK_PURPLE + " *"),
	SLOWNESS(false, ChatColor.DARK_GRAY + "* " + ChatColor.GRAY + "Slowness (10 sec)" + ChatColor.DARK_GRAY + " *"),
	LESSONELIFE(false, ChatColor.GOLD + "Respawn...");

	public boolean chance;
	public String title;

	private LuckyType(boolean chance, String title) {
		this.chance = chance;
		this.title = title; 
	}

	public boolean isChance() {
		return chance;
	}

	public String getTitle() {
		return title;
	}
}