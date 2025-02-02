package wwz.pedro.vital;

import org.bukkit.configuration.file.FileConfiguration;
import wwz.pedro.vital.BukkitMain;

public class Config {

    private final BukkitMain plugin;
    private FileConfiguration config;

    public Config(BukkitMain plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public String getMySQLHost() {
        return config.getString("mysql.host", "localhost");
    }

    public int getMySQLPort() {
        return config.getInt("mysql.port", 3306);
    }

    public String getMySQLDatabase() {
        return config.getString("mysql.database", "minecraft");
    }

    public String getMySQLUsername() {
        return config.getString("mysql.username", "root");
    }

    public String getMySQLPassword() {
        return config.getString("mysql.password", "");
    }
}
