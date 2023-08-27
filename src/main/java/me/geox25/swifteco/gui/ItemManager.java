package me.geox25.swifteco.gui;

import me.geox25.swifteco.service.ServiceManager;
import me.geox25.swifteco.service.variety.DataModule;
import me.geox25.swifteco.skull.SkullCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.template.ItemStackTemplate;
import org.ipvp.canvas.template.StaticItemTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemManager {

    private ServiceManager serviceManager;

    public ItemManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public ItemStack generateAccountsButton() {
        ItemStack accountsBtn = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgwNjQ0MGY1NTg4NjQ5NDdkYzA5MzI2NTAwNmVhODBkNzE0NTI0NDQyYjhhMDA5MDZmMmZiMDc1MDc3Y2ViMyJ9fX0=");
        ItemMeta meta = accountsBtn.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + String.valueOf(ChatColor.BOLD) + "Accounts");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "View other accounts"
        ));
        accountsBtn.setItemMeta(meta);

        return accountsBtn;
    }

    public ItemStack generatePrimaryAccountItem(Player player) {
                /*
        Account information variables
         */
        String accountName = player.getName();
        String uuid = serviceManager.getUUIDOrName(accountName);
        double balance = serviceManager.getDataModule().getDataFile().getDouble("account." + uuid + ".balance");
        double debt = serviceManager.getDataModule().getDataFile().getDouble("account." + uuid + ".creditBalance");

        ItemStack primaryAccountItem = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTllNzdmYWU1MzEzYmFjMTliZjE0NTc3ZDUwMDkzZTQ3MzhlYmQ3MGZkNTRhNGRlMWEyNzQ3NWQwZWM5NTM4ZiJ9fX0=");
        ItemMeta meta = primaryAccountItem.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + String.valueOf(ChatColor.BOLD) + "Primary Account Info");
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + accountName + ":",
                ChatColor.GRAY + "Balance: " + ChatColor.DARK_GREEN + serviceManager.formatMoneyComponent(balance),
                ChatColor.GRAY + "Debt: " + serviceManager.formatDebtComponent(debt)
        ));
        primaryAccountItem.setItemMeta(meta);

        return primaryAccountItem;
    }

    public ArrayList<ItemStack> generateAccountItems(Player player) {

        ArrayList<ItemStack> items = new ArrayList<>();

        // DataModule Instance instead of getting everytime
        DataModule dataModule = serviceManager.getDataModule();
        YamlConfiguration dataFile = dataModule.getDataFile();

        // List of accounts to loop through
        List<String> accountList = dataFile.getStringList("account_list");

        // Player's Name and UUID
        String playerName = player.getName();
        String playerUUID = serviceManager.getUUIDOrName(playerName);

        for (String account : accountList) {
            String accountPlayerName = dataFile.getString("account." + account + ".player");
            boolean notPrimaryAccount = !Objects.equals(account, playerUUID);
            boolean isManager = dataFile.getStringList("account." + account + ".managers").contains(playerUUID);

            // If player is authorized in account
            if (notPrimaryAccount && isManager) {
                double balance = dataFile.getDouble("account." + account + ".balance");
                double debt = dataFile.getDouble("account." + account + ".creditBalance");

                String accountDisplayName;
                String accountType = dataFile.getString("account." + account + ".type");

                assert accountType != null;
                if (accountType.equalsIgnoreCase("inst") || accountType.equalsIgnoreCase("institution")) {
                    accountDisplayName = account;
                } else {
                    accountDisplayName = accountPlayerName;
                }

                ItemStack accountInfo = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTllNzdmYWU1MzEzYmFjMTliZjE0NTc3ZDUwMDkzZTQ3MzhlYmQ3MGZkNTRhNGRlMWEyNzQ3NWQwZWM5NTM4ZiJ9fX0=");
                ItemMeta meta = accountInfo.getItemMeta();
                assert meta != null;
                meta.setDisplayName(ChatColor.AQUA + String.valueOf(ChatColor.BOLD) + "Other Account");
                meta.setLore(Arrays.asList(
                        ChatColor.GRAY + accountDisplayName + ":",
                        ChatColor.GRAY + "Balance: " + ChatColor.DARK_GREEN + serviceManager.formatMoneyComponent(balance),
                        ChatColor.GRAY + "Debt: " + serviceManager.formatDebtComponent(debt)
                ));
                accountInfo.setItemMeta(meta);

                items.add(accountInfo);
            }
        }

        return items;
    }

    public ItemStack generateMaterialsButton() {
        ItemStack materials = new ItemStack(Material.SCAFFOLDING);
        ItemMeta meta = materials.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + String.valueOf(ChatColor.BOLD) + "Materials");
        materials.setItemMeta(meta);

        return materials;
    }

    public ItemStack generateValuablesButton() {
        ItemStack valuables = new ItemStack(Material.DIAMOND);
        ItemMeta meta = valuables.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + String.valueOf(ChatColor.BOLD) + "Valuables");
        valuables.setItemMeta(meta);

        return valuables;
    }

    public ItemStack generateFarmingButton() {
        ItemStack farming = new ItemStack(Material.GOLDEN_CARROT);
        ItemMeta meta = farming.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + "Farming");
        farming.setItemMeta(meta);

        return farming;
    }

    public ItemStack generateShopButton() {
        ItemStack shopButton = SkullCreator.itemFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdhOGRiMjFhNDUxOTQ0NGZkZDM2OTNmN2NmMjExMTFjMjhhNjk5NmNiZDNhYmM0MTZiM2QxNWM1YmFlN2VmMyJ9fX0=");
        ItemMeta meta = shopButton.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.AQUA + String.valueOf(ChatColor.BOLD) + "Shop");
        meta.setLore(Arrays.asList(
           ChatColor.GRAY + "Buy & sell items"
        ));
        shopButton.setItemMeta(meta);

        return shopButton;
    }

    public ItemStack generateShopItem(ItemStack item, double buyPrice, double sellPrice) {
        String buyLine = ChatColor.GRAY + "Buy: " + ChatColor.DARK_GREEN + serviceManager.formatMoneyComponent(buyPrice);
        String sellLine = ChatColor.GRAY + "Sell: " + ChatColor.DARK_GREEN + serviceManager.formatMoneyComponent(sellPrice);

        if (buyPrice == 0) {
            buyLine = ChatColor.RED + "Item cannot be bought";
        }

        if (sellPrice == 0) {
            sellLine = ChatColor.RED + "Item cannot be sold";
        }

        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setLore(Arrays.asList(buyLine, sellLine));
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack generateUnavailableItem() {

        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(ChatColor.RED + "Unavailable");
        item.setItemMeta(itemMeta);

        return item;
    }
}
