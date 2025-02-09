package wwz.pedro.vital;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import wwz.pedro.vital.essencial.GroupManager;
import wwz.pedro.vital.essencial.Rank;

public class ServerTweaks implements Listener {

    private final JavaPlugin plugin;

    public ServerTweaks(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startDaylightCycle();
    }

    private void startDaylightCycle() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (World world : Bukkit.getWorlds()) {
                world.setTime(6000); // Set time to day
            }
        }, 0L, 100L); // Run every 5 seconds
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Rank playerRank = GroupManager.getPlayerRank(player.getName());
        event.setJoinMessage(null);
        }
    }

