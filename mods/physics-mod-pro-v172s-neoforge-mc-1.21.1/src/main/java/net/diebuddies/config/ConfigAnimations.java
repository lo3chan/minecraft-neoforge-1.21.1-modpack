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
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundEvents
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
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.animation.AnimationType;
import net.diebuddies.physics.animation.CurveType;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;

public final class ConfigAnimations {
    private static final String DIR = "config/physicsmod";
    public static Animation DEFAULT_ANIMATION;
    private static final String CONFIG = "physics_animations_client_config.json";
    public static Long2ObjectMap<Animation> animations;

    public static void loadDefaultConfigSettings() {
        DEFAULT_ANIMATION = new Animation("default", CurveType.Linear, 0.5f);
        animations.put(0L, (Object)DEFAULT_ANIMATION);
        Animation vanish = new Animation("vanish", CurveType.Ease_out, 0.5f);
        vanish.despawnType = AnimationType.Vanish;
        animations.put(1L, (Object)vanish);
        Animation bounce = new Animation("bounce", CurveType.Bounce, 0.6f);
        bounce.despawnType = AnimationType.Shrink;
        animations.put(2L, (Object)bounce);
        Animation explosion = new Animation("explosion", CurveType.Ease_in, 0.5f);
        explosion.despawnType = AnimationType.Shrink_and_Vanish;
        explosion.addParticleSpawn((ParticleOptions)ParticleTypes.LAVA, 4, 0.5, 1.0, 0.0, 0.0, 0.0, 0.1, SoundEvents.LAVA_EXTINGUISH);
        explosion.addParticleSpawn((ParticleOptions)ParticleTypes.POOF, 7, 0.5, 1.0, 0.0, 0.0, 0.0, 0.1, null);
        animations.put(3L, (Object)explosion);
        Animation poof = new Animation("poof", CurveType.Linear, 0.0f);
        poof.despawnType = AnimationType.Shrink;
        poof.addParticleSpawn((ParticleOptions)ParticleTypes.POOF, 6, 0.8, 1.0, 0.0, 0.0, 0.0, 0.2, SoundEvents.FOX_SNIFF);
        animations.put(4L, (Object)poof);
        Animation portal = new Animation("portal", CurveType.Linear, 0.0f);
        portal.despawnType = AnimationType.Shrink;
        portal.addParticleSpawn((ParticleOptions)ParticleTypes.REVERSE_PORTAL, 16, 0.8, 1.0, 0.0, 0.0, 0.0, 0.1, SoundEvents.PORTAL_AMBIENT);
        animations.put(5L, (Object)portal);
        Animation splash = new Animation("splash", CurveType.Linear, 0.0f);
        splash.despawnType = AnimationType.Shrink;
        splash.addParticleSpawn((ParticleOptions)ParticleTypes.SPLASH, 16, 0.7, 1.0, 0.0, 0.0, 0.0, 0.1, SoundEvents.DOLPHIN_SPLASH);
        animations.put(6L, (Object)splash);
    }

    public static void reload() {
        File configFile;
        ConfigAnimations.loadDefaultConfigSettings();
        JsonObject config = ConfigAnimations.createConfig();
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!(configFile = new File("config/physicsmod/physics_animations_client_config.json")).exists()) {
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
            animations.clear();
            JsonArray array = config.get("customizedAnimations").getAsJsonArray();
            for (int i = 0; i < array.size(); ++i) {
                JsonObject obj = array.get(i).getAsJsonObject();
                long identifier = obj.get("identifier").getAsLong();
                Animation animation = (Animation)AdjustableUtil.readObject(Animation.class, obj);
                animations.put(identifier, (Object)animation);
            }
        }
        catch (Exception e) {
            ConfigAnimations.loadDefaultConfigSettings();
            ConfigAnimations.save();
        }
    }

    public static void init() {
    }

    private static JsonObject createConfig() {
        JsonObject config = new JsonObject();
        JsonArray array = new JsonArray();
        for (Map.Entry entry : animations.entrySet()) {
            Long id = (Long)entry.getKey();
            Animation animation = (Animation)entry.getValue();
            JsonObject obj = new JsonObject();
            obj.add("identifier", (JsonElement)new JsonPrimitive((Number)id));
            AdjustableUtil.writeObject(obj, animation);
            array.add((JsonElement)obj);
        }
        config.add("customizedAnimations", (JsonElement)array);
        return config;
    }

    public static void save() {
        File configFile;
        File directory = new File(DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if ((configFile = new File("config/physicsmod/physics_animations_client_config.json")).exists()) {
            configFile.delete();
        }
        JsonObject config = ConfigAnimations.createConfig();
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
        animations = new Long2ObjectOpenHashMap();
        ConfigAnimations.reload();
    }
}

