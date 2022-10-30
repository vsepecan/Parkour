package com.vsepecan.parkour.config;

import com.vsepecan.parkour.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Objects;

public class ConfigManager {

    private static FileConfiguration fileConfiguration;

    private static ArrayList<Location> playerStartLocations;
    private static ArrayList<Location> finishBlocksLocations;
    private static Location leaveCommandLocation;

    public static void setupConfig(Parkour parkour) {
        ConfigManager.fileConfiguration = parkour.getConfig();
        parkour.saveDefaultConfig();

        playerStartLocations = new ArrayList<>();
        finishBlocksLocations = new ArrayList<>();

        for (String locationId : Objects.requireNonNull(fileConfiguration.getConfigurationSection("player-start-locations")).getKeys(false)) {
            playerStartLocations.add(new Location(Bukkit.getWorld("world"),
                    fileConfiguration.getDouble("player-start-locations." + locationId + ".x"),
                    fileConfiguration.getDouble("player-start-locations." + locationId + ".y"),
                    fileConfiguration.getDouble("player-start-locations." + locationId + ".z"),
                    (float) fileConfiguration.getDouble("player-start-locations." + locationId + ".jaw"),
                    (float) fileConfiguration.getDouble("player-start-locations." + locationId + ".pitch")));
        }

        for (String locationId : Objects.requireNonNull(fileConfiguration.getConfigurationSection("finish-blocks-locations")).getKeys(false)) {
            finishBlocksLocations.add(new Location(Bukkit.getWorld("world"),
                    fileConfiguration.getInt("finish-blocks-locations." + locationId + ".x"),
                    fileConfiguration.getInt("finish-blocks-locations." + locationId + ".y"),
                    fileConfiguration.getInt("finish-blocks-locations." + locationId + ".z")));
        }

        leaveCommandLocation = new Location(Bukkit.getWorld("world"),
                fileConfiguration.getDouble("leave-command-location.x"),
                fileConfiguration.getDouble("leave-command-location.y"),
                fileConfiguration.getDouble("leave-command-location.z"),
                (float) fileConfiguration.getDouble("leave-command-location.yaw"),
                (float) fileConfiguration.getDouble("leave-command-location.pitch"));
    }

    public static ArrayList<Location> getPlayerStartLocations() { return playerStartLocations; }
    public static ArrayList<Location> getFinishBlocksLocations() { return finishBlocksLocations; }
    public static Location getLeaveCommandLocation() { return leaveCommandLocation; }

    public static int getYCoordinateOfFall() { return fileConfiguration.getInt("y-coordinate-of-fall"); }
    public static int getCountdownSeconds() { return fileConfiguration.getInt("countdown-seconds"); }
    public static boolean getMoveDuringCountdown() { return fileConfiguration.getBoolean("move-during-countdown"); }

    public static String getHost() { return fileConfiguration.getString("host"); }
    public static int getPort() { return fileConfiguration.getInt("port"); }
    public static String getDatabase() { return fileConfiguration.getString("database"); }
    public static String getUser() { return fileConfiguration.getString("user"); }
    public static String getPassword() { return fileConfiguration.getString("password"); }

    public static boolean inCourseMusic() { return fileConfiguration.getBoolean("in-course-music"); }

}
