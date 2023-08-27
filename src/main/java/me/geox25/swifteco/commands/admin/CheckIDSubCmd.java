package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class CheckIDSubCmd extends SubCommand {

    public CheckIDSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("checkid");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender) && !sender.hasPermission("swift.id")) {
            return;
        }

        try {
            boolean valid_id = false;

            ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            List<String> lore = meta.getLore();
            String name = meta.getDisplayName().substring(2);
            String id = "";

            assert lore != null;
            if (lore.get(lore.size() - 1).contains("ID")) {
                id = lore.get(lore.size() - 1).replace("ID: ", "").substring(2);
            }

            if (name.equals("Receipt")) {
                String recipient = lore.get(0).substring(6).replace("§7", "");
                String sender_name = lore.get(1).substring(8).replace("§7", "");
                double amount = Double.parseDouble(lore.get(2).substring(13).replace(",", ""));
                valid_id = idsModule.checkReceipt(id, recipient, sender_name, amount);
                Logger log = Bukkit.getLogger();
                log.info("id: " + id);
                log.info("recipient: " + recipient);
                log.info("sender_name: " + sender_name);
                log.info("amount: " + amount);
            }

            if (name.equals("Banknote")) {
                String account = lore.get(0).substring(11);
                double amount = Double.parseDouble(lore.get(1).substring(13).replace(",", ""));
                valid_id = idsModule.checkNote(id, account, amount);
            }

            if (valid_id) {
                sender.sendMessage(ChatColor.GREEN + "True");
            } else {
                sender.sendMessage(ChatColor.RED + "False");
            }
        } catch (StringIndexOutOfBoundsException e) {
            sender.sendMessage(ChatColor.RED + "Invalid Receipt/Note!");
        }
    }
}
