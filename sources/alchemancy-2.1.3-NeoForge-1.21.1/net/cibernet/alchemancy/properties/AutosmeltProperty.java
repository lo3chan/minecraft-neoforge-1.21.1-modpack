package net.cibernet.alchemancy.properties;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import org.jetbrains.annotations.Nullable;

public class AutosmeltProperty extends Property implements IDataHolder<Integer> {
   @Override
   public void onStackedOverMe(
      ItemStack carriedItem,
      ItemStack stackedOnItem,
      Player player,
      ClickAction clickAction,
      SlotAccess carriedSlot,
      Slot stackedOnSlot,
      AtomicBoolean isCancelled
   ) {
      if (clickAction == ClickAction.SECONDARY && this.getData(stackedOnItem) <= 0) {
         int fuel = carriedItem.getBurnTime(RecipeType.SMELTING) / 200;
         if (fuel > 0) {
            if (carriedItem.hasCraftingRemainingItem()) {
               ItemStack remainder = carriedItem.getCraftingRemainingItem();
               if (carriedItem.getCount() > 1) {
                  player.drop(remainder, true);
                  carriedItem.shrink(1);
               } else {
                  carriedSlot.set(remainder.copy());
               }
            } else {
               carriedItem.shrink(1);
            }

            this.setData(stackedOnItem, fuel);
            playRefuelSound(player);
            isCancelled.set(true);
         }
      }
   }

   @Override
   public void modifyBlockDrops(Entity breaker, ItemStack tool, EquipmentSlot slot, List<ItemEntity> drops, BlockDropsEvent event) {
      if (this.checkAndConsumeFuel(breaker, tool)) {
         Level level = breaker.level();

         for (ItemEntity drop : drops) {
            ItemStack stack = drop.getItem();
            RecipeHolder<? extends AbstractCookingRecipe> recipe = (RecipeHolder<? extends AbstractCookingRecipe>)level.getRecipeManager()
               .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level)
               .orElse(null);
            if (recipe == null) {
               recipe = (RecipeHolder<? extends AbstractCookingRecipe>)level.getRecipeManager()
                  .getRecipeFor(RecipeType.BLASTING, new SingleRecipeInput(stack), level)
                  .orElse(null);
            }

            if (recipe != null) {
               ItemStack result = ((AbstractCookingRecipe)recipe.value()).getResultItem(level.registryAccess()).copy();
               result.setCount(result.getCount() * stack.getCount());
               drop.setItem(result);
            }
         }
      }
   }

   @Override
   public void modifyLivingDrops(LivingEntity dropsSource, ItemStack weapon, LivingEntity user, Collection<ItemEntity> drops, LivingDropsEvent event) {
      if (this.checkAndConsumeFuel(dropsSource, weapon)) {
         Level level = dropsSource.level();

         for (ItemEntity itemEntity : drops) {
            RecipeHolder<? extends AbstractCookingRecipe> recipe = (RecipeHolder<? extends AbstractCookingRecipe>)level.getRecipeManager()
               .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(itemEntity.getItem()), level)
               .orElse(null);
            if (recipe == null) {
               recipe = (RecipeHolder<? extends AbstractCookingRecipe>)level.getRecipeManager()
                  .getRecipeFor(RecipeType.BLASTING, new SingleRecipeInput(itemEntity.getItem()), level)
                  .orElse(null);
            }

            if (recipe != null) {
               ItemStack result = ((AbstractCookingRecipe)recipe.value()).getResultItem(level.registryAccess()).copy();
               result.setCount(result.getCount() * itemEntity.getItem().getCount());
               itemEntity.setItem(result);
            }
         }
      }
   }

   public static void playRefuelSound(Entity entity) {
      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)AlchemancySoundEvents.SMELTING_RECHARGE.value(), SoundSource.PLAYERS, 1.0F, 1.0F
         );
   }

   public void playOutOfFuelSound(Entity entity) {
      entity.playSound((SoundEvent)AlchemancySoundEvents.SMELTING_DEPLETED.value());
   }

   public boolean checkAndConsumeFuel(Entity entity, ItemStack stack) {
      int fuel = this.getData(stack);
      ItemStack storedStack = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(stack);
      if (!storedStack.isEmpty()) {
         int refillFuel = storedStack.getBurnTime(RecipeType.SMELTING) / 200;
         if (fuel <= 1 && refillFuel > 0) {
            if (storedStack.hasCraftingRemainingItem()) {
               ItemStack remainder = storedStack.getCraftingRemainingItem();
               if (storedStack.getCount() > 1) {
                  HollowProperty.drop(entity, remainder.copy(), false, true);
                  storedStack.shrink(1);
               } else {
                  storedStack = remainder.copy();
               }
            } else {
               storedStack.shrink(1);
            }

            ((HollowProperty)AlchemancyProperties.HOLLOW.get()).setData(stack, storedStack);
            fuel += refillFuel;
            playRefuelSound(entity);
         }
      }

      if (fuel > 0) {
         this.setData(stack, fuel - 1);
         if (fuel == 1) {
            this.playOutOfFuelSound(entity);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16734991;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Component name = super.getDisplayText(stack);
      int fuel = this.getData(stack);
      return Component.translatable(
            "property.detail",
            new Object[]{
               name.copy().withStyle(ChatFormatting.BOLD),
               fuel > 0 ? Component.translatable("property.detail.uses_left", new Object[]{fuel}) : Component.translatable("property.detail.needs_refueling")
            }
         )
         .withColor(this.getColor(stack));
   }

   public Integer readData(CompoundTag tag) {
      return tag.getInt("fuel");
   }

   public CompoundTag writeData(final Integer data) {
      return new CompoundTag() {
         {
            this.putInt("fuel", data);
         }
      };
   }

   public Integer combineData(@Nullable Integer currentData, Integer newData) {
      return currentData == null ? newData : currentData + newData;
   }

   public Integer getDefaultData() {
      return 32;
   }
}
