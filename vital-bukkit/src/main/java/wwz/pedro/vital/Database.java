package wwz.pedro.vital;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import wwz.pedro.vital.BukkitMain;

public class Database {

    private final BukkitMain plugin;
    private Connection connection;
    private String host, database, username, password;
    private int port;

    public Database(BukkitMain plugin, String host, int port, String database, String username, String password) {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        connect();
        createTable();
    }

    public void connect() {
        try {
            if (connection != null && !connection.isClosed()) return;
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String url = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, database
            );
            
            connection = DriverManager.getConnection(url, username, password);
            plugin.getLogger().info("MySQL conectado com sucesso!");
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().severe("Falha na conexão MySQL: " + e.getMessage());
            e.printStackTrace();
            return; // Evita NPE em createTable()
        }
    }

    public void createTable() {
        if (connection == null) {
            plugin.getLogger().severe("Não é possível criar tabela - conexão não estabelecida");
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS player_tags (" +
            "uuid VARCHAR(36) PRIMARY KEY, " + 
            "tag VARCHAR(255))"
        )) {
            ps.executeUpdate();
            plugin.getLogger().info("Tabela criada/verificada com sucesso!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Falha ao criar tabela: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("MySQL connection closed!");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close MySQL connection!");
            e.printStackTrace();
        }
    }

    public void setPlayerTag(String uuid, String tag) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO player_tags (uuid, tag) VALUES (?, ?) ON DUPLICATE KEY UPDATE tag = ?")) {
            ps.setString(1, uuid);
            ps.setString(2, tag);
            ps.setString(3, tag);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to set player tag!");
            e.printStackTrace();
        }
    }

    public String getPlayerTag(String uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT tag FROM player_tags WHERE uuid = ?")) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tag");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get player tag!");
            e.printStackTrace();
        }
        return null;
    }
}
