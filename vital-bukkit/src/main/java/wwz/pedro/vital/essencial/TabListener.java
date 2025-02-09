package wwz.pedro.vital.essencial;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;

public class TabListener implements Listener {

    private final String header = "\n§b§lSOUTHMC\n";
    private final String footer = "\n§fVisite nosso site: §asouthmc.com\n";

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendTabList(event.getPlayer());
        updatePlayerListName(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Optional: Clear the player's list name on quit
        event.getPlayer().setPlayerListName(null);
    }

    public void updatePlayerListName(Player player) {
        Rank playerRank = GroupManager.getPlayerRank(player.getName());
        PrefixType prefixType = GroupManager.getPlayerPrefixType(player);
        Tag playerTag = GroupManager.getPlayerTag(player);

        String formattedName;
        if (playerRank == Rank.MEMBER) {
            formattedName = "§7" + player.getName();
        } else {
            formattedName = prefixType.getFormatter().format(playerTag) + playerTag.getColor() + player.getName();
        }

        player.setPlayerListName(formattedName);
    }

    public void sendTabList(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        try {
            Field a = packet.getClass().getDeclaredField("a");
            a.setAccessible(true);
            a.set(packet, new ChatComponentText(header));

            Field b = packet.getClass().getDeclaredField("b");
            b.setAccessible(true);
            b.set(packet, new ChatComponentText(footer));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        craftPlayer.getHandle().playerConnection.sendPacket(packet);
    }
}
