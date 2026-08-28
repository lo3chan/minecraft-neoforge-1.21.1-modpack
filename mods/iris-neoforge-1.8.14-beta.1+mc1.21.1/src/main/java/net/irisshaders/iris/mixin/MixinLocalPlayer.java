/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.resources.sounds.AmbientSoundHandler
 *  net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package net.irisshaders.iris.mixin;

import java.util.List;
import net.irisshaders.iris.mixinterface.BiomeAmbienceInterface;
import net.irisshaders.iris.mixinterface.LocalPlayerInterface;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={LocalPlayer.class})
public class MixinLocalPlayer
implements LocalPlayerInterface {
    @Shadow
    @Final
    private List<AmbientSoundHandler> ambientSoundHandlers;

    @Override
    public float getCurrentConstantMood() {
        for (AmbientSoundHandler ambientSoundHandler : this.ambientSoundHandlers) {
            if (!(ambientSoundHandler instanceof BiomeAmbientSoundsHandler)) continue;
            return ((BiomeAmbienceInterface)ambientSoundHandler).getConstantMood();
        }
        return 0.0f;
    }
}

