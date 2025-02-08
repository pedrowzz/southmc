package wwz.pedro.vital.essencial;

    import java.util.HashMap;
    import java.util.Map;
    import java.util.UUID;

    import org.bukkit.entity.Player;
    import org.bukkit.scheduler.BukkitRunnable;

    import wwz.pedro.vital.BukkitMain;
    import wwz.pedro.vital.Database;
    import wwz.pedro.vital.utills.Messages;

    public class GroupManager {

        private static final Map<String, Rank> playerRanks = new HashMap<>();
        private static final Map<UUID, Tag> temporaryTags = new HashMap<>();
        private static final Map<UUID, Long> tagExpiration = new HashMap<>();
        private static final Map<UUID, PrefixType> playerPrefixes = new HashMap<>();
        private static Database database;

        public static void setup(BukkitMain plugin) {
            database = new Database(plugin,
                "localhost", // host
                3306,       // port
                "minecraft", // database
                "root",     // username
                "");        // password
        }

        public static void close() {
            if (database != null) {
                database.close();
            }
        }

        public static Rank getPlayerRank(String playerName) {
            // Lógica de atribuição de ranks (por enquanto, por nome de jogador)
            if (playerName.equalsIgnoreCase("Pedro")) {
                return Rank.DEVELOPER_ADMIN;
            } else if (playerName.equalsIgnoreCase("Maria")) {
                return Rank.VIP;
            } else {
                return Rank.MEMBER;
            }
        }

        public static void setTemporaryTag(Player player, Tag tag, long duration, BukkitMain plugin) {
            temporaryTags.put(player.getUniqueId(), tag);
            database.setPlayerTag(player.getUniqueId().toString(), tag.name());
            if (duration != -1) {
                tagExpiration.put(player.getUniqueId(), System.currentTimeMillis() + duration * 1000);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (tagExpiration.containsKey(player.getUniqueId()) && System.currentTimeMillis() >= tagExpiration.get(player.getUniqueId())) {
                            temporaryTags.remove(player.getUniqueId());
                            tagExpiration.remove(player.getUniqueId());
                            player.sendMessage(Messages.MENSAGEM_TAG_EXPIRADA);
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 20);
            }
        }

        public static Tag getPlayerTag(Player player) {
            if (temporaryTags.containsKey(player.getUniqueId())) {
                return temporaryTags.get(player.getUniqueId());
            }
            String tag = database.getPlayerTag(player.getUniqueId().toString());
            if (tag != null) {
                return Tag.fromUsages(tag);
            }
            return getPlayerRank(player.getName()).getDefaultTag();
        }

        public static PrefixType getPlayerPrefixType(Player player) {
            return playerPrefixes.getOrDefault(player.getUniqueId(), PrefixType.DEFAULT);
        }

        public static boolean hasPermission(Player player, String permission) {
            Rank rank = getPlayerRank(player.getName());

            if (rank.getId() >= Rank.ADMINISTRATOR.getId()) {
                return true; // Admin e acima têm todas as permissões
            }

            if (rank.getId() >= Rank.PRIMARY_MOD.getId()) {
                if (permission.equals("vital.staff.b") || permission.equals("vital.staffc")) {
                    return true;
                }
            }

            if (rank.getId() >= Rank.STREAMER_PLUS.getId() && (rank.isStaffer() || rank == Rank.STREAMER_PLUS || rank == Rank.YOUTUBER_PLUS)) {
                 if (permission.equals("vital.staffc")) {
                    return true;
                }
            }

            return false;
        }
    }
