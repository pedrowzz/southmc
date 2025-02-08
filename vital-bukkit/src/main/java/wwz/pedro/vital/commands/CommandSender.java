package wwz.pedro.vital.commands;

import java.util.UUID;

import org.bukkit.entity.Player;

public interface CommandSender {

  UUID getUniqueId();

  Player getPlayer();

  String getNick();

  boolean isPlayer();

  String getRealNick();

  void sendMessage(String arg0);

  String getArgs(String[] args, int começo);
}
