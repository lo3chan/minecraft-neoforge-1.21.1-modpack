package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ItemGhostlyPickaxe extends PickaxeItem {
   public ItemGhostlyPickaxe(Properties props) {
      super(Tiers.IRON, props.attributes(PickaxeItem.createAttributes(Tiers.IRON, 1.0F, -2.8F)));
   }

   public static boolean shouldStoreInGhost(LivingEntity player, ItemStack stack) {
      return player instanceof Player && ((Player)player).getInventory().getFreeSlot() == -1;
   }

   public float getDestroySpeed(ItemStack stack, BlockState blockState) {
      return blockState.is(BlockTags.MINEABLE_WITH_PICKAXE) ? 20.0F : 1.0F;
   }

   public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity user) {
      if (shouldStoreInGhost(user, stack)) {
         if (user instanceof Player player) {
            player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
            player.causeFoodExhaustion(0.005F);
         }

         if (!level.isClientSide()) {
            BlockEntity blockentity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
            Block.getDrops(state, (ServerLevel)level, pos, blockentity, user, stack).forEach(item -> putItemInGhostInventoryOrDrop(user, stack, item));
            state.spawnAfterBreak((ServerLevel)level, pos, stack, true);
            int exp = state.getExpDrop(level, pos, blockentity, user, stack);
            if (exp > 0) {
               state.getBlock().popExperience((ServerLevel)level, pos, exp);
            }
         }
      }

      return super.mineBlock(stack, level, state, pos, user);
   }

   private static void putItemInGhostInventoryOrDrop(LivingEntity user, ItemStack pickaxe, ItemStack item) {
      Provider provider = user.level().registryAccess();
      CompoundTag compoundtag = AMCompat.getOrCreateTag(pickaxe);
      SimpleContainer container = new SimpleContainer(9);
      if (compoundtag.contains("Items")) {
         AMCompat.fromTag(provider, container, AMCompat.getList(compoundtag, "Items", 10));
      }

      if (user instanceof Player player) {
         if (player.getInventory().add(item)) {
            return;
         }

         if (container.canAddItem(item)) {
            ItemStack leftover = container.addItem(item);
            compoundtag.put("Items", AMCompat.createTag(provider, container));
            AMCompat.setTag(pickaxe, compoundtag);
            item = leftover;
         }
      }

      if (!item.isEmpty()) {
         AMCompat.spawnAtLocation(user, item);
      }
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean offhand) {
      super.inventoryTick(stack, level, entity, i, offhand);
      if (entity instanceof Player player && player.tickCount % 3 == 0) {
         CompoundTag compoundtag = AMCompat.getOrCreateTag(stack);
         SimpleContainer container = new SimpleContainer(9);
         boolean flag = false;
         if (compoundtag.contains("Items")) {
            AMCompat.fromTag(level.registryAccess(), container, AMCompat.getList(compoundtag, "Items", 10));
         }

         for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stackAt = container.getItem(slot);
            if (!stackAt.isEmpty() && player.addItem(stackAt)) {
               container.removeItem(slot, stack.getCount());
               flag = true;
               break;
            }
         }

         if (flag) {
            compoundtag.put("Items", AMCompat.createTag(level.registryAccess(), container));
            AMCompat.setTag(stack, compoundtag);
         }
      }
   }

   public boolean isValidRepairItem(ItemStack pickaxe, ItemStack stack) {
      return stack.is(Items.PHANTOM_MEMBRANE);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, context, tooltip, flagIn);
      Provider provider = context.registries();
      CompoundTag compoundtag = AMCompat.getTag(stack);
      if (compoundtag != null && AMCompat.contains(compoundtag, "Items", 9)) {
         SimpleContainer container = new SimpleContainer(9);
         AMCompat.fromTag(provider, container, AMCompat.getList(compoundtag, "Items", 10));
         int i = 0;
         int j = 0;

         for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack itemstack = container.getItem(slot);
            if (!itemstack.isEmpty()) {
               j++;
               if (i <= 4) {
                  i++;
                  MutableComponent mutablecomponent = itemstack.getHoverName().copy();
                  mutablecomponent.append(" x").append(String.valueOf(itemstack.getCount()));
                  tooltip.add(mutablecomponent.withStyle(ChatFormatting.DARK_AQUA));
               }
            }
         }

         if (j - i > 0) {
            tooltip.add(
               Component.translatable("container.shulkerBox.more", new Object[]{j - i})
                  .withStyle(new ChatFormatting[]{ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC})
            );
         }
      }
   }

   public void dropAllContents(Level level, Vec3 vec3, ItemStack pickaxe) {
      Provider provider = level.registryAccess();
      CompoundTag compoundtag = AMCompat.getTag(pickaxe);
      if (compoundtag != null && AMCompat.contains(compoundtag, "Items", 9)) {
         SimpleContainer container = new SimpleContainer(9);
         AMCompat.fromTag(provider, container, AMCompat.getList(compoundtag, "Items", 10));

         for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack itemstack = container.getItem(slot);
            if (!itemstack.isEmpty()) {
               ItemEntity itemEntity = new ItemEntity(level, vec3.x, vec3.y, vec3.z, itemstack.copy());
               if (level.addFreshEntity(itemEntity)) {
                  container.removeItem(slot, itemstack.getCount());
               }
            }
         }

         compoundtag.put("Items", AMCompat.createTag(provider, container));
         AMCompat.setTag(pickaxe, compoundtag);
      }
   }

   public void onDestroyed(ItemEntity itemEntity) {
      this.dropAllContents(itemEntity.level(), itemEntity.position(), itemEntity.getItem());
   }

   public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
      int i = super.damageItem(stack, amount, entity, onBroken);
      if (i + stack.getDamageValue() >= stack.getMaxDamage() && entity != null) {
         this.dropAllContents(entity.level(), entity.position(), stack);
      }

      return i;
   }

   public int getMaxDamage(ItemStack stack) {
      return 700;
   }
}
