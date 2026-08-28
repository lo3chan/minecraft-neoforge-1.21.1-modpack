/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 */
package net.diebuddies.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

public final class ConfigServer {
    private static final String DIR = "config/physicsmod";
    private static final String CONFIG = "physics_server_config.json";
    public static volatile boolean collapse;
    public static volatile boolean dropBlocks;
    public static volatile int maxCollapseObjects;
    public static volatile int collapseSpeed;

    public static void init() {
    }

    private static JsonObject createConfig() {
        JsonObject config = new JsonObject();
        config.add("collapse", (JsonElement)new JsonPrimitive(Boolean.valueOf(collapse)));
        config.add("maxCollapseObjects", (JsonElement)new JsonPrimitive((Number)maxCollapseObjects));
        config.add("collapseSpeed", (JsonElement)new JsonPrimitive((Number)collapseSpeed));
        config.add("dropBlocks", (JsonElement)new JsonPrimitive(Boolean.valueOf(dropBlocks)));
        return config;
    }

    public static void save() {
        File configFile;
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if ((configFile = new File("config/physicsmod/physics_server_config.json")).exists()) {
            configFile.delete();
        }
        JsonObject config = ConfigServer.createConfig();
        try {
            configFile.createNewFile();
            try (FileWriter writer = new FileWriter(configFile);){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson((JsonElement)config, (Appendable)writer);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        File configFile;
        collapse = true;
        dropBlocks = true;
        maxCollapseObjects = 100;
        collapseSpeed = 10;
        JsonObject config = ConfigServer.createConfig();
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!(configFile = new File("config/physicsmod/physics_server_config.json")).exists()) {
            try {
                configFile.createNewFile();
                try (FileWriter writer = new FileWriter(configFile);){
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    gson.toJson((JsonElement)config, (Appendable)writer);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Gson gson = new Gson();
            try {
                config = (JsonObject)gson.fromJson((Reader)new FileReader(configFile), JsonObject.class);
            }
            catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            collapse = config.get("collapse").getAsBoolean();
            maxCollapseObjects = config.get("maxCollapseObjects").getAsInt();
            collapseSpeed = config.get("collapseSpeed").getAsInt();
            dropBlocks = config.get("dropBlocks").getAsBoolean();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

