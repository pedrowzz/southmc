package wwz.pedro.vital.essencial;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;
import wwz.pedro.vital.BukkitMain;
import wwz.pedro.vital.essencial.*;

@Getter
public class ChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Tag playerTag = GroupManager.getPlayerTag(player);
        PrefixType prefixType = GroupManager.getPlayerPrefixType(player);
        String message = event.getMessage();
        event.setFormat(prefixType.getFormatter().format(playerTag) + " §7" + player.getName() + " §8» §f" + message);
    }
}
