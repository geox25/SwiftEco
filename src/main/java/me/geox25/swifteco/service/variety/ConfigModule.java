package me.geox25.swifteco.service.variety;

import me.geox25.swifteco.SwiftEco;
import me.geox25.swifteco.service.ConfigService;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigModule implements ConfigService {

    // SwiftEco instance
    private final SwiftEco swiftEco;

    // Config File to Manage
    private final FileConfiguration configFile;

    public ConfigModule(FileConfiguration configFile, SwiftEco swiftEco) {
        this.configFile = configFile;
        this.swiftEco = swiftEco;
    }

    @Override
    public FileConfiguration getConfig() {
        return configFile;
    }

    public void reloadConfigFile() {
        swiftEco.reloadConfigFile();;
    }

    public boolean isAdmin(CommandSender sender) {
        boolean cfgAdmin = configFile.getStringList("admins").contains(sender.getName());
        boolean permsAdmin = sender.hasPermission("swift.admin");

        return (cfgAdmin || permsAdmin);
    }
}
