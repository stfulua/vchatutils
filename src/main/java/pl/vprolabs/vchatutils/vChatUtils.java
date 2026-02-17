package pl.vprolabs.vchatutils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class vChatUtils extends JavaPlugin implements Listener {

    private boolean chatEnabled = true;
    private boolean hideJoinMessages = true;
    private boolean hideAdvancements = true;
    private boolean supportVProLabs = true;
    private String prefix = "&8[&cvProLabs&8] ";
    private String currentLang = "en";

    private FileConfiguration messages;
    private Map<String, FileConfiguration> messageFiles = new HashMap<>();

    // Animated brand variables
    private int brandTaskId = -1;
    private int colorIndex = 0;
    private final String[] BRAND_COLORS = {"#00FFFF", "#00CED1", "#1E90FF", "#9370DB", "#8A2BE2", "#FF00FF", "#FF1493"};
    private final String BRAND_TEXT = "vProLabs";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        loadMessages();
        startBrandAnimation();

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("[vChatUtils] Plugin enabled! Version 1.0.1 by vProLabs");
        getLogger().info("[vChatUtils] Language: " + currentLang.toUpperCase());
    }

    @Override
    public void onDisable() {
        stopBrandAnimation();
        getLogger().info("[vChatUtils] Plugin disabled.");
    }

    private void loadConfig() {
        chatEnabled = getConfig().getBoolean("chat-enabled", true);
        hideJoinMessages = getConfig().getBoolean("hide-join-messages", true);
        hideAdvancements = getConfig().getBoolean("hide-advancements", true);
        supportVProLabs = getConfig().getBoolean("support-vprolabs", true);
        prefix = getConfig().getString("prefix", "&8[&cvProLabs&8] ");
        currentLang = getConfig().getString("language", "en");
    }

    private void loadMessages() {
        // Save default language files if they don't exist
        saveResource("lang/messages-en.yml", false);
        saveResource("lang/messages-pl.yml", false);

        // Load all available languages
        File langFolder = new File(getDataFolder(), "lang");
        if (langFolder.exists()) {
            for (File file : langFolder.listFiles()) {
                if (file.getName().startsWith("messages-") && file.getName().endsWith(".yml")) {
                    String lang = file.getName().replace("messages-", "").replace(".yml", "");
                    messageFiles.put(lang, YamlConfiguration.loadConfiguration(file));
                }
            }
        }

        // Set current language
        if (messageFiles.containsKey(currentLang)) {
            messages = messageFiles.get(currentLang);
        } else {
            messages = messageFiles.getOrDefault("en", new YamlConfiguration());
            getLogger().warning("[vChatUtils] Language '" + currentLang + "' not found! Using English.");
        }
    }

    public String getMessage(String path) {
        String msg = messages.getString(path, "&cMissing message: " + path);
        return ChatColor.translateAlternateColorCodes('&', msg.replace("%prefix%", prefix));
    }

    public String getMessage(String path, String... placeholders) {
        String msg = getMessage(path);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                msg = msg.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return msg;
    }

    private void startBrandAnimation() {
        if (!supportVProLabs) return;

        brandTaskId = new BukkitRunnable() {
            @Override
            public void run() {
                StringBuilder brand = new StringBuilder();
                int length = BRAND_TEXT.length();

                for (int i = 0; i < length; i++) {
                    int colorIdx = (colorIndex + i) % BRAND_COLORS.length;
                    brand.append("<").append(BRAND_COLORS[colorIdx]).append(">")
                         .append(BRAND_TEXT.charAt(i))
                         .append("</").append(BRAND_COLORS[colorIdx]).append(">");
                }

                Component component = MiniMessage.miniMessage().deserialize(brand.toString());

                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendPlayerListHeader(component);
                }

                colorIndex = (colorIndex + 1) % BRAND_COLORS.length;
            }
        }.runTaskTimer(this, 0L, 5L).getTaskId();
    }

    private void stopBrandAnimation() {
        if (brandTaskId != -1) {
            Bukkit.getScheduler().cancelTask(brandTaskId);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("chat")) return false;

        if (args.length == 0) {
            sender.sendMessage(getMessage("commands.usage"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "clear":
            case "wyczysc":
                return clearChat(sender);
            case "on":
            case "wlacz":
                return toggleChat(sender, true);
            case "off":
            case "wylacz":
                return toggleChat(sender, false);
            case "status":
            case "info":
                return showStatus(sender);
            case "reload":
                return reloadPlugin(sender);
            default:
                sender.sendMessage(getMessage("commands.unknown"));
                return true;
        }
    }

    private boolean clearChat(CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < 100; i++) {
                player.sendMessage("");
            }
        }

        String playerName = sender.getName();
        String message = getMessage("commands.clear.success", "%player%", playerName);
        Bukkit.broadcastMessage(message);

        getLogger().info(getMessage("commands.clear.console", "%player%", playerName));
        return true;
    }

    private boolean toggleChat(CommandSender sender, boolean enable) {
        chatEnabled = enable;
        getConfig().set("chat-enabled", chatEnabled);
        saveConfig();

        String playerName = sender.getName();
        String message;

        if (enable) {
            message = getMessage("commands.toggle.enabled", "%player%", playerName);
            getLogger().info(getMessage("commands.toggle.console-enabled", "%player%", playerName));
        } else {
            message = getMessage("commands.toggle.disabled", "%player%", playerName);
            getLogger().info(getMessage("commands.toggle.console-disabled", "%player%", playerName));
        }

        Bukkit.broadcastMessage(message);
        return true;
    }

    private boolean showStatus(CommandSender sender) {
        String chatStatus = chatEnabled ? getMessage("commands.status.enabled") : getMessage("commands.status.disabled");
        String joinStatus = hideJoinMessages ? getMessage("commands.status.hidden") : getMessage("commands.status.visible");
        String advStatus = hideAdvancements ? getMessage("commands.status.hidden") : getMessage("commands.status.visible");

        sender.sendMessage(getMessage("commands.status.header"));
        sender.sendMessage(getMessage("commands.status.chat", "%status%", chatStatus));
        sender.sendMessage(getMessage("commands.status.join-messages", "%status%", joinStatus));
        sender.sendMessage(getMessage("commands.status.advancements", "%status%", advStatus));
        sender.sendMessage(getMessage("commands.status.footer"));
        return true;
    }

    private boolean reloadPlugin(CommandSender sender) {
        try {
            reloadConfig();
            loadConfig();
            loadMessages();

            // Restart brand animation if setting changed
            stopBrandAnimation();
            startBrandAnimation();

            sender.sendMessage(getMessage("other.reload-success"));
            getLogger().info("[vChatUtils] Config reloaded by " + sender.getName());
        } catch (Exception e) {
            sender.sendMessage(getMessage("other.reload-fail"));
            getLogger().severe("[vChatUtils] Failed to reload: " + e.getMessage());
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!chatEnabled && !event.getPlayer().hasPermission("vchatutils.bypass")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getMessage("chat.disabled"));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (hideJoinMessages) {
            event.joinMessage(null);
        }

        // Send animated brand to new player immediately
        if (supportVProLabs) {
            StringBuilder brand = new StringBuilder();
            int length = BRAND_TEXT.length();
            for (int i = 0; i < length; i++) {
                int colorIdx = (colorIndex + i) % BRAND_COLORS.length;
                brand.append("<").append(BRAND_COLORS[colorIdx]).append(">")
                     .append(BRAND_TEXT.charAt(i))
                     .append("</").append(BRAND_COLORS[colorIdx]).append(">");
            }
            Component component = MiniMessage.miniMessage().deserialize(brand.toString());
            event.getPlayer().sendPlayerListHeader(component);
        }
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if (hideAdvancements) {
            event.message(null);
        }
    }
}
