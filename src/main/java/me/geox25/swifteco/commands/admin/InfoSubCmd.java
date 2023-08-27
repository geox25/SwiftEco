package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.SwiftEco;
import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import static me.geox25.swifteco.SwiftManager.*;
import static me.geox25.swifteco.SwiftManager.Types.*;

import me.geox25.swifteco.SwiftManager.Types;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InfoSubCmd extends SubCommand {

    public InfoSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("info");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender) && !sender.hasPermission("swift.info")) {
            return;
        }

        boolean valid = serviceManager.checkArgs(args, new Types[]{STRING}, sender);
        if (!valid) {return;}

        String account = serviceManager.getUUIDOrName(args[0]);

        // Check if account is invalid
        if (!dataFile.contains("account." + account)) {
            sender.sendMessage(ChatColor.RED + "Invalid Account");
            return;
        }

        // If sender is not an admin, they cannot access restricted accounts
        if (!configModule.isAdmin(sender)) {
            if (dataModule.checkFlag(account, "%", dataFile)) {
                sender.sendMessage(ChatColor.RED + "Restricted Account!");
                return;
            }
        }

        int credit = dataFile.getInt("account." + account + ".creditScore");
        double credit_balance = Math.ceil(dataFile.getDouble("account." + account + ".creditBalance"));
        double credit_ceiling = config.getDouble("creditCeiling");
        double p = (double) credit / 1000;
        double credit_limit = (p * credit_ceiling);

        // Player of account
        String player = dataFile.getString("account." + account + ".player");
        if (player == null) {
            player = ChatColor.RED + "null";
        }

        ChatColor color = ChatColor.GRAY;
        ChatColor balance_color = ChatColor.RED;

        if (credit >= 0)
            color = ChatColor.RED;
        if (credit >= 300)
            color = ChatColor.YELLOW;
        if (credit >= 500)
            color = ChatColor.GRAY;
        if (credit >= 700)
            color = ChatColor.GOLD;
        if (credit >= 900)
            color = ChatColor.DARK_GREEN;

        if (credit_balance == 0)
            balance_color = ChatColor.DARK_GREEN;

        sender.sendMessage(ChatColor.DARK_AQUA + "========================");
        sender.sendMessage(ChatColor.GRAY + "Balance: " + ChatColor.AQUA + "$" + serviceManager.formatMoney(dataFile.getDouble("account." + account + ".balance")));
        sender.sendMessage(ChatColor.GRAY + "Credit Score: " + color + credit);
        sender.sendMessage(ChatColor.GRAY + "Credit Limit: " + ChatColor.AQUA + "$" + serviceManager.formatMoney(credit_limit));
        sender.sendMessage(ChatColor.GRAY + "Credit Balance (Owed): " + balance_color + "$" + serviceManager.formatMoney(credit_balance));
        sender.sendMessage(ChatColor.GRAY + "Player: " + ChatColor.AQUA + player);
        sender.sendMessage(ChatColor.GRAY + "Managers: " + dataFile.getStringList("account." + account + ".managers"));
        sender.sendMessage(ChatColor.GRAY + "Type: " + ChatColor.BLUE + Objects.requireNonNull(dataFile.getString("account." + account + ".type")).toUpperCase());
        sender.sendMessage(ChatColor.GRAY + "Flags: " + dataFile.getStringList("account." + account + ".flags"));
        sender.sendMessage(ChatColor.DARK_AQUA + "========================");
    }
}
