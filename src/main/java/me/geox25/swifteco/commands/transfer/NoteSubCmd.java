package me.geox25.swifteco.commands.transfer;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static me.geox25.swifteco.SwiftManager.Types.DOUBLE;


public class NoteSubCmd extends SubCommand {

    public NoteSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("note");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!sender.hasPermission("swift.note")) {
            sender.sendMessage(ChatColor.RED + "Insufficient Permissions!");
            return;
        }

        boolean valid = serviceManager.checkArgs(args, new SwiftManager.Types[]{DOUBLE}, sender);
        if (!valid) {return;}

        Player player = (Player) sender;
        String uuid = serviceManager.getUUIDOrName(sender.getName());
        double amount = Double.parseDouble(args[0]);

        double balance = dataFile.getDouble("account." + uuid + ".balance");
        if (balance < amount) {
            sender.sendMessage(ChatColor.RED + "Insufficient Funds");
            return;
        }

        dataFile.set("account." + uuid + ".balance", balance - amount);

        ItemStack note = idsModule.generateNote(uuid, amount);
        player.getInventory().addItem(note);

        sender.sendMessage(ChatColor.GREEN + "Transaction Posted");

        if (Objects.requireNonNull(dataFile.getString("account." + uuid + ".type")).equalsIgnoreCase("ira")) {
            double ira_penalty = config.getDouble("iraPenalty") / 100;
            double penalty_amount = ira_penalty * amount;
            double bal = dataFile.getDouble("account." + uuid + ".balance");
            double gov_bal = dataFile.getDouble("account.gov.balance");
            dataFile.set("account." + uuid + ".balance", bal - penalty_amount);
            dataFile.set("account.gov.balance", gov_bal + penalty_amount);
            sender.sendMessage(ChatColor.GRAY + "IRA Penalty @ " + ChatColor.BLUE + (ira_penalty * 100) + "%" + ChatColor.GRAY + ": " + ChatColor.AQUA + "$" + String.format("%,.2f", penalty_amount));
        }
    }
}
