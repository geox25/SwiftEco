package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

import static me.geox25.swifteco.SwiftManager.Types.STRING;

public class RemoveManagerSubCmd extends SubCommand {
    public RemoveManagerSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("removemanager", "rmvmang", "rmvman");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender)) {
            return;
        }

        boolean valid = serviceManager.checkArgs(args, new SwiftManager.Types[]{STRING, STRING}, sender);
        if (!valid) {return;}

        String account = serviceManager.getUUIDOrName(args[0]);
        String uuid = serviceManager.getUUIDOrName(args[1]);

        // If account is invalid
        if (!dataFile.contains("account." + account)) {
            sender.sendMessage(ChatColor.RED + "Invalid Account");
            return;
        }

        if (!dataFile.getStringList("account." + account + ".managers").contains(uuid)) {
            sender.sendMessage(ChatColor.RED + "That account doesn't have that manager!");
            return;
        }

        dataModule.removeManager(account, uuid);
        sender.sendMessage(ChatColor.GREEN + "Success");
    }
}
