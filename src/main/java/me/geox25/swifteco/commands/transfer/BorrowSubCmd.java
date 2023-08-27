package me.geox25.swifteco.commands.transfer;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static me.geox25.swifteco.SwiftManager.Types.*;


public class BorrowSubCmd extends SubCommand {

    public BorrowSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("borrow");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        boolean valid = serviceManager.checkArgs(args, new SwiftManager.Types[]{DOUBLE}, sender);
        if (!valid) {return;}

        Player player = (Player) sender;
        String uuid = serviceManager.getUUIDOrName(sender.getName());

        int credit = dataFile.getInt("account." + uuid + ".creditScore");
        double credit_balance = dataFile.getDouble("account." + uuid + ".creditBalance");
        double credit_ceiling = config.getDouble("creditCeiling");
        double p = (double) credit / 1000;
        double credit_limit = (p * credit_ceiling);

        double amount_requested = Double.parseDouble(args[0]);

        if (credit_balance + amount_requested > credit_limit) {
            sender.sendMessage(ChatColor.RED + "Your credit limit is $" + String.format("%,.2f", credit_limit));
            return;
        }

        String lendingAccount = config.getString("lendingAccount");
        double lending_balance = dataFile.getDouble("account." + lendingAccount + ".balance");
        double player_balance = dataFile.getDouble("account." + uuid + ".balance");

        dataFile.set("account." + lendingAccount + ".balance", lending_balance - amount_requested);
        dataFile.set("account." + uuid + ".lastLoan", 0);
        dataFile.set("account." + uuid + ".creditBalance", credit_balance + amount_requested);
        dataFile.set("account." + uuid + ".balance", player_balance + amount_requested);
        sender.sendMessage(ChatColor.GREEN + "Transaction Posted");
    }
}
