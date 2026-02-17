package pl.vprolabs.vchatutils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
<<<<<<< HEAD
=======
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
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
<<<<<<< HEAD
=======
import org.bukkit.event.player.PlayerQuitEvent;
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
<<<<<<< HEAD
=======
import java.util.stream.Collectors;
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)

public class vChatUtils extends JavaPlugin implements Listener {

    private boolean chatEnabled = true;
<<<<<<< HEAD
    private boolean hideJoinMessages = true;
    private boolean hideAdvancements = true;
    private boolean supportVProLabs = true;
=======
    private boolean hideJoinMessages = false;
    private boolean hideLeaveMessages = false;
    private boolean hideAdvancements = false;
    private boolean supportVProLabs = true;
    private boolean polishAliases = true;
    private boolean englishAliases = true;
    private boolean luckPermsIntegration = false;
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
    private String prefix = "&8[&cvProLabs&8] ";
    private String currentLang = "en";

    private FileConfiguration messages;
    private Map<String, FileConfiguration> messageFiles = new HashMap<>();
<<<<<<< HEAD
=======
    private LuckPerms luckPerms;
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)

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
<<<<<<< HEAD
        startBrandAnimation();

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("[vChatUtils] Plugin enabled! Version 1.0.1 by vProLabs");
        getLogger().info("[vChatUtils] Language: " + currentLang.toUpperCase());
=======
        setupLuckPerms();
        startBrandAnimation();

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("[vChatUtils] Plugin enabled! Version 1.0.2 by vProLabs");
        getLogger().info("[vChatUtils] Language: " + currentLang.toUpperCase());
        if (luckPermsIntegration && luckPerms != null) {
            getLogger().info("[vChatUtils] LuckPerms integration enabled!");
        }
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
    }

    @Override
    public void onDisable() {
        stopBrandAnimation();
        getLogger().info("[vChatUtils] Plugin disabled.");
    }

    private void loadConfig() {
        chatEnabled = getConfig().getBoolean("chat-enabled", true);
<<<<<<< HEAD
        hideJoinMessages = getConfig().getBoolean("hide-join-messages", true);
        hideAdvancements = getConfig().getBoolean("hide-advancements", true);
        supportVProLabs = getConfig().getBoolean("support-vprolabs", true);
=======
        hideJoinMessages = getConfig().getBoolean("hide-join-messages", false);
        hideLeaveMessages = getConfig().getBoolean("hide-leave-messages", false);
        hideAdvancements = getConfig().getBoolean("hide-advancements", false);
        supportVProLabs = getConfig().getBoolean("support-vprolabs", true);
        polishAliases = getConfig().getBoolean("polish-aliases", true);
        englishAliases = getConfig().getBoolean("english-aliases", true);
        luckPermsIntegration = getConfig().getBoolean("luckperms-integration", false);
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
        prefix = getConfig().getString("prefix", "&8[&cvProLabs&8] ");
        currentLang = getConfig().getString("language", "en");
    }

<<<<<<< HEAD
=======
    private void setupLuckPerms() {
        if (!luckPermsIntegration) return;

        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                luckPerms = LuckPermsProvider.get();
                getLogger().info("[vChatUtils] LuckPerms hooked successfully!");
            } catch (Exception e) {
                getLogger().warning("[vChatUtils] Failed to hook LuckPerms: " + e.getMessage());
                luckPerms = null;
            }
        } else {
            getLogger().warning("[vChatUtils] LuckPerms integration enabled but LuckPerms not found!");
        }
    }

>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
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

<<<<<<< HEAD
=======
    private String getPlayerPrefix(Player player) {
        if (luckPermsIntegration && luckPerms != null) {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                String prefix = user.getCachedData().getMetaData().getPrefix();
                return prefix != null ? ChatColor.translateAlternateColorCodes('&', prefix) : "";
            }
        }
        return "";
    }

    private String getPlayerSuffix(Player player) {
        if (luckPermsIntegration && luckPerms != null) {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                String suffix = user.getCachedData().getMetaData().getSuffix();
                return suffix != null ? ChatColor.translateAlternateColorCodes('&', suffix) : "";
            }
        }
        return "";
    }

    /**
     * Check if player has chat bypass permission (supports LuckPerms groups)
     */
    private boolean hasChatBypass(Player player) {
        // Check standard Bukkit permission first
        if (player.hasPermission("vchatutils.bypass")) {
            return true;
        }

        // Check LuckPerms group permissions if enabled
        if (luckPermsIntegration && luckPerms != null) {
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                // Check if any of player's groups have vchatutils.bypass permission
                return user.getInheritedGroups(user.getQueryOptions())
                        .stream()
                        .anyMatch(group -> group.getCachedData().getPermissionData().checkPermission("vchatutils.bypass").asBoolean());
            }
        }

        return false;
    }

    /**
     * Get list of groups that have chat bypass (for admin info)
     */
    private String getBypassGroups() {
        if (!luckPermsIntegration || luckPerms == null) return "LuckPerms not enabled";

        return luckPerms.getGroupManager().getLoadedGroups().stream()
                .filter(group -> group.getCachedData().getPermissionData().checkPermission("vchatutils.bypass").asBoolean())
                .map(Group::getName)
                .collect(Collectors.joining(", "));
    }

