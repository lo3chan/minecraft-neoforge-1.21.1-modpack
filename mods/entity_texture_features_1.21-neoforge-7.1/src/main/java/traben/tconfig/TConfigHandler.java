/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonSyntaxException
 */
package traben.tconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Supplier;
import traben.entity_texture_features.ETF;
import traben.tconfig.TConfig;
import traben.tconfig.TConfigLog;

public class TConfigHandler<T extends TConfig> {
    private final Supplier<T> newConfigSupplier;
    private final String configFileName;
    private final Class<T> configClass;
    private final String logID;
    private T CONFIG = null;

    public TConfigHandler(Supplier<T> newConfigSupplier, String configFileName, String logID) {
        this.newConfigSupplier = newConfigSupplier;
        this.logID = logID;
        this.configFileName = configFileName.endsWith(".json") ? configFileName : configFileName + ".json";
        this.configClass = Objects.requireNonNull(((TConfig)newConfigSupplier.get()).getClass());
        this.loadFromFile();
    }

    public T getConfig() {
        if (this.CONFIG == null) {
            this.loadFromFile();
        }
        return this.CONFIG;
    }

    public void setConfig(T CONFIG) {
        this.CONFIG = CONFIG;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TConfigHandler that = (TConfigHandler)o;
        return this.configClass.equals(that.configClass) && this.configFileName.equals(that.configFileName);
    }

    public boolean configEquals(Object that) {
        if (this.CONFIG == that) {
            return true;
        }
        if (that == null || this.CONFIG.getClass() != that.getClass()) {
            return false;
        }
        return this.toJson().equals(this.toJson(that));
    }

    public int hashCode() {
        return Objects.hash(this.configFileName, this.configClass);
    }

    public void saveToFile() {
        Path configDir = ETF.getConfigDirectory();
        if (configDir == null) {
            return;
        }
        File config = new File(configDir.toFile(), this.configFileName);
        config.getParentFile().mkdirs();
        try (FileWriter fileWriter = new FileWriter(config);){
            fileWriter.write(this.toJson());
            fileWriter.close();
            this.loadFromFile(false);
        }
        catch (IOException e) {
            TConfigLog.logError(this.logID, "Config file could not be saved: " + e.getMessage());
        }
    }

    public String toJson() {
        return this.toJson(this.CONFIG);
    }

    public String toJson(Object config) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(config);
    }

    public void loadFromFile() {
        this.loadFromFile(true);
    }

    public void loadFromFile(boolean saveAfterLoad) {
        TConfig newConfig;
        Path configDir = ETF.getConfigDirectory();
        if (configDir == null) {
            this.CONFIG = (TConfig)this.newConfigSupplier.get();
            return;
        }
        File config = new File(configDir.toFile(), this.configFileName);
        if (config.exists()) {
            try (FileReader fileReader = new FileReader(config);){
                newConfig = this.fromJson(fileReader);
            }
            catch (Exception e) {
                TConfigLog.logError(this.logID, "Config could not be loaded, using defaults");
                newConfig = (TConfig)this.newConfigSupplier.get();
            }
        } else {
            newConfig = (TConfig)this.newConfigSupplier.get();
        }
        if (newConfig == null) {
            TConfigLog.logError(this.logID, "Config was null, using defaults");
            this.CONFIG = (TConfig)this.newConfigSupplier.get();
        } else {
            this.CONFIG = newConfig;
        }
        if (saveAfterLoad) {
            this.saveToFile();
        }
    }

    public T fromJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return (T)((TConfig)gson.fromJson(json, this.configClass));
    }

    public T fromJson(FileReader json) throws JsonIOException, JsonSyntaxException {
        Gson gson = new GsonBuilder().setLenient().create();
        return (T)((TConfig)gson.fromJson((Reader)json, this.configClass));
    }

    public T copyOfConfig() {
        return this.fromJson(this.toJson());
    }

    public boolean doesGUI() {
        return ((TConfig)this.getConfig()).doesGUI();
    }
}

