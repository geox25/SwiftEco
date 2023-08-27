package me.geox25.swifteco.service;

import me.geox25.swifteco.SwiftManager;
import me.geox25.swifteco.gui.GuiManager;
import me.geox25.swifteco.service.variety.ConfigModule;
import me.geox25.swifteco.service.variety.DataModule;
import me.geox25.swifteco.service.variety.IDSModule;
import me.geox25.swifteco.service.variety.ShopModule;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.text.DecimalFormat;

public class ServiceManager {

    // Data Module Instance (data.yml)
    private final DataModule dataModule;
    private final YamlConfiguration dataFile;

    // Id Module Instance (ids.yml)
    private final IDSModule idsModule;

    // Config Module Instance (config.yml)
    private ConfigModule configModule;

    // Shop Module Instance (shop.yml)
    private ShopModule shopModule;

    // Economy Service Instance (vault)
    private final Economy economy;

    private GuiManager guiManager;

    public ServiceManager(DataModule dataModule, IDSModule idsModule, ConfigModule configModule, ShopModule shopModule, Economy economy) {
        this.dataModule = dataModule;
        this.dataFile = dataModule.getDataFile();
        this.idsModule = idsModule;
        this.configModule = configModule;
        this.shopModule = shopModule;
        this.economy = economy;
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public ConfigModule getConfigModule() {
        return this.configModule;
    }

    public void setConfigModule(ConfigModule configModule) {
        this.configModule = configModule;
    }

    public DataModule getDataModule() { return this.dataModule; }

    public IDSModule getIDSModule() {
        return this.idsModule;
    }

    public ShopModule getShopModule() { return this.shopModule; }

    public void setGuiManager(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    public GuiManager getGuiManager() {
        return this.guiManager;
    }

    /*
    6/13/2023, Migrating to UUID's
    New methods for manipulating datafile more cleanly
     */
    public String getUUIDOrName(String account) {
        // If account is a bedrock username return itself
        if (account.startsWith(".")) {
            return account;
        }

        // If account is already a UUID return itself
        if (isUUID(account)) {
            return account;
        }

        String path = "link." + account;
        String[] parts = new String[0];

        if (account.contains(".")) {
            parts = account.split("\\.");
            path = "link." + parts[0];
        }

        // If it is not a player linked account, return the name
        // Else if it is, return the player UUID as String
        if (!dataFile.contains(path)) {
            return account;
        } else {
            String uuid = dataFile.getString(path);
            assert uuid != null;
            StringBuilder result = new StringBuilder(uuid);

            if (parts.length > 1) {
                int i = 0;
                for (String part : parts) {
                    if (i > 0) {
                        result.append(".").append(part);
                    }
                    i++;
                }
            }
            return result.toString();
        }
    }

    public boolean isUUID(String possibleUUID) {
        return possibleUUID.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    public String formatMoney(double money) {
        double roundedBalance = Math.floor(money * 100) / 100; // Truncate to hundredth place

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        return decimalFormat.format(roundedBalance);
    }

    public String formatMoneyComponent(double money) {
        return "$" + formatMoney(money);
    }

    public String formatDebtComponent(double debt) {
        String component = formatMoney(debt);

        if (debt > 0) {
            return ChatColor.RED + "$" + component;
        } else {
            return ChatColor.GRAY + "$" + component;
        }
    }

    public boolean checkArgs(String[] args, SwiftManager.Types[] types, CommandSender sender) {
        if (args.length < types.length) {
            sender.sendMessage(ChatColor.RED + "Missing Required Args!");
            return false;
        }

        int i = 0;
        for (String arg : args) {
            try {
                switch (types[i]) {
                    case ANY:
                    case ACCOUNT:
                        if (!dataFile.contains("account." + arg)) {
                            sender.sendMessage(ChatColor.RED + "Invalid Account!");
                            return false;
                        }
                        break;
                    case STRING:
                        break;
                    case DOUBLE:
                        if (Double.parseDouble(arg) < 0) {
                            sender.sendMessage(ChatColor.RED + "Invalid argument(s)!");
                            return false;
                        }
                        break;
                    case INTEGER:
                        if (Integer.parseInt(arg) < 0) {
                            sender.sendMessage(ChatColor.RED + "Invalid argument(s)!");
                            return false;
                        }
                        break;
                    case CREDIT_SCORE:
                        int num = Integer.parseInt(arg);
                        if (num < -10000 || num > 10000) {
                            sender.sendMessage(ChatColor.RED + "Invalid argument(s)!");
                            return false;
                        }
                        break;
                    case OPT_FLAG:
                        if (arg != null && !arg.contains("-")) {
                            sender.sendMessage(ChatColor.RED + "Invalid Flag Syntax!");
                            return false;
                        }
                        break;
                }
            } catch (NumberFormatException nfe) {
                sender.sendMessage(ChatColor.RED + "Invalid argument(s)!");
                return false;
            }
            i++;
        }
        return true;
    }

    public void safeSetPlayer(String path, String playerName) {
        if (dataFile.contains(path)) {
            dataFile.set(path + ".player", playerName);
        }
    }
}
