package wwz.pedro.vital;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import wwz.pedro.vital.commands.BukkitCommandFramework;
import wwz.pedro.vital.essencial.ChatListener;
import wwz.pedro.vital.essencial.GroupManager;

public class BukkitMain extends JavaPlugin {
  @Getter
  private Config configManager;
  private BukkitCommandFramework commandFramework;

  @Override
  public void onEnable() {
    this.configManager = new Config(this);
    getLogger().info("§aAs bibliotecas vitais foram iniciadas corretamente, e os recursos foram iniciados.");
    GroupManager.setup(this);
    getServer().getPluginManager().registerEvents(new ChatListener(), this);

    // Initialize and register commands
    commandFramework = new BukkitCommandFramework(this);
    loadCommands();
  }

  private void loadCommands() {
    commandFramework.loadCommands(this, "wwz.pedro.vital.commands.register");
  }

  @Override
  public void onDisable() {
    getLogger().info("§cAs bibliotecas vitais foram desabilitadas corretamente, e os recursos foram salvos com sucesso.");
    GroupManager.close();
  }

  public static BukkitMain getInstance() {
    return getPlugin(BukkitMain.class);
  }
}
