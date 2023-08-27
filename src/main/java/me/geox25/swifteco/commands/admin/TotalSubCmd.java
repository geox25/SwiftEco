package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class TotalSubCmd extends SubCommand {

    public TotalSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("total");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender)) {
            return;
        }

        List<String> account_list = dataFile.getStringList("account_list");
        double total = 0;

        for (String account : account_list) {
            if (!dataFile.getStringList("account." + account + ".flags").contains("hide"))
                total += (dataFile.getDouble("account." + account + ".balance"));
        }

        sender.sendMessage(ChatColor.DARK_AQUA + "========================");
        sender.sendMessage(ChatColor.GRAY + "Total: " + ChatColor.AQUA + "$" + serviceManager.formatMoney(total));
        sender.sendMessage(ChatColor.DARK_AQUA + "========================");
    }
}
