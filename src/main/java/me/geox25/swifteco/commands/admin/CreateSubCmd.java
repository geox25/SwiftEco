package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;

import static me.geox25.swifteco.SwiftManager.*;
import static me.geox25.swifteco.SwiftManager.Types.*;

import me.geox25.swifteco.SwiftManager.Types;

public class CreateSubCmd extends SubCommand {

    public CreateSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("create");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender)) {
            return;
        }

        boolean valid = serviceManager.checkArgs(args, new Types[]{STRING, STRING, STRING, DOUBLE, INTEGER}, sender);
        if (!valid) {return;}

        String account = serviceManager.getUUIDOrName(args[0]);
        String playerName = args[1];
        String type = args[2];
        Double startingBalance = Double.parseDouble(args[3]);
        int startingScore = Integer.parseInt(args[4]);

        if (dataFile.contains("account." + account)) {
            sender.sendMessage(ChatColor.RED + "That account already exists!");
            return;
        }

        if (!dataModule.isValidPlayer(playerName)) {
            sender.sendMessage(ChatColor.RED + "Invalid Player!");
            return;
        }

        String uuid = serviceManager.getUUIDOrName(playerName);

        /*
        Example:
        /swift create evg.bank DaarkDev inst 1000 500
         */

        dataFile.set("account." + account + ".balance", startingBalance);
        dataFile.set("account." + account + ".player", playerName);
        ArrayList<String> managers = new ArrayList<>();
        dataFile.set("account." + account + ".managers", managers);
        dataFile.set("account." + account + ".creditScore", startingScore);
        dataFile.set("account." + account + ".type", type);

        List<String> account_list = dataFile.getStringList("account_list");
        account_list.add(account);
        dataFile.set("account_list", account_list);

        sender.sendMessage(ChatColor.GREEN + "Setup Finished!");
    }
}