package net.cibernet.alchemancy.properties.special;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.cibernet.alchemancy.crafting.ForgePropertyRecipe;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.EnderProperty;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.RotationDataProperty;
import net.cibernet.alchemancy.properties.WayfindingProperty;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.cibernet.alchemancy.util.ClientUtil;
import net.cibernet.alchemancy.util.ColorUtils;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import org.jetbrains.annotations.Nullable;

public class WaywardWarpProperty extends Property implements IDataHolder<WayfindingProperty.WayfindData> {
   public static final Component DIMENSION_MISMATCH = Component.translatable("property.alchemancy.wayward_warp.dimension_mismatch");
   public static final Component MISSING_DESTINATION = Component.translatable("property.alchemancy.wayward_warp.missing_destination");
   public static final Component OBSTRUCTED_DESTINATION = Component.translatable("property.alchemancy.wayward_warp.obstructed_destination");
   public static final InnatePropertyItem.Tooltip MEDALLION_TOOLTIP = (stack, context, tooltipComponents, tooltipFlag) -> {
      WayfindingProperty.WayfindData data = ((WaywardWarpProperty)AlchemancyProperties.WAYWARD_WARP.value()).getData(stack);
      Level level = context.level();
      int color = ((WaywardWarpProperty)AlchemancyProperties.WAYWARD_WARP.value()).getColor(stack);
      if (level != null && level.isClientSide() && hasDeathTracker(ClientUtil.getLocalPlayer(), stack)) {
         tooltipComponents.add(
            Component.translatable("item.alchemancy.wayward_medallion.bound_to_death_point")
               .withColor(ColorUtils.interpolateColorsOverTime(1.0F, color, ((RotationDataProperty)AlchemancyProperties.DEATH_TRACKER.get()).getColor(stack)))
         );
      } else if (data.hasTarget()) {
         if (data.targetedPlayer().isPresent()) {
            Optional<Player> targetPlayer = CommonUtils.getPlayerByUUID((UUID)data.targetedPlayer().get().getA());
            tooltipComponents.add(
               Component.translatable(
                     "item.alchemancy.wayward_medallion.bound_to_player",
                     new Object[]{targetPlayer.isPresent() ? targetPlayer.get().getGameProfile().getName() : data.targetedPlayer().get().getB()}
                  )
                  .withColor(color)
            );
         } else if (data.targetedPos().isPresent()) {
            GlobalPos pos = data.targetedPos().get();
            tooltipComponents.add(
               Component.translatable("item.alchemancy.wayward_medallion.bound_to_position", new Object[]{pos.pos().getX(), pos.pos().getY(), pos.pos().getZ()})
                  .withColor(color)
            );
            tooltipComponents.add(
               Component.translatable("item.alchemancy.wayward_medallion.in", new Object[]{pos.dimension().location().toString()}).withColor(color)
            );
         }
      }
   };

   @Override
   public void onInfusedByForgeRecipe(ItemStack stack, ForgePropertyRecipe recipe, ForgeRecipeGrid grid) {
      super.onInfusedByForgeRecipe(stack, recipe, grid);
      WayfindingProperty.WayfindData wayfindData = (WayfindingProperty.WayfindData)((WayfindingProperty)AlchemancyProperties.WAYFINDING.get())
         .getData(stack)
         .getA();
      if (wayfindData.hasTarget()) {
         this.setData(stack, wayfindData);
      }
   }

   @Override
   public boolean onInfusedByDormantProperty(
      ItemStack stack, ItemStack propertySource, ForgeRecipeGrid grid, List<Holder<Property>> propertiesToAdd, boolean consumeItem
   ) {
      WayfindingProperty.WayfindData wayfindData = (WayfindingProperty.WayfindData)((WayfindingProperty)AlchemancyProperties.WAYFINDING.get())
         .getData(stack)
         .getA();
      if (consumeItem && wayfindData.hasTarget()) {
         this.setData(stack, wayfindData);
      }

      return super.onInfusedByDormantProperty(stack, propertySource, grid, propertiesToAdd, consumeItem);
   }

