package wwz.pedro.vital.essencial;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import wwz.pedro.vital.events.PlayerUpdateTablistEvent;
import wwz.pedro.vital.BukkitMain;

public class TabUpdateListener implements Listener {

    @EventHandler
    public void onPlayerUpdateTablist(PlayerUpdateTablistEvent event) {
        Player player = event.getPlayer();
        TabListener tabListener = new TabListener();
        tabListener.updatePlayerListName(player);
        PrefixListener prefixListener = new PrefixListener(BukkitMain.getInstance());
        prefixListener.updatePrefix(player);
    }
}
