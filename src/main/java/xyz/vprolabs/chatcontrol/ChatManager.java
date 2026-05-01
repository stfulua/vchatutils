package xyz.vprolabs.chatcontrol;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager implements Listener {

    private final ChatControl plugin;
    private final Map<UUID, Long> slowmodeCooldowns = new HashMap<>();

    public ChatManager(ChatControl plugin) {
        this.plugin = plugin;
    }

    private static final String CLEAR_BUFFER = new String(new char[100]).replace("\0", "\n");

    public void clearChat(Player sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Component.text(CLEAR_BUFFER));
        }

        String playerName = sender.getName();
        Component msg = plugin.getMessageManager().get("commands.clear.success", "%player%", playerName);
        Bukkit.broadcast(msg);
        plugin.getLogger().info(playerName + " cleared the chat.");
    }

    public void clearChatForConsole() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Component.text(CLEAR_BUFFER));
        }
        Component msg = plugin.getMessageManager().get("commands.clear.success", "%player%", "Console");
        Bukkit.broadcast(msg);
        plugin.getLogger().info("Console cleared the chat.");
    }

    public void toggleChat(CommandSender sender, boolean enable) {
        plugin.getConfigManager().saveChatEnabled(enable);

        String playerName = sender.getName();
        Component msg;

        if (enable) {
            msg = plugin.getMessageManager().get("commands.toggle.enabled", "%player%", playerName);
        } else {
            msg = plugin.getMessageManager().get("commands.toggle.disabled", "%player%", playerName);
        }

        Bukkit.broadcast(msg);
    }

    public void showStatus(CommandSender sender) {
        MessageManager mm = plugin.getMessageManager();
        ConfigManager cm = plugin.getConfigManager();
        LuckPermsManager lm = plugin.getLuckPermsManager();

        Component chatStatus = cm.isChatEnabled() ? mm.get("commands.status.enabled") : mm.get("commands.status.disabled");
        Component joinStatus = cm.isHideJoinMessages() ? mm.get("commands.status.hidden") : mm.get("commands.status.visible");
        Component leaveStatus = cm.isHideLeaveMessages() ? mm.get("commands.status.hidden") : mm.get("commands.status.visible");
        Component advStatus = cm.isHideAdvancements() ? mm.get("commands.status.hidden") : mm.get("commands.status.visible");
        Component lpStatus = (cm.isLuckPermsIntegration() && lm.isHooked()) ? mm.get("commands.status.enabled") : mm.get("commands.status.disabled");

        sender.sendMessage(mm.get("commands.status.header"));
        sender.sendMessage(mm.get("commands.status.chat", "%status%", serialize(chatStatus)));
        sender.sendMessage(mm.get("commands.status.join-messages", "%status%", serialize(joinStatus)));
        sender.sendMessage(mm.get("commands.status.leave-messages", "%status%", serialize(leaveStatus)));
        sender.sendMessage(mm.get("commands.status.advancements", "%status%", serialize(advStatus)));
        sender.sendMessage(mm.get("commands.status.luckperms", "%status%", serialize(lpStatus)));

        if (cm.isLuckPermsIntegration() && lm.isHooked() && sender.hasPermission("chatcontrol.admin")) {
            sender.sendMessage(mm.get("commands.status.bypass-groups", "%groups%", lm.getBypassGroups()));
        }

        sender.sendMessage(mm.get("commands.status.footer"));
    }

    private String serialize(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Admin bypass
        if (player.hasPermission("chatcontrol.admin")) return;

        // Chat enabled check
        if (!plugin.getConfigManager().isChatEnabled() &&
            !plugin.getLuckPermsManager().hasChatBypass(player)) {
            event.setCancelled(true);
            player.sendMessage(plugin.getMessageManager().get("chat.disabled"));
            return;
        }

        // Slowmode logic
        int slowmodeSeconds = plugin.getConfigManager().getChatSlowmode();
        if (slowmodeSeconds > 0) {
            long lastMessage = slowmodeCooldowns.getOrDefault(player.getUniqueId(), 0L);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastMessage < slowmodeSeconds * 1000L) {
                event.setCancelled(true);
                long secondsLeft = (slowmodeSeconds * 1000L - (currentTime - lastMessage)) / 1000L + 1;
                player.sendMessage(plugin.getMessageManager().get("chat.cooldown", "%seconds%", String.valueOf(secondsLeft)));
                return;
            }
            slowmodeCooldowns.put(player.getUniqueId(), currentTime);
        }

        // Regex Filter logic
        for (String pattern : plugin.getConfigManager().getChatFilter()) {
            if (message.matches(".*" + pattern + ".*")) {
                event.setCancelled(true);
                player.sendMessage(plugin.getMessageManager().get("chat.filtered"));
                return;
            }
        }

        // MiniMessage format handling
        if (plugin.getConfigManager().isLuckPermsIntegration() &&
            plugin.getLuckPermsManager().isHooked()) {
            
            String prefix = plugin.getLuckPermsManager().getPrefix(player);
            String suffix = plugin.getLuckPermsManager().getSuffix(player);
            
            // Convert MiniMessage to legacy for AsyncPlayerChatEvent (legacy event)
            String format = prefix + player.getName() + suffix + " &8» &f%2$s";
            event.setFormat(LegacyComponentSerializer.legacyAmpersand().serialize(MiniMessage.miniMessage().deserialize(format)));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfigManager().isHideJoinMessages()) {
            event.joinMessage(null);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.getConfigManager().isHideLeaveMessages()) {
            event.quitMessage(null);
        }
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if (plugin.getConfigManager().isHideAdvancements()) {
            event.message(null);
        }
    }
}
