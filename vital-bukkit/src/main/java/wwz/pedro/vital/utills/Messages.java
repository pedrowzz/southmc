package wwz.pedro.vital.utils;

import org.bukkit.ChatColor;

public class Messages {

    public static final String MENSAGEM_SEM_PERM = ChatColor.RED + "You do not have permission to use this command.";
    public static final String MENSAGEM_TAG_INVALIDA = ChatColor.RED + "Invalid tag.";
    public static final String MENSAGEM_TEMPO_INVALIDO = ChatColor.RED + "Invalid duration.";
    public static final String MENSAGEM_JOGADOR_NAO_ENCONTRADO = ChatColor.RED + "Player not found.";
    public static final String MENSAGEM_TAG_ADICIONADA = ChatColor.GREEN + "Tag %tag% added to %player% %duration%.";
    public static final String MENSAGEM_TAG_EXPIRADA = ChatColor.RED + "Your temporary tag has expired.";
    public static final String MENSAGEM_USO_COMANDO = ChatColor.RED + "Usage: /acc nick add tag <nick> <tag> <tempo|eterno>";

    public static final String PERMISSAO_DE_COMANDOS_PERIGOSOS = "vital.acc";

    public static String format(String message, String... args) {
        String formatted = message;
        for (int i = 0; i < args.length; i++) {
            formatted = formatted.replace("%" + (i + 1) + "%", args[i]);
        }
        return formatted;
    }
}
