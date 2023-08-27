package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


import static me.geox25.swifteco.SwiftManager.Types.*;
import me.geox25.swifteco.SwiftManager.Types;

import java.util.Arrays;
import java.util.List;

public class InterestSubCmd extends SubCommand {


    public InterestSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("interest");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender)) {
            return;
        }

        boolean valid = serviceManager.checkArgs(args, new Types[]{DOUBLE, DOUBLE}, sender);
        if (!valid) { return; }

        double interest_rate = Double.parseDouble(args[0]) / 100;
        double loan_rate = Double.parseDouble(args[1]) / 100;
        double interest_after = (1 + interest_rate);
        double loan_after = (1 + loan_rate);

        List<String> account_list = dataFile.getStringList("account_list");
        double total = 0;

        for (String account : account_list) {
            String type = dataFile.getString("account." + account + ".type");
            double balance = dataFile.getDouble("account." + account + ".balance");
            double credit_balance = dataFile.getDouble("account." + account + ".creditBalance");

            assert type != null;
            if (type.equals("checking") || type.equals("savings") || type.equals("ira")) {
                total += (interest_rate * balance);
                dataFile.set("account." + account + ".balance", interest_after * balance);
                dataFile.set("account." + account + ".creditBalance", Math.ceil(loan_after * credit_balance));
            }
        }

        String i_acc = config.getString("interestAccount");
        double i_acc_bal = dataFile.getDouble("account." + i_acc + ".balance");
        dataFile.set("account." + i_acc + ".balance", i_acc_bal - total);

        sender.sendMessage(ChatColor.DARK_AQUA + "========================");
        sender.sendMessage(ChatColor.GRAY + "Interest @: " + ChatColor.BLUE + (interest_rate * 100) + "%");
        sender.sendMessage(ChatColor.GRAY + "Total: " + ChatColor.AQUA + "$" + String.format("%,.2f", total));
        sender.sendMessage(ChatColor.DARK_AQUA + "========================");
    }
}
