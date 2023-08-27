package me.geox25.swifteco;

import me.geox25.swifteco.api.MainSwiftAPI;
import me.geox25.swifteco.event.Events;
import me.geox25.swifteco.gui.GuiManager;
import me.geox25.swifteco.service.ServiceManager;
import me.geox25.swifteco.service.variety.ConfigModule;
import me.geox25.swifteco.service.variety.DataModule;
import me.geox25.swifteco.service.variety.IDSModule;
import me.geox25.swifteco.service.variety.ShopModule;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ipvp.canvas.MenuFunctionListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;

public final class SwiftEco extends JavaPlugin implements SwiftAPICompatible {

    // data.yml classes
    private YamlConfiguration dataYMLConfig;
    private File dataYMLFile;

    // ids.yml classes
    private YamlConfiguration idYMLConfig;
    private File idsYMLFile;

    // Configuration class
    private FileConfiguration config;
    private File configFile;

    // shop.yml classes
    private YamlConfiguration shopYMLConfig;
    private File shopYMLFile;

    // ServiceManager Instance
    private ServiceManager serviceManager;

    // SwiftManager Instance
    private SwiftManager manager;

    // API
    private MainSwiftAPI api;

    // Economy instance
    private Economy economy;

    // GuiManager instance
    private GuiManager guiManager;

    @Override
    public void onEnable() {

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        } else {
            getLogger().log(Level.SEVERE, "Could not hook onto Vault, disabling...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Save default config if one does not exist
        saveDefaultConfig();

        // Retrieve config
        config = getConfig();

        // Plugin Data Folder
        File dataFolder = getDataFolder();

        // dataYMLFile Handle
        dataYMLFile = createOrAssignDataFile(dataFolder, "data.yml");
        idsYMLFile = createOrAssignDataFile(dataFolder, "ids.yml");
        shopYMLFile = copyOrAssignResource(dataFolder, "shop.yml");

        // Load Configuration into YML Classes from files
        dataYMLConfig = YamlConfiguration.loadConfiguration(dataYMLFile);
        idYMLConfig = YamlConfiguration.loadConfiguration(idsYMLFile);
        shopYMLConfig = YamlConfiguration.loadConfiguration(shopYMLFile);

        // Variety Classes for managing data
        DataModule dataModule = new DataModule(dataYMLConfig, dataYMLFile);
        IDSModule idsModule = new IDSModule(idYMLConfig, idsYMLFile);
        ConfigModule configModule = new ConfigModule(config, this);
        ShopModule shopModule = new ShopModule(shopYMLConfig, shopYMLFile);

        // Initialize ServiceManager
        // Pass data.yml, ids.yml, config.yml, and vault economy instance
        serviceManager = new ServiceManager(dataModule, idsModule, configModule, shopModule, economy);

        // Initialize GuiManager
        guiManager = new GuiManager(serviceManager, this);

        // If government account does not exist, create it
        if (!dataYMLConfig.contains("account.gov")) {
            dataYMLConfig.set("account.gov.balance", 0);
            dataYMLConfig.set("account.gov.type", "gov");
            dataYMLConfig.set("account.gov.managers", new ArrayList<String>());
        }

        /*
        Register commands
         */
        this.manager = new SwiftManager(dataYMLConfig, config, serviceManager, guiManager);
        this.api = new MainSwiftAPI(serviceManager);
        Objects.requireNonNull(getCommand("swift")).setExecutor(this.manager);

        getServer().getPluginManager().registerEvents(new Events(serviceManager), this);
        getServer().getPluginManager().registerEvents(new MenuFunctionListener(), this);
        getServer().getServicesManager().register(SwiftAPICompatible.class, this, this, ServicePriority.High);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // Saves the current config file
        saveConfig();

        // Save dataFile
        try {
            dataYMLConfig.save(dataYMLFile);
            idYMLConfig.save(idsYMLFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadConfigFile() {
        reloadConfig();
        config = getConfig();

        shopYMLConfig = YamlConfiguration.loadConfiguration(shopYMLFile);

        // Variety Classes for managing data
        DataModule dataModule = new DataModule(dataYMLConfig, dataYMLFile);
        IDSModule idsModule = new IDSModule(idYMLConfig, idsYMLFile);
        ConfigModule configModule = new ConfigModule(config, this);
        ShopModule shopModule = new ShopModule(shopYMLConfig, shopYMLFile);

        // Initialize ServiceManager
        // Pass data.yml, ids.yml, config.yml, and vault economy instance
        serviceManager = new ServiceManager(dataModule, idsModule, configModule, shopModule, economy);

        // Initialize GuiManager
        guiManager = new GuiManager(serviceManager, this);
        api = new MainSwiftAPI(serviceManager);

        manager.reloadCmds(dataYMLConfig, config, serviceManager, guiManager);
        dataYMLConfig.set("account.gov.managers", config.getStringList("admins"));
    }

    public File createOrAssignDataFile(File dataFolder, String fileName) {
        File file = new File(dataFolder + File.separator + fileName);

        // Create the file if it does not exist
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                getLogger().log(Level.SEVERE, "Error with " + fileName + ", disabling...");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }

        return file;
    }

    public File copyOrAssignResource(File dataFolder, String fileName) {
        File file = new File(getDataFolder(), fileName);

        if (!file.exists()) {

            // Get the default shop.yml from the plugin's resources
            try (InputStream inputStream = getResource(fileName)) {
                assert inputStream != null;
                Files.copy(inputStream, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception appropriately for your plugin
            }
        }

        return file;
    }

    public SwiftAPI getAPI() {
        return this.api;
    }
}
