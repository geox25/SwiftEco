package me.geox25.swifteco.commands.transfer;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


import static me.geox25.swifteco.SwiftManager.Types.*;


import me.geox25.swifteco.SwiftManager.Types;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class WireSubCmd extends SubCommand {

    public WireSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("wire");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        boolean valid = serviceManager.checkArgs(args, new Types[]{STRING, STRING, DOUBLE}, sender);
        if (!valid) {return;}

        Player player = (Player) sender;
        String uuid = serviceManager.getUUIDOrName(sender.getName());
        String name = player.getName();

        String senderUUID = serviceManager.getUUIDOrName(args[0]);
        String recipientUUID = serviceManager.getUUIDOrName(args[1]);

        if (!dataFile.contains("account." + senderUUID) || !dataFile.contains("account." + recipientUUID)) {
            sender.sendMessage(ChatColor.RED + "Invalid Account");
            return;
        }

        if (!dataFile.getStringList("account." + senderUUID + ".managers").contains(uuid) && !Objects.equals(dataFile.getString("account." + senderUUID + ".player"), name)) {
            sender.sendMessage(ChatColor.RED + "Access Denied");
            return;
        }

        double amount = Double.parseDouble(args[2]);
        double balance = dataFile.getDouble("account." + senderUUID + ".balance");

        if (!(amount > 0)) {
            sender.sendMessage(ChatColor.RED + "Amount cannot be negative!");
            return;
        }

        if (!(balance >= amount)) {
            sender.sendMessage(ChatColor.RED + "Insufficient Funds");
            return;
        }

        dataFile.set("account." + senderUUID + ".balance", balance - amount);
        double recipient_balance = dataFile.getDouble("account." + recipientUUID + ".balance");
        dataFile.set("account." + recipientUUID + ".balance", recipient_balance + amount);

        ItemStack receipt = idsModule.generateReceipt(senderUUID, recipientUUID, amount);
        Logger log = Bukkit.getLogger();
        log.info("sender: " + senderUUID);
        log.info("recipient: " + recipientUUID);
        log.info("amount: " + amount);
        player.getInventory().addItem(receipt);

        sender.sendMessage(ChatColor.GREEN + "Transaction Posted");

        Player recipient = Bukkit.getPlayer(args[1]);
        if (recipient != null && !recipient.getName().equals(sender.getName())) {
            recipient.getInventory().addItem(receipt);
            recipient.sendMessage(ChatColor.GRAY + args[0] + " has paid you " + ChatColor.AQUA + "$" + String.format("%,.2f", amount));
        }

        if (Objects.requireNonNull(dataFile.getString("account." + senderUUID + ".type")).equalsIgnoreCase("ira")) {
            double ira_penalty = config.getDouble("iraPenalty") / 100;
            double penalty_amount = ira_penalty * amount;
            double bal = dataFile.getDouble("account." + senderUUID + ".balance");
            double gov_bal = dataFile.getDouble("account.gov.balance");
            dataFile.set("account." + senderUUID + ".balance", bal - penalty_amount);
            dataFile.set("account.gov.balance", gov_bal + penalty_amount);
            sender.sendMessage(ChatColor.GRAY + "IRA Penalty @ " + ChatColor.BLUE + (ira_penalty * 100) + "%" + ChatColor.GRAY + ": " + ChatColor.AQUA + "$" + String.format("%,.2f", penalty_amount));
        }
    }
}
