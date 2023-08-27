package me.geox25.swifteco.commands.admin;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;


import static me.geox25.swifteco.SwiftManager.Types.*;


import me.geox25.swifteco.SwiftManager.Types;

import java.util.Arrays;
import java.util.List;

public class ScoreSubCmd extends SubCommand {

    public ScoreSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("score");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!configModule.isAdmin(sender)) {
            return;
        }

        boolean valid = serviceManager.checkArgs(args, new Types[]{STRING, CREDIT_SCORE}, sender);
        if (!valid) {return;}

        String account = serviceManager.getUUIDOrName(args[0]);
        int scoreChange = Integer.parseInt(args[1]);

        // Check if account is invalid
        if (!dataFile.contains("account." + account)) {
            sender.sendMessage(ChatColor.RED + "Invalid Account");
            return;
        }

        // Get account's credit score
        int score = dataFile.getInt("account." + account + ".creditScore");

        // Update account's credit score
        dataFile.set("account." + account + ".creditScore", score + scoreChange);

        sender.sendMessage(ChatColor.GREEN + "Success");
    }
}
