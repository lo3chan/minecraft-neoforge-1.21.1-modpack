package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Info("Invoked when an entity eats food.\n")
public class FoodEatenKubeEvent implements KubeEntityEvent {
   private final Entity entity;
   private final ItemStack item;

   public FoodEatenKubeEvent(LivingEntity e, ItemStack is) {
      this.entity = e;
      this.item = is;
   }

   @Info("The entity that ate the food.")
   @Override
   public Entity getEntity() {
      return this.entity;
   }

   @Info("The food that was eaten.")
   public ItemStack getItem() {
      return this.item;
   }
}
