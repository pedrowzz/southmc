package wwz.pedro.vital;

import java.time.Instant;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import wwz.pedro.vital.essencial.GroupManager;
import wwz.pedro.vital.essencial.Rank;

public class UUIDCollector implements Listener {

    private final BukkitMain plugin;

    public UUIDCollector(BukkitMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String nick = player.getName();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String pais = GroupManager.getCountry(player);
                String estado = GroupManager.getState(player);
                String cidade = GroupManager.getCity(player);
                Rank cargo = GroupManager.getPlayerRank(nick);
                String asn = GroupManager.getASN(player);
                Instant ultimoLogin = Instant.now();
                Instant primeiroLogin = GroupManager.getFirstLogin(playerUUID);
                String contaTipo = GroupManager.isCracked(player) ? "Cracked" : "Premium";

                plugin.getLogger().info("Saving player data for " + nick + " (" + playerUUID + ")");
                plugin.getLogger().info("País: " + pais + ", Estado: " + estado + ", Cidade: " + cidade);

                plugin.getMySQLDatabase().setPlayerData(playerUUID.toString(), nick, pais, estado, cidade, cargo, asn, ultimoLogin, primeiroLogin, contaTipo);
            } catch (Exception e) {
                plugin.getLogger().severe("Erro ao salvar dados do jogador " + nick + ": " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}