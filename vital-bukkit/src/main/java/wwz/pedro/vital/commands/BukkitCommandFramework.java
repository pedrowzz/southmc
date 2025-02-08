package wwz.pedro.vital.commands;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import wwz.pedro.vital.essencial.GroupManager;
import wwz.pedro.vital.essencial.Rank;

public class BukkitCommandFramework {

  private final Map<String, Entry<Method, Object>> commandMap = new HashMap<>();
  public static BukkitCommandFramework INSTANCE;
  private CommandMap map;
  private final JavaPlugin plugin;

  public BukkitCommandFramework(JavaPlugin plugin) {
    INSTANCE = this;
    this.plugin = plugin;
    if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
      SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();
      try {
        Field field = SimplePluginManager.class.getDeclaredField("commandMap");
        field.setAccessible(true);
        map = (CommandMap) field.get(manager);
      } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException e) {
        e.printStackTrace();
      }
    }
  }

  public JavaPlugin getPlugin() {
    return plugin;
  }

  public boolean handleCommand(CommandSender sender, String label, BukkitCommand bukkitCommand, String[] args) {
    StringBuilder line = new StringBuilder();
    line.append(label);
    for (String arg : args) {
      line.append(" ").append(arg);
    }

    for (int i = args.length; i >= 0; --i) {
      StringBuilder buffer = new StringBuilder();
      buffer.append(label.toLowerCase());

      for (int x = 0; x < i; ++x) {
        buffer.append(".").append(args[x].toLowerCase());
      }

      String cmdLabel = buffer.toString();
      if (this.commandMap.containsKey(cmdLabel)) {
        Entry<Method, Object> entry = this.commandMap.get(cmdLabel);
        Command command = entry.getKey().getAnnotation(Command.class);
        if (sender instanceof Player) {
          Player p = (Player) sender;
          Rank playerRank = GroupManager.getPlayerRank(p.getName());
          boolean hasPermission = false;

          for (Rank requiredRank : command.groupsToUse()) {
            if (playerRank.getId() >= requiredRank.getId()) {
              hasPermission = true;
              break;
            }
          }

          if (!hasPermission) {
            p.sendMessage("§cVocê não possui permissão para utilizar este comando.");
            return true;
          }
        }

        try {
          entry.getKey().invoke(entry.getValue(), new BukkitCommandSender(sender), label, args);
        } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException var19) {
          var19.printStackTrace();
        }

        return true;
      }
    }

    return true;
  }

  public void registerCommands(CommandClass commandClass) {
    for (Method m : commandClass.getClass().getMethods()) {
      if (m.getAnnotation(Command.class) != null) {
        Command command = m.getAnnotation(Command.class);
        if (m.getParameterTypes().length == 3
            && BukkitCommandSender.class.isAssignableFrom(m.getParameterTypes()[0])
            && String.class.isAssignableFrom(m.getParameterTypes()[1])
            && String[].class.isAssignableFrom(m.getParameterTypes()[2])) {
          registerCommand(command, command.name(), m, commandClass);
          for (String alias : command.aliases()) {
            registerCommand(command, alias, m, commandClass);
          }
        } else {
          System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
        }
      } else if (m.getAnnotation(Completer.class) != null) {
        Completer comp = m.getAnnotation(Completer.class);
        if (m.getParameterTypes().length == 3
            && BukkitCommandSender.class.isAssignableFrom(m.getParameterTypes()[0])
            && String.class.isAssignableFrom(m.getParameterTypes()[1])
            && String[].class.isAssignableFrom(m.getParameterTypes()[2])) {
          if (m.getReturnType() == List.class) {
            registerCompleter(comp.name(), m, commandClass);
            for (String alias : comp.aliases()) {
              registerCompleter(alias, m, commandClass);
            }
          } else {
            System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
          }
        } else {
          System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
        }
      }
    }
  }

  public void registerHelp() {
    Set<HelpTopic> help = new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance());
    for (String s : commandMap.keySet()) {
      if (!s.contains(".")) {
        org.bukkit.command.Command cmd = map.getCommand(s);
        HelpTopic topic = new GenericCommandHelpTopic(cmd);
        help.add(topic);
      }
    }
    IndexHelpTopic topic = new IndexHelpTopic(plugin.getName(), "All commands for " + plugin.getName(), null, help,
        "Below is a list of all " + plugin.getName() + " commands:");
    Bukkit.getServer().getHelpMap().addTopic(topic);
  }

  private void registerCommand(Command command, String label, Method m, Object obj) {
    Entry<Method, Object> entry = new AbstractMap.SimpleEntry<>(m, obj);
    commandMap.put(label.toLowerCase(), entry);
    String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
    if (map.getCommand(cmdLabel) == null) {
      org.bukkit.command.Command cmd = new BukkitCommand(cmdLabel, plugin);
      map.register(plugin.getName(), cmd);
    }
    if (!command.description().equalsIgnoreCase("") && cmdLabel == label) {
      map.getCommand(cmdLabel).setDescription(command.description());
    }
    if (!command.usage().equalsIgnoreCase("") && cmdLabel == label) {
      map.getCommand(cmdLabel).setUsage(command.usage());
    }
  }

  private void registerCompleter(String label, Method m, Object obj) {
    String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
    if (map.getCommand(cmdLabel) == null) {
      org.bukkit.command.Command command = new BukkitCommand(cmdLabel, plugin);
      map.register(plugin.getName(), command);
    }
    if (map.getCommand(cmdLabel) instanceof BukkitCommand) {
      BukkitCommand command = (BukkitCommand) map.getCommand(cmdLabel);
      if (command.completer == null) {
        command.completer = new BukkitCompleter();
      }
      command.completer.addCompleter(label, m, obj);
    } else if (map.getCommand(cmdLabel) instanceof PluginCommand) {
      try {
        Object command = map.getCommand(cmdLabel);
        Field field = command.getClass().getDeclaredField("completer");
        field.setAccessible(true);
        if (field.get(command) == null) {
          BukkitCompleter completer = new BukkitCompleter();
          completer.addCompleter(label, m, obj);
          field.set(command, completer);
        } else if (field.get(command) instanceof BukkitCompleter) {
          BukkitCompleter completer = (BukkitCompleter) field.get(command);
          completer.addCompleter(label, m, obj);
        } else {
          System.out.println("Unable to register tab completer " + m.getName()
              + ". A tab completer is already registered for that command!");
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  public void loadCommands(JavaPlugin plugin, String packageName) {
    try {
      List<Class<?>> classes = getClassesInPackage(plugin, packageName);
      for (Class<?> clazz : classes) {
        if (CommandClass.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
          try {
            CommandClass commandClass = (CommandClass) clazz.newInstance();
            registerCommands(commandClass);
          } catch (InstantiationException | IllegalAccessException e) {
            System.out.println("Failed to instantiate command class: " + clazz.getName());
            e.printStackTrace();
          }
        }
      }
    } catch (ClassNotFoundException | IOException e) {
      System.out.println("Failed to load commands from package: " + packageName);
      e.printStackTrace();
    }
  }

  private List<Class<?>> getClassesInPackage(JavaPlugin plugin, String packageName)
      throws ClassNotFoundException, IOException {
    List<Class<?>> classes = new ArrayList<>();
    String path = packageName.replace('.', '/');
    ClassLoader classLoader = plugin.getClass().getClassLoader();
    Enumeration<URL> resources = classLoader.getResources(path);

    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      File file = new File(resource.getFile());

      if (file.isDirectory()) {
        classes.addAll(findClassesInDirectory(file, packageName));
      } else {
        classes.addAll(findClassesInJar(plugin, resource, packageName));
      }
    }
    return classes;
  }

  private List<Class<?>> findClassesInDirectory(File directory, String packageName)
      throws ClassNotFoundException {
    List<Class<?>> classes = new ArrayList<>();
    if (!directory.exists()) {
      return classes;
    }

    File[] files = directory.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          classes.addAll(findClassesInDirectory(file, packageName + "." + file.getName()));
        } else if (file.getName().endsWith(".class")) {
          String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
          try {
            Class<?> clazz = Class.forName(className);
            classes.add(clazz);
          } catch (Throwable e) {
            // Ignore any class loading errors
          }
        }
      }
    }
    return classes;
  }

  private List<Class<?>> findClassesInJar(JavaPlugin plugin, URL resource, String packageName) throws IOException {
    List<Class<?>> classes = new ArrayList<>();
    String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
    try (JarFile jarFile = new JarFile(new File(jarPath))) {
      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        String name = entry.getName();
        if (name.startsWith(packageName.replace('.', '/')) && name.endsWith(".class")) {
          String className = name.replace("/", ".").substring(0, name.length() - 6);
          try {
            Class<?> clazz = Class.forName(className, true, plugin.getClass().getClassLoader());
            classes.add(clazz);
          } catch (Throwable e) {
            // Ignore any class loading errors
          }
        }
      }
    }
    return classes;
  }

  static class BukkitCommand extends org.bukkit.command.Command {

    private final Plugin owningPlugin;
    protected BukkitCompleter completer;

    protected BukkitCommand(String label, Plugin owner) {
      super(label);
      this.owningPlugin = owner;
      this.usageMessage = "";
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
      if (!this.owningPlugin.isEnabled()) {
        return false;
      } else {
        try {
          boolean success = BukkitCommandFramework.INSTANCE.handleCommand(sender, commandLabel, this, args);
          return success;
        } catch (Throwable var6) {
          throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + this.owningPlugin.getDescription().getFullName(), var6);
        }
      }
    }

    @Override
    public List<String> tabComplete(org.bukkit.command.CommandSender sender, String alias, String[] args)
        throws CommandException, IllegalArgumentException {
      Validate.notNull(sender, "Sender cannot be null");
      Validate.notNull(args, "Arguments cannot be null");
      Validate.notNull(alias, "Alias cannot be null");

      List<String> completions = null;
      try {
        if (completer != null) {
          completions = completer.onTabComplete(sender, this, alias, args);
        }
        if (completions == null && owningPlugin instanceof TabCompleter) {
          completions = ((TabCompleter) owningPlugin).onTabComplete(sender, this, alias, args);
        }
      } catch (Throwable ex) {
        StringBuilder message = new StringBuilder();
        message.append("Unhandled exception during tab completion for command '/").append(alias).append(' ');
        for (String arg : args) {
          message.append(arg).append(' ');
        }
        message.deleteCharAt(message.length() - 1).append("' in plugin ")
            .append(owningPlugin.getDescription().getFullName());
        throw new CommandException(message.toString(), ex);
      }

      if (completions == null) {
        return super.tabComplete(sender, alias, args);
      }
      return completions;
    }
  }

  static class BukkitCompleter implements TabCompleter {

    private final Map<String, Entry<Method, Object>> completers = new HashMap<>();

    public void addCompleter(String label, Method m, Object obj) {
      completers.put(label, new AbstractMap.SimpleEntry<>(m, obj));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> onTabComplete(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command,
        String label, String[] args) {
      for (int i = args.length; i >= 0; i--) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(label.toLowerCase());
        for (int x = 0; x < i; x++) {
          if (!args[x].equals("") && !args[x].equals(" ")) {
            buffer.append(".").append(args[x].toLowerCase());
          }
        }
        String cmdLabel = buffer.toString();
        if (completers.containsKey(cmdLabel)) {
          Entry<Method, Object> entry = completers.get(cmdLabel);
          try {
            return (List<String>) entry.getKey().invoke(entry.getValue(), new BukkitCommandSender(sender),
                label, args);
          } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }
      return null;
    }
  }
}
