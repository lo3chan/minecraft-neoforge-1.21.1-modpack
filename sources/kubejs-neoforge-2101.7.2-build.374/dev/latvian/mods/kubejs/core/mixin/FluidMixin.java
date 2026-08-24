package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.FluidKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@RemapPrefixForJS("kjs$")
@Mixin(
   value = {Fluid.class},
   priority = 1001
)
public abstract class FluidMixin implements FluidKJS {
   @Shadow
   @Final
   private Reference<Fluid> builtInRegistryHolder;
   @Unique
   private ResourceKey<Fluid> kjs$registryKey;
   @Unique
   private String kjs$id;

   public Reference<Fluid> kjs$asHolder() {
      return this.builtInRegistryHolder;
   }

   @Override
   public ResourceKey<Fluid> kjs$getKey() {
      if (this.kjs$registryKey == null) {
         this.kjs$registryKey = FluidKJS.super.kjs$getKey();
      }

      return this.kjs$registryKey;
   }

   @Override
   public String kjs$getId() {
      if (this.kjs$id == null) {
         this.kjs$id = FluidKJS.super.kjs$getId();
      }

      return this.kjs$id;
   }
}
