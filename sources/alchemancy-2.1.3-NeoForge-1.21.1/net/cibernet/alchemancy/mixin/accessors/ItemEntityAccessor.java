package net.cibernet.alchemancy.mixin.accessors;

import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ItemEntity.class})
public interface ItemEntityAccessor {
   @Accessor
   int getPickupDelay();

   @Accessor
   int getAge();

   @Accessor
   void setAge(int var1);
}
