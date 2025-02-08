package wwz.pedro.vital.essencial;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import lombok.Getter;

@Getter
public class ChatListener implements Listener {

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();
    Tag playerTag = GroupManager.getPlayerTag(player);
    PrefixType prefixType = GroupManager.getPlayerPrefixType(player);
    String message = event.getMessage();
    event.setFormat(prefixType.getFormatter().format(playerTag) + playerTag.getColor() + player.getName() + " §7» §f" + message);
  }
}
