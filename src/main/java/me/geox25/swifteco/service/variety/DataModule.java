package me.geox25.swifteco.service.variety;

import me.geox25.swifteco.service.YMLService;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataModule implements YMLService {

    // Primary yml file to manage
    private final YamlConfiguration dataFile;

    // I/O Handle on yml file
    private final File file;

    public DataModule(YamlConfiguration dataFile, File file) {
        this.dataFile = dataFile;
        this.file = file;
    }

    public void addFlag(String account, String flag, YamlConfiguration dataFile) {
        List<String> flags = dataFile.getStringList("account." + account + ".flags");
        flags.add(flag);
        dataFile.set("account." + account + ".flags", flags);
    }

    public void removeFlag(String account, String flag, YamlConfiguration dataFile) {
        List<String> flags = dataFile.getStringList("account." + account + ".flags");
        flags.remove(flag);
        dataFile.set("account." + account + ".flags", flags);
    }

    public boolean checkFlag(String account, String flag, YamlConfiguration dataFile) {
        List<String> flags = dataFile.getStringList("account." + account + ".flags");
        return flags.contains(flag);
    }

    public void addManager(String account, String manager) {
        List<String> managers = dataFile.getStringList("account." + account + ".managers");

        if (!managers.contains(manager)) {
            managers.add(manager);
            dataFile.set("account." + account + ".managers", managers);
        }
    }

    public void removeManager(String account, String manager) {
        List<String> managers = dataFile.getStringList("account." + account + ".managers");

        if (managers.contains(manager)) {
            managers.remove(manager);
            dataFile.set("account." + account + ".managers", managers);
        }
    }

    public boolean isValidPlayer(String playerName) {
        // Player object
        return dataFile.contains("link." + playerName);
    }

    public boolean isAuthorized(String account, String playerName, String uuid) {
        boolean isOwner = dataFile.getString("account." + account + ".player").equals(playerName);
        boolean isManager = dataFile.getStringList("account." + account + ".managers").contains(uuid);

        return isOwner || isManager;
    }

    public List<String> getPublicPlayerAccounts() {
        List<String> publicAccounts = new ArrayList<String>();
        List<String> accounts = dataFile.getStringList("account_list");

        for (String account : accounts) {
            // Account flags
            List<String> flags = dataFile.getStringList("account." + account + ".flags");

            // If it is public and a player account
            if (!flags.contains("hide") && !flags.contains("%")) {
                // Player assigned to account
                String player = dataFile.getString("account." + account + ".player");
                publicAccounts.add(player);
            }
        }

        return publicAccounts;
    }

    public YamlConfiguration getDataFile() {
        return this.dataFile;
    }

    @Override
    public void save() throws IOException {
        dataFile.save(file);
    }
}
