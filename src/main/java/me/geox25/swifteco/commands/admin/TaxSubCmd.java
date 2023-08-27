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

public class TaxSubCmd extends SubCommand {

    public TaxSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("tax");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender)) {
            return;
        }

        double tax_rate;

        if (args.length == 0) {
            tax_rate = config.getDouble("checkingTaxRate") / 100;
        } else {
            boolean valid = serviceManager.checkArgs(args, new Types[]{DOUBLE}, sender);
            if (!valid) {
                return;
            }

            tax_rate = Double.parseDouble(args[0]) / 100;
        }

        List<String> account_list = dataFile.getStringList("account_list");
        double after = (1 - tax_rate);

        double savings_rate = config.getDouble("savingsTaxRate") / 100;
        double savings_after = (1 - (config.getDouble("savingsTaxRate") / 100));

        double total = 0;

        for (String account : account_list) {
            String type = dataFile.getString("account." + account + ".type");
            double balance = dataFile.getDouble("account." + account + ".balance");
            double essentials_bal = economy.getBalance(account);

            assert type != null;
            if (type.equals("checking")) {
                total += (tax_rate * balance);
                total += (tax_rate * essentials_bal);
                dataFile.set("account." + account + ".balance", after * balance);
                economy.withdrawPlayer(sender.getName(), tax_rate * essentials_bal);
                int credit_score = dataFile.getInt("account." + account + ".creditScore");
                double lastLoan = dataFile.getDouble("account." + account + ".lastLoan");
                double credit_weight = config.getDouble("creditWeight") / 100;

                double new_credit_score = credit_score + (credit_weight * lastLoan);
                if (new_credit_score > 1000) {
                    dataFile.set("account." + account + ".creditScore", 1000);
                } else {
                    dataFile.set("account." + account + ".creditScore", new_credit_score);
                }

                dataFile.set("account." + account + ".lastLoan", 0);
            } else if (type.equals("savings")) {
                total += (savings_rate * balance);
                dataFile.set("account." + account + ".balance", savings_after * balance);
            }
        }
        // Add tax money to government account
        double gov_bal = dataFile.getDouble("account.gov.balance");
        dataFile.set("account.gov.balance", gov_bal + total);

        sender.sendMessage(ChatColor.DARK_AQUA + "========================");
        sender.sendMessage(ChatColor.GRAY + "Taxed Checking Accounts @: " + ChatColor.BLUE + (tax_rate * 100) + "%");
        sender.sendMessage(ChatColor.GRAY + "Taxed Savings Accounts @: " + ChatColor.BLUE + (savings_rate * 100) + "%");
        sender.sendMessage(ChatColor.GRAY + "Total: " + ChatColor.AQUA + "$" + String.format("%,.2f", total));
        sender.sendMessage(ChatColor.DARK_AQUA + "========================");
    }
}