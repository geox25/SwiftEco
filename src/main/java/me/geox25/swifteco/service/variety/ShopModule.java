package me.geox25.swifteco.service.variety;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ShopModule {

    // Primary yml file to manage
    private final YamlConfiguration shopDataFile;

    // I/O handle on yml file
    private final File file;

    public ShopModule(YamlConfiguration shopDataFile, File file) {
        this.shopDataFile = shopDataFile;
        this.file = file;
    }

    public YamlConfiguration getShopDataFile() {
        return this.shopDataFile;
    }
}
