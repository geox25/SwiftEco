package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

import static me.geox25.swifteco.SwiftManager.Types.*;



public class SetSubCmd extends SubCommand {

    public SetSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("set");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender)) {
            return;
        }

        boolean valid = serviceManager.checkArgs(args, new SwiftManager.Types[]{STRING, DOUBLE}, sender);
        if (!valid) {return;}

        String account = serviceManager.getUUIDOrName(args[0]);

        // Check if account is invalid
        if (!dataFile.contains("account." + account)) {
            sender.sendMessage(ChatColor.RED + "Invalid Account");
            return;
        }

        // Balance set amount
        double amount = Double.parseDouble(args[1]);

        // Set new balance
        dataFile.set("account." + account + ".balance", amount);

        sender.sendMessage(ChatColor.GREEN + "Success");
    }
}
