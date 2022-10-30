package com.vsepecan.parkour;

import com.vsepecan.parkour.command.LeaveParkourCommand;
import com.vsepecan.parkour.command.ParkourCommand;
import com.vsepecan.parkour.listener.Course;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static com.vsepecan.parkour.config.ConfigManager.setupConfig;

public final class Parkour extends JavaPlugin implements Listener {
	
	private Course course;
	
	@Override
	public void onEnable() {
		setupConfig(this);
		
		this.course = new Course(this);
		
		// Events
		Bukkit.getPluginManager().registerEvents(course, this);
		Bukkit.getPluginManager().registerEvents(this, this);
		
		// Commands
		Objects.requireNonNull(getCommand("parkour")).setExecutor(new ParkourCommand(this));
		Objects.requireNonNull(getCommand("leaveparkour")).setExecutor(new LeaveParkourCommand(this));
	}
	
	@Override
	public void onDisable() {
		course.getDBConnection().disconnect();
	}
	
	@EventHandler
	public void onPlayerQuit (PlayerQuitEvent e) {
		e.getPlayer().performCommand("leaveparkour");
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		
		if (getCourse().getLiveGameTimerHashMap().get(Objects.requireNonNull(player).getUniqueId()) != null
				|| getCourse().getCountdownHashMap().get(player.getUniqueId()) != null) {
			if (!(e.getPlayer().isOp() || e.getPlayer().hasPermission("MBH-Parkour.bypasscommand"))) {
				if (!(e.getMessage().equals("/parkour") || e.getMessage().equals("/leaveparkour"))) {
					e.setCancelled(true);
					e.getPlayer().sendMessage(ChatColor.RED + "You are still in parkour. Run /leaveparkour to leave it.");
				}
			}
		}
	}
	
	public Course getCourse() { return course; }
	
}
