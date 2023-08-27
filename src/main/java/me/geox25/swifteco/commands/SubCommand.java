package me.geox25.swifteco.commands;

import me.geox25.swifteco.service.ServiceManager;
import me.geox25.swifteco.service.variety.ConfigModule;
import me.geox25.swifteco.service.variety.DataModule;
import me.geox25.swifteco.service.variety.IDSModule;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public abstract class SubCommand {

    // ServiceManager Instance
    protected final ServiceManager serviceManager;

    // ConfigModule Instance
    protected final ConfigModule configModule;

    // DataModule Instance
    protected final DataModule dataModule;

    // IDSModule Instance
    protected final IDSModule idsModule;

    // DataFile Instance
    protected final YamlConfiguration dataFile;

    // Config Instance
    protected final FileConfiguration config;

    // Economy Instance
    protected final Economy economy;

    public abstract List<String> getNames();

    public abstract void run(CommandSender sender, String[] args);

    public SubCommand(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
        this.configModule = serviceManager.getConfigModule();
        this.config = configModule.getConfig();
        this.dataModule = serviceManager.getDataModule();
        this.dataFile = dataModule.getDataFile();
        this.idsModule = serviceManager.getIDSModule();
        this.economy = serviceManager.getEconomy();
    }
}
