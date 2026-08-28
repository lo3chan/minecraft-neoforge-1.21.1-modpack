/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.level.block.Block
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.diebuddies.mixins;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Registry.class})
public interface MixinRegistry {
    @Inject(at={@At(value="HEAD")}, method={"register(Lnet/minecraft/core/Registry;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/Object;)Ljava/lang/Object;"})
    private static void register(Registry registry, ResourceLocation resourceLocation, Object entry, CallbackInfoReturnable<Object> ci) {
        if (registry == BuiltInRegistries.BLOCK) {
            if (entry instanceof Block) {
                Block block = (Block)entry;
                String id = resourceLocation.toString();
                PhysicsMod.registeredBlocks.put(block, id);
                PhysicsMod.invRegisteredBlocks.put(id, block);
            }
        } else if (registry == BuiltInRegistries.PARTICLE_TYPE) {
            if (entry instanceof ParticleOptions) {
                ParticleOptions particle = (ParticleOptions)entry;
                String identifier = resourceLocation.getNamespace() + ":" + resourceLocation.getPath();
                PhysicsMod.registeredParticles.put(identifier, particle);
                PhysicsMod.invRegisteredParticles.put(particle, identifier);
            }
        } else if (registry == BuiltInRegistries.SOUND_EVENT && entry instanceof SoundEvent) {
            SoundEvent sound = (SoundEvent)entry;
            String identifier = resourceLocation.getNamespace() + ":" + resourceLocation.getPath();
            PhysicsMod.registeredSounds.put(identifier, sound);
            PhysicsMod.invRegisteredSounds.put(sound, identifier);
        }
    }
}

