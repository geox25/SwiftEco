package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

import static me.geox25.swifteco.SwiftManager.Types.*;



public class AddFlagSubCmd extends SubCommand {

    public AddFlagSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("addflag");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender)) {
            return;
        }

        boolean valid = serviceManager.checkArgs(args, new SwiftManager.Types[]{STRING, STRING}, sender);
        if (!valid) {return;}

        String account = serviceManager.getUUIDOrName(args[0]);
        String flag = args[1];

        // If account is invalid
        if (!dataFile.contains("account." + account)) {
            sender.sendMessage(ChatColor.RED + "Invalid Account");
            return;
        }

        if (dataFile.getStringList("account." + account + ".flags").contains(flag)) {
            sender.sendMessage(ChatColor.RED + "That account already has that flag!");
            return;
        }

        dataModule.addFlag(account, flag, dataFile);
        sender.sendMessage(ChatColor.GREEN + "Success");
    }
}
