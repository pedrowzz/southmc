package wwz.pedro.vital.commands.register;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import wwz.pedro.vital.BukkitMain;
import wwz.pedro.vital.Database;
import wwz.pedro.vital.commands.BukkitCommandSender;
import wwz.pedro.vital.commands.Command;
import wwz.pedro.vital.commands.CommandClass;
import wwz.pedro.vital.essencial.GroupManager;
import wwz.pedro.vital.essencial.Rank;

public class InfoCommand implements CommandClass {

    private final BukkitMain plugin;

    public InfoCommand(BukkitMain plugin) {
        this.plugin = plugin;
    }

    public InfoCommand() {
        this.plugin = BukkitMain.getInstance();
    }

    @Command(
            name = "info",
            aliases = {"playerinfo"},
            groupsToUse = {Rank.ADMINISTRATOR},
            description = "Display player information",
            usage = "/info <nick>"
    )
    public void infoCommand(BukkitCommandSender sender, String label, String[] args) {
        if (!sender.isPlayer()) {
            sender.sendMessage("§cThis command is only for players.");
            return;
        }

        Player player = sender.getPlayer();
        Rank playerRank = GroupManager.getPlayerRank(player.getName());

        if (args.length != 1) {
            sender.sendMessage("§cUsage: /info <nick>");
            return;
        }

        if (playerRank.getId() < Rank.ADMINISTRATOR.getId()) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return;
        }

        UUID playerUUID = target.getUniqueId();
        Database database = GroupManager.getDatabase();
        ResultSet rs = database.getPlayerData(playerUUID.toString());

        try {
            if (rs != null) {
                if (rs.next()) {
                    String nick = rs.getString("nick");
                    String cargo = rs.getString("cargo");
                    String accountType = rs.getString("conta_tipo");

                    System.out.println("Retrieved from database - UUID: " + playerUUID + ", Nick: " + nick + ", Cargo: " + cargo + ", Account Type: " + accountType);

                    sender.sendMessage("§aInformações de " + targetName + ":");
                    sender.sendMessage("§aUUID: §f" + playerUUID);
                    sender.sendMessage("§aNick: §f" + nick);
                    sender.sendMessage("§aCargo: §f" + cargo);
                    sender.sendMessage("§aAccount Type: §f" + accountType);

                } else {
                    sender.sendMessage("§cNo data found for player " + targetName + " (ResultSet is empty).");
                }
            } else {
                sender.sendMessage("§cNo data found for player " + targetName + " (ResultSet is null).");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage()); // More specific error message
            e.printStackTrace();
            sender.sendMessage("§cAn error occurred while retrieving player data.");
        }
    }
}
