package wwz.pedro.vital.essencial;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.avaje.ebean.EbeanServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import java.io.StringReader;

import wwz.pedro.vital.BukkitMain;
import wwz.pedro.vital.Database;
import wwz.pedro.vital.utills.Messages;

public class GroupManager {

    private static final Map<String, Rank> playerRanks = new HashMap<>();
    private static final Map<UUID, Tag> temporaryTags = new HashMap<>();
    private static final Map<UUID, Long> tagExpiration = new HashMap<>();
    private static final Map<UUID, PrefixType> playerPrefixes = new HashMap<>();
    private static Database database;

    public static void setup(BukkitMain plugin) {
        database = new Database(plugin,
                plugin.getConfig().getString("mysql.host", "localhost"),
                plugin.getConfig().getInt("mysql.port", 3306),
                plugin.getConfig().getString("mysql.database", "minecraft"),
                plugin.getConfig().getString("mysql.username", "root"),
                plugin.getConfig().getString("mysql.password", ""));
    }

    public static void close() {
        if (database != null) {
            database.close();
        }
    }

    public static Rank getPlayerRank(String playerName) {
        return playerRanks.computeIfAbsent(playerName, name -> {
            if (name.equalsIgnoreCase("Pedro")) {
                return Rank.DEVELOPER_ADMIN;
            } else if (name.equalsIgnoreCase("Maria")) {
                return Rank.VIP;
            } else {
                return Rank.MEMBER;
            }
        });
    }

    public static void setTemporaryTag(Player player, Tag tag, long duration, BukkitMain plugin) {
        temporaryTags.put(player.getUniqueId(), tag);

        UUID playerUUID = player.getUniqueId();
        String nick = player.getName();
        String pais = getCountry(player);
        String estado = getState(player);
        String cidade = getCity(player);
        Rank cargo = getPlayerRank(nick);
        String asn = getASN(player);
        Instant ultimoLogin = Instant.now();
        Instant primeiroLogin = getFirstLogin(playerUUID);
        String contaTipo = isCracked(player) ? "Cracked" : "Original";

        database.setPlayerData(playerUUID.toString(), nick, pais, estado, cidade, cargo, asn, ultimoLogin, primeiroLogin, contaTipo);

        if (duration != -1) {
            tagExpiration.put(player.getUniqueId(), System.currentTimeMillis() + duration * 1000);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (tagExpiration.containsKey(player.getUniqueId()) && System.currentTimeMillis() >= tagExpiration.get(player.getUniqueId())) {
                        removeTemporaryTag(player, plugin);
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 20);
        }
    }

    public static void removeTemporaryTag(Player player, BukkitMain plugin) {
        temporaryTags.remove(player.getUniqueId());
        tagExpiration.remove(player.getUniqueId());

        UUID playerUUID = player.getUniqueId();
        String nick = player.getName();
        String pais = getCountry(player);
        String estado = getState(player);
        String cidade = getCity(player);
        Rank cargo = getPlayerRank(nick);
        String asn = getASN(player);
        Instant ultimoLogin = Instant.now();
        Instant primeiroLogin = getFirstLogin(playerUUID);
        String contaTipo = isCracked(player) ? "Cracked" : "Original";

        database.setPlayerData(playerUUID.toString(), nick, pais, estado, cidade, cargo, asn, ultimoLogin, primeiroLogin, contaTipo);
        player.sendMessage(Messages.MENSAGEM_TAG_EXPIRADA);
    }

    public static Tag getPlayerTag(Player player) {
        if (temporaryTags.containsKey(player.getUniqueId())) {
            return temporaryTags.get(player.getUniqueId());
        }

        UUID playerUUID = player.getUniqueId();
        ResultSet rs = database.getPlayerData(playerUUID.toString());
        try {
            if (rs != null && rs.next()) {
                String cargoString = rs.getString("cargo");
                if (cargoString != null) {
                    try {
                        Rank rank = Rank.valueOf(cargoString);
                        return rank.getDefaultTag();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getPlayerRank(player.getName()).getDefaultTag();
    }

    public static PrefixType getPlayerPrefixType(Player player) {
        return playerPrefixes.getOrDefault(player.getUniqueId(), PrefixType.DEFAULT);
    }

    public static String getCountry(Player player) {
        try {
            InetAddress ipAddress = player.getAddress().getAddress();
            String ip = ipAddress.getHostAddress();
            URL url = new URL("https://ipapi.co/" + ip + "/json/");
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            JsonObject jsonObject = JsonParser.parseReader(new StringReader(json.toString())).getAsJsonObject();
            return jsonObject.get("country_name").getAsString();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public static String getState(Player player) {
        try {
            InetAddress ipAddress = player.getAddress().getAddress();
            String ip = ipAddress.getHostAddress();
            URL url = new URL("https://ipapi.co/" + ip + "/json/");
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            JsonObject jsonObject = new Gson().fromJson(json.toString(), JsonObject.class);
            return jsonObject.get("region").getAsString();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public static String getCity(Player player) {
        try {
            InetAddress ipAddress = player.getAddress().getAddress();
            String ip = ipAddress.getHostAddress();
            URL url = new URL("https://ipapi.co/" + ip + "/json/");
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            JsonObject jsonObject = JsonParser.parseString(json.toString()).getAsJsonObject();
            return jsonObject.get("city").getAsString();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public static String getASN(Player player) {
        try {
            InetAddress ipAddress = player.getAddress().getAddress();
            return "ASN Lookup Not Implemented";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public static Instant getFirstLogin(UUID playerUUID) {
        EbeanServer db = BukkitMain.getInstance().getDatabase();
        ResultSet rs = database.getPlayerData(playerUUID.toString());       try {
            if (rs != null && rs.next()) {
                Timestamp timestamp = rs.getTimestamp("primeiro_login");
                if (timestamp != null) {
                    return timestamp.toInstant();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Instant.now();
    }

    public static Database getDatabase() {
        return database;
    }

    public static boolean isCracked(Player player) {
        return !player.hasPlayedBefore();
    }
}
