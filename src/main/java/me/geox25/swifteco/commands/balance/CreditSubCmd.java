package me.geox25.swifteco.commands.balance;

import me.geox25.swifteco.commands.SubCommand;
import static me.geox25.swifteco.SwiftManager.*;

import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CreditSubCmd extends SubCommand {

    public CreditSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("credit");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String uuid = serviceManager.getUUIDOrName(sender.getName());

        int credit = dataFile.getInt("account." + uuid + ".creditScore");
        double credit_balance = Math.ceil(dataFile.getDouble("account." + uuid + ".creditBalance"));
        double credit_ceiling = config.getDouble("creditCeiling");
        double p = (double) credit / 1000;
        double credit_limit = (p * credit_ceiling);

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
        sender.sendMessage(ChatColor.GRAY + "Credit Score: " + color + credit);
        sender.sendMessage(ChatColor.GRAY + "Credit Limit: " + ChatColor.AQUA + "$" + serviceManager.formatMoney(credit_limit));
        sender.sendMessage(ChatColor.GRAY + "Credit Balance (Owed): " + balance_color + "$" + serviceManager.formatMoney(credit_balance));
        sender.sendMessage(ChatColor.DARK_AQUA + "========================");
    }
}
