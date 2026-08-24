package vectorwing.farmersdelight.common.utility;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import vectorwing.farmersdelight.common.item.KnifeItem;
import vectorwing.farmersdelight.common.tag.ModTags;

public class ItemUtils {
   public static boolean isValidTool(ItemStack stack, ItemAbility toolAction, TagKey<Item> fallbackTag) {
      return stack.canPerformAction(toolAction) || stack.is(fallbackTag);
   }

   public static boolean isKnife(ItemStack stack) {
      return isValidTool(stack, KnifeItem.KNIFE_HARVEST, ModTags.Items.KNIVES);
   }

   public static void dropItems(Level level, BlockPos pos, IItemHandler inventory) {
      for (int slot = 0; slot < inventory.getSlots(); slot++) {
         Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(slot));
      }
   }

   public static void clearItems(ItemStackHandler inventory) {
      for (int i = 0; i < inventory.getSlots(); i++) {
         inventory.setStackInSlot(i, ItemStack.EMPTY);
      }
   }

   public static boolean doesInventoryHaveItems(IItemHandler inventory) {
      for (int i = 0; i < inventory.getSlots(); i++) {
         if (!inventory.getStackInSlot(i).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public static void spawnItemEntity(Level level, ItemStack stack, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
      ItemEntity entity = new ItemEntity(level, x, y, z, stack);
      entity.setDeltaMovement(xMotion, yMotion, zMotion);
      level.addFreshEntity(entity);
   }

   public static int getValidatedEnchantmentLevel(ResourceKey<Enchantment> enchantment, Provider registries, ItemStack stack) {
      Optional<Reference<Enchantment>> fortune = registries.holder(enchantment);
      return fortune.<Integer>map(stack::getEnchantmentLevel).orElse(0);
   }
}
