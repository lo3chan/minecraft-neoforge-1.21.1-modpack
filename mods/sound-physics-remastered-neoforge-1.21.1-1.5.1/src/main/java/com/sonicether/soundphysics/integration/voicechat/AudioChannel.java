/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.phys.Vec3
 */
package com.sonicether.soundphysics.integration.voicechat;

import com.sonicether.soundphysics.SoundPhysics;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class AudioChannel {
    private static final String VOICECHAT = "voicechat";
    private static final ResourceLocation VOICECHAT_SOUND = ResourceLocation.fromNamespaceAndPath((String)"voicechat", (String)"voicechat");
    private final UUID channelId;
    private long lastUpdate;
    private Vec3 lastPos;

    public AudioChannel(UUID channelId) {
        this.channelId = channelId;
    }

    public void onSound(int source, @Nullable Vec3 soundPos, boolean auxOnly, @Nullable String category) {
        if (soundPos == null) {
            SoundPhysics.setDefaultEnvironment(source, auxOnly);
            return;
        }
        long time = System.currentTimeMillis();
        if (time - this.lastUpdate < 500L && this.lastPos != null && this.lastPos.distanceTo(soundPos) < 1.0) {
            return;
        }
        SoundPhysics.setLastSoundCategoryAndName(SoundSource.MASTER, category == null ? VOICECHAT_SOUND : ResourceLocation.fromNamespaceAndPath((String)VOICECHAT, (String)category));
        if (auxOnly) {
            SoundPhysics.onPlayReverb(soundPos.x(), soundPos.y(), soundPos.z(), source);
        } else {
            SoundPhysics.onPlaySound(soundPos.x(), soundPos.y(), soundPos.z(), source);
        }
        this.lastUpdate = time;
        this.lastPos = soundPos;
    }

    public UUID getChannelId() {
        return this.channelId;
    }

    public boolean canBeRemoved() {
        return System.currentTimeMillis() - this.lastUpdate > 5000L;
    }

    public static boolean isVoicechatSound(ResourceLocation sound) {
        return sound.getNamespace().equals(VOICECHAT);
    }
}

