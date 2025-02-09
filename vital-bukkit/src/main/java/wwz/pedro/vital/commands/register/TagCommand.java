package wwz.pedro.vital.commands.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import wwz.pedro.vital.BukkitMain;
import wwz.pedro.vital.commands.BukkitCommandSender;
import wwz.pedro.vital.commands.Command;
import wwz.pedro.vital.commands.CommandClass;
import wwz.pedro.vital.commands.Completer;
import wwz.pedro.vital.essencial.GroupManager;
import wwz.pedro.vital.essencial.PrefixType;
import wwz.pedro.vital.essencial.Rank;
import wwz.pedro.vital.essencial.Tag;
import wwz.pedro.vital.events.PlayerUpdateTablistEvent;

public class TagCommand implements CommandClass {

    @Command(
            name = "tag",
            usage = "/tag [tag]",
            groupsToUse = {Rank.MEMBER},
            description = "Select a tag"
    )
    public void handleCommand(BukkitCommandSender context, String label, String[] args) {
        if (!context.isPlayer()) {
            context.sendMessage("§cApenas jogadores podem usar este comando.");
            return;
        }

        Player player = context.getPlayer();

        if (args.length == 0) {
            List<Tag> availableTags = getAvailableTags(player);

            if (availableTags.isEmpty()) {
                context.sendMessage("§cVocê não possui nenhuma tag disponível.");
                return;
            }

            String tagList = availableTags.stream()
                    .map(tag -> tag.getColor() + tag.getName())
                    .collect(Collectors.joining(ChatColor.WHITE + ", "));

            context.sendMessage("§aSuas tags: " + tagList);

        } else if (args.length == 1) {
            String tagName = args[0];
            Tag selectedTag = Tag.fromUsages(tagName);

            if (selectedTag == null) {
                context.sendMessage("§cTag inválida.");
                return;
            }

            List<Tag> availableTags = getAvailableTags(player);
            if (!availableTags.contains(selectedTag)) {
                context.sendMessage("§cVocê não possui a tag " + selectedTag.getColor() + selectedTag.getName() + "§c.");
                return;
            }

            // Set the player's tag using GroupManager
            GroupManager.setTemporaryTag(player, selectedTag, -1, BukkitMain.getInstance());

            context.sendMessage("§aTag alterada para: " + selectedTag.getColor() + selectedTag.getName());

            // Store the preferred tag in the database
            UUID playerUUID = player.getUniqueId();

            // Call the PlayerUpdateTablistEvent
            PrefixType prefixType = GroupManager.getPlayerPrefixType(player);
            PlayerUpdateTablistEvent event = new PlayerUpdateTablistEvent(player, selectedTag, prefixType);
            Bukkit.getPluginManager().callEvent(event);

        } else {
            context.sendMessage("§cUsage: /tag [tag]");
        }
    }

    private List<Tag> getAvailableTags(Player player) {
        Rank playerRank = GroupManager.getPlayerRank(player.getName());
        List<Tag> availableTags = new ArrayList<>();
        for (Tag tag : Tag.values()) {
            if (tag.getId() <= playerRank.getId()) {
                availableTags.add(tag);
            }
        }
        return availableTags;
    }

    @Completer(name = "tag")
    public List<String> handleComplete(BukkitCommandSender context, String label, String[] args) {
        if (args.length == 1) {
            Player player = context.getPlayer();
            List<Tag> availableTags = getAvailableTags(player);
            return availableTags.stream()
                    .map(tag -> tag.getUsages()[0]) // Use the first usage as the tag name
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
