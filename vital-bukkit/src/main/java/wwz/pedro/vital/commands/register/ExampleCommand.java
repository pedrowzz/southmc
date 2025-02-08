package wwz.pedro.vital.commands.register;

import wwz.pedro.vital.commands.BukkitCommandSender;
import wwz.pedro.vital.commands.Command;
import wwz.pedro.vital.commands.CommandClass;
import wwz.pedro.vital.essencial.Rank;

public class ExampleCommand implements CommandClass {

  @Command(
      name = "example",
      aliases = {"spop"},
      groupsToUse = {Rank.ADMINISTRATOR},
      description = "An example command",
      usage = "/example"
  )
  public void exampleCommand(BukkitCommandSender sender, String label, String[] args) {
    sender.sendMessage("This is an example command!");
  }
}
