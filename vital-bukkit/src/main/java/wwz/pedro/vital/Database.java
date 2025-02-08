package wwz.pedro.vital;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import wwz.pedro.vital.essencial.Rank;

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
                "CREATE TABLE IF NOT EXISTS player_data (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "nick VARCHAR(255), " +
                        "pais VARCHAR(255), " +
                        "estado VARCHAR(255), " +
                        "cidade VARCHAR(255), " +
                        "cargo VARCHAR(255), " +
                        "asn VARCHAR(255), " +
                        "ultimo_login TIMESTAMP, " +
                        "primeiro_login TIMESTAMP," +
                        "conta_tipo VARCHAR(255))"
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

    public void setPlayerData(String uuid, String nick, String pais, String estado, String cidade, Rank cargo, String asn, Instant ultimoLogin, Instant primeiroLogin, String contaTipo) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO player_data (uuid, nick, pais, estado, cidade, cargo, asn, ultimo_login, primeiro_login, conta_tipo) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "nick = ?, pais = ?, estado = ?, cidade = ?, cargo = ?, asn = ?, ultimo_login = ?, primeiro_login = ?, conta_tipo = ?"
        )) {
            ps.setString(1, uuid);
            ps.setString(2, nick);
            ps.setString(3, pais);
            ps.setString(4, estado);
            ps.setString(5, cidade);
            ps.setString(6, (cargo != null) ? cargo.name() : null);
            ps.setString(7, asn);
            ps.setTimestamp(8, (ultimoLogin != null) ? Timestamp.from(ultimoLogin) : null);
            ps.setTimestamp(9, (primeiroLogin != null) ? Timestamp.from(primeiroLogin) : null);
            ps.setString(10, contaTipo);

            ps.setString(11, nick);
            ps.setString(12, pais);
            ps.setString(13, estado);
            ps.setString(14, cidade);
            ps.setString(15, (cargo != null) ? cargo.name() : null);
            ps.setString(16, asn);
            ps.setTimestamp(17, (ultimoLogin != null) ? Timestamp.from(ultimoLogin) : null);
            ps.setTimestamp(18, (primeiroLogin != null) ? Timestamp.from(primeiroLogin) : null);
            ps.setString(19, contaTipo);

            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to set player data!");
            e.printStackTrace();
        }
    }

    public ResultSet getPlayerData(String uuid) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM player_data WHERE uuid = ?");
            ps.setString(1, uuid);
            return ps.executeQuery();
        } catch (SQLException e) {
            plugin.getLogger().severe("Falha ao obter dados do jogador: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
