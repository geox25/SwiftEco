package me.geox25.swifteco.commands.transfer;

import me.geox25.swifteco.SwiftEco;
import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.service.ServiceManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import static me.geox25.swifteco.SwiftManager.Types.*;
import me.geox25.swifteco.SwiftManager.Types;

import java.util.Arrays;
import java.util.List;

public class WithdrawSubCmd extends SubCommand {

    public WithdrawSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("withdraw");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        boolean valid = serviceManager.checkArgs(args, new Types[]{DOUBLE}, sender);
        if (!valid) {return;}

        Player player = (Player) sender;
        String uuid = serviceManager.getUUIDOrName(sender.getName());

        double amount = Double.parseDouble(args[0]);
        double balance = dataFile.getDouble("account." + uuid + ".balance");

        if (amount < 0) {
            sender.sendMessage(ChatColor.RED + "Amount cannot be negative!");
            return;
        }

        if (!(balance >= amount)) {
            sender.sendMessage(ChatColor.RED + "Insufficient Funds");
            return;
        }

        dataFile.set("account." + uuid + ".balance", balance - amount);
        economy.depositPlayer(player, amount);

        sender.sendMessage(ChatColor.GREEN + "Transaction Posted");
    }
}
