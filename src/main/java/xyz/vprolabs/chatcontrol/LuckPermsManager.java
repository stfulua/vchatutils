package xyz.vprolabs.chatcontrol;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

public class LuckPermsManager {

    private final ChatControl plugin;
    private LuckPerms luckPerms;
    private boolean hooked = false;

    public LuckPermsManager(ChatControl plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup() {
        try {
            if (!plugin.getConfigManager().isLuckPermsIntegration()) {
                hooked = false;
                return;
            }

            if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
                try {
                    luckPerms = LuckPermsProvider.get();
                    hooked = true;
                    plugin.getLogger().info("[ChatControl] LuckPerms hooked successfully!");
                } catch (Exception e) {
                    plugin.getLogger().warning("[ChatControl] Failed to hook LuckPerms: " + e.getMessage());
                    hooked = false;
                }
            } else {
                plugin.getLogger().warning("[ChatControl] LuckPerms integration enabled but LuckPerms not found!");
                hooked = false;
            }
        } catch (Throwable t) {
            BugReport.log(t, "LuckPermsManager.setup");
        }
    }

    public boolean isHooked() { return hooked; }

    public String getPrefix(Player player) {
        if (!hooked) return "";
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();
            return prefix != null ? prefix : "";
        }
        return "";
    }

    public String getSuffix(Player player) {
        if (!hooked) return "";
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String suffix = user.getCachedData().getMetaData().getSuffix();
            return suffix != null ? suffix : "";
        }
        return "";
    }

    public boolean hasChatBypass(Player player) {
        if (player.hasPermission("chatcontrol.bypass")) return true;
        if (!hooked) return false;

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            return user.getInheritedGroups(user.getQueryOptions()).stream()
                    .anyMatch(group -> group.getCachedData().getPermissionData().checkPermission("chatcontrol.bypass").asBoolean());
        }
        return false;
    }

    public String getBypassGroups() {
        if (!hooked) return "LuckPerms not enabled";
        Collection<Group> loadedGroups = luckPerms.getGroupManager().getLoadedGroups();

        return loadedGroups.stream()
                .filter(group -> group.getCachedData().getPermissionData().checkPermission("chatcontrol.bypass").asBoolean())
                .map(Group::getName)
                .collect(Collectors.joining(", "));
    }
}
