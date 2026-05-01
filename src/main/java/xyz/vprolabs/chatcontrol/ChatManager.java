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
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(CLEAR_BUFFER);
            }

            plugin.getMessageManager().sendAll("commands.clear.success", "%player%", sender.getName());
            plugin.getLogger().info(sender.getName() + " cleared the chat.");
        } catch (Throwable t) {
            BugReport.log(t, "clearChat", "sender=" + sender.getName());
        }
    }

    public void clearChatForConsole() {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(CLEAR_BUFFER);
            }
            plugin.getMessageManager().sendAll("commands.clear.success", "%player%", "Console");
            plugin.getLogger().info("Console cleared the chat.");
        } catch (Throwable t) {
            BugReport.log(t, "clearChatForConsole");
        }
    }

    public void toggleChat(CommandSender sender, boolean enable) {
        try {
            plugin.getConfigManager().saveChatEnabled(enable);

            String path = enable ? "commands.toggle.enabled" : "commands.toggle.disabled";
            plugin.getMessageManager().sendAll(path, "%player%", sender.getName());
        } catch (Throwable t) {
            BugReport.log(t, "toggleChat", "enable=" + enable + " sender=" + sender.getName());
        }
    }

    public void showStatus(CommandSender sender) {
        try {
            MessageManager mm = plugin.getMessageManager();
            ConfigManager cm = plugin.getConfigManager();
            LuckPermsManager lm = plugin.getLuckPermsManager();

            Component chatStatus = cm.isChatEnabled() ? mm.get("commands.status.enabled") : mm.get("commands.status.disabled");
            Component joinStatus = cm.isHideJoinMessages() ? mm.get("commands.status.hidden") : mm.get("commands.status.visible");
            Component leaveStatus = cm.isHideLeaveMessages() ? mm.get("commands.status.hidden") : mm.get("commands.status.visible");
            Component advStatus = cm.isHideAdvancements() ? mm.get("commands.status.hidden") : mm.get("commands.status.visible");
            Component lpStatus = (cm.isLuckPermsIntegration() && lm.isHooked()) ? mm.get("commands.status.enabled") : mm.get("commands.status.disabled");

            mm.send(sender, "commands.status.header");
            mm.send(sender, "commands.status.chat", "%status%", serialize(chatStatus));
            mm.send(sender, "commands.status.join-messages", "%status%", serialize(joinStatus));
            mm.send(sender, "commands.status.leave-messages", "%status%", serialize(leaveStatus));
            mm.send(sender, "commands.status.advancements", "%status%", serialize(advStatus));
            mm.send(sender, "commands.status.luckperms", "%status%", serialize(lpStatus));

            if (cm.isLuckPermsIntegration() && lm.isHooked() && sender.hasPermission("chatcontrol.admin")) {
                mm.send(sender, "commands.status.bypass-groups", "%groups%", lm.getBypassGroups());
            }

            mm.send(sender, "commands.status.footer");
        } catch (Throwable t) {
            BugReport.log(t, "showStatus", "sender=" + sender.getName());
        }
    }

    private String serialize(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        try {
            Player player = event.getPlayer();
            String message = event.getMessage();

            if (player.hasPermission("chatcontrol.admin")) return;

            if (!plugin.getConfigManager().isChatEnabled() &&
                !plugin.getLuckPermsManager().hasChatBypass(player)) {
                event.setCancelled(true);
                player.sendMessage(plugin.getMessageManager().getLegacy("chat.disabled"));
                return;
            }

            int slowmodeSeconds = plugin.getConfigManager().getChatSlowmode();
            if (slowmodeSeconds > 0) {
                long lastMessage = slowmodeCooldowns.getOrDefault(player.getUniqueId(), 0L);
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastMessage < slowmodeSeconds * 1000L) {
                    event.setCancelled(true);
                    long secondsLeft = (slowmodeSeconds * 1000L - (currentTime - lastMessage)) / 1000L + 1;
                    player.sendMessage(plugin.getMessageManager().getLegacy("chat.cooldown", "%seconds%", String.valueOf(secondsLeft)));
                    return;
                }
                slowmodeCooldowns.put(player.getUniqueId(), currentTime);
            }

            for (String pattern : plugin.getConfigManager().getChatFilter()) {
                if (message.matches(".*" + pattern + ".*")) {
                    event.setCancelled(true);
                    player.sendMessage(plugin.getMessageManager().getLegacy("chat.filtered"));
                    return;
                }
            }

            applyChatFormat(event, player);
        } catch (Throwable t) {
            BugReport.log(t, "onPlayerChat", "player=" + event.getPlayer().getName());
        }
    }

    private void applyChatFormat(AsyncPlayerChatEvent event, Player player) {
        try {
            String format = plugin.getConfigManager().getChatFormat();
            if (format == null || format.isEmpty()) return;

            String prefix = "";
            String suffix = "";

            if (plugin.getConfigManager().isLuckPermsIntegration() && plugin.getLuckPermsManager().isHooked()) {
                prefix = plugin.getLuckPermsManager().getPrefix(player);
                suffix = plugin.getLuckPermsManager().getSuffix(player);
            }

            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                try {
                    Class<?> papi = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                    format = (String) papi.getMethod("setPlaceholders", org.bukkit.entity.Player.class, String.class)
                        .invoke(null, player, format);
                } catch (Exception ignored) {}
            }

            format = format.replace("{prefix}", prefix)
                           .replace("{suffix}", suffix)
                           .replace("{username}", "%1$s")
                           .replace("{message}", "%2$s");

            event.setFormat(LegacyComponentSerializer.legacyAmpersand().serialize(
                MiniMessage.miniMessage().deserialize(format)));
        } catch (Throwable t) {
            BugReport.log(t, "applyChatFormat", "player=" + player.getName() + " format=" + plugin.getConfigManager().getChatFormat());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            if (plugin.getConfigManager().isHideJoinMessages()) {
                event.setJoinMessage(null);
            }
        } catch (Throwable t) {
            BugReport.log(t, "onPlayerJoin", "player=" + event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            if (plugin.getConfigManager().isHideLeaveMessages()) {
                event.setQuitMessage(null);
            }
        } catch (Throwable t) {
            BugReport.log(t, "onPlayerQuit", "player=" + event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        try {
            if (plugin.getConfigManager().isHideAdvancements()) {
                try {
                    event.message(null);
                } catch (NoSuchMethodError e) {
                    plugin.getLogger().warning("Advancement hiding requires Paper or fork.");
                }
            }
        } catch (Throwable t) {
            BugReport.log(t, "onAdvancement", "player=" + event.getPlayer().getName());
        }
    }
}
