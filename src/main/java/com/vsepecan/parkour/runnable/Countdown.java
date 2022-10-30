package com.vsepecan.parkour.runnable;

import com.vsepecan.parkour.Parkour;
import com.vsepecan.parkour.config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {

    private final Parkour parkour;

    private final Player player;
    private int seconds;

    public Countdown(Parkour parkour, Player player) {
        this.parkour = parkour;
        this.player = player;
        seconds = ConfigManager.getCountdownSeconds();

        runTaskTimer(parkour, 0, 20);
    }

    @Override
    public void run() {
        if (seconds == 0) {
            parkour.getCourse().getCountdownHashMap().remove(player.getUniqueId());
            parkour.getCourse().getLiveGameTimerHashMap().put(player.getUniqueId(), new LiveGameTimer(parkour, player));

            player.teleport(ConfigManager.getPlayerStartLocations().get(0));
            parkour.getCourse().getCheckpointIndexHashMap().put(player.getUniqueId(), 0);

            cancel();
            return;
        }

        player.sendTitle(ChatColor.GREEN + String.valueOf(seconds), "", 10, 70, 20);

        seconds--;
    }
}
