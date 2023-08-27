package me.geox25.swifteco.commands.transfer;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static me.geox25.swifteco.SwiftManager.Types.DOUBLE;


public class PayoffSubCmd extends SubCommand {

    public PayoffSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("payoff");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        boolean valid = serviceManager.checkArgs(args, new SwiftManager.Types[]{DOUBLE}, sender);
        if (!valid) {return;}

        Player player = (Player) sender;
        String uuid = serviceManager.getUUIDOrName(sender.getName());

        String lendingAccount = config.getString("lendingAccount");
        double lending_balance = dataFile.getDouble("account." + lendingAccount + ".balance");
        double credit_balance = Math.ceil(dataFile.getDouble("account." + uuid + ".creditBalance"));
        double payoff_amount = Double.parseDouble(args[0]);
        double player_balance = dataFile.getDouble("account." + uuid + ".balance");

        if (payoff_amount > credit_balance) {
            sender.sendMessage(ChatColor.RED + "You cannot pay off more than you owe!");
            return;
        }

        if (payoff_amount > player_balance) {
            sender.sendMessage(ChatColor.RED + "Insufficient Funds");
            return;
        }

        dataFile.set("account." + uuid + ".balance", player_balance - payoff_amount);
        dataFile.set("account." + lendingAccount + ".balance", lending_balance + payoff_amount);
        dataFile.set("account." + uuid + ".creditBalance", credit_balance - payoff_amount);
        dataFile.set("account." + uuid + ".lastLoan", payoff_amount);
        sender.sendMessage(ChatColor.GREEN + "Transaction Posted");
    }
}