   @Override
   public void onRightClickEntity(EntityInteractSpecific event) {
      if (!hasDeathTracker(event.getEntity(), event.getItemStack())) {
         WayfindingProperty.WayfindData data = this.getData(event.getItemStack());
         if (!data.hasTarget() && event.getTarget() instanceof Player target) {
            this.setData(event.getItemStack(), data.withPlayer(target));
            WayfindingProperty.playWayfindingSound(target);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
         }
      }
   }

   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      if (event.getLevel().getBlockState(event.getPos()).is(AlchemancyTags.Blocks.WAYFINDING_TARGETABLE)
         && !hasDeathTracker(event.getPlayer(), event.getItemStack())) {
         WayfindingProperty.WayfindData data = this.getData(event.getItemStack());
         if (!data.hasTarget()) {
            this.setData(event.getItemStack(), data.withBlockPosition(new GlobalPos(event.getLevel().dimension(), event.getPos())));
            WayfindingProperty.playWayfindingSound(event.getLevel(), event.getPos());
            event.setCancellationResult(ItemInteractionResult.SUCCESS);
            event.setCanceled(true);
         }
      }
   }

   public static boolean hasDeathTracker(Entity user, ItemStack stack) {
      return InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.DEATH_TRACKER)
         && user instanceof Player player
         && player.getLastDeathLocation().isPresent();
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (hasDeathTracker(event.getEntity(), event.getItemStack()) || this.getData(event.getItemStack()).hasTarget()) {
         event.getEntity().startUsingItem(event.getHand());
         event.setCancellationResult(InteractionResult.CONSUME);
         event.setCanceled(true);
      }
   }

   @Override
   public int modifyUseDuration(ItemStack stack, int original, int result) {
      return Math.max(32, result);
   }

   @Override
   public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
      return current.isEmpty() && original == UseAnim.NONE ? Optional.of(UseAnim.BOW) : current;
   }

   @Override
   public boolean onFinishUsingItem(LivingEntity user, Level level, ItemStack stack) {
      return this.teleport(user, user, level, stack);
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      this.teleport(source instanceof LivingEntity living ? living : null, target, target.level(), stack);
   }

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.SHATTERING)
         && rayTraceResult.getType() == Type.ENTITY
         && rayTraceResult instanceof EntityHitResult entityHitResult) {
         this.teleport(null, entityHitResult.getEntity(), projectile.level(), stack);
      }
   }

   public boolean teleport(@Nullable LivingEntity effectSource, Entity user, Level level, ItemStack stack) {
      if (level.isClientSide()) {
         return false;
      } else {
         WayfindingProperty.WayfindData data = this.getData(stack);
         boolean deathTracker = hasDeathTracker(effectSource, stack);
         Optional<ResourceKey<Level>> targetDimension = data.getTargetDimension(level);
         Optional<BlockPos> targetPos = data.getTargetPos(level);
         if (!deathTracker && !data.hasTarget()) {
            return false;
         } else {
            if (deathTracker) {
               Player player = (Player)effectSource;
               targetDimension = Optional.of(((GlobalPos)player.getLastDeathLocation().get()).dimension());
               targetPos = Optional.of(((GlobalPos)player.getLastDeathLocation().get()).pos());
            }

            if (targetDimension.isPresent() && !targetDimension.get().location().equals(user.level().dimension().location())) {
               if (user instanceof Player player) {
                  player.displayClientMessage(DIMENSION_MISMATCH, true);
               }

               return false;
            } else if (deathTracker
               || !targetPos.isEmpty()
                  && (!data.targetedPos().isPresent() || level.getBlockState(targetPos.get()).is(AlchemancyTags.Blocks.WAYFINDING_TARGETABLE))) {
               Optional<Vec3> destination = deathTracker
                  ? targetPos.map(BlockPos::getBottomCenter)
                  : RespawnAnchorBlock.findStandUpPosition(user.getType(), user.level(), targetPos.get());
               if (!destination.isPresent()) {
                  if (user instanceof Player player) {
                     player.displayClientMessage(OBSTRUCTED_DESTINATION, true);
                  }

                  return false;
               } else {
                  user.teleportTo(destination.get().x, destination.get().y, destination.get().z);
                  EnderProperty.playSound(level, destination.get());
                  EnderProperty.playParticles(level, destination.get(), user.getRandom());
                  EquipmentSlot slot = effectSource != null && effectSource.getUsedItemHand() == InteractionHand.OFF_HAND
                     ? EquipmentSlot.OFFHAND
                     : EquipmentSlot.MAINHAND;
                  this.damageOrConsumeItem(user.level(), effectSource, stack, slot, deathTracker ? 500 : 10);
                  return true;
               }
            } else {
               if (user instanceof Player player) {
                  player.displayClientMessage(MISSING_DESTINATION, true);
               }

               return false;
            }
         }
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(2.0F, -13075275, -3407622);
   }

   public WayfindingProperty.WayfindData readData(CompoundTag tag) {
      return WayfindingProperty.WayfindData.fromNbt(tag);
   }

   public CompoundTag writeData(WayfindingProperty.WayfindData data) {
      return data.toNbt();
   }

   public WayfindingProperty.WayfindData getDefaultData() {
      return WayfindingProperty.WayfindData.DEFAULT;
   }

   @Override
   public boolean hasJournalEntry() {
      return false;
   }
}
