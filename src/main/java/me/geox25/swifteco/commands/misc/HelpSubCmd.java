package me.geox25.swifteco.commands.misc;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class HelpSubCmd extends SubCommand {
    public static final String message = ChatColor.DARK_AQUA + "========================\n"
            + ChatColor.GRAY + "By Evergreen©\n"
            + ChatColor.AQUA + "\n/swift : Main command\n"
            + "/swift bal [account] : Balance of your account, uses default if not provided\n"
            + "/swift help : Displays this message\n"
            + "/swift credit : Shows your credit score\n"
            + "/swift pay {user} {$} : Sends $ to user\n"
            + "/swift wire {from} {to} {$} : Sends $ from first account to second account\n"
            + "/swift withdraw {$} : Transfers $ to your essentials /bal\n"
            + "/swift deposit {$} : Transfers $ from your essentials /bal\n"
            + "/swift borrow {$} : Requests a loan of $\n"
            + "/swift payoff {$} : Pays $ off of your amount owed\n";

    public HelpSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("help");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(message);
    }
}
