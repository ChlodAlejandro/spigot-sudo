package net.chlod.spigot.sudo.commands;

import net.chlod.spigot.sudo.SudoPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.Objects;

public class SuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        FileConfiguration config = SudoPlugin.plugin().config;

        if (
            config.getBoolean("use-permission", false)
                && !commandSender.hasPermission("sudo.su")
        ) {
            commandSender.sendMessage(
                SudoPlugin.plugin().chat("&cYou are not allowed to access this command.")
            );
        } else if (
            !config.getBoolean("use-permission", false)
                && !config.getStringList("sudoers.users").contains(commandSender.getName())
                && commandSender != Bukkit.getConsoleSender()
        ) {
            commandSender.sendMessage(
                SudoPlugin.plugin().chat("&cYou are not allowed to access this command.")
            );
        } else if (strings.length < 2) {
            commandSender.sendMessage(
                SudoPlugin.plugin().chat("&cYou need to enter a user and a command.")
            );
        } else {
            if (config.getBoolean("use-log", true))
                SudoPlugin.plugin().log(
                    String.format(
                        "su [%s]: <%s> %s",
                        commandSender.getName(),
                        strings[0],
                        StringUtils.join(Arrays.copyOfRange(strings, 1, strings.length), ' ')
                    )
                );

            try {
                Objects.requireNonNull(Bukkit.getPlayer(strings[0])).performCommand(
                    StringUtils.join(Arrays.copyOfRange(strings, 1, strings.length), ' ')
                );
                commandSender.sendMessage(
                    SudoPlugin.plugin().chat("Command dispatched.")
                );
            } catch (NullPointerException e){
                commandSender.sendMessage(
                    SudoPlugin.plugin().chat("&cUser not found.")
                );
            }
        }

        return true;
    }

}
