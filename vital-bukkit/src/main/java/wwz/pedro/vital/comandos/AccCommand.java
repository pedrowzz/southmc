package wwz.pedro.vital.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import wwz.pedro.vital.BukkitMain;
import wwz.pedro.vital.utils.Messages;
import wwz.pedro.vital.essencial.*;

public class AccCommand implements CommandExecutor {

    private final BukkitMain plugin;

    public AccCommand(BukkitMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Messages.PERMISSAO_DE_COMANDOS_PERIGOSOS)) {
            sender.sendMessage(Messages.MENSAGEM_SEM_PERM);
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(Messages.MENSAGEM_USO_COMANDO);
            return true;
        }

        if (!args[0].equalsIgnoreCase("nick") || !args[1].equalsIgnoreCase("add") || !args[2].equalsIgnoreCase("tag")) {
            sender.sendMessage(Messages.MENSAGEM_USO_COMANDO);
            return true;
        }

        String targetName = args[3];
        Tag tag = Tag.fromUsages(args[4]);
        if (tag == null) {
            sender.sendMessage(Messages.MENSAGEM_TAG_INVALIDA);
            return true;
        }

        long duration = -1;
        if (!args[5].equalsIgnoreCase("eterno")) {
            try {
                duration = Long.parseLong(args[5]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Messages.MENSAGEM_TEMPO_INVALIDO);
                return true;
            }
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(Messages.MENSAGEM_JOGADOR_NAO_ENCONTRADO);
            return true;
        }

        GroupManager.setTemporaryTag(target, tag, duration, plugin);
        sender.sendMessage(Messages.format(Messages.MENSAGEM_TAG_ADICIONADA, tag.getName(), targetName, duration == -1 ? "for eternity." : "for " + duration + " seconds."));
        return true;
    }
}
