package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.EntityTypeKJS;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({EntityType.class})
public abstract class EntityTypeMixin implements EntityTypeKJS {
   @Shadow
   @Final
   private Reference<EntityType<?>> builtInRegistryHolder;
   @Unique
   private ResourceKey<EntityType<?>> kjs$registryKey;
   @Unique
   private String kjs$id;

   @Override
   public Holder<EntityType<?>> kjs$asHolder() {
      return this.builtInRegistryHolder;
   }

   @Override
   public ResourceKey<EntityType<?>> kjs$getKey() {
      if (this.kjs$registryKey == null) {
         this.kjs$registryKey = EntityTypeKJS.super.kjs$getKey();
      }

      return this.kjs$registryKey;
   }

   @Override
   public String kjs$getId() {
      if (this.kjs$id == null) {
         this.kjs$id = EntityTypeKJS.super.kjs$getId();
      }

      return this.kjs$id;
   }
}
