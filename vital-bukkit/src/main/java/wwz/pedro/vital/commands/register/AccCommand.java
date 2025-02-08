package wwz.pedro.vital.commands.register;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import wwz.pedro.vital.BukkitMain;
import wwz.pedro.vital.commands.BukkitCommandSender;
import wwz.pedro.vital.commands.Command;
import wwz.pedro.vital.commands.CommandClass;
import wwz.pedro.vital.essencial.GroupManager;
import wwz.pedro.vital.essencial.Rank;
import wwz.pedro.vital.essencial.Tag;
import wwz.pedro.vital.utills.Messages;

public class AccCommand implements CommandClass {

  private final BukkitMain plugin;

  public AccCommand(BukkitMain plugin) {
    this.plugin = plugin;
  }

  @Command(
      name = "acc",
      aliases = {"account"},
      groupsToUse = {Rank.MEMBER},
      description = "Manage player tags",
      usage = "/acc <nick> [add/remove <tag> <tempo|eterno>]"
  )
  public void accCommand(BukkitCommandSender sender, String label, String[] args) {
    if (sender.isPlayer()) {
      Player player = sender.getPlayer();
      Rank playerRank = GroupManager.getPlayerRank(player.getName());

      if (args.length == 1) {
        // Member usage: /acc <nick> (only works on themselves)
        if (args[0].equalsIgnoreCase(player.getName())) {
          sender.sendMessage("§aSeu nick: §f" + sender.getNick());
        } else {
          sender.sendMessage("§c/acc <nick>");
        }
      } else if (args.length >= 3 && playerRank.getId() >= Rank.ADMINISTRATOR.getId()) {
        // Admin usage: /acc <nick> add/remove <tag> [tempo/eterno]
        String targetName = args[0];
        String action = args[1];

        if (!action.equalsIgnoreCase("add") && !action.equalsIgnoreCase("remove")) {
          sender.sendMessage(Messages.MENSAGEM_USO_COMANDO);
          return;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
          sender.sendMessage(Messages.MENSAGEM_JOGADOR_NAO_ENCONTRADO);
          return;
        }

        if (action.equalsIgnoreCase("add")) {
            if (args.length < 4) {
                sender.sendMessage("§c/acc <nick> add <tag> <tempo|eterno>");
                return;
            }

            String tagString = args[2];
            Tag tag = Tag.fromUsages(tagString);
            if (tag == null) {
                sender.sendMessage(Messages.MENSAGEM_TAG_INVALIDA);
                return;
            }

            long duration = -1;
            if (!args[3].equalsIgnoreCase("eterno")) {
                try {
                    duration = Long.parseLong(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Messages.MENSAGEM_TEMPO_INVALIDO);
                    return;
                }
            }

            GroupManager.setTemporaryTag(target, tag, duration, plugin);
            sender.sendMessage(Messages.format(Messages.MENSAGEM_TAG_ADICIONADA, tag.getName(), targetName, duration == -1 ? "for eternity." : "for " + duration + " seconds."));
        } else if (action.equalsIgnoreCase("remove")) {
          // Remove tag logic
          GroupManager.removeTemporaryTag(target, plugin);
          sender.sendMessage("§aTag removed from " + targetName + ".");
        }
      } else {
        // Incorrect usage
        if (playerRank.getId() >= Rank.ADMINISTRATOR.getId()) {
          sender.sendMessage("§c/acc <nick> add/remove <tag> [tempo/eterno]");
        } else {
          sender.sendMessage("§c/acc <nick>");
        }
      }
    } else {
      sender.sendMessage("§cThis command is only for players.");
    }
  }
}
