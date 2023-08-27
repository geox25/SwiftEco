package me.geox25.swifteco.service.variety;

import me.geox25.swifteco.service.YMLService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IDSModule implements YMLService {

    // Primary yml file to manage
    private final YamlConfiguration dataFile;

    // I/O Handle on yml file
    private final File file;

    public IDSModule(YamlConfiguration dataFile, File file) {
        this.dataFile = dataFile;
        this.file = file;
    }

    public boolean checkId(String id) {
        return dataFile.getStringList("generic").contains(id);
    }

    public void registerId(String id) {
        List<String> generic = dataFile.getStringList("generic");
        generic.add(id);
        dataFile.set("generic", generic);
    }

    public void removeId(String id) {
        List<String> generic = dataFile.getStringList("generic");
        generic.remove(id);
        dataFile.set("generic", generic);
    }

    public ItemStack generateReceipt(String sender, String recipient, double amount) {
        ItemStack receipt = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = receipt.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        assert meta != null;

        String amt = String.format("%,.2f", amount);
        double d_amt = Double.parseDouble(amt.replace(",", ""));

        meta.setDisplayName(ChatColor.AQUA + "Receipt");
        lore.add(ChatColor.GRAY + "TO: " + recipient);
        lore.add(ChatColor.GRAY + "FROM: " + sender);
        lore.add(ChatColor.GRAY + "AMOUNT: " + ChatColor.AQUA + "$" + amt);
        Calendar cal = Calendar.getInstance();
        lore.add(ChatColor.GRAY + "TIME: " + cal.getTime());
        String id = String.valueOf(cal.getTimeInMillis());
        lore.add(ChatColor.GRAY + "ID: " + id);
        meta.setLore(lore);
        receipt.setItemMeta(meta);

        registerReceipt(id, recipient, sender, d_amt);

        return receipt;
    }

    public void registerReceipt(String id, String recipient, String sender, double amount) {
        dataFile.set("receipts." + id + ".recipient", recipient);
        dataFile.set("receipts." + id + ".sender", sender);
        dataFile.set("receipts." + id + ".amount", amount);
        dataFile.set("receipts." + id + ".active", true);
    }

    public boolean checkReceipt(String id, String recipient, String sender, double amount) {
        recipient = recipient.replace("§7", "");
        if (!dataFile.contains("receipts." + id)) {return false;}

        String id_recipient = dataFile.getString("receipts." + id + ".recipient");
        String id_sender = dataFile.getString("receipts." + id + ".sender");
        double id_amount = dataFile.getDouble("receipts." + id + ".amount");
        boolean active = dataFile.getBoolean("receipts." + id + ".active");

        return recipient.equals(id_recipient) && sender.equals(id_sender) && amount == id_amount && active;
    }

    public ItemStack generateNote(String account, double amount) {
        ItemStack note = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = note.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        assert meta != null;

        String amt = String.format("%,.2f", amount);
        double d_amt = Double.parseDouble(amt.replace(",", ""));

        meta.setDisplayName(ChatColor.AQUA + "Banknote");
        lore.add(ChatColor.GRAY + "ACCOUNT: " + account);
        lore.add(ChatColor.GRAY + "AMOUNT: " + ChatColor.AQUA + "$" + amt);
        Calendar cal = Calendar.getInstance();
        lore.add(ChatColor.GRAY + "TIME: " + cal.getTime());
        String id = String.valueOf(cal.getTimeInMillis());
        lore.add(ChatColor.GRAY + "ID: " + id);
        meta.setLore(lore);
        note.setItemMeta(meta);

        registerNote(id, account, d_amt);

        return note;
    }

    public void registerNote(String id, String account, double amount) {
        dataFile.set("notes." + id + ".account", account);
        dataFile.set("notes." + id + ".amount", amount);
        dataFile.set("notes." + id + ".active", true);
    }

    public boolean checkNote(String id, String account, double amount) {
        if (!dataFile.contains("notes." + id)) {return false;}

        String id_account = dataFile.getString("notes." + id + ".account");
        double id_amount = dataFile.getDouble("notes." + id + ".amount");
        boolean active = dataFile.getBoolean("notes." + id + ".active");

        return account.equals(id_account) && amount == id_amount && active;
    }

    public void removeNote(String id) {
        if (dataFile.contains("notes." + id)) {
            dataFile.set("notes." + id, null);
        }
    }

    public boolean isActive(String path) {
        return dataFile.getBoolean(path + ".active");
    }

    public void setActive(String path, boolean value) {
        dataFile.set(path + ".active", value);
    }

    @Override
    public void save() throws IOException {
        dataFile.save(file);
    }
}
