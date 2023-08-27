package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static me.geox25.swifteco.SwiftManager.Types.STRING;


public class RemoveIDSubCmd extends SubCommand {

    public RemoveIDSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("removeid");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender) && !sender.hasPermission("swift.id")) {
            return;
        }

        boolean valid = serviceManager.checkArgs(args, new SwiftManager.Types[]{STRING, STRING}, sender);
        if (!valid) {return;}

        if (args[1].equals("hand")) {
            List<String> lore = Objects.requireNonNull(((Player) sender).getInventory().getItemInMainHand().getItemMeta()).getLore();
            assert lore != null;
            if (lore.get(lore.size() - 1).contains("ID")) {
                String id = lore.get(lore.size() - 1).replace("ID: ", "").substring(2);
                if (!idsModule.isActive(args[0] + "." + id)) {
                    sender.sendMessage(ChatColor.RED + "Invalid ID!");
                    return;
                }
                idsModule.setActive(args[0] + "." + id, false);
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid Receipt Item!");
                return;
            }
        } else {
            idsModule.setActive(args[0] + "." + args[1], false);
        }

        sender.sendMessage(ChatColor.GREEN + "Success");
    }
}
