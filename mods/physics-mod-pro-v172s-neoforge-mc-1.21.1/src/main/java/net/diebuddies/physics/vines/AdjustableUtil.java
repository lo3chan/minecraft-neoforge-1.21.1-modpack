/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.client.Options
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.locale.Language
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.level.block.Block
 *  org.joml.Vector3f
 */
package net.diebuddies.physics.vines;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.gui.AnimationOption;
import net.diebuddies.physics.settings.gui.ButtonOption;
import net.diebuddies.physics.settings.gui.LabelOption;
import net.diebuddies.physics.settings.gui.ParticleOption;
import net.diebuddies.physics.settings.gui.SoundOption;
import net.diebuddies.physics.settings.gui.TextOption;
import net.diebuddies.physics.settings.gui.legacy.CycleOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.gui.legacy.ProgressOption;
import net.diebuddies.physics.settings.vines.BlockOption;
import net.diebuddies.physics.vines.Adjustable;
import net.diebuddies.physics.vines.DynamicSetting;
import net.diebuddies.physics.vines.DynamicSettingEnum;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import org.joml.Vector3f;

public class AdjustableUtil {
    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        if (type.getSuperclass() != null) {
            AdjustableUtil.getAllFields(fields, type.getSuperclass());
        }
        fields.addAll(Arrays.asList(type.getDeclaredFields()));
        return fields;
    }

    public static Object readObject(Class<?> clazz, JsonObject json) {
        ObjectArrayList fields = new ObjectArrayList();
        AdjustableUtil.getAllFields((List<Field>)fields, clazz);
        Object object = null;
        try {
            object = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            AdjustableUtil.readFields(object, json, (List<Field>)fields);
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static JsonObject writeObject(JsonObject json, Object object) {
        Class<?> type = object.getClass();
        ObjectArrayList fields = new ObjectArrayList();
        AdjustableUtil.getAllFields((List<Field>)fields, type);
        AdjustableUtil.writeFields(object, json, (List<Field>)fields);
        return json;
    }

    public static DynamicSetting readDynamicSetting(JsonObject json) {
        int settingID = json.get("settingID").getAsInt();
        Class<?> type = null;
        for (DynamicSettingEnum ds : DynamicSettingEnum.values()) {
            if (ds.getID() != settingID) continue;
            type = ds.getType();
        }
        if (type == null) {
            return null;
        }
        ObjectArrayList fields = new ObjectArrayList();
        AdjustableUtil.getAllFields((List<Field>)fields, type);
        DynamicSetting object = null;
        try {
            object = (DynamicSetting)type.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
        }
        AdjustableUtil.readFields(object, json, (List<Field>)fields);
        return object;
    }

    public static void writeDynamicSetting(JsonObject json, DynamicSetting object) {
        Class<?> type = object.getClass();
        ObjectArrayList fields = new ObjectArrayList();
        AdjustableUtil.getAllFields((List<Field>)fields, type);
        int settingID = 0;
        for (DynamicSettingEnum ds : DynamicSettingEnum.values()) {
            if (ds.getType() != object.getClass()) continue;
            settingID = ds.getID();
        }
        json.add("settingID", (JsonElement)new JsonPrimitive((Number)settingID));
        AdjustableUtil.writeFields(object, json, (List<Field>)fields);
    }

    private static void readFields(Object object, JsonObject json, List<Field> fields) {
        for (Field f : fields) {
            try {
                if (!f.isAnnotationPresent(Adjustable.class)) continue;
                Adjustable adjustable = f.getAnnotation(Adjustable.class);
                if (f.getType().equals(Byte.TYPE)) {
                    f.setByte(object, json.get(adjustable.id()).getAsByte());
                    continue;
                }
                if (f.getType().equals(Integer.TYPE)) {
                    f.setInt(object, json.get(adjustable.id()).getAsInt());
                    continue;
                }
                if (f.getType().equals(Long.TYPE)) {
                    f.setLong(object, json.get(adjustable.id()).getAsLong());
                    continue;
                }
                if (f.getType().equals(Float.TYPE)) {
                    f.setFloat(object, json.get(adjustable.id()).getAsFloat());
                    continue;
                }
                if (f.getType().equals(Double.TYPE)) {
                    f.setDouble(object, json.get(adjustable.id()).getAsDouble());
                    continue;
                }
                if (f.getType().equals(Short.TYPE)) {
                    f.setShort(object, json.get(adjustable.id()).getAsShort());
                    continue;
                }
                if (f.getType().equals(Boolean.TYPE)) {
                    f.setBoolean(object, json.get(adjustable.id()).getAsBoolean());
                    continue;
                }
                if (f.getType().equals(String.class)) {
                    f.set(object, json.get(adjustable.id()).getAsString());
                    continue;
                }
                if (f.getType().isEnum()) {
                    f.set(object, f.getType().getEnumConstants()[json.get(adjustable.id()).getAsInt()]);
                    continue;
                }
                if (f.getType().equals(Vector3f.class)) {
                    Field xField = Vector3f.class.getDeclaredField("x");
                    Field yField = Vector3f.class.getDeclaredField("y");
                    Field zField = Vector3f.class.getDeclaredField("z");
                    Vector3f value = (Vector3f)f.get(object);
                    xField.setFloat(value, json.get(adjustable.id() + " x").getAsFloat());
                    yField.setFloat(value, json.get(adjustable.id() + " y").getAsFloat());
                    zField.setFloat(value, json.get(adjustable.id() + " z").getAsFloat());
                    continue;
                }
                if (f.getType().equals(Block.class)) {
                    JsonElement linkObj = json.get(adjustable.id());
                    Block link = null;
                    if (linkObj != null && !linkObj.isJsonNull()) {
                        link = PhysicsMod.invRegisteredBlocks.get(linkObj.getAsString());
                    }
                    f.set(object, link);
                    continue;
                }
                if (f.getType().equals(ParticleOptions.class)) {
                    f.set(object, PhysicsMod.registeredParticles.get(json.get(adjustable.id()).getAsString()));
                    continue;
                }
                if (f.getType().equals(SoundEvent.class)) {
                    JsonElement id = json.get(adjustable.id());
                    if (id == null || id.isJsonNull()) {
                        f.set(object, null);
                        continue;
                    }
                    f.set(object, PhysicsMod.registeredSounds.get(json.get(adjustable.id()).getAsString()));
                    continue;
                }
                if (f.getType().equals(Animation.class)) {
                    f.set(object, ConfigAnimations.animations.get(json.get(adjustable.id()).getAsLong()));
                    continue;
                }
                if (Collection.class.isAssignableFrom(f.getType())) {
                    Collection collection = (Collection)f.get(object);
                    JsonArray arr = json.get(adjustable.id()).getAsJsonArray();
                    for (int i = 0; i < arr.size(); ++i) {
                        JsonObject subObj = arr.get(i).getAsJsonObject();
                        Object result = AdjustableUtil.readObject((Class)((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0], subObj);
                        collection.add(result);
                    }
                    continue;
                }
                Object result = AdjustableUtil.readObject(f.getType(), json.get(adjustable.id()).getAsJsonObject());
                f.set(object, result);
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeFields(Object object, JsonObject json, List<Field> fields) {
        for (Field f : fields) {
            try {
                if (!f.isAnnotationPresent(Adjustable.class)) continue;
                Adjustable adjustable = f.getAnnotation(Adjustable.class);
                if (f.getType().equals(Byte.TYPE)) {
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive((Number)f.getByte(object)));
                    continue;
                }
                if (f.getType().equals(Integer.TYPE)) {
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive((Number)f.getInt(object)));
                    continue;
                }
                if (f.getType().equals(Long.TYPE)) {
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive((Number)f.getLong(object)));
                    continue;
                }
                if (f.getType().equals(Float.TYPE)) {
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive((Number)Float.valueOf(f.getFloat(object))));
                    continue;
                }
                if (f.getType().equals(Double.TYPE)) {
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive((Number)f.getDouble(object)));
                    continue;
                }
                if (f.getType().equals(Short.TYPE)) {
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive((Number)f.getShort(object)));
                    continue;
                }
                if (f.getType().equals(Boolean.TYPE)) {
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive(Boolean.valueOf(f.getBoolean(object))));
                    continue;
                }
                if (f.getType().equals(String.class)) {
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive((String)f.get(object)));
                    continue;
                }
                if (f.getType().isEnum()) {
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive((Number)((Enum)f.get(object)).ordinal()));
                    continue;
                }
                if (f.getType().equals(Vector3f.class)) {
                    Field xField = Vector3f.class.getDeclaredField("x");
                    Field yField = Vector3f.class.getDeclaredField("y");
                    Field zField = Vector3f.class.getDeclaredField("z");
                    Vector3f value = (Vector3f)f.get(object);
                    json.add(adjustable.id() + " x", (JsonElement)new JsonPrimitive((Number)Float.valueOf(xField.getFloat(value))));
                    json.add(adjustable.id() + " y", (JsonElement)new JsonPrimitive((Number)Float.valueOf(yField.getFloat(value))));
                    json.add(adjustable.id() + " z", (JsonElement)new JsonPrimitive((Number)Float.valueOf(zField.getFloat(value))));
                    continue;
                }
                if (f.getType().equals(Block.class)) {
                    Block block = (Block)f.get(object);
                    if (block == null) {
                        json.add(adjustable.id(), (JsonElement)JsonNull.INSTANCE);
                        continue;
                    }
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive(PhysicsMod.registeredBlocks.get(block)));
                    continue;
                }
                if (f.getType().equals(ParticleOptions.class)) {
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive(PhysicsMod.invRegisteredParticles.getOrDefault(f.get(object), "minecraft:lava")));
                    continue;
                }
                if (f.getType().equals(SoundEvent.class)) {
                    String sound = PhysicsMod.invRegisteredSounds.get(f.get(object));
                    if (sound == null) {
                        json.add(adjustable.id(), (JsonElement)JsonNull.INSTANCE);
                        continue;
                    }
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive(PhysicsMod.invRegisteredSounds.get(f.get(object))));
                    continue;
                }
                if (f.getType().equals(Animation.class)) {
                    Object animation = f.get(object);
                    long id = -1L;
                    for (Long2ObjectMap.Entry entry : ConfigAnimations.animations.long2ObjectEntrySet()) {
                        if (!((Animation)entry.getValue()).equals(animation)) continue;
                        id = entry.getLongKey();
                        break;
                    }
                    json.add(adjustable.id(), (JsonElement)new JsonPrimitive((Number)id));
                    continue;
                }
                if (Collection.class.isAssignableFrom(f.getType())) {
                    Collection collection = (Collection)f.get(object);
                    JsonArray arr = new JsonArray();
                    for (Object it : collection) {
                        JsonObject subObj = new JsonObject();
                        AdjustableUtil.writeObject(subObj, it);
                        arr.add((JsonElement)subObj);
                    }
                    json.add(adjustable.id(), (JsonElement)arr);
                    continue;
                }
                JsonObject subObj = new JsonObject();
                AdjustableUtil.writeObject(subObj, f.get(object));
                json.add(adjustable.id(), (JsonElement)subObj);
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<LegacyOption> generateOptions(Screen screen, Object object) {
        return AdjustableUtil.generateOptions(screen, object, () -> {}, () -> {});
    }

    public static List<LegacyOption> generateOptions(Screen screen, Object object, Runnable onRefresh, Runnable onUpdate) {
        Class<?> type = object.getClass();
        ObjectArrayList fields = new ObjectArrayList();
        AdjustableUtil.getAllFields((List<Field>)fields, type);
        ObjectArrayList options = new ObjectArrayList();
        for (Field f : fields) {
            try {
                if (!f.isAnnotationPresent(Adjustable.class)) continue;
                Adjustable adjustable = f.getAnnotation(Adjustable.class);
                if (f.getType().equals(Byte.TYPE)) {
                    options.add(AdjustableUtil.createProgressOptionByte(Language.getInstance().getOrDefault(adjustable.translationId()), adjustable.min(), adjustable.max(), adjustable.step(), Language.getInstance().getOrDefault(adjustable.maxTranslationId()), f, object, onUpdate));
                    continue;
                }
                if (f.getType().equals(Integer.TYPE)) {
                    options.add(AdjustableUtil.createProgressOptionInt(Language.getInstance().getOrDefault(adjustable.translationId()), adjustable.min(), adjustable.max(), adjustable.step(), Language.getInstance().getOrDefault(adjustable.maxTranslationId()), f, object, onUpdate));
                    continue;
                }
                if (f.getType().equals(Long.TYPE)) {
                    options.add(AdjustableUtil.createProgressOptionLong(Language.getInstance().getOrDefault(adjustable.translationId()), adjustable.min(), adjustable.max(), adjustable.step(), Language.getInstance().getOrDefault(adjustable.maxTranslationId()), f, object, onUpdate));
                    continue;
                }
                if (f.getType().equals(Float.TYPE)) {
                    options.add(AdjustableUtil.createProgressOptionFloat(Language.getInstance().getOrDefault(adjustable.translationId()), adjustable.min(), adjustable.max(), adjustable.step(), Language.getInstance().getOrDefault(adjustable.maxTranslationId()), f, object, onUpdate));
                    continue;
                }
                if (f.getType().equals(Double.TYPE)) {
                    options.add(AdjustableUtil.createProgressOptionDouble(Language.getInstance().getOrDefault(adjustable.translationId()), adjustable.min(), adjustable.max(), adjustable.step(), Language.getInstance().getOrDefault(adjustable.maxTranslationId()), f, object, onUpdate));
                    continue;
                }
                if (f.getType().equals(Short.TYPE)) {
                    options.add(AdjustableUtil.createProgressOptionShort(Language.getInstance().getOrDefault(adjustable.translationId()), adjustable.min(), adjustable.max(), adjustable.step(), Language.getInstance().getOrDefault(adjustable.maxTranslationId()), f, object, onUpdate));
                    continue;
                }
                if (f.getType().equals(Boolean.TYPE)) {
                    options.add(CycleOption.createOnOff(Language.getInstance().getOrDefault(adjustable.translationId()), gameOptions -> {
                        try {
                            return f.getBoolean(object);
                        }
                        catch (IllegalAccessException | IllegalArgumentException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }, (gameOptions, option, value) -> {
                        try {
                            f.setBoolean(object, (boolean)value);
                            onUpdate.run();
                        }
                        catch (IllegalAccessException | IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }));
                    continue;
                }
                if (f.getType().equals(String.class)) {
                    options.add(AdjustableUtil.createTextOption(Language.getInstance().getOrDefault(adjustable.translationId()), f, object, onUpdate));
                    continue;
                }
                if (f.getType().isEnum()) {
                    options.add(AdjustableUtil.createEnumOption(Language.getInstance().getOrDefault(adjustable.translationId()), f, object, onUpdate));
                    continue;
                }
                if (f.getType().equals(Vector3f.class)) {
                    Field xField = Vector3f.class.getDeclaredField("x");
                    Field yField = Vector3f.class.getDeclaredField("y");
                    Field zField = Vector3f.class.getDeclaredField("z");
                    Vector3f value2 = (Vector3f)f.get(object);
                    options.add(AdjustableUtil.createProgressOptionFloat(Language.getInstance().getOrDefault(adjustable.translationId()) + " X", adjustable.min(), adjustable.max(), adjustable.step(), Language.getInstance().getOrDefault(adjustable.maxTranslationId()), xField, value2, onUpdate));
                    options.add(AdjustableUtil.createProgressOptionFloat(Language.getInstance().getOrDefault(adjustable.translationId()) + " Y", adjustable.min(), adjustable.max(), adjustable.step(), Language.getInstance().getOrDefault(adjustable.maxTranslationId()), yField, value2, onUpdate));
                    options.add(AdjustableUtil.createProgressOptionFloat(Language.getInstance().getOrDefault(adjustable.translationId()) + " Z", adjustable.min(), adjustable.max(), adjustable.step(), Language.getInstance().getOrDefault(adjustable.maxTranslationId()), zField, value2, onUpdate));
                    continue;
                }
                if (f.getType().equals(Block.class)) {
                    Block block = (Block)f.get(object);
                    options.add(new BlockOption(Language.getInstance().getOrDefault(adjustable.translationId()), PhysicsMod.registeredBlocks.get(block), true, screen, blockChange -> {
                        try {
                            if (blockChange == null) {
                                f.set(object, null);
                            } else {
                                f.set(object, PhysicsMod.invRegisteredBlocks.get(blockChange));
                                onUpdate.run();
                            }
                        }
                        catch (IllegalAccessException | IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }));
                    continue;
                }
                if (f.getType().equals(ParticleOptions.class)) {
                    ParticleOptions particle = (ParticleOptions)f.get(object);
                    options.add(new ParticleOption(Language.getInstance().getOrDefault(adjustable.translationId()), PhysicsMod.invRegisteredParticles.get(particle), screen, particleChange -> {
                        try {
                            if (particleChange == null) {
                                f.set(object, null);
                            } else {
                                f.set(object, PhysicsMod.registeredParticles.get(particleChange));
                                onUpdate.run();
                            }
                        }
                        catch (IllegalAccessException | IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }));
                    continue;
                }
                if (f.getType().equals(SoundEvent.class)) {
                    SoundEvent sound = (SoundEvent)f.get(object);
                    options.add(new SoundOption(Language.getInstance().getOrDefault(adjustable.translationId()), PhysicsMod.invRegisteredSounds.get(sound), screen, soundChange -> {
                        try {
                            if (soundChange == null) {
                                f.set(object, null);
                            } else {
                                f.set(object, PhysicsMod.registeredSounds.get(soundChange));
                                onUpdate.run();
                            }
                        }
                        catch (IllegalAccessException | IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }));
                    continue;
                }
                if (f.getType().equals(Animation.class)) {
                    Animation animation = (Animation)f.get(object);
                    long id = -1L;
                    for (Long2ObjectMap.Entry entry : ConfigAnimations.animations.long2ObjectEntrySet()) {
                        if (!((Animation)entry.getValue()).equals(animation)) continue;
                        id = entry.getLongKey();
                        break;
                    }
                    options.add(new AnimationOption(Language.getInstance().getOrDefault(adjustable.translationId()), id, screen, particleChange -> {
                        try {
                            if (particleChange == null) {
                                f.set(object, null);
                            } else {
                                f.set(object, ConfigAnimations.animations.get(Long.parseLong((String)particleChange)));
                                onUpdate.run();
                            }
                        }
                        catch (IllegalAccessException | IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }, Language.getInstance().getOrDefault("physicsmod.prop.mainrule"), true));
                    continue;
                }
                if (!Collection.class.isAssignableFrom(f.getType())) continue;
                Collection collection = (Collection)f.get(object);
                String fieldName = Language.getInstance().getOrDefault(adjustable.translationId());
                options.add(new LabelOption(""));
                options.add(new ButtonOption(String.format(Language.getInstance().getOrDefault("physicsmod.prop.add"), fieldName), button -> {
                    try {
                        collection.add(((Class)((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0]).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
                        onRefresh.run();
                        onUpdate.run();
                    }
                    catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }));
                int fieldCount = 0;
                for (Object subObject : collection) {
                    options.add(new LabelOption(fieldName + " " + ++fieldCount));
                    options.addAll(AdjustableUtil.generateOptions(screen, subObject, onRefresh, onUpdate));
                    options.add(new ButtonOption(String.format(Language.getInstance().getOrDefault("physicsmod.prop.remove"), fieldName), button -> {
                        collection.remove(subObject);
                        onRefresh.run();
                        onUpdate.run();
                    }));
                }
            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }
        }
        return options;
    }

    private static LegacyOption createTextOption(String name, Field f, Object object, Runnable onUpdate) {
        try {
            TextOption textOption = new TextOption(name, (String)f.get(object), textChanged -> {
                try {
                    f.set(object, textChanged);
                    onUpdate.run();
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            });
            return textOption;
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static LegacyOption createEnumOption(String name, Field f, Object object, Runnable onUpdate) {
        try {
            Object[] enumConstants = f.getType().getEnumConstants();
            CycleOption<Object> cycleOption = CycleOption.create(name, enumConstants, model -> {
                try {
                    return Component.translatable((String)((Enum)model).toString());
                }
                catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return Component.translatable((String)"physicsmod.prop.error");
                }
            }, gameOptions -> {
                try {
                    return (Enum)f.get(object);
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                    return enumConstants[0];
                }
            }, (gameOptions, option, model) -> {
                Enum type = (Enum)model;
                try {
                    f.set(object, type);
                    onUpdate.run();
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            });
            return cycleOption;
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static LegacyOption createProgressOptionDouble(String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate) {
        ProgressOption progressOption = new ProgressOption(name, min, max, (float)step, options -> {
            try {
                return f.getDouble(object);
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
                return 0.0;
            }
        }, (options, value) -> {
            try {
                f.setDouble(object, (double)value);
                onUpdate.run();
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }, (options, option) -> {
            double val = option.get((Options)options);
            if (val >= max && !maxText.isEmpty()) {
                return Component.literal((String)(name + ": " + maxText));
            }
            return Component.literal((String)(name + ": " + String.format("%.2f", val)));
        });
        return progressOption;
    }

    private static LegacyOption createProgressOptionFloat(String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate) {
        ProgressOption progressOption = new ProgressOption(name, min, max, (float)step, options -> {
            try {
                return f.getFloat(object);
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
                return 0.0;
            }
        }, (options, value) -> {
            try {
                f.setFloat(object, value.floatValue());
                onUpdate.run();
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }, (options, option) -> {
            double val = option.get((Options)options);
            if (val >= max && !maxText.isEmpty()) {
                return Component.literal((String)(name + ": " + maxText));
            }
            return Component.literal((String)(name + ": " + String.format("%.2f", val)));
        });
        return progressOption;
    }

    private static LegacyOption createProgressOptionInt(String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate) {
        ProgressOption progressOption = new ProgressOption(name, min, max, (float)step, options -> {
            try {
                return f.getInt(object);
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
                return 0.0;
            }
        }, (options, value) -> {
            try {
                f.setInt(object, value.intValue());
                onUpdate.run();
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }, (options, option) -> {
            double val = option.get((Options)options);
            if (val >= max && !maxText.isEmpty()) {
                return Component.literal((String)(name + ": " + maxText));
            }
            return Component.literal((String)(name + ": " + String.format("%.2f", val)));
        });
        return progressOption;
    }

    private static LegacyOption createProgressOptionLong(String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate) {
        ProgressOption progressOption = new ProgressOption(name, min, max, (float)step, options -> {
            try {
                return f.getLong(object);
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
                return 0.0;
            }
        }, (options, value) -> {
            try {
                f.setLong(object, value.longValue());
                onUpdate.run();
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }, (options, option) -> {
            double val = option.get((Options)options);
            if (val >= max && !maxText.isEmpty()) {
                return Component.literal((String)(name + ": " + maxText));
            }
            return Component.literal((String)(name + ": " + String.format("%.2f", val)));
        });
        return progressOption;
    }

    private static LegacyOption createProgressOptionShort(String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate) {
        ProgressOption progressOption = new ProgressOption(name, min, max, (float)step, options -> {
            try {
                return f.getShort(object);
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
                return 0.0;
            }
        }, (options, value) -> {
            try {
                f.setShort(object, value.shortValue());
                onUpdate.run();
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }, (options, option) -> {
            double val = option.get((Options)options);
            if (val >= max && !maxText.isEmpty()) {
                return Component.literal((String)(name + ": " + maxText));
            }
            return Component.literal((String)(name + ": " + String.format("%.2f", val)));
        });
        return progressOption;
    }

    private static LegacyOption createProgressOptionByte(String name, double min, double max, double step, String maxText, Field f, Object object, Runnable onUpdate) {
        ProgressOption progressOption = new ProgressOption(name, min, max, (float)step, options -> {
            try {
                return f.getByte(object);
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
                return 0.0;
            }
        }, (options, value) -> {
            try {
                f.setByte(object, value.byteValue());
                onUpdate.run();
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }, (options, option) -> {
            double val = option.get((Options)options);
            if (val >= max && !maxText.isEmpty()) {
                return Component.literal((String)(name + ": " + maxText));
            }
            return Component.literal((String)(name + ": " + String.format("%.2f", val)));
        });
        return progressOption;
    }
}

