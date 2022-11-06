package com.vsepecan.parkour.listener;

import com.vsepecan.parkour.Parkour;
import com.vsepecan.parkour.config.ConfigManager;
import com.vsepecan.parkour.database.DBConnection;
import com.vsepecan.parkour.runnable.Countdown;
import com.vsepecan.parkour.runnable.LiveGameTimer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static com.vsepecan.parkour.config.ConfigManager.getMoveDuringCountdown;

public class Course implements Listener {

    Parkour parkour;

    HashMap<UUID, LiveGameTimer> liveGameTimerHashMap;
    HashMap<UUID, Countdown> countdownHashMap;
    HashMap<UUID, Integer> checkpointIndexHashMap;

    //private final DBConnection dbConnection;

    public Course(Parkour parkour) {
        this.parkour = parkour;

        liveGameTimerHashMap = new HashMap<>();
        countdownHashMap = new HashMap<>();
        checkpointIndexHashMap = new HashMap<>();

        //dbConnection = new DBConnection();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) /*throws SQLException*/ {
        Player player = e.getPlayer();
        Location playerLocation = player.getLocation();
        Location standingBlockLocation = playerLocation.getBlock().getRelative(BlockFace.DOWN).getLocation();

        if (countdownHashMap.get(player.getUniqueId()) != null) {    // IN COUNTDOWN
            e.setCancelled(!getMoveDuringCountdown());
        } else if (liveGameTimerHashMap.get(player.getUniqueId()) != null) {    // IN GAME
            if (playerLocation.getY() < ConfigManager.getYCoordinateOfFall()) { // LOSS
                player.teleport(ConfigManager.getPlayerStartLocations().get(checkpointIndexHashMap.get(player.getUniqueId())));
                player.sendTitle(ChatColor.GREEN + "Level " + (checkpointIndexHashMap.get(player.getUniqueId()) + 1), "", 10, 70, 20);
            } else if (ConfigManager.getFinishBlocksLocations().contains(standingBlockLocation)) {  // A FINISH

                int index = ConfigManager.getFinishBlocksLocations().indexOf(standingBlockLocation);

                if (index == ConfigManager.getFinishBlocksLocations().size() - 1) { // LAST FINISH BLOCK
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
                    player.sendTitle(ChatColor.GREEN + "Good job!",
                            ChatColor.GREEN + "Your time is " + liveGameTimerHashMap.get(player.getUniqueId()).getDisplay(), 10, 70, 20);

                    //writeIntoDB(player, liveGameTimerHashMap.get(player.getUniqueId()).getSeconds(), ConfigManager.getFinishBlocksLocations().size());

                    checkpointIndexHashMap.remove(player.getUniqueId());
                    liveGameTimerHashMap.get(player.getUniqueId()).cancel();
                    liveGameTimerHashMap.remove(player.getUniqueId());

                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    player.stopSound(Sound.AMBIENT_UNDERWATER_LOOP);
                    player.teleport(ConfigManager.getLeaveCommandLocation());
                } else {
                    player.teleport(ConfigManager.getPlayerStartLocations().get(index + 1));
                    checkpointIndexHashMap.put(player.getUniqueId(), index + 1);
                    player.sendTitle(ChatColor.GREEN + "Level " + (index + 2), "", 10, 70, 20);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)
                && (countdownHashMap.get(event.getEntity().getUniqueId()) != null
                    || liveGameTimerHashMap.get(event.getEntity().getUniqueId()) != null)) {
            event.setCancelled(true);
        }
    }
/*
    public void writeIntoDB(Player player, float currentTime, int currentLevel) throws SQLException {
        // Write the best time or level of the current day
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();

        ResultSet selectResultSet;
        try (PreparedStatement selectPreparedStatement = dbConnection.getConnection().prepareStatement(
                "SELECT " + (currentTime > 0 ? "best_time" : "highest_level_achieved") +
                        " FROM parkour_times WHERE uuid = '" + player.getUniqueId() +
                        "' AND date_value = '" + format.format(today) + "'")) {
            selectResultSet = selectPreparedStatement.executeQuery();

            if (selectResultSet.next()) {   // If a record of this uuid and this date exists
                float previousTime;
                int previousHighestLevel;

                if (currentTime > 0) {  // A win
                    previousTime = selectResultSet.getFloat(1);

                    if (previousTime == -1 || currentTime < previousTime) {   // Won for the first time OR Won again with better time
                        player.sendMessage(ChatColor.GREEN + "Your best score today!");
                        try (PreparedStatement updatePreparedStatement = dbConnection.getConnection().prepareStatement(
                                "UPDATE parkour_times SET best_time = " + currentTime + ", highest_level_achieved = " + ConfigManager.getFinishBlocksLocations().size() +
                                        " WHERE uuid = '" + player.getUniqueId() + "' AND date_value = '" + format.format(today) + "'")) {
                            updatePreparedStatement.executeUpdate();
                        }
                    }
                } else {    // Not a win
                    previousHighestLevel = selectResultSet.getInt(1);

                    if (currentLevel > previousHighestLevel) {
                        player.sendMessage(ChatColor.GREEN + "Your best score today!");
                        try (PreparedStatement updatePreparedStatement = dbConnection.getConnection().prepareStatement(
                                "UPDATE parkour_times SET best_time = -1, highest_level_achieved = " + currentLevel +
                                        " WHERE uuid = '" + player.getUniqueId() + "' AND date_value = '" + format.format(today) + "'")) {
                            updatePreparedStatement.executeUpdate();
                        }
                    }
                }
            } else {
                try (PreparedStatement insertPreparedStatement = dbConnection.getConnection().prepareStatement(
                        "INSERT INTO parkour_times VALUES ('" + player.getUniqueId() + "', '" +
                                format.format(today) + "', " + currentTime + ", " + currentLevel + ")")) {
                    insertPreparedStatement.executeUpdate();
                }
            }
        }
    }

    public DBConnection getDBConnection() { return dbConnection; }
*/
    public HashMap<UUID, LiveGameTimer> getLiveGameTimerHashMap() { return liveGameTimerHashMap; }
    public HashMap<UUID, Countdown> getCountdownHashMap() { return countdownHashMap; }
    public HashMap<UUID, Integer> getCheckpointIndexHashMap() { return checkpointIndexHashMap; }

}
