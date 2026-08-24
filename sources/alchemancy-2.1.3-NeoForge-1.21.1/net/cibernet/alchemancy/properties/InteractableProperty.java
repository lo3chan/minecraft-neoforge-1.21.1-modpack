package net.cibernet.alchemancy.properties;

import java.util.List;
import net.cibernet.alchemancy.blocks.blockentities.RootedItemBlockEntity;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import org.jetbrains.annotations.Nullable;

public class InteractableProperty extends Property {
   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!event.isCanceled()) {
         ItemStack stack = event.getItemStack();
         activateByEntity(event.getEntity(), event.getEntity(), stack);
         Player var4 = event.getEntity();
         if (var4 instanceof Player) {
            applyCooldown(var4, event.getItemStack());
         }

         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   @Override
   public void onRightClickEntity(EntityInteractSpecific event) {
      if (!event.isCanceled()) {
         ItemStack stack = event.getItemStack();
         activateByEntity(event.getEntity(), event.getTarget(), stack);
         Player var4 = event.getEntity();
         if (var4 instanceof Player) {
            applyCooldown(var4, event.getItemStack());
         }

         event.setCancellationResult(InteractionResult.SUCCESS);
         event.setCanceled(true);
      }
   }

   public static void applyCooldown(Player player, ItemStack stack, int amount) {
      ItemCooldowns cooldowns = player.getCooldowns();
      if (!cooldowns.isOnCooldown(stack.getItem())) {
         cooldowns.addCooldown(stack.getItem(), amount);
      }
   }

   protected static void applyCooldown(Player player, ItemStack stack) {
      applyCooldown(player, stack, 20);
   }

   @Override
   public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(
      BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult
   ) {
      ServerLevel serverlevel = blockSource.level();
      BlockPos blockpos = blockSource.pos().relative((Direction)blockSource.state().getValue(DispenserBlock.FACING));
      List<Entity> list = serverlevel.getEntitiesOfClass(Entity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS);
      if (!list.isEmpty()) {
         activateByBlock(serverlevel, blockpos, (Entity)list.getFirst(), stack);
         InfusionPropertyDispenseBehavior.playDefaultEffects(blockSource, direction);
         return InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
      } else {
         return InfusionPropertyDispenseBehavior.DispenseResult.PASS;
      }
   }

   @Nullable
   @Override
   public ItemInteractionResult onRootedRightClick(RootedItemBlockEntity root, Player user, InteractionHand hand, BlockHitResult hitResult) {
      activateByBlock(root, user);
      return ItemInteractionResult.SUCCESS;
   }

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (rayTraceResult instanceof EntityHitResult entityHitResult) {
         activateByEntity(projectile, ((EntityHitResult)rayTraceResult).getEntity(), stack);
      }
   }

   @Override
   public int getColor(ItemStack stack) {
      return 16737126;
   }
}
