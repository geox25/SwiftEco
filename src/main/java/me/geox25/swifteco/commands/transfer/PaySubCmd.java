package me.geox25.swifteco.commands.transfer;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;


import static me.geox25.swifteco.SwiftManager.Types.*;


import me.geox25.swifteco.SwiftManager.Types;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class PaySubCmd extends SubCommand {

    public PaySubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("pay");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        boolean valid = serviceManager.checkArgs(args, new Types[]{STRING, DOUBLE}, sender);
        if (!valid) {return;}

        Player player = (Player) sender;
        String uuid = serviceManager.getUUIDOrName(sender.getName());
        String recipientUUID = serviceManager.getUUIDOrName(args[0]);

        double amount = Double.parseDouble(args[1]);
        double balance = dataFile.getDouble("account." + uuid + ".balance");

        if (!(amount > 0)) {
            sender.sendMessage(ChatColor.RED + "Amount cannot be negative!");
            return;
        }

        if (!dataFile.contains("account." + recipientUUID)) {
            sender.sendMessage(ChatColor.RED + "That user does not have an account yet! Tell them to do /swift");
            return;
        }

        if (!(balance >= amount)) {
            sender.sendMessage(ChatColor.RED + "Insufficient Funds");
            return;
        }

        dataFile.set("account." + uuid + ".balance", balance - amount);
        double recipient_balance = dataFile.getDouble("account." + recipientUUID + ".balance");
        dataFile.set("account." + recipientUUID + ".balance", recipient_balance + amount);

        ItemStack receipt = idsModule.generateReceipt(uuid, recipientUUID, amount);
        player.getInventory().addItem(receipt);

        sender.sendMessage(ChatColor.GREEN + "Transaction Posted");

        Player recipient = Bukkit.getPlayer(args[0]);
        if (recipient != null) {
            recipient.getInventory().addItem(receipt);
            recipient.sendMessage(ChatColor.GRAY + uuid + " has paid you " + ChatColor.AQUA + "$" + String.format("%,.2f", amount));
        }
    }
}
