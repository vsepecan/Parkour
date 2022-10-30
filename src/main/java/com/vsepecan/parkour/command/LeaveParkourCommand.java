package com.vsepecan.parkour.command;

import com.vsepecan.parkour.Parkour;
import com.vsepecan.parkour.config.ConfigManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Objects;

public class LeaveParkourCommand implements CommandExecutor {

    private final Parkour parkour;

    private final Location leaveCommandLocation;

    public LeaveParkourCommand(Parkour parkour) {
        this.parkour = parkour;
        leaveCommandLocation = ConfigManager.getLeaveCommandLocation();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player && args.length == 0) {
            Player player = ((Player) sender).getPlayer();

            if (parkour.getCourse().getLiveGameTimerHashMap().get(Objects.requireNonNull(player).getUniqueId()) != null) {
                parkour.getCourse().getLiveGameTimerHashMap().get(player.getUniqueId()).cancel();
                parkour.getCourse().getLiveGameTimerHashMap().remove(player.getUniqueId());

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
                player.sendTitle("", "", 10, 70, 20);

                try {
                    parkour.getCourse().writeIntoDB(player, -1, parkour.getCourse().getCheckpointIndexHashMap().get(player.getUniqueId()));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (parkour.getCourse().getCountdownHashMap().get(player.getUniqueId()) != null) {
                parkour.getCourse().getCountdownHashMap().get(player.getUniqueId()).cancel();
                parkour.getCourse().getCountdownHashMap().remove(player.getUniqueId());

                player.sendTitle("", "", 10, 70, 20);
            } else {
                player.sendMessage(ChatColor.RED + "You are not in the parkour game.");
                return true;
            }

            parkour.getCourse().getCheckpointIndexHashMap().remove(player.getUniqueId());

            player.sendMessage(ChatColor.GREEN + "You left the game.");
            player.sendMessage(ChatColor.GREEN + "Run /parkour to play again.");

            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.stopSound(Sound.AMBIENT_UNDERWATER_LOOP);
            player.teleport(leaveCommandLocation);

            return true;
        }

        return false;
    }
}
