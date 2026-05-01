package xyz.vprolabs.chatcontrol;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final ChatControl plugin;

    public CommandHandler(ChatControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("chatcontrol").setExecutor(this);
        plugin.getCommand("chatcontrol").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (!command.getName().equalsIgnoreCase("chatcontrol")) return false;

            if (!plugin.getConfigManager().isShortAlias() && label.equalsIgnoreCase("cc")) {
                plugin.getMessageManager().send(sender, "commands.short-alias-disabled");
                return true;
            }

            if (args.length == 0) {
                plugin.getMessageManager().send(sender, "commands.about.header");
                plugin.getMessageManager().send(sender, "commands.about.version", "%version%", plugin.getDescription().getVersion());
                plugin.getMessageManager().send(sender, "commands.about.author");
                plugin.getMessageManager().send(sender, "commands.about.website");
                plugin.getMessageManager().send(sender, "commands.about.discord");
                plugin.getMessageManager().send(sender, "commands.about.help");
                return true;
            }

            String sub = args[0].toLowerCase();
            boolean valid = false;

            if (plugin.getConfigManager().isEnglishAliases()) {
                switch (sub) {
                    case "clear": case "on": case "off": case "status": case "info": case "reload":
                        valid = true; break;
                }
            }

            if (plugin.getConfigManager().isPolishAliases() && !valid) {
                switch (sub) {
                    case "wyczysc": case "wlacz": case "wylacz": case "przeladuj":
                        valid = true; break;
                }
            }

            if (!valid) {
                plugin.getMessageManager().send(sender, "commands.unknown");
                return true;
            }

            if (!sender.hasPermission("chatcontrol.admin")) {
                plugin.getMessageManager().send(sender, "commands.no-permission");
                return true;
            }

            switch (sub) {
                case "clear": case "wyczysc":
                    if (args.length > 1) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            plugin.getMessageManager().send(sender, "commands.clear.not-found");
                            return true;
                        }
                        if (sender instanceof Player) {
                            plugin.getChatManager().clearChat((Player) sender, target);
                        } else {
                            plugin.getChatManager().clearChatForConsole(target);
                        }
                    } else if (sender instanceof Player) {
                        plugin.getChatManager().clearChat((Player) sender);
                    } else {
                        plugin.getChatManager().clearChatForConsole();
                    }
                    return true;
                case "on": case "wlacz":
                    plugin.getChatManager().toggleChat(sender, true);
                    return true;
                case "off": case "wylacz":
                    plugin.getChatManager().toggleChat(sender, false);
                    return true;
                case "status": case "info":
                    plugin.getChatManager().showStatus(sender);
                    return true;
                case "reload": case "przeladuj":
                    reload(sender);
                    return true;
                default:
                    plugin.getMessageManager().send(sender, "commands.unknown");
                    return true;
            }
        } catch (Throwable t) {
            BugReport.log(t, "onCommand", "sender=" + sender.getName() + " args=" + String.join(" ", args));
            sender.sendMessage("§c[ChatControl] §7An internal error occurred. Check bugreport.txt.");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        try {
            if (args.length == 1) {
                List<String> suggestions = new ArrayList<>();
                if (plugin.getConfigManager().isEnglishAliases()) {
                    suggestions.addAll(Arrays.asList("clear", "on", "off", "status", "reload"));
                }
                if (plugin.getConfigManager().isPolishAliases()) {
                    suggestions.addAll(Arrays.asList("wyczysc", "wlacz", "wylacz", "przeladuj"));
                }
                return suggestions.stream()
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (args.length == 2 && (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("wyczysc"))) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        } catch (Throwable t) {
            BugReport.log(t, "onTabComplete");
        }
        return new ArrayList<>();
    }

    private void reload(CommandSender sender) {
        try {
            plugin.getConfigManager().reload();
            plugin.getMessageManager().load();
            plugin.getLuckPermsManager().setup();

            plugin.getMessageManager().send(sender, "other.reload-success");
            plugin.getLogger().info("[ChatControl] Config reloaded by " + sender.getName());
        } catch (Throwable t) {
            BugReport.log(t, "reload", "sender=" + sender.getName());
            plugin.getMessageManager().send(sender, "other.reload-fail");
        }
    }
}
