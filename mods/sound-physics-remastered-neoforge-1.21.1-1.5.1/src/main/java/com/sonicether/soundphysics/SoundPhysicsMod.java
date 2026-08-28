/*
 * Decompiled with CFR 0.152.
 */
package com.sonicether.soundphysics;

import com.sonicether.soundphysics.Loggers;
import com.sonicether.soundphysics.config.OcclusionConfig;
import com.sonicether.soundphysics.config.ReflectivityConfig;
import com.sonicether.soundphysics.config.SoundPhysicsConfig;
import com.sonicether.soundphysics.config.SoundRateConfig;
import de.maxhenkel.sound_physics_remastered.configbuilder.ConfigBuilder;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public abstract class SoundPhysicsMod {
    public static final String MODID = "sound_physics_remastered";
    public static SoundPhysicsConfig CONFIG;
    public static ReflectivityConfig REFLECTIVITY_CONFIG;
    public static OcclusionConfig OCCLUSION_CONFIG;
    public static SoundRateConfig SOUND_RATE_CONFIG;

    public void init() {
        this.initConfig();
    }

    public void initClient() {
        this.initConfig();
        CONFIG.reloadClient();
        this.renameAllowedSounds();
        REFLECTIVITY_CONFIG = new ReflectivityConfig(this.getConfigFolder().resolve(MODID).resolve("reflectivity.properties"));
        OCCLUSION_CONFIG = new OcclusionConfig(this.getConfigFolder().resolve(MODID).resolve("occlusion.properties"));
        SOUND_RATE_CONFIG = new SoundRateConfig(this.getConfigFolder().resolve(MODID).resolve("sound_rates.properties"));
    }

    private void initConfig() {
        if (CONFIG == null) {
            CONFIG = ConfigBuilder.builder(SoundPhysicsConfig::new).path(this.getConfigFolder().resolve(MODID).resolve("soundphysics.properties")).build();
        }
    }

    private void renameAllowedSounds() {
        Path oldPath = this.getConfigFolder().resolve(MODID).resolve("allowed_sounds.properties");
        Path newPath = this.getConfigFolder().resolve(MODID).resolve("sound_rates.properties");
        try {
            Files.move(oldPath, newPath, new CopyOption[0]);
            Loggers.log("{} file renamed to {}", oldPath.getFileName(), newPath.getFileName());
        }
        catch (FileAlreadyExistsException | NoSuchFileException fileSystemException) {
        }
        catch (IOException e) {
            Loggers.error("Error renaming allowed_sounds config", e);
        }
    }

    public abstract Path getConfigFolder();
}

