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
 *  net.minecraft.world.level.block.Blocks
 *  org.joml.Vector3f
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
import net.diebuddies.physics.vines.AdjustableUtil;
import net.diebuddies.physics.vines.DoorSetting;
import net.diebuddies.physics.vines.DynamicSetting;
import net.diebuddies.physics.vines.HangingSignSetting;
import net.diebuddies.physics.vines.TrapdoorSetting;
import net.diebuddies.physics.vines.VineHelper;
import net.diebuddies.physics.vines.VineSetting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3f;

public final class ConfigVines {
    private static final String DIR = "config/physicsmod";
    private static final String CONFIG = "physics_vines_client_config.json";
    public static Map<Block, DynamicSetting> configSettings;

    public static void loadDefaultConfigSettings() {
        configSettings.clear();
        configSettings.put(Blocks.TWISTING_VINES, new VineSetting(true, true, false, new Vector3f(1.0f), 2400.0f, 10.0f, true, Blocks.TWISTING_VINES_PLANT));
        configSettings.put(Blocks.KELP, new VineSetting(true, false, false, new Vector3f(1.0f), 45.0f, 45.0f, true, Blocks.KELP_PLANT));
        configSettings.put(Blocks.WEEPING_VINES, new VineSetting(false, false, false, new Vector3f(1.0f), 45.0f, 45.0f, true, Blocks.WEEPING_VINES_PLANT));
        configSettings.put(Blocks.CAVE_VINES, new VineSetting(false, false, false, new Vector3f(1.0f), 45.0f, 45.0f, true, Blocks.CAVE_VINES_PLANT));
        configSettings.put(Blocks.SUGAR_CANE, new VineSetting(true, true, false, new Vector3f(1.0f), 2300.0f, 90.0f, true, null));
        configSettings.put(Blocks.VINE, new VineSetting(false, false, true, new Vector3f(1.0f), 45.0f, 45.0f, true, null));
        configSettings.put(Blocks.CHAIN, new VineSetting(false, false, true, new Vector3f(1.5f, 1.0f, 1.5f), 60.0f, 40.0f, true, null));
        configSettings.put(Blocks.ROSE_BUSH, new VineSetting(true, true, false, new Vector3f(1.0f), 110.0f, 48.0f, true, null));
        configSettings.put(Blocks.TALL_GRASS, new VineSetting(true, true, false, new Vector3f(1.0f), 110.0f, 48.0f, true, null));
        configSettings.put(Blocks.LARGE_FERN, new VineSetting(true, true, false, new Vector3f(1.0f), 110.0f, 48.0f, true, null));
        configSettings.put(Blocks.PEONY, new VineSetting(true, true, false, new Vector3f(1.0f), 110.0f, 48.0f, true, null));
        configSettings.put(Blocks.LILAC, new VineSetting(true, true, false, new Vector3f(1.0f), 110.0f, 48.0f, true, null));
        configSettings.put(Blocks.SUNFLOWER, new VineSetting(true, true, false, new Vector3f(1.0f), 110.0f, 48.0f, true, null));
        configSettings.put(Blocks.TALL_SEAGRASS, new VineSetting(true, false, false, new Vector3f(1.0f), 110.0f, 48.0f, true, null));
        float scale = 0.85f;
        float stiffness = 1580.0f;
        float damping = 8.0f;
        configSettings.put(Blocks.OAK_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.ACACIA_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.BIRCH_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.DARK_OAK_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.IRON_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.JUNGLE_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.SPRUCE_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WARPED_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.CRIMSON_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.BAMBOO_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.MANGROVE_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.CHERRY_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.COPPER_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.EXPOSED_COPPER_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WEATHERED_COPPER_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.OXIDIZED_COPPER_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WAXED_COPPER_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.OAK_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.ACACIA_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.BIRCH_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.DARK_OAK_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.IRON_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.JUNGLE_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.SPRUCE_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WARPED_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.CRIMSON_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.BAMBOO_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.MANGROVE_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.CHERRY_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.COPPER_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.EXPOSED_COPPER_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WEATHERED_COPPER_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.OXIDIZED_COPPER_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WAXED_COPPER_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WAXED_EXPOSED_COPPER_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WAXED_WEATHERED_COPPER_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.WAXED_OXIDIZED_COPPER_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
        configSettings.put(Blocks.OAK_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9f, 0.9f, 0.9f), 45.0f, 45.0f, true));
        configSettings.put(Blocks.ACACIA_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9f, 0.9f, 0.9f), 45.0f, 45.0f, true));
        configSettings.put(Blocks.BIRCH_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9f, 0.9f, 0.9f), 45.0f, 45.0f, true));
        configSettings.put(Blocks.DARK_OAK_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9f, 0.9f, 0.9f), 45.0f, 45.0f, true));
        configSettings.put(Blocks.JUNGLE_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9f, 0.9f, 0.9f), 45.0f, 45.0f, true));
        configSettings.put(Blocks.SPRUCE_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9f, 0.9f, 0.9f), 45.0f, 45.0f, true));
        configSettings.put(Blocks.WARPED_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9f, 0.9f, 0.9f), 45.0f, 45.0f, true));
        configSettings.put(Blocks.CRIMSON_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9f, 0.9f, 0.9f), 45.0f, 45.0f, true));
        configSettings.put(Blocks.BAMBOO_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9f, 0.9f, 0.9f), 45.0f, 45.0f, true));
        configSettings.put(Blocks.MANGROVE_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9f, 0.9f, 0.9f), 45.0f, 45.0f, true));
        configSettings.put(Blocks.CHERRY_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9f, 0.9f, 0.9f), 45.0f, 45.0f, true));
    }

    public static void init() {
    }

    private static JsonObject createConfig() {
        JsonObject config = new JsonObject();
        JsonArray array = new JsonArray();
        for (Map.Entry<Block, DynamicSetting> entry : configSettings.entrySet()) {
            String id = PhysicsMod.registeredBlocks.get(entry.getKey());
            if (id == null) continue;
            JsonObject obj = new JsonObject();
            obj.add("blockID", (JsonElement)new JsonPrimitive(id));
            DynamicSetting setting = entry.getValue();
            AdjustableUtil.writeDynamicSetting(obj, setting);
            array.add((JsonElement)obj);
        }
        config.add("customizedDynamicBlocks", (JsonElement)array);
        return config;
    }

    public static void resetVines() {
        ConfigVines.loadDefaultConfigSettings();
        VineHelper.initFromConfigSettings();
        ConfigVines.save();
    }

    public static void save() {
        File configFile;
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if ((configFile = new File("config/physicsmod/physics_vines_client_config.json")).exists()) {
            configFile.delete();
        }
        JsonObject config = ConfigVines.createConfig();
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
        configSettings = new Object2ObjectOpenHashMap();
        ConfigVines.loadDefaultConfigSettings();
        JsonObject config = ConfigVines.createConfig();
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!(configFile = new File("config/physicsmod/physics_vines_client_config.json")).exists()) {
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
            configSettings.clear();
            JsonArray array = config.get("customizedDynamicBlocks").getAsJsonArray();
            for (int i = 0; i < array.size(); ++i) {
                JsonObject obj = array.get(i).getAsJsonObject();
                Block block = PhysicsMod.invRegisteredBlocks.get(obj.get("blockID").getAsString());
                DynamicSetting setting = AdjustableUtil.readDynamicSetting(obj);
                configSettings.put(block, setting);
            }
        }
        catch (Exception e) {
            ConfigVines.loadDefaultConfigSettings();
            ConfigVines.save();
        }
        VineHelper.initFromConfigSettings();
    }
}

