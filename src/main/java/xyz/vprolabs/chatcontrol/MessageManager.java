package xyz.vprolabs.chatcontrol;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final ChatControl plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();
    private FileConfiguration messages;
    private Map<String, FileConfiguration> messageFiles = new HashMap<>();

    public MessageManager(ChatControl plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        try {
            if (!new File(plugin.getDataFolder(), "lang/messages-en.yml").exists()) {
                plugin.saveResource("lang/messages-en.yml", false);
            }
            if (!new File(plugin.getDataFolder(), "lang/messages-pl.yml").exists()) {
                plugin.saveResource("lang/messages-pl.yml", false);
            }

            File langFolder = new File(plugin.getDataFolder(), "lang");
            messageFiles.clear();
            if (langFolder.exists() && langFolder.isDirectory()) {
                File[] files = langFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().startsWith("messages-") && file.getName().endsWith(".yml")) {
                            String lang = file.getName().replace("messages-", "").replace(".yml", "");
                            messageFiles.put(lang, YamlConfiguration.loadConfiguration(file));
                        }
                    }
                }
            }

            String currentLang = plugin.getConfigManager().getCurrentLang();
            if (messageFiles.containsKey(currentLang)) {
                messages = messageFiles.get(currentLang);
            } else {
                messages = messageFiles.getOrDefault("en", new YamlConfiguration());
                if (messageFiles.isEmpty()) {
                    plugin.getLogger().severe("No language files loaded!");
                } else {
                    plugin.getLogger().warning("Language '" + currentLang + "' not found! Using English.");
                }
            }
        } catch (Throwable t) {
            BugReport.log(t, "MessageManager.load");
        }
    }

    public Component get(String path) {
        try {
            String msg = messages.getString(path, "<red>Missing message: " + path);
            return miniMessage.deserialize(msg, Placeholder.parsed("prefix", plugin.getConfigManager().getPrefix()));
        } catch (Throwable t) {
            BugReport.log(t, "MessageManager.get", "path=" + path);
            return Component.text("<red>Error loading message: " + path);
        }
    }

    public Component get(String path, String... placeholders) {
        try {
            String msg = messages.getString(path, "<red>Missing message: " + path);
            for (int i = 0; i < placeholders.length; i += 2) {
                if (i + 1 < placeholders.length) {
                    String key = placeholders[i].replace("%", "");
                    msg = msg.replace(placeholders[i], "<" + key + ">");
                }
            }

            Component component = miniMessage.deserialize(msg, Placeholder.parsed("prefix", plugin.getConfigManager().getPrefix()));
            
            for (int i = 0; i < placeholders.length; i += 2) {
                if (i + 1 < placeholders.length) {
                    String key = placeholders[i].replace("%", "");
                    String value = placeholders[i+1];
                    component = component.replaceText(builder -> builder.matchLiteral("<" + key + ">").replacement(value));
                }
            }
            
            return component;
        } catch (Throwable t) {
            BugReport.log(t, "MessageManager.get", "path=" + path);
            return Component.text("<red>Error loading message: " + path);
        }
    }

    public String getLegacy(String path, String... placeholders) {
        return legacySerializer.serialize(get(path, placeholders));
    }

    public void send(CommandSender sender, String path, String... placeholders) {
        sender.sendMessage(getLegacy(path, placeholders));
    }

    public void sendAll(String path, String... placeholders) {
        try {
            String msg = getLegacy(path, placeholders);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(msg);
            }
        } catch (Throwable t) {
            BugReport.log(t, "MessageManager.sendAll", "path=" + path);
        }
    }
}
