package wwz.pedro.vital.comandos;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import wwz.pedro.vital.essencial.GroupManager;
import wwz.pedro.vital.essencial.Tag;

public class TagCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        Tag currentTag = GroupManager.getPlayerTag(player);

        List<Tag> sortedTags = Arrays.stream(Tag.values())
                .sorted(Comparator.comparingInt(Tag::getId))
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder("§aSuas tags: ");
        for (Tag tag : sortedTags) {
            if (tag.getId() <= currentTag.getId()) {
                sb.append(tag.getFormattedColor()).append(tag.getName()).append("§f, ");
            }
        }

        // Remove the trailing comma and space
        if (sb.length() > 11) {
            sb.delete(sb.length() - 2, sb.length());
        }

        player.sendMessage(sb.toString());
        return true;
    }
}
