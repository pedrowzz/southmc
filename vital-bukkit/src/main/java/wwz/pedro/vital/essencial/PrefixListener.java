package wwz.pedro.vital.essencial;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import wwz.pedro.vital.BukkitMain;

public class PrefixListener implements Listener {

    private final BukkitMain plugin;

    public PrefixListener(BukkitMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePrefix(player);
    }

    public void updatePrefix(Player player) {
        Scoreboard scoreboard = player.getServer().getScoreboardManager().getMainScoreboard();
        Rank playerRank = GroupManager.getPlayerRank(player.getName());
        Tag playerTag = GroupManager.getPlayerTag(player);
        PrefixType prefixType = GroupManager.getPlayerPrefixType(player);
        String teamName = playerTag.getOrder() + "_" + playerRank.getName();

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.setPrefix(prefixType.getFormatter().format(playerTag));
            team.setDisplayName(playerTag.getName());
        }

        team.addEntry(player.getName());

        String displayName;
        if (playerTag == Tag.MEMBER) {
            displayName = "§7" + player.getName();
        } else {
            // Usar a mesma formatação que está no tab
            String prefix = prefixType.getFormatter().format(playerTag);
            String color = playerTag.getColor();
            displayName = prefix + color + player.getName();
        }
        player.setDisplayName(displayName);
        player.setPlayerListName(displayName); // Sincroniza com o tab
    }
}
