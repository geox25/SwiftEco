package me.geox25.swifteco;

import me.geox25.swifteco.commands.SubCommand;
import me.geox25.swifteco.commands.admin.*;
import me.geox25.swifteco.commands.balance.BalSubCmd;
import me.geox25.swifteco.commands.balance.BaltopSubCmd;
import me.geox25.swifteco.commands.balance.CreditSubCmd;
import me.geox25.swifteco.commands.admin.ScoreSubCmd;
import me.geox25.swifteco.commands.misc.HelpSubCmd;
import me.geox25.swifteco.commands.transfer.*;
import me.geox25.swifteco.gui.GuiManager;
import me.geox25.swifteco.service.ServiceManager;
import me.geox25.swifteco.service.variety.DataModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.command.defaults.HelpCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class SwiftManager implements CommandExecutor, TabCompleter {

    private static ArrayList<SubCommand> subcmds;

    private DataModule dataModule;

    private YamlConfiguration dataFile;

    private FileConfiguration config;

    private ServiceManager serviceManager;

    private GuiManager guiManager;

    public SwiftManager(YamlConfiguration dataFile, FileConfiguration config, ServiceManager serviceManager, GuiManager guiManager) {
        reloadCmds(dataFile, config, serviceManager, guiManager);
    }

    public void reloadCmds(YamlConfiguration dataFile, FileConfiguration config, ServiceManager serviceManager, GuiManager guiManager) {
        this.dataModule = serviceManager.getDataModule();
        this.dataFile = dataFile;
        this.config = config;
        this.serviceManager = serviceManager;
        this.guiManager = guiManager;

        subcmds = new ArrayList<>();
        subcmds.add(new HelpSubCmd(serviceManager));
        subcmds.add(new BalSubCmd(serviceManager));
        subcmds.add(new CreditSubCmd(serviceManager));
        subcmds.add(new PaySubCmd(serviceManager));
        subcmds.add(new WireSubCmd(serviceManager));
        subcmds.add(new WithdrawSubCmd(serviceManager));
        subcmds.add(new DepositSubCmd(serviceManager));
        subcmds.add(new CreateSubCmd(serviceManager));
        subcmds.add(new InfoSubCmd(serviceManager));
        subcmds.add(new ScoreSubCmd(serviceManager));
        subcmds.add(new TaxSubCmd(serviceManager));
        subcmds.add(new ReloadSubCmd(serviceManager));
        subcmds.add(new InterestSubCmd(serviceManager));
        subcmds.add(new BorrowSubCmd(serviceManager));
        subcmds.add(new PayoffSubCmd(serviceManager));
        subcmds.add(new GiveSubCmd(serviceManager));
        subcmds.add(new TakeSubCmd(serviceManager));
        subcmds.add(new SetSubCmd(serviceManager));
        subcmds.add(new ListSubCmd(serviceManager));
        subcmds.add(new CheckIDSubCmd(serviceManager));
        subcmds.add(new TotalSubCmd(serviceManager));
        subcmds.add(new RemoveIDSubCmd(serviceManager));
        subcmds.add(new AddIDSubCmd(serviceManager));
        subcmds.add(new AddFlagSubCmd(serviceManager));
        subcmds.add(new RemoveFlagSubCmd(serviceManager));
        subcmds.add(new NoteSubCmd(serviceManager));
        subcmds.add(new BaltopSubCmd(serviceManager));
        subcmds.add(new SetOwedSubCmd(serviceManager));
        subcmds.add(new ForcePayoffSubCmd(serviceManager));
        subcmds.add(new AddManagerSubCmd(serviceManager));
        subcmds.add(new RemoveManagerSubCmd(serviceManager));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only for players");
            return false;
        }

        Player player = (Player) sender;
        String name = player.getName();
        String UUIDOrName = ((Player) sender).getUniqueId().toString();
        String path = "account." + UUIDOrName;

        // If they are a bedrock player, change path to their name
        if (name.startsWith(".")) {
            path = "account." + name; // change path from uuid to name if bedrock
            UUIDOrName = name; // Set UUIDOrName to name if bedrock
        }

        if (!dataFile.contains(path)) {
            sender.sendMessage(ChatColor.GRAY + "Userdata not found, executing one-time setup...");

            // Manager List
            List<String> managers = new ArrayList<String>();
            managers.add(UUIDOrName);

            // Add account to account_list
            List<String> account_list = dataFile.getStringList("account_list");
            account_list.add(UUIDOrName);

            dataFile.set(path + ".balance", config.get("startBalance"));
            dataFile.set(path + ".player", name);
            dataFile.set(path + ".managers", managers);
            dataFile.set(path + ".creditScore", 0);
            dataFile.set(path + ".type", "checking");
            dataFile.set("account_list", account_list);
            dataFile.set("link." + name, UUIDOrName);
            dataFile.set("link." + UUIDOrName, name);

            sender.sendMessage(ChatColor.GREEN + "Setup Finished!");
        }

        // If their name is not linked
        if (!dataFile.contains("link." + name)) {
            sender.sendMessage(ChatColor.GRAY + "Executing one-time UUID linking protocol...");

            // Unlink old username
            dataFile.set("link." + dataFile.get("link." + UUIDOrName), null);

            // Link new username
            dataFile.set("link." + name, UUIDOrName);

            // Link UUID to new username
            dataFile.set("link." + UUIDOrName, name);

            // Safe set .player field
            serviceManager.safeSetPlayer(path, name);
            serviceManager.safeSetPlayer(path + ".ira", name);
            serviceManager.safeSetPlayer(path + ".savings", name);

            sender.sendMessage(ChatColor.GREEN + "Setup Finished!");
        }

        // Manage subcommands
        if (args.length > 0) {
            boolean found = false;
            // Check to execute correct subcommand
            for (SubCommand subcmd : subcmds) {
                if (subcmd.getNames().contains(args[0])) {
                    found = true;
                    subcmd.run(sender, afterIndex(0, args));
                    guiManager.update();
                }
            }
            if (!found)
                sender.sendMessage(ChatColor.RED + "Invalid SubCommand!");
        } else {
            guiManager.openMainMenu((Player) sender);
        }

        return true;
    }

    public static String[] afterIndex(int index, String[] arr) {
        int i = 0;
        String[] result = new String[arr.length - (index + 1)];
        for (String s : arr) {
            if (i > index)
                result[(i - (index + 1))] = s;
            i++;
        }
        return result;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        // List of strings
        List<String> completions = new ArrayList<String>();
        List<String> commands = getAllCommandNames();

        // Command name
        String cmdName = command.getName();

        // If cmdName is swift
        boolean isSwift = cmdName.equals("sw") || cmdName.equals("swift");

        // If the sender is not a player, return no completions
        // If the command is not swift, return no completions
        if (!(commandSender instanceof Player)) {
            return completions;
        } else if (!isSwift) {
            return completions;
        }

        if (args != null) {

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], commands, completions);
            }

            if (args.length >= 2) {
                StringUtil.copyPartialMatches(args[1], Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), completions);
            }
        }

        return completions;
    }

    public List<String> getAllCommandNames() {
        List<String> cmdNames = new ArrayList<String>();

        for (SubCommand subcmd : subcmds) {
            cmdNames.addAll(subcmd.getNames());
        }

        return cmdNames;
    }

    public enum Types {
        ANY,
        DOUBLE,
        INTEGER,
        STRING,
        CREDIT_SCORE,
        OPT_FLAG,
        ACCOUNT
    }
}
