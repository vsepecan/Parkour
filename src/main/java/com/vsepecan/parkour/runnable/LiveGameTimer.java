package com.vsepecan.parkour.runnable;

import com.vsepecan.parkour.Parkour;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LiveGameTimer extends BukkitRunnable {

    private final Player player;
    float seconds;

    int minutes;
    String displayMinutes, displaySecsAndMillisecs;

    public LiveGameTimer(Parkour parkour, Player player) {
        this.player = player;
        seconds = 0f;

        player.sendTitle(ChatColor.GREEN + "GO!", ChatColor.GREEN + "Level 1", 10, 70, 20);
        parkour.getCourse().getCheckpointIndexHashMap().put(player.getUniqueId(), 0);

        runTaskTimer(parkour, 0, 1);
    }

    @Override
    public void run() {
        seconds = seconds + 0.05f;  // 0.05  ->  1 / 20 of a second

        if (seconds == Float.MAX_VALUE) {
            player.performCommand("leaveparkour");
        }

        minutes = (int) (seconds / 60);
        displayMinutes = (minutes > 0) ? String.format("%d", minutes) + ":" : "";
        displaySecsAndMillisecs = String.format("%05.2f", seconds - minutes * 60);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(ChatColor.GREEN + "Time passed: " + displayMinutes + displaySecsAndMillisecs));
    }

    public float getSeconds() { return seconds; }

    public String getDisplay() { return displayMinutes + displaySecsAndMillisecs; }

}
