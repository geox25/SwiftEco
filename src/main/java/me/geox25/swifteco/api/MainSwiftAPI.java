package me.geox25.swifteco.api;

import me.geox25.swifteco.SwiftAPI;
import me.geox25.swifteco.service.ServiceManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

/*
    7/13/23

    SwiftAPI Implementation
    Version: 1.2-beta
 */
public class MainSwiftAPI implements SwiftAPI {

    private final ServiceManager serviceManager;
    private final YamlConfiguration dataFile;

    public MainSwiftAPI(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
        this.dataFile = serviceManager.getDataModule().getDataFile();
    }

    @Override
    public void setAccountProperty(String path, String property) {
        this.dataFile.set("account." + path, property);
    }

    @Override
    public String getAccountProperty(String path) {
        return this.dataFile.getString("account." + path);
    }

    @Override
    public boolean accountExists(String path) {
        return this.dataFile.contains("account." + path);
    }

    @Override
    public List<String> getAccountManagers(String path) {
        return this.dataFile.getStringList("account." + path + ".managers");
    }

    @Override
    public boolean isManager(String path, String uuid) {
        return this.dataFile.getStringList("account." + path + ".managers").contains(uuid);
    }

    @Override
    public void setBalance(String path, double balance) {
        this.dataFile.set("account." + path + ".balance", balance);
    }

    @Override
    public double getBalance(String path) {
        return this.dataFile.getDouble("account." + path + ".balance");
    }
}
