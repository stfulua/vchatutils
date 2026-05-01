package xyz.vprolabs.chatcontrol;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final ChatControl plugin;
    private boolean chatEnabled;
    private boolean hideJoinMessages;
    private boolean hideLeaveMessages;
    private boolean hideAdvancements;
    private boolean polishAliases;
    private boolean englishAliases;
    private boolean luckPermsIntegration;
    private String prefix;
    private String currentLang;
    private int chatSlowmode;
    private List<String> chatFilter;

    public ConfigManager(ChatControl plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        FileConfiguration config = plugin.getConfig();
        chatEnabled = config.getBoolean("chat-enabled", true);
        hideJoinMessages = config.getBoolean("hide-join-messages", false);
        hideLeaveMessages = config.getBoolean("hide-leave-messages", false);
        hideAdvancements = config.getBoolean("hide-advancements", false);
        polishAliases = config.getBoolean("polish-aliases", true);
        englishAliases = config.getBoolean("english-aliases", true);
        luckPermsIntegration = config.getBoolean("luckperms-integration", false);
        prefix = config.getString("prefix", "<dark_gray>[<red>vProLabs<dark_gray>] ");
        currentLang = config.getString("language", "en");
        chatSlowmode = config.getInt("chat-slowmode", 0);
        chatFilter = config.getStringList("chat-filter");
    }

    public void reload() {
        plugin.reloadConfig();
        load();
    }

    public void saveChatEnabled(boolean enabled) {
        chatEnabled = enabled;
        plugin.getConfig().set("chat-enabled", enabled);
        plugin.saveConfig();
    }

    public boolean isChatEnabled() { return chatEnabled; }
    public boolean isHideJoinMessages() { return hideJoinMessages; }
    public boolean isHideLeaveMessages() { return hideLeaveMessages; }
    public boolean isHideAdvancements() { return hideAdvancements; }
    public boolean isPolishAliases() { return polishAliases; }
    public boolean isEnglishAliases() { return englishAliases; }
    public boolean isLuckPermsIntegration() { return luckPermsIntegration; }
    public String getPrefix() { return prefix; }
    public String getCurrentLang() { return currentLang; }
    public int getChatSlowmode() { return chatSlowmode; }
    public List<String> getChatFilter() { return chatFilter; }
}
