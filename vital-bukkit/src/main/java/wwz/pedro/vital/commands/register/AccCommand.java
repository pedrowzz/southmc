package wwz.pedro.vital.commands.register;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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

    public AccCommand() {
        this.plugin = BukkitMain.getInstance();
    }

    @Command(
            name = "acc",
            aliases = {"account"},
            groupsToUse = {Rank.MEMBER},
            description = "Manage player tags",
            usage = "/acc <nick> [add/remove <tag> <tempo|eterno>]"
    )
    public void accCommand(BukkitCommandSender sender, String label, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage("§cThis command is only for players.");
            return;
        }

        Player player = sender.getPlayer();
        Rank playerRank = GroupManager.getPlayerRank(player.getName());

        if (args.length == 1) {
            String targetName = args[0];
            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                sender.sendMessage(Messages.MENSAGEM_JOGADOR_NAO_ENCONTRADO);
                return;
            }

            if (playerRank.getId() >= Rank.ADMINISTRATOR.getId()) {
                // Staff viewing another player's info
                UUID playerUUID = target.getUniqueId();
                ResultSet rs = GroupManager.getDatabase().getPlayerData(playerUUID.toString());

                try {
                    if (rs != null) {
                        if (rs.next()) {
                            String nick = rs.getString("nick");
                            String cargo = rs.getString("cargo");
                            String accountType = rs.getString("conta_tipo");

                            sender.sendMessage("§aInformações de " + targetName + ":");
                            sender.sendMessage("§aUUID: §f" + playerUUID);
                            sender.sendMessage("§aNick: §f" + nick);
                            sender.sendMessage("§aCargo: §f" + cargo);
                            sender.sendMessage("§aAccount Type: §f" + accountType);

                        } else {
                            sender.sendMessage("§cNo data found for player " + targetName + ".");
                        }
                    } else {
                        sender.sendMessage("§cNo data found for player " + targetName + ".");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("§cAn error occurred while retrieving player data.");
                }
            } else {
                // Member viewing their own info
                sender.sendMessage("§aSeu nick: §f" + sender.getNick());
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
    }
}
