package net.chlod.spigot.sudo;

import com.google.common.base.MoreObjects;
import net.chlod.spigot.sudo.commands.SuCommand;
import net.chlod.spigot.sudo.commands.SudoCommand;
import net.chlod.spigot.sudo.commands.SudoConfigCommand;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.logging.Logger;

public class SudoPlugin extends JavaPlugin {

    public Logger log;
    public FileConfiguration config;
    public FileOutputStream sudoLogStream;

    public static SudoPlugin plugin() {
        return SudoPlugin.getPlugin(SudoPlugin.class);
    }

    @Override
    public void onEnable() {
        this.log = this.getLogger();
        this.log.info("sudo enabling...");

        // Save config if does not exist.
        this.saveDefaultConfig();
        this.config = getConfig();

        try {
            // Create the sudo log file stream.
            if (config.getBoolean("use-log", true)) {
                File sudoLog = new File(SudoPlugin.plugin().getDataFolder(), "sudo.log");
                sudoLog.createNewFile();
                sudoLogStream = new FileOutputStream(sudoLog);
            }

            // Register commands.
            Objects.requireNonNull(this.getCommand("sudo")).setExecutor(new SudoCommand());
            Objects.requireNonNull(this.getCommand("console")).setExecutor(new SudoCommand());
            Objects.requireNonNull(this.getCommand("su")).setExecutor(new SuCommand());
            Objects.requireNonNull(this.getCommand("sudoconfig")).setExecutor(new SudoConfigCommand());
        } catch (NullPointerException e) {
            this.log.severe("sudo failed to load: Commands cannot be registered.");
        } catch (IOException e) {
            this.log.severe("sudo failed to load: Log file cannot be created.");
        }
    }

    @Override
    public void onDisable() {
        this.log.info("sudo disabling...");
        if (sudoLogStream != null) {
            try {
                sudoLogStream.flush();
                sudoLogStream.close();
            } catch (IOException e) {
                this.log.severe("sudo failed to flush the log file.");
            }
        }
        this.log.info("sudo disabled.");
    }

    public String chat(String message) {
        return ChatColor.translateAlternateColorCodes(
            '&',
            MoreObjects.firstNonNull(
                config.getString("message-prefix", "&8[sudo]: &7"), "&8[sudo]: &7"
            )
        ) + ChatColor.translateAlternateColorCodes('&', message);
    }

    public void log(String entry) {
        if (config.getBoolean("use-log", true) && sudoLogStream != null) {
            try {
                sudoLogStream.write(String.format("%s\n", entry).getBytes(StandardCharsets.UTF_8));
                sudoLogStream.flush();
            } catch (IOException e) {
                log.warning(String.format("Failed to add entry to sudo log: %s", entry));
            }
        }
    }

}
