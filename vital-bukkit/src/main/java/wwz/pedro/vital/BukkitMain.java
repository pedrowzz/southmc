package wwz.pedro.vital;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import wwz.pedro.vital.commands.BukkitCommandFramework;
import wwz.pedro.vital.essencial.ChatListener;
import wwz.pedro.vital.essencial.GroupManager;
import wwz.pedro.vital.essencial.TabListener;
public class BukkitMain extends JavaPlugin {
    @Getter
    private Config configManager;
    private BukkitCommandFramework commandFramework;
    private Database database;
    private static BukkitMain instance;


    @Override
    public void onEnable() {
        getLogger().info("§aAs bibliotecas vitais foram iniciadas corretamente, e os recursos foram iniciados.");
        // Configurações do banco de dados
        String host = "localhost";
        int port = 3306;
        String databaseName = "minecraft";
        String username = "root";
        String password = "";

        instance = this;

        // Inicializa a instância do banco de dados
        database = new Database(this, host, port, databaseName, username, password);

        // Registra o evento
        getServer().getPluginManager().registerEvents(new TabListener(), this);
        getServer().getPluginManager().registerEvents(new UUIDCollector(this), this);
        GroupManager.setup(this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        commandFramework = new BukkitCommandFramework(this);
        loadCommands();
    }

    private void loadCommands() {
      commandFramework.loadCommands(this, "wwz.pedro.vital.commands.register");
    }

    public static BukkitMain getInstance() {
      return instance;
    }
    public Database getMySQLDatabase() {
      return database;
    }
}