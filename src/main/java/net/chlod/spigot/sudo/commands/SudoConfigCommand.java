package net.chlod.spigot.sudo.commands;

import net.chlod.spigot.sudo.SudoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class SudoConfigCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        FileConfiguration config = SudoPlugin.plugin().config;

        if (
            config.getBoolean("use-permission", false)
                && !commandSender.hasPermission("sudo.config")
        ) {
            SudoPlugin.plugin().chat("&cYou are not allowed to access this command.");
        } else if (
            !config.getBoolean("use-permission", false)
                && !config.getStringList("sudoers.console").contains(commandSender.getName())
                && commandSender != Bukkit.getConsoleSender()
        ) {
            SudoPlugin.plugin().chat("&cYou are not allowed to access this command.");
        } else if (strings.length == 0) {
            commandSender.sendMessage(
                SudoPlugin.plugin().chat("&fAvailable options:\n" +
                    " - allow-sudo: Allows a user to use the /sudo command\n" +
                    " - deny-sudo: Disallows a user to use the /sudo command\n" +
                    " - allow-su: Allows a user to use the /su command\n" +
                    " - deny-su: Disallows a user to use the /su command\n")
            );
        } else {
            switch (strings[0]) {
                case "allow-sudo": {
                    List<String> newSudoers = config.getStringList("sudoers.console");
                    newSudoers.add(strings[1]);
                    config.set("sudoers.console", newSudoers);
                    SudoPlugin.plugin().saveConfig();
                    commandSender.sendMessage(SudoPlugin.plugin().chat(
                        strings[1] + " added to list of /sudo users."
                    ));

                    break;
                }
                case "deny-sudo": {
                    List<String> newSudoers = config.getStringList("sudoers.console");
                    newSudoers.remove(strings[1]);
                    config.set("sudoers.console", newSudoers);
                    SudoPlugin.plugin().saveConfig();
                    commandSender.sendMessage(SudoPlugin.plugin().chat(
                        strings[1] + " removed from list of /sudo users."
                    ));

                    break;
                }
                case "allow-su": {
                    List<String> newSudoers = config.getStringList("sudoers.users");
                    newSudoers.add(strings[1]);
                    config.set("sudoers.users", newSudoers);
                    SudoPlugin.plugin().saveConfig();
                    commandSender.sendMessage(SudoPlugin.plugin().chat(
                        strings[1] + " added to list of /su users."
                    ));

                    break;
                }
                case "deny-su": {
                    List<String> newSudoers = config.getStringList("sudoers.users");
                    newSudoers.remove(strings[1]);
                    config.set("sudoers.users", newSudoers);
                    SudoPlugin.plugin().saveConfig();
                    commandSender.sendMessage(SudoPlugin.plugin().chat(
                        strings[1] + " removed from list of /su users."
                    ));

                    break;
                }
                default: {
                    commandSender.sendMessage(SudoPlugin.plugin().chat("&cInvalid option."));

                    break;
                }
            }
        }

        return true;
    }

}
