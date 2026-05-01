package xyz.vprolabs.chatcontrol;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class BugReport {
    private static final String DISCORD = "https://discord.gg/SNzUYWbc5Q";

    private BugReport() {}

    public static void log(Throwable error, String context) {
        log(error, context, null);
    }

    public static void log(Throwable error, String context, String extra) {
        ChatControl plugin = ChatControl.getInstance();
        if (plugin == null) {
            error.printStackTrace();
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        try {
            File file = new File(plugin.getDataFolder(), "bugreport.txt");
            boolean newFile = !file.exists();
            try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
                if (newFile) {
                    pw.println("=== ChatControl Bug Report ===");
                    pw.println("Something went wrong! Send this file to:");
                    pw.println(DISCORD);
                    pw.println("===============================");
                    pw.println();
                }
                pw.println("--- " + timestamp + " ---");
                pw.println("Context: " + context);
                if (extra != null) pw.println("Extra: " + extra);
                pw.println("Server: " + Bukkit.getName() + " " + Bukkit.getVersion());
                pw.println("Plugin: ChatControl " + plugin.getDescription().getVersion());
                pw.println("Java: " + System.getProperty("java.version"));
                pw.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
                error.printStackTrace(pw);
                pw.println();
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to write bugreport.txt: " + e.getMessage());
        }

        plugin.getLogger().severe("=== ChatControl Error ===");
        plugin.getLogger().severe("Context: " + context);
        plugin.getLogger().severe("Error: " + error.getClass().getName() + ": " + error.getMessage());
        plugin.getLogger().severe("Details written to plugins/ChatControl/bugreport.txt");
        plugin.getLogger().severe("Report at: " + DISCORD);

        String msg = "§c[ChatControl] §7An error occurred! Check console for details.";
        String msg2 = "§c[ChatControl] §7Report at: " + DISCORD;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("chatcontrol.admin")) {
                p.sendMessage(msg);
                p.sendMessage(msg2);
            }
        }
    }
}
