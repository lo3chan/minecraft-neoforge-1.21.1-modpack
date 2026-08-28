/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonIOException
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.world.level.block.Block
 */
package net.diebuddies.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.blocks.BlockSetting;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.minecraft.world.level.block.Block;

public final class ConfigBlocks {
    private static final String DIR = "config/physicsmod";
    public static final BlockSetting PARENT_BLOCK_SETTING = new BlockSetting();
    private static final String CONFIG = "physics_blocks_client_config.json";
    public static Map<String, BlockSetting> customizedBlocks = new Object2ObjectOpenHashMap();

    public static void reload() {
        File configFile;
        JsonObject config = ConfigBlocks.createConfig();
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!(configFile = new File("config/physicsmod/physics_blocks_client_config.json")).exists()) {
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
            JsonArray array = config.get("customizedBlocks").getAsJsonArray();
            for (int i = 0; i < array.size(); ++i) {
                JsonObject obj = array.get(i).getAsJsonObject();
                String identifier = obj.get("identifier").getAsString();
                BlockSetting setting = (BlockSetting)AdjustableUtil.readObject(BlockSetting.class, obj);
                customizedBlocks.put(identifier, setting);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void init() {
    }

    public static BlockSetting getBlockSetting(Block block) {
        BlockSetting val = customizedBlocks.get(PhysicsMod.registeredBlocks.get(block));
        if (val == null) {
            val = PARENT_BLOCK_SETTING;
        }
        return val;
    }

    private static JsonObject createConfig() {
        JsonObject config = new JsonObject();
        JsonArray array = new JsonArray();
        for (String id : PhysicsMod.registeredBlocks.values()) {
            BlockSetting setting = customizedBlocks.get(id);
            if (setting == null) {
                setting = PARENT_BLOCK_SETTING;
            }
            JsonObject obj = new JsonObject();
            obj.add("identifier", (JsonElement)new JsonPrimitive(id));
            AdjustableUtil.writeObject(obj, setting);
            array.add((JsonElement)obj);
        }
        config.add("customizedBlocks", (JsonElement)array);
        return config;
    }

    public static void resetBlocks() {
        for (Map.Entry<String, BlockSetting> entry : customizedBlocks.entrySet()) {
            customizedBlocks.put(entry.getKey(), PARENT_BLOCK_SETTING);
        }
        ConfigBlocks.save();
    }

    public static void save() {
        File configFile;
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if ((configFile = new File("config/physicsmod/physics_blocks_client_config.json")).exists()) {
            configFile.delete();
        }
        JsonObject config = ConfigBlocks.createConfig();
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
        ConfigBlocks.reload();
    }
}

