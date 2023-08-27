package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class ListSubCmd extends SubCommand {

    public ListSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("list");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender)) {
            return;
        }

        List<String> account_list = dataFile.getStringList("account_list");
        sender.sendMessage(account_list.toString());
    }
}
