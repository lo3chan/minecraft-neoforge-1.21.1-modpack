/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.client.particle.FireworkParticles$Starter
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.resources.ResourceLocation
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package me.flashyreese.mods.sodiumextra.mixin.particle;

import it.unimi.dsi.fastutil.ints.IntList;
import me.flashyreese.mods.sodiumextra.client.SodiumExtraClientMod;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={FireworkParticles.Starter.class})
public class MixinFireworkParticle {
    @Unique
    private final ResourceLocation fireworkIdentifier = ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"firework");

    @Inject(method={"createParticle"}, at={@At(value="HEAD")}, cancellable=true)
    public void addExplosionParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, IntList colors, IntList targetColors, boolean trail, boolean flicker, CallbackInfo ci) {
        if (!SodiumExtraClientMod.options().particleSettings.isParticleEnabled(this.fireworkIdentifier)) {
            ci.cancel();
        }
    }

    @Redirect(method={"tick"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/particle/Particle;setColor(FFF)V"))
    public void tick(Particle instance, float red, float green, float blue) {
        if (instance != null) {
            instance.setColor(red, green, blue);
        }
    }
}

