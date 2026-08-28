/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.shedaniel.clothconfig2.api.AbstractConfigListEntry
 *  me.shedaniel.clothconfig2.api.ConfigBuilder
 *  me.shedaniel.clothconfig2.api.ConfigCategory
 *  me.shedaniel.clothconfig2.api.ConfigEntryBuilder
 *  me.shedaniel.clothconfig2.gui.entries.FloatListEntry
 *  me.shedaniel.clothconfig2.impl.builders.DoubleFieldBuilder
 *  me.shedaniel.clothconfig2.impl.builders.FloatFieldBuilder
 *  me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package com.sonicether.soundphysics.integration;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.SoundPhysicsMod;
import com.sonicether.soundphysics.config.blocksound.BlockDefinition;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.BooleanConfigEntry;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.ConfigEntry;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.DoubleConfigEntry;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.FloatConfigEntry;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.IntegerConfigEntry;
import de.maxhenkel.sound_physics_remastered.configbuilder.entry.StringConfigEntry;
import java.util.LinkedHashMap;
import java.util.Map;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.FloatListEntry;
import me.shedaniel.clothconfig2.impl.builders.DoubleFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.FloatFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigIntegration {
    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle((Component)Component.translatable((String)"cloth_config.sound_physics_remastered.settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory((Component)Component.translatable((String)"cloth_config.sound_physics_remastered.category.general"));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.enabled"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.enabled.description"), SoundPhysicsMod.CONFIG.enabled));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.simple_voice_chat_integration"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.simple_voice_chat_integration.description"), SoundPhysicsMod.CONFIG.simpleVoiceChatIntegration));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.simple_voice_chat_hear_self"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.simple_voice_chat_hear_self.description"), SoundPhysicsMod.CONFIG.hearSelf));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.attenuation_factor"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.attenuation_factor.description"), SoundPhysicsMod.CONFIG.attenuationFactor));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.reverb_attenuation_distance"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.reverb_attenuation_distance.description"), SoundPhysicsMod.CONFIG.reverbAttenuationDistance));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.reverb_gain"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.reverb_gain.description"), SoundPhysicsMod.CONFIG.reverbGain));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.reverb_brightness"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.reverb_brightness.description"), SoundPhysicsMod.CONFIG.reverbBrightness));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.reverb_distance"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.reverb_distance.description"), SoundPhysicsMod.CONFIG.reverbDistance));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.block_absorption"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.block_absorption.description"), SoundPhysicsMod.CONFIG.blockAbsorption));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.occlusion_variation"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.occlusion_variation.description"), SoundPhysicsMod.CONFIG.occlusionVariation));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.default_block_reflectivity"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.default_block_reflectivity.description"), SoundPhysicsMod.CONFIG.defaultBlockReflectivity));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.default_block_occlusion_factor"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.default_block_occlusion_factor.description"), SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.sound_distance_allowance"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.sound_distance_allowance.description"), SoundPhysicsMod.CONFIG.soundDistanceAllowance));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.air_absorption"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.air_absorption.description"), SoundPhysicsMod.CONFIG.airAbsorption));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.underwater_filter"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.underwater_filter.description"), SoundPhysicsMod.CONFIG.underwaterFilter));
        general.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.evaluate_ambient_sounds"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.evaluate_ambient_sounds.description"), SoundPhysicsMod.CONFIG.evaluateAmbientSounds));
        ConfigCategory performance = builder.getOrCreateCategory((Component)Component.translatable((String)"cloth_config.sound_physics_remastered.category.performance"));
        performance.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.environment_evaluation_ray_count"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.environment_evaluation_ray_count.description"), SoundPhysicsMod.CONFIG.environmentEvaluationRayCount));
        performance.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.environment_evaluation_ray_bounces"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.environment_evaluation_ray_bounces.description"), SoundPhysicsMod.CONFIG.environmentEvaluationRayBounces));
        performance.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.non_full_block_occlusion_factor"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.non_full_block_occlusion_factor.description"), SoundPhysicsMod.CONFIG.nonFullBlockOcclusionFactor));
        performance.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.max_occlusion_rays"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.max_occlusion_rays.description"), SoundPhysicsMod.CONFIG.maxOcclusionRays));
        performance.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.max_occlusion"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.max_occlusion.description"), SoundPhysicsMod.CONFIG.maxOcclusion));
        performance.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.strict_occlusion"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.strict_occlusion.description"), SoundPhysicsMod.CONFIG.strictOcclusion));
        performance.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.sound_direction_evaluation"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.sound_direction_evaluation.description"), SoundPhysicsMod.CONFIG.soundDirectionEvaluation));
        performance.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.redirect_non_occluded_sounds"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.redirect_non_occluded_sounds.description"), SoundPhysicsMod.CONFIG.redirectNonOccludedSounds));
        performance.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.update_moving_sounds"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.update_moving_sounds.description"), SoundPhysicsMod.CONFIG.updateMovingSounds));
        performance.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.sound_update_interval"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.sound_update_interval.description"), SoundPhysicsMod.CONFIG.soundUpdateInterval));
        ConfigCategory reflectivity = builder.getOrCreateCategory((Component)Component.translatable((String)"cloth_config.sound_physics_remastered.category.reflectivity"));
        LinkedHashMap<BlockDefinition, Float> defaultReflectivityMap = new LinkedHashMap<BlockDefinition, Float>();
        SoundPhysicsMod.REFLECTIVITY_CONFIG.addDefaults(defaultReflectivityMap);
        for (Map.Entry<BlockDefinition, Float> entry : SoundPhysicsMod.REFLECTIVITY_CONFIG.getBlockDefinitions().entrySet()) {
            FloatListEntry e = ((FloatFieldBuilder)entryBuilder.startFloatField(entry.getKey().getName(), entry.getValue().floatValue()).setMin(0.01f).setMax(10.0f).setDefaultValue((Object)defaultReflectivityMap.getOrDefault(entry.getKey(), SoundPhysicsMod.CONFIG.defaultBlockReflectivity.get()))).setSaveConsumer(value -> SoundPhysicsMod.REFLECTIVITY_CONFIG.setBlockDefinitionValue((BlockDefinition)entry.getKey(), value.floatValue())).build();
            reflectivity.addEntry((AbstractConfigListEntry)e);
        }
        ConfigCategory occlusion = builder.getOrCreateCategory((Component)Component.translatable((String)"cloth_config.sound_physics_remastered.category.occlusion"));
        LinkedHashMap<BlockDefinition, Float> defaultOcclusionMap = new LinkedHashMap<BlockDefinition, Float>();
        SoundPhysicsMod.OCCLUSION_CONFIG.addDefaults(defaultOcclusionMap);
        for (Map.Entry<BlockDefinition, Float> entry : SoundPhysicsMod.OCCLUSION_CONFIG.getBlockDefinitions().entrySet()) {
            FloatListEntry e = ((FloatFieldBuilder)entryBuilder.startFloatField(entry.getKey().getName(), entry.getValue().floatValue()).setMin(0.0f).setMax(10.0f).setDefaultValue((Object)defaultOcclusionMap.getOrDefault(entry.getKey(), SoundPhysicsMod.CONFIG.defaultBlockOcclusionFactor.get()))).setSaveConsumer(value -> SoundPhysicsMod.OCCLUSION_CONFIG.setBlockDefinitionValue((BlockDefinition)entry.getKey(), value.floatValue())).build();
            occlusion.addEntry((AbstractConfigListEntry)e);
        }
        ConfigCategory logging = builder.getOrCreateCategory((Component)Component.translatable((String)"cloth_config.sound_physics_remastered.category.debug"));
        logging.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.debug_logging"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.debug_logging.description"), SoundPhysicsMod.CONFIG.debugLogging));
        logging.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.occlusion_logging"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.occlusion_logging.description"), SoundPhysicsMod.CONFIG.occlusionLogging));
        logging.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.environment_logging"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.environment_logging.description"), SoundPhysicsMod.CONFIG.environmentLogging));
        logging.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.performance_logging"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.performance_logging.description"), SoundPhysicsMod.CONFIG.performanceLogging));
        logging.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.render_sound_bounces"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.render_sound_bounces.description"), SoundPhysicsMod.CONFIG.renderSoundBounces));
        logging.addEntry(ClothConfigIntegration.fromConfigEntry(entryBuilder, (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.render_occlusion"), (Component)Component.translatable((String)"cloth_config.sound_physics_remastered.render_occlusion.description"), SoundPhysicsMod.CONFIG.renderOcclusion));
        builder.setSavingRunnable(() -> {
            Loggers.log("Saving configs", new Object[0]);
            SoundPhysicsMod.CONFIG.enabled.save();
            SoundPhysicsMod.REFLECTIVITY_CONFIG.save();
            SoundPhysicsMod.OCCLUSION_CONFIG.save();
            SoundPhysicsMod.SOUND_RATE_CONFIG.save();
            SoundPhysicsMod.CONFIG.reloadClient();
        });
        return builder.build();
    }

    private static <T> AbstractConfigListEntry<T> fromConfigEntry(ConfigEntryBuilder entryBuilder, Component name, Component description, ConfigEntry<T> entry) {
        if (entry instanceof DoubleConfigEntry) {
            DoubleConfigEntry e = (DoubleConfigEntry)entry;
            return ((DoubleFieldBuilder)((DoubleFieldBuilder)entryBuilder.startDoubleField(name, ((Double)e.get()).doubleValue()).setTooltip(new Component[]{description}).setMin((Object)((Double)e.getMin()))).setMax((Object)((Double)e.getMax()))).setDefaultValue(e::getDefault).setSaveConsumer(d -> e.set(d)).build();
        }
        if (entry instanceof FloatConfigEntry) {
            FloatConfigEntry e = (FloatConfigEntry)entry;
            return ((FloatFieldBuilder)((FloatFieldBuilder)entryBuilder.startFloatField(name, ((Float)e.get()).floatValue()).setTooltip(new Component[]{description}).setMin((Object)((Float)e.getMin()))).setMax((Object)((Float)e.getMax()))).setDefaultValue(e::getDefault).setSaveConsumer(d -> e.set(d)).build();
        }
        if (entry instanceof IntegerConfigEntry) {
            IntegerConfigEntry e = (IntegerConfigEntry)entry;
            return ((IntFieldBuilder)((IntFieldBuilder)entryBuilder.startIntField(name, ((Integer)e.get()).intValue()).setTooltip(new Component[]{description}).setMin((Object)((Integer)e.getMin()))).setMax((Object)((Integer)e.getMax()))).setDefaultValue(e::getDefault).setSaveConsumer(i -> e.set(i)).build();
        }
        if (entry instanceof BooleanConfigEntry) {
            BooleanConfigEntry e = (BooleanConfigEntry)entry;
            return entryBuilder.startBooleanToggle(name, ((Boolean)e.get()).booleanValue()).setTooltip(new Component[]{description}).setDefaultValue(e::getDefault).setSaveConsumer(b -> e.set(b)).build();
        }
        if (entry instanceof StringConfigEntry) {
            StringConfigEntry e = (StringConfigEntry)entry;
            return entryBuilder.startStrField(name, (String)e.get()).setTooltip(new Component[]{description}).setDefaultValue(e::getDefault).setSaveConsumer(s -> e.set(s)).build();
        }
        return null;
    }
}

