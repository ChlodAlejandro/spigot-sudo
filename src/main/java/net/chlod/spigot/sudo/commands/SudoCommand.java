package net.chlod.spigot.sudo.commands;

import net.chlod.spigot.sudo.SudoPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class SudoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        FileConfiguration config = SudoPlugin.plugin().config;

        if (
            config.getBoolean("use-permission", false)
            && !commandSender.hasPermission("sudo.sudo")
        ) {
            commandSender.sendMessage(
                SudoPlugin.plugin().chat("&cYou are not allowed to access this command.")
            );
        } else if (
            !config.getBoolean("use-permission", false)
            && !config.getStringList("sudoers.console").contains(commandSender.getName())
            && commandSender != Bukkit.getConsoleSender()
        ) {
            commandSender.sendMessage(
                SudoPlugin.plugin().chat("&cYou are not allowed to access this command.")
            );
        } else if (strings.length == 0) {
            commandSender.sendMessage(
                SudoPlugin.plugin().chat("&cYou need to enter a command.")
            );
        }else {
            if (config.getBoolean("use-log", true))
                SudoPlugin.plugin().log(
                    String.format("sudo [%s]: %s", commandSender.getName(), StringUtils.join(strings, ' '))
                );

            SudoPlugin.plugin().log.info(StringUtils.join(strings, ' '));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.join(strings, ' '));
            commandSender.sendMessage(
                SudoPlugin.plugin().chat("Command dispatched.")
            );
        }

        return true;
    }

}
