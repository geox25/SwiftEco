package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

import static me.geox25.swifteco.SwiftManager.Types.*;



public class GiveSubCmd extends SubCommand {

    public GiveSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("give");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender)) {
            return;
        }

        boolean valid = serviceManager.checkArgs(args, new SwiftManager.Types[]{STRING, DOUBLE}, sender);
        if (!valid) {return;}

        String account = serviceManager.getUUIDOrName(args[0]);

        // Account balance
        double accountBalance = dataFile.getDouble("account." + account + ".balance");

        // Amount to give to the account
        double giveAmount = Double.parseDouble(args[1]);

        // Set new balance to bal + amount
        dataFile.set("account." + account + ".balance", accountBalance + giveAmount);

        sender.sendMessage(ChatColor.GREEN + "Success");
    }
}