>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
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

<<<<<<< HEAD
        switch (args[0].toLowerCase()) {
=======
        String subCommand = args[0].toLowerCase();

        // Check if subcommand is valid based on enabled aliases
        boolean isValid = false;

        // English commands
        if (englishAliases) {
            switch (subCommand) {
                case "clear": case "on": case "off": case "status": case "info": case "reload":
                    isValid = true;
                    break;
            }
        }

        // Polish commands
        if (polishAliases && !isValid) {
            switch (subCommand) {
                case "wyczysc": case "wlacz": case "wylacz": case "przeladuj":
                    isValid = true;
                    break;
            }
        }

        if (!isValid) {
            sender.sendMessage(getMessage("commands.unknown"));
            return true;
        }

        // Process command
        switch (subCommand) {
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
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
<<<<<<< HEAD
=======
            case "przeladuj":
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
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
<<<<<<< HEAD
        String advStatus = hideAdvancements ? getMessage("commands.status.hidden") : getMessage("commands.status.visible");
=======
        String leaveStatus = hideLeaveMessages ? getMessage("commands.status.hidden") : getMessage("commands.status.visible");
        String advStatus = hideAdvancements ? getMessage("commands.status.hidden") : getMessage("commands.status.visible");
        String lpStatus = (luckPermsIntegration && luckPerms != null) ? getMessage("commands.status.enabled") : getMessage("commands.status.disabled");
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)

        sender.sendMessage(getMessage("commands.status.header"));
        sender.sendMessage(getMessage("commands.status.chat", "%status%", chatStatus));
        sender.sendMessage(getMessage("commands.status.join-messages", "%status%", joinStatus));
<<<<<<< HEAD
        sender.sendMessage(getMessage("commands.status.advancements", "%status%", advStatus));
=======
        sender.sendMessage(getMessage("commands.status.leave-messages", "%status%", leaveStatus));
        sender.sendMessage(getMessage("commands.status.advancements", "%status%", advStatus));
        sender.sendMessage(getMessage("commands.status.luckperms", "%status%", lpStatus));

        // Show bypass groups if LuckPerms is enabled
        if (luckPermsIntegration && luckPerms != null && sender.hasPermission("vchatutils.admin")) {
            String bypassGroups = getBypassGroups();
            sender.sendMessage(getMessage("commands.status.bypass-groups", "%groups%", bypassGroups));
        }

>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
        sender.sendMessage(getMessage("commands.status.footer"));
        return true;
    }

    private boolean reloadPlugin(CommandSender sender) {
        try {
            reloadConfig();
            loadConfig();
<<<<<<< HEAD
            loadMessages();
=======
            setupLuckPerms();
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)

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
<<<<<<< HEAD
        if (!chatEnabled && !event.getPlayer().hasPermission("vchatutils.bypass")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(getMessage("chat.disabled"));
=======
        Player player = event.getPlayer();

        if (!chatEnabled && !hasChatBypass(player)) {
            event.setCancelled(true);
            player.sendMessage(getMessage("chat.disabled"));
            return;
        }

        // LuckPerms chat formatting
        if (luckPermsIntegration && luckPerms != null) {
            String lpPrefix = getPlayerPrefix(player);
            String lpSuffix = getPlayerSuffix(player);

            String format = lpPrefix + player.getName() + lpSuffix + " &8» &f%2$s";
            event.setFormat(ChatColor.translateAlternateColorCodes('&', format));
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
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
<<<<<<< HEAD
=======
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (hideLeaveMessages) {
            event.quitMessage(null);
        }
    }

    @EventHandler
>>>>>>> 356f130 (v1.0.2 - Added LuckPerms integration and alias toggles)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if (hideAdvancements) {
            event.message(null);
        }
    }
}
