package me.geox25.swifteco.service.variety;

import me.geox25.swifteco.service.YMLService;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YMLModule implements YMLService {

    // Primary yml file to manage
    private final YamlConfiguration dataFile;

    // I/O Handle on yml file
    private final File file;

    public YMLModule(YamlConfiguration dataFile, File file) {
        this.dataFile = dataFile;
        this.file = file;
    }

    @Override
    public void save() throws IOException {
        dataFile.save(file);
    }
}
