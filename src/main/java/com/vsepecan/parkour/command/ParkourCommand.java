package com.vsepecan.parkour.command;

import com.vsepecan.parkour.Parkour;
import com.vsepecan.parkour.config.ConfigManager;
import com.vsepecan.parkour.runnable.Countdown;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ParkourCommand implements CommandExecutor {

    private final Parkour parkour;

    public ParkourCommand(Parkour parkour) { this.parkour = parkour; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player && args.length == 0) {
            Player player = ((Player) sender).getPlayer();

            if (parkour.getCourse().getLiveGameTimerHashMap().get(Objects.requireNonNull(player).getUniqueId()) == null
                    && parkour.getCourse().getCountdownHashMap().get(player.getUniqueId()) == null) {
                player.teleport(ConfigManager.getPlayerStartLocations().get(0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 255, false, false, false));
                if (ConfigManager.inCourseMusic())
                    player.playSound(player.getLocation(), Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 1, 1);

                // starts itself in the constructor
                parkour.getCourse().getCountdownHashMap().put(player.getUniqueId(), new Countdown(parkour, player));
            } else player.sendMessage(ChatColor.RED + "You are already in Parkour. Run /leaveparkour to leave it.");

            return true;
        }

        return false;
    }

}
