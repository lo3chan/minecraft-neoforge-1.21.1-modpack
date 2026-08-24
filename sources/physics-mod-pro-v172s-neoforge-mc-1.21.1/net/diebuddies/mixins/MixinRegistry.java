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

@Mixin({Registry.class})
public interface MixinRegistry {
   @Inject(
      at = {@At("HEAD")},
      method = {"register(Lnet/minecraft/core/Registry;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/Object;)Ljava/lang/Object;"}
   )
   private static void register(Registry registry, ResourceLocation resourceLocation, Object entry, CallbackInfoReturnable<Object> ci) {
      if (registry == BuiltInRegistries.BLOCK) {
         if (entry instanceof Block block) {
            String id = resourceLocation.toString();
            PhysicsMod.registeredBlocks.put(block, id);
            PhysicsMod.invRegisteredBlocks.put(id, block);
         }
      } else if (registry == BuiltInRegistries.PARTICLE_TYPE) {
         if (entry instanceof ParticleOptions particle) {
            String identifier = resourceLocation.getNamespace() + ":" + resourceLocation.getPath();
            PhysicsMod.registeredParticles.put(identifier, particle);
            PhysicsMod.invRegisteredParticles.put(particle, identifier);
         }
      } else if (registry == BuiltInRegistries.SOUND_EVENT && entry instanceof SoundEvent sound) {
         String identifier = resourceLocation.getNamespace() + ":" + resourceLocation.getPath();
         PhysicsMod.registeredSounds.put(identifier, sound);
         PhysicsMod.invRegisteredSounds.put(sound, identifier);
      }
   }
}
