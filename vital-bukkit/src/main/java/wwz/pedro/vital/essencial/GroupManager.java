package wwz.pedro.vital.essencial;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
                "localhost", // host
                3306,       // port
                "minecraft", // database
                "root",     // username
                "");        // password
    }

    public static void close() {
        if (database != null) {
            database.close();
        }
    }

    public static Rank getPlayerRank(String playerName) {
        // Lógica de atribuição de ranks (por enquanto, por nome de jogador)
        if (playerName.equalsIgnoreCase("Pedro")) {
            return Rank.DEVELOPER_ADMIN;
        } else if (playerName.equalsIgnoreCase("Maria")) {
            return Rank.VIP;
        } else {
            return Rank.MEMBER;
        }
    }

    public static void setTemporaryTag(Player player, Tag tag, long duration, BukkitMain plugin) {
        temporaryTags.put(player.getUniqueId(), tag);
        //database.setPlayerTag(player.getUniqueId().toString(), tag.name()); // Old tag system

        // Get player data
        UUID playerUUID = player.getUniqueId();
        String nick = player.getName();
        String pais = getCountry(player); // Implement location logic
        String estado = getState(player);
        String cidade = getCity(player);
        Rank cargo = getPlayerRank(nick); // Get the player's rank
        String asn = getASN(player); // Implement ASN logic
        Instant ultimoLogin = Instant.now(); // Get last login time
        Instant primeiroLogin = getFirstLogin(playerUUID); // Get first login time
        String accountType = isCracked(player) ? "Cracked" : "Original";

        System.out.println("Setting player data for UUID: " + playerUUID);
        System.out.println("  Nick: " + nick);
        System.out.println("  Pais: " + pais);
        System.out.println("  Estado: " + estado);
        System.out.println("  Cidade: " + cidade);
        System.out.println("  Cargo: " + cargo);
        System.out.println("  ASN: " + asn);
        System.out.println("  Ultimo Login: " + ultimoLogin);
        System.out.println("  Primeiro Login: " + primeiroLogin);
        System.out.println("  Account Type: " + accountType);

        database.setPlayerData(playerUUID.toString(), nick, pais, estado, cidade, cargo, asn, ultimoLogin, primeiroLogin, accountType);

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
        //database.setPlayerTag(player.getUniqueId().toString(), null); // Remove tag from database

        // Update player data in database
        UUID playerUUID = player.getUniqueId();
        String nick = player.getName();
        String pais = getCountry(player); // Implement location logic
        String estado = getState(player);
        String cidade = getCity(player);
        Rank cargo = getPlayerRank(nick); // Get the player's rank
        String asn = getASN(player); // Implement ASN logic
        Instant ultimoLogin = Instant.now(); // Get last login time
        Instant primeiroLogin = getFirstLogin(playerUUID); // Get first login time
        String accountType = isCracked(player) ? "Cracked" : "Original";

        database.setPlayerData(playerUUID.toString(), nick, pais, estado, cidade, cargo, asn, ultimoLogin, primeiroLogin, accountType);
        player.sendMessage(Messages.MENSAGEM_TAG_EXPIRADA);
    }

    public static Tag getPlayerTag(Player player) {
        if (temporaryTags.containsKey(player.getUniqueId())) {
            return temporaryTags.get(player.getUniqueId());
        }

        // Retrieve player data from database
        UUID playerUUID = player.getUniqueId();
        ResultSet rs = database.getPlayerData(playerUUID.toString());
        try {
            if (rs != null && rs.next()) {
                String tagString = rs.getString("cargo");
                if (tagString != null) {
                    return Tag.fromUsages(tagString);
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

    private static String getCountry(Player player) {
        try {
            InetAddress ipAddress = player.getAddress().getAddress();
            String ip = ipAddress.getHostAddress();
            String apiKey = "YOUR_API_KEY"; // Replace with your actual API key
            URL url = new URL("https://ipapi.co/" + ip + "/json/?key=" + apiKey);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            JsonObject jsonObject = JsonParser.parseString(json.toString()).getAsJsonObject();
            return jsonObject.get("country_name").getAsString();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private static String getState(Player player) {
        try {
            InetAddress ipAddress = player.getAddress().getAddress();
            String ip = ipAddress.getHostAddress();
            String apiKey = "YOUR_API_KEY"; // Replace with your actual API key
            URL url = new URL("https://ipapi.co/" + ip + "/json/?key=" + apiKey);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            JsonObject jsonObject = JsonParser.parseString(json.toString()).getAsJsonObject();
            return jsonObject.get("region").getAsString();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private static String getCity(Player player) {
        try {
            InetAddress ipAddress = player.getAddress().getAddress();
            String ip = ipAddress.getHostAddress();
            String apiKey = "YOUR_API_KEY"; // Replace with your actual API key
            URL url = new URL("https://ipapi.co/" + ip + "/json/?key=" + apiKey);
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

    private static String getASN(Player player) {
        try {
            InetAddress ipAddress = player.getAddress().getAddress();
            // Implement ASN lookup logic here (e.g., using an external API)
            // This is just a placeholder
            return "ASN Lookup Not Implemented";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private static Instant getFirstLogin(UUID playerUUID) {
        ResultSet rs = database.getPlayerData(playerUUID.toString());
        try {
            if (rs != null && rs.next()) {
                java.sql.Timestamp timestamp = rs.getTimestamp("primeiro_login");
                if (timestamp != null) {
                    return timestamp.toInstant();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Instant.now(); // Default to now if not found
    }

    public static Database getDatabase() {
        return database;
    }

    private static boolean isCracked(Player player) {
        return !player.hasPlayedBefore(); // A simple check, might not be 100% accurate
    }
}
