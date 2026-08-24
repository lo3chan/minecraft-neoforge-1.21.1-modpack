package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCatfish;
import com.github.alexthe666.alexsmobs.entity.EntityLobster;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ItemModFishBucket extends MobBucketItem {
   private final Supplier<? extends EntityType<?>> fishTypeSupplier;

   public ItemModFishBucket(Supplier<? extends EntityType<? extends Mob>> fishTypeIn, Fluid fluid, Properties builder) {
      super(fishTypeIn.get(), fluid, SoundEvents.BUCKET_EMPTY_FISH, builder.stacksTo(1));
      this.fishTypeSupplier = fishTypeIn;
   }

   protected EntityType<?> getFishType() {
      return (EntityType<?>)this.fishTypeSupplier.get();
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
      EntityType fishType = this.getFishType();
      if (fishType == AMEntityRegistry.LOBSTER.get()) {
         CompoundTag compoundnbt = AMCompat.getTag(stack);
         if (compoundnbt != null && AMCompat.contains(compoundnbt, "BucketVariantTag", 3)) {
            int i = AMCompat.getInt(compoundnbt, "BucketVariantTag");
            String s = "entity.alexsmobs.lobster.variant_" + EntityLobster.getVariantName(i);
            tooltip.add(Component.translatable(s).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
         }
      }

      if (fishType == AMEntityRegistry.TERRAPIN.get()) {
         CompoundTag compoundnbt = AMCompat.getTag(stack);
         if (compoundnbt != null && compoundnbt.contains("TerrapinData")) {
            int i = AMCompat.getInt(AMCompat.getCompound(compoundnbt, "TerrapinData"), "TurtleType");
            tooltip.add(
               Component.translatable(TerrapinTypes.values()[Mth.clamp(i, 0, TerrapinTypes.values().length - 1)].getTranslationName())
                  .withStyle(ChatFormatting.GRAY)
                  .withStyle(ChatFormatting.ITALIC)
            );
         }
      }

      if (fishType == AMEntityRegistry.COMB_JELLY.get()) {
         CompoundTag compoundnbt = AMCompat.getTag(stack);
         if (compoundnbt != null && AMCompat.contains(compoundnbt, "BucketVariantTag", 3)) {
            int i = AMCompat.getInt(compoundnbt, "BucketVariantTag");
            String s = "entity.alexsmobs.comb_jelly.variant_" + i;
            tooltip.add(Component.translatable(s).withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
         }
      }
   }

   public void checkExtraContent(@Nullable Player player, Level level, ItemStack stack, BlockPos pos) {
      if (level instanceof ServerLevel) {
         this.spawnFish((ServerLevel)level, stack, pos);
         level.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
      }
   }

   private void spawnFish(ServerLevel serverLevel, ItemStack stack, BlockPos pos) {
      spawnFish(this.getFishType(), serverLevel, stack, pos);
   }

   static void spawnFish(EntityType<?> fishType, ServerLevel serverLevel, ItemStack stack, BlockPos pos) {
      Entity entity = fishType.spawn(serverLevel, stack, (Player)null, pos, MobSpawnType.BUCKET, true, false);
      if (entity instanceof Bucketable bucketable) {
         bucketable.loadFromBucketTag(AMCompat.getOrCreateTag(stack));
         bucketable.setFromBucket(true);
      }

      addExtraAttributes(entity, stack);
   }

   private static void addExtraAttributes(Entity entity, ItemStack stack) {
      if (entity instanceof EntityCatfish catfish) {
         if (stack.is(AMItemRegistry.SMALL_CATFISH_BUCKET.get())) {
            catfish.setCatfishSize(0);
         } else if (stack.is(AMItemRegistry.MEDIUM_CATFISH_BUCKET.get())) {
            catfish.setCatfishSize(1);
         } else if (stack.is(AMItemRegistry.LARGE_CATFISH_BUCKET.get())) {
            catfish.setCatfishSize(2);
         }
      }
   }
}
