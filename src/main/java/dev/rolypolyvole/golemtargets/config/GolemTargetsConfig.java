package dev.rolypolyvole.golemtargets.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class GolemTargetsConfig {

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("golem-targets.properties");

    public static double range = 48.0;

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
            props.load(in);
            range = Double.parseDouble(props.getProperty("range", "48.0"));
        } catch (IOException | NumberFormatException e) {
            range = 48.0;
            save();
        }
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("range", String.valueOf(range));

        try (OutputStream out = Files.newOutputStream(CONFIG_PATH)) {
            props.store(out, "Golem Targets Config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
