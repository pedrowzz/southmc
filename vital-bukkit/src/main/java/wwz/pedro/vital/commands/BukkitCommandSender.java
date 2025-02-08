package wwz.pedro.vital.commands;

import java.beans.ConstructorProperties;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import lombok.NonNull;
import wwz.pedro.vital.essencial.GroupManager;
import wwz.pedro.vital.essencial.Rank;

public class BukkitCommandSender implements CommandSender, org.bukkit.command.CommandSender {
  @NonNull
  private org.bukkit.command.CommandSender commandSender;

  public UUID getUniqueId() {
    return this.commandSender instanceof Player ? ((Player) this.commandSender).getUniqueId() : UUID.randomUUID();
  }

  public Player getPlayer() {
    return (Player) this.commandSender;
  }

  public String getNick() {
    return this.commandSender instanceof Player ? this.commandSender.getName() : "CONSOLE";
  }

  public boolean isPlayer() {
    if (this.commandSender instanceof Player) {
      return true;
    } else {
      this.commandSender.sendMessage("§cComando disponível apenas para Jogadores.");
      return false;
    }
  }

  public String getRealNick() {
    if (this.commandSender instanceof Player) {
      Player player = (Player) this.commandSender;
      Rank rank = GroupManager.getPlayerRank(player.getName());
      return rank.getDisplayName(); // Assuming getDisplayName is what you want here
    } else {
      return "CONSOLE";
    }
  }

  public PermissionAttachment addAttachment(Plugin arg0) {
    return this.commandSender.addAttachment(arg0);
  }

  public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
    return this.commandSender.addAttachment(arg0);
  }

  public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
    return this.commandSender.addAttachment(arg0, arg1, arg2);
  }

  public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
    return this.commandSender.addAttachment(arg0, arg1, arg2, arg3);
  }

  public Set<PermissionAttachmentInfo> getEffectivePermissions() {
    return this.commandSender.getEffectivePermissions();
  }

  public boolean hasPermission(String arg0) {
    return this.commandSender.hasPermission(arg0);
  }

  public boolean hasPermission(Permission arg0) {
    return this.commandSender.hasPermission(arg0);
  }

  public boolean isPermissionSet(String arg0) {
    return this.commandSender.isPermissionSet(arg0);
  }

  public boolean isPermissionSet(Permission arg0) {
    return this.commandSender.isPermissionSet(arg0);
  }

  public void recalculatePermissions() {
    this.commandSender.recalculatePermissions();
  }

  public void removeAttachment(PermissionAttachment arg0) {
    this.commandSender.removeAttachment(arg0);
  }

  public boolean isOp() {
    return this.commandSender.isOp();
  }

  public void setOp(boolean arg0) {
    this.commandSender.setOp(arg0);
  }

  public String getName() {
    return this.commandSender.getName();
  }

  public Server getServer() {
    return this.commandSender.getServer();
  }

  public void sendMessage(String arg0) {
    this.commandSender.sendMessage(arg0);
  }

  public void sendMessage(String[] arg0) {
    this.commandSender.sendMessage(arg0);
  }

  public String getArgs(String[] args, int começo) {
    StringBuilder stringBuilder = new StringBuilder();

    for (int i = começo; i < args.length; ++i) {
      stringBuilder.append(args[i]).append(" ");
    }

    return stringBuilder.toString();
  }

  @ConstructorProperties({"commandSender"})
  public BukkitCommandSender(@NonNull org.bukkit.command.CommandSender commandSender) {
    if (commandSender == null) {
      throw new NullPointerException("commandSender");
    } else {
      this.commandSender = commandSender;
    }
  }
}
