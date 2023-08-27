package me.geox25.swifteco.commands.balance;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.service.ServiceManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

public class BaltopSubCmd extends SubCommand {

    public BaltopSubCmd(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("baltop", "top");
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        List<String> account_list = dataFile.getStringList("account_list");
        double total = 0;
        HashMap<String, Double> map = new HashMap<>();

        for (String account : account_list) {

            if (!dataFile.getStringList("account." + account + ".flags").contains("hide")) {
                double b = dataFile.getDouble("account." + account + ".balance");
                String reference;

                if (Objects.requireNonNull(dataFile.getString("account." + account + ".type")).equalsIgnoreCase("inst")) {
                    reference = account;
                } else {
                    reference = dataFile.getString("account." + account + ".player");
                }

                // If player already in map, combine balances
                if (map.containsKey(reference)) {
                    double other_bal = map.get(reference);
                    map.put(reference, other_bal + b);
                } else {
                    map.put(reference, b);
                }

                total += b;
            }
        }

        Map<String, Double> sortedMap = map.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        sender.sendMessage(ChatColor.DARK_AQUA + "=============================");
        sender.sendMessage(ChatColor.WHITE + String.valueOf(ChatColor.BOLD) + "TOTAL: " + ChatColor.AQUA + String.valueOf(ChatColor.BOLD) + "$" + String.format("%,.2f", total) + "\n");

        int i = 0;
        int count = 1;

        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            if (count > 10) {
                break;
            }
            int place = i + 1;
            ChatColor color = ChatColor.GRAY;

            if (place == 3)
                color = ChatColor.of("#CD7F32");
            if (place == 2)
                color = ChatColor.of("#d6d6d6");
            if (place == 1)
                color = ChatColor.of("#FFD700");

            sender.sendMessage(ChatColor.GRAY + "[" + color + place + ChatColor.GRAY + "] " + entry.getKey() + ": " + ChatColor.AQUA + "$" + String.format("%,.2f", entry.getValue()));
            i++;
            count++;
        }
        sender.sendMessage(ChatColor.DARK_AQUA + "=============================");
    }
}
