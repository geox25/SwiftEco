package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

import static me.geox25.swifteco.SwiftManager.Types.*;

public class ForcePayoffSubCmd extends SubCommand {

    public ForcePayoffSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("fp", "forcepayoff", "forcePayoff");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender)) {
            return;
        }

        boolean valid = serviceManager.checkArgs(args, new SwiftManager.Types[]{ACCOUNT, DOUBLE, OPT_FLAG}, sender);
        if (!valid) {return;}

        // The name of the account in question
        String account = serviceManager.getUUIDOrName(args[0]);

        // How much the player has
        double balance = dataFile.getDouble("account." + account + ".balance");

        // How much the player owes
        double creditBalance = dataFile.getDouble("account." + account + ".creditBalance");

        // How much they are forced to payoff
        double payoffAmount = Double.parseDouble(args[1]);

        // Make sure that amount paid off is not more than what is owed
        if (payoffAmount > creditBalance) {
            sender.sendMessage(ChatColor.RED + "Payoff Amount is not <= Credit Balance");
            return;
        }

        if (balance < payoffAmount) {
            sender.sendMessage(ChatColor.RED + "That account does not have $" + serviceManager.formatMoney(payoffAmount));
            return;
        }

        dataFile.set("account." + account + ".balance", balance - payoffAmount);
        dataFile.set("account." + account + ".creditBalance", creditBalance - payoffAmount);
        sender.sendMessage(ChatColor.GREEN + "Transaction Posted");
    }
}
