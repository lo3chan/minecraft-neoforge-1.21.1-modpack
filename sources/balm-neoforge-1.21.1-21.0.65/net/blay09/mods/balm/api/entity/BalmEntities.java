package net.blay09.mods.balm.api.entity;

import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType.Builder;

@Deprecated
public interface BalmEntities {
   @Deprecated
   <T extends Entity> DeferredObject<EntityType<T>> registerEntity(ResourceLocation var1, Builder<T> var2);

   @Deprecated
   <T extends LivingEntity> DeferredObject<EntityType<T>> registerEntity(
      ResourceLocation var1, Builder<T> var2, Supplier<net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder> var3
   );
}
