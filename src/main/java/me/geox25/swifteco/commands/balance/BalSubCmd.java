package me.geox25.swifteco.commands.balance;

import me.geox25.swifteco.commands.SubCommand;
import static me.geox25.swifteco.SwiftManager.*;

import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BalSubCmd extends SubCommand {

    public BalSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("bal", "balance");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        double balance;

        if (args.length == 0) {
            String account = serviceManager.getUUIDOrName(sender.getName());
            balance = dataFile.getDouble("account." + account + ".balance");
        } else {
            boolean isAuth;
            boolean validAccount;
            String account = serviceManager.getUUIDOrName(args[0]);
            String senderUUID = serviceManager.getUUIDOrName(sender.getName());
            String playerName = sender.getName();

            if (dataFile.contains("account." + account)) {
                validAccount = true;
                isAuth = Objects.equals(dataFile.getString("account." + account + ".player"), playerName) || dataFile.getStringList("account." + account + ".managers").contains(senderUUID);
            } else {
                isAuth = false;
                validAccount = false;
            }

            if (isAuth || sender.hasPermission("swift.admin")) {
                if (!validAccount) {
                    sender.sendMessage(ChatColor.RED + "Invalid Account!");
                    return;
                }
                balance = dataFile.getDouble("account." + account + ".balance");
            } else {
                sender.sendMessage(ChatColor.RED + "Access Denied");
                return;
            }
        }
        String formattedBalance = serviceManager.formatMoney(balance);
        sender.sendMessage(ChatColor.GRAY + "Balance: " + ChatColor.AQUA + "$" + formattedBalance);
    }
}
