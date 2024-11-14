package com.nettakrim.panorandom;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Data {
    private File data = null;

    public Data() {
        load();
    }

    private void ResolveDataFile() {
        if (data != null) return;
        data = FabricLoader.getInstance().getConfigDir().resolve(PanorandomClient.MOD_ID+".txt").toFile();
    }

    public void save() {
        ResolveDataFile();
        try {
            FileWriter writer = new FileWriter(data);
            StringBuilder s = new StringBuilder();
            s.append(PanorandomClient.rerollMode);

            for (Identifier identifier : PanorandomClient.DISABLED) {
                s.append("\n").append(identifier.toString());
            }

            writer.write(s.toString());
            writer.close();
        } catch (IOException e) {
            PanorandomClient.LOGGER.info("Failed to save data");
        }
    }

    public void load() {
        ResolveDataFile();
        try {
            data.createNewFile();
            Scanner scanner = new Scanner(data);

            if (scanner.hasNext()) {
                PanorandomClient.rerollMode = Integer.parseInt(scanner.nextLine());

                while (scanner.hasNext()) {
                    String s = scanner.nextLine();
                    PanorandomClient.DISABLED.add(Identifier.of(s));
                }
            }

            scanner.close();
        } catch (IOException e) {
            PanorandomClient.LOGGER.info("Failed to load data");
        }
    }
}
