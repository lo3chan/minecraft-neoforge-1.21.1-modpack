package net.cibernet.alchemancy.properties;

import java.util.List;
import java.util.Optional;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

public class EdibleProperty extends Property {
   @Override
   public <T> Object modifyDataComponent(ItemStack stack, DataComponentType<? extends T> dataType, T data) {
      if (dataType == DataComponents.FOOD) {
         if (data == null) {
            return new FoodProperties(2, 0.5F, true, 1.6F, Optional.empty(), List.of());
         }

         if (data instanceof FoodProperties foodProperties) {
            return new FoodProperties(
               (int)(foodProperties.nutrition() * 1.5F),
               foodProperties.saturation() * 1.25F,
               true,
               foodProperties.eatSeconds(),
               foodProperties.usingConvertsTo(),
               foodProperties.effects()
            );
         }
      }

      return super.modifyDataComponent(stack, dataType, data);
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      ItemStack stack = event.getItemStack();
      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.DEAD)) {
         event.getEntity().startUsingItem(event.getHand());
         event.setCancellationResult(InteractionResult.CONSUME);
         event.setCanceled(true);
      }
   }

   @Override
   public boolean onFinishUsingItem(LivingEntity user, Level level, ItemStack stack) {
      ItemStack food = stack.copy();
      if (this.damageItem(user, stack, EquipmentSlot.MAINHAND, 10)) {
         user.eat(level, food);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16742263;
   }
}
