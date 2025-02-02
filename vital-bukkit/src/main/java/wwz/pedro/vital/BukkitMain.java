package wwz.pedro.vital;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import wwz.pedro.vital.comandos.*;
import wwz.pedro.vital.essencial.*;
import wwz.pedro.vital.*;
import wwz.pedro.vital.Config;
import wwz.pedro.vital.essencial.GroupManager;
import wwz.pedro.vital.essencial.ChatListener;
import wwz.pedro.vital.utills.Messages;
import wwz.pedro.vital.comandos.AccCommand;

public class BukkitMain extends JavaPlugin {
    @Getter
    private Config configManager;

    @Override
    public void onEnable() {
        this.configManager = new Config(this);
        getLogger().info("§aAs bibliotecas vitais foram iniciadas corretamente, e os recursos foram iniciados.");
        GroupManager.setup(this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getCommand("acc").setExecutor(new AccCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("§cAs bibliotecas vitais foram desabilitadas corretamente, e os recursos foram salvos com sucesso.");
        GroupManager.close();
    }
}
