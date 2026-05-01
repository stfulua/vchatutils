package xyz.vprolabs.chatcontrol;

import org.bukkit.plugin.java.JavaPlugin;

public class ChatControl extends JavaPlugin {

    private static ChatControl instance;
    private ChatManager chatManager;
    private LuckPermsManager luckPermsManager;
    private MessageManager messageManager;
    private ConfigManager configManager;
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        luckPermsManager = new LuckPermsManager(this);
        chatManager = new ChatManager(this);
        commandHandler = new CommandHandler(this);

        getServer().getPluginManager().registerEvents(chatManager, this);

        getLogger().info("[ChatControl] Plugin enabled! Version " + getDescription().getVersion() + " by vProLabs");
        getLogger().info("[ChatControl] Language: " + configManager.getCurrentLang().toUpperCase());
    }

    @Override
    public void onDisable() {
        instance = null;
        getLogger().info("[ChatControl] Plugin disabled.");
    }

    public static ChatControl getInstance() { return instance; }
    public ChatManager getChatManager() { return chatManager; }
    public LuckPermsManager getLuckPermsManager() { return luckPermsManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public CommandHandler getCommandHandler() { return commandHandler; }
}
