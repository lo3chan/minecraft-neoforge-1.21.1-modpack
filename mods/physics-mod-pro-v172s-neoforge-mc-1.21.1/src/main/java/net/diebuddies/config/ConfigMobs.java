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
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
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
import net.diebuddies.physics.settings.mobs.MobSetting;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class ConfigMobs {
    private static final String DIR = "config/physicsmod";
    public static final MobSetting PARENT_MOB_SETTING;
    private static final String CONFIG = "physics_mobs_client_config.json";
    public static Map<String, MobSetting> customizedMobs;

    public static void init() {
    }

    public static MobSetting getMobSetting(EntityType<?> type) {
        MobSetting val = customizedMobs.get(EntityType.getKey(type).toString());
        if (val == null) {
            val = PARENT_MOB_SETTING;
        }
        return val;
    }

    public static MobSetting getMobSetting(Entity entity) {
        return ConfigMobs.getMobSetting(entity.getType());
    }

    private static JsonObject createConfig() {
        JsonObject config = new JsonObject();
        JsonArray array = new JsonArray();
        for (EntityType<?> type : PhysicsMod.renderers.keySet()) {
            String id = EntityType.getKey(type).toString();
            MobSetting setting = customizedMobs.get(id);
            if (setting == null) {
                setting = PARENT_MOB_SETTING;
            }
            JsonObject obj = new JsonObject();
            obj.add("identifier", (JsonElement)new JsonPrimitive(id));
            AdjustableUtil.writeObject(obj, setting);
            array.add((JsonElement)obj);
        }
        config.add("customizedMobs", (JsonElement)array);
        return config;
    }

    public static void resetMobs() {
        for (Map.Entry<String, MobSetting> entry : customizedMobs.entrySet()) {
            customizedMobs.put(entry.getKey(), PARENT_MOB_SETTING);
        }
        ConfigMobs.save();
    }

    public static void save() {
        File configFile;
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if ((configFile = new File("config/physicsmod/physics_mobs_client_config.json")).exists()) {
            configFile.delete();
        }
        JsonObject config = ConfigMobs.createConfig();
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
        PARENT_MOB_SETTING = new MobSetting();
        customizedMobs = new Object2ObjectOpenHashMap();
        JsonObject config = ConfigMobs.createConfig();
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!(configFile = new File("config/physicsmod/physics_mobs_client_config.json")).exists()) {
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
            JsonArray array = config.get("customizedMobs").getAsJsonArray();
            for (int i = 0; i < array.size(); ++i) {
                JsonObject obj = array.get(i).getAsJsonObject();
                String identifier = obj.get("identifier").getAsString();
                MobSetting setting = (MobSetting)AdjustableUtil.readObject(MobSetting.class, obj);
                customizedMobs.put(identifier, setting);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

