package net.cibernet.alchemancy.properties;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.cibernet.alchemancy.entity.InfusedItemProjectile;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.network.C2SDispenseFromItemPayload;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.CommonUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent.UsePhase;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class DispensingProperty extends Property {
   private static final GameProfile FAKE_GAME_PROFILE = new GameProfile(UUID.randomUUID(), "Michael");

   @Override
   public void onProjectileImpact(ItemStack stack, Projectile projectile, HitResult rayTraceResult, ProjectileImpactEvent event) {
      if (!projectile.level().isClientSide()) {
         Level level = projectile.level();
         Vec3 direction = projectile.position().subtract(rayTraceResult.getLocation());
         double d0 = direction.horizontalDistance();
         float xRot = (float)(Mth.atan2(direction.y, d0) * 180.0 / 3.141592653589793);
         float yRot = (float)(Mth.atan2(direction.x, direction.z) * 180.0 / 3.141592653589793);
         GameProfile profile = FAKE_GAME_PROFILE;
         Player michael = CommonUtils.createFakePlayer(level, profile);
         michael.moveTo(projectile.position().subtract(0.0, projectile.getEyeHeight(), 0.0), yRot, xRot);
         michael.setYHeadRot(yRot);
         stack.set(AlchemancyItems.Components.RECURSION_PREVENTION, Unit.INSTANCE);
         michael.setItemInHand(InteractionHand.MAIN_HAND, stack);
         if (rayTraceResult instanceof BlockHitResult clip) {
            UseOnContext context = new UseOnContext(
               level,
               michael,
               InteractionHand.MAIN_HAND,
               stack,
               new BlockHitResult(clip.getLocation(), clip.getDirection(), clip.getBlockPos(), clip.isInside())
            );
            InteractionResult result = useItemOnBlock(michael, InteractionHand.MAIN_HAND, context);
            if (!result.consumesAction()) {
               result = useItem(michael, InteractionHand.MAIN_HAND);
               stack = michael.getItemInHand(InteractionHand.MAIN_HAND);
            } else {
               stack = context.getItemInHand();
            }
         } else {
            useItem(michael, InteractionHand.MAIN_HAND);
            stack = michael.getItemInHand(InteractionHand.MAIN_HAND);
         }

         if (stack.isEmpty()) {
            projectile.discard();
         } else {
            stack.remove(AlchemancyItems.Components.RECURSION_PREVENTION);
            if (projectile instanceof InfusedItemProjectile infusedItemProjectile) {
               infusedItemProjectile.setItem(stack);
            }
         }
      }
   }

   @Override
   public void onEquippedTick(LivingEntity user, EquipmentSlot slot, ItemStack stack) {
      if ((slot == EquipmentSlot.FEET || slot == EquipmentSlot.BODY) && user.tickCount % 5 == 0 && !(user.getKnownMovement().lengthSqr() <= 0.007)) {
         Level level = user.level();
         ItemStack stored = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(stack);
         if (!stored.isEmpty() && (!(user instanceof Player) || level.isClientSide())) {
            if (!user.onGround() && !InfusedPropertiesHelper.hasInfusedProperty(stack, AlchemancyProperties.ANTIGRAV)) {
               BlockHitResult check = level.clip(
                  new ClipContext(
                     user.position(), new Vec3(user.getX(), Math.ceil(user.getY() * 4.0) / 4.0 - 2.0, user.getZ()), Block.COLLIDER, Fluid.NONE, user
                  )
               );
               if (check.getType() == Type.MISS) {
                  return;
               }
            }

            placeByActivation(user, level, user.position(), user.getPose(), stack, user.getYRot(), 90.0F);
            if (user instanceof Player) {
               PacketDistributor.sendToServer(
                  new C2SDispenseFromItemPayload(stack, user.position(), user.getPose(), 90.0F, user.getYRot()), new CustomPacketPayload[0]
               );
            }
         }
      }
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      if (!this.preventRecursion(event.getItemStack())) {
         ItemStack stack = event.getItemStack();
         if (!InfusedPropertiesHelper.hasProperty(stack, AlchemancyProperties.THROWABLE)) {
            Level level = event.getLevel();
            Player player = event.getEntity();
            ItemStack stored = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(stack);
            if (!stored.isEmpty()) {
               Player michael = CommonUtils.createFakePlayer(level, player.getGameProfile());
               michael.moveTo(player.position(), player.getYRot(), player.getXRot());
               michael.setYHeadRot(player.getYHeadRot());
               michael.setPose(player.getPose());
               michael.setItemInHand(event.getHand(), stored.copy());
               InteractionResult result = useItem(michael, event.getHand());
               ((HollowProperty)AlchemancyProperties.HOLLOW.get()).setData(event.getItemStack(), michael.getItemInHand(event.getHand()));
               dropFakePlayerInventory(michael, event.getHand());
               if (result.consumesAction()) {
                  event.setCancellationResult(result);
                  event.setCanceled(true);
               }
            }
         }
      }
   }

   @Override
   public void onRightClickBlock(UseItemOnBlockEvent event) {
      if (!this.preventRecursion(event.getItemStack())) {
         Level level = event.getLevel();
         Player player = event.getPlayer();
         ItemStack stored = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(event.getItemStack());
         if (!stored.isEmpty()) {
            Player michael = CommonUtils.createFakePlayer(level, player == null ? FAKE_GAME_PROFILE : player.getGameProfile());
            michael.moveTo(player.position(), player.getYRot(), player.getXRot());
            michael.setYHeadRot(player.getYHeadRot());
            michael.setPose(player.getPose());
            michael.setItemInHand(event.getHand(), stored.copy());
            UseOnContext context = event.getUseOnContext();
            context = new UseOnContext(
               level,
               michael,
               context.getHand(),
               stored,
               new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside())
            );
            InteractionResult result = useItemOnBlock(michael, event.getHand(), context);
            if (!result.consumesAction()) {
               result = useItem(michael, event.getHand());
            }

            ((HollowProperty)AlchemancyProperties.HOLLOW.get()).setData(event.getItemStack(), michael.getItemInHand(context.getHand()));
            dropFakePlayerInventory(michael, context.getHand());
            if (result.consumesAction()) {
               event.setCancellationResult(result(result));
               event.setCanceled(true);
            }
         }
      }
   }

   private static void dropFakePlayerInventory(Player fakePlayer, InteractionHand hand) {
      ItemStack heldItem = fakePlayer.getItemInHand(hand);
      Inventory inventory = fakePlayer.getInventory();

      for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
         ItemStack item = inventory.getItem(slot);
         if (!item.isEmpty() && item != heldItem) {
            ItemEntity itemEntity = fakePlayer.drop(item, false);
            if (itemEntity != null) {
               itemEntity.setNoPickUpDelay();
            }
         }
      }
   }

   @Override
   public void onActivation(@Nullable Entity source, Entity target, ItemStack stack, DamageSource damageSource) {
      if (source != null) {
         if (!(source instanceof Player) || source.level().isClientSide()) {
            placeByActivation(source, stack);
            if (source instanceof Player user) {
               PacketDistributor.sendToServer(
                  new C2SDispenseFromItemPayload(stack, user.position(), user.getPose(), user.getXRot(), user.getYHeadRot()), new CustomPacketPayload[0]
               );
            }
         }
      } else {
         Vec3 pos = damageSource.getSourcePosition();
         float xRot;
         float yRot;
         if (pos == null) {
            pos = target.getEyePosition();
            xRot = target.getXRot();
            yRot = target.getYHeadRot();
         } else {
            pos = pos.subtract(0.0, 1.0, 0.0);
            double d0 = target.getX() - pos.x();
            double d1 = target.getY() - pos.y();
            double d2 = target.getZ() - pos.z();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            xRot = Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * 180.0 / 3.141592653589793)));
            yRot = Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * 180.0 / 3.141592653589793) - 90.0F);
         }

         placeByActivation(null, target.level(), pos, Pose.STANDING, stack, yRot, xRot);
      }
   }

   public static void placeByActivation(@Nonnull Entity user, ItemStack stack) {
      placeByActivation(user, user.level(), user.getEyePosition(), user.getPose(), stack, user.getYHeadRot(), user.getXRot());
   }

   public static void placeByActivation(@Nullable Entity user, Level level, Vec3 position, Pose pose, ItemStack stack, float yRot, float xRot) {
      ItemStack stored = ((HollowProperty)AlchemancyProperties.HOLLOW.get()).getData(stack);
      GameProfile profile = user instanceof Player player ? player.getGameProfile() : FAKE_GAME_PROFILE;
      Player michael = CommonUtils.createFakePlayer(level, profile);
      michael.moveTo(position, yRot, xRot);
      michael.setYHeadRot(yRot);
      michael.setPose(pose);
      michael.setItemInHand(InteractionHand.MAIN_HAND, stored);
      BlockHitResult clip = level.clip(
         new ClipContext(
            position,
            new Vec3(position.x(), Math.ceil(position.y() * 4.0) / 4.0 - 1.0, position.z()),
            Block.COLLIDER,
            Fluid.NONE,
            user == null ? CollisionContext.empty() : CollisionContext.of(user)
         )
      );
      UseOnContext context = new UseOnContext(
         level, michael, InteractionHand.MAIN_HAND, stored, new BlockHitResult(clip.getLocation(), Direction.UP, clip.getBlockPos(), clip.isInside())
      );
      if (stored.getItem() instanceof BlockItem blockItem) {
         BlockState placeState = blockItem.getBlock().getStateForPlacement(new BlockPlaceContext(context));
         if (placeState != null) {
            double topY = context.getClickedPos().getY() + placeState.getCollisionShape(level, context.getClickedPos()).max(Axis.Y);
            if (user != null && user.getY() < topY) {
               user.setPos(user.getX(), topY, user.getZ());
            }
         }
      }

      InteractionResult result = useItemOnBlock(michael, InteractionHand.MAIN_HAND, context);
      ItemStack resultStack;
      if (!result.consumesAction()) {
         result = useItem(michael, InteractionHand.MAIN_HAND);
         resultStack = michael.getItemInHand(InteractionHand.MAIN_HAND);
      } else {
         resultStack = michael.getItemInHand(context.getHand());
      }

      ((HollowProperty)AlchemancyProperties.HOLLOW.get()).setData(stack, resultStack);
      dropFakePlayerInventory(michael, InteractionHand.MAIN_HAND);
   }

   public static InteractionResult useItem(Player player, InteractionHand hand) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (player.getCooldowns().isOnCooldown(itemstack.getItem())) {
         return InteractionResult.PASS;
      } else {
         InteractionResult cancelResult = CommonHooks.onItemRightClick(player, hand);
         if (cancelResult != null) {
            return cancelResult;
         } else {
            InteractionResultHolder<ItemStack> interactionresultholder = itemstack.use(player.level(), player, hand);
            ItemStack itemstack1 = (ItemStack)interactionresultholder.getObject();
            if (itemstack1 != itemstack) {
               player.setItemInHand(hand, itemstack1);
            }

            return interactionresultholder.getResult();
         }
      }
   }

   public static InteractionResult useItemOnBlock(Player player, InteractionHand hand, UseOnContext context) {
      ItemStack itemstack = player.getItemInHand(hand);
      if (player.getCooldowns().isOnCooldown(itemstack.getItem())) {
         return InteractionResult.PASS;
      } else {
         UseItemOnBlockEvent event = (UseItemOnBlockEvent)NeoForge.EVENT_BUS.post(new UseItemOnBlockEvent(context, UsePhase.ITEM_AFTER_BLOCK));
         return event.isCanceled() ? event.getCancellationResult().result() : CommonHooks.onPlaceItemIntoWorld(context);
      }
   }

   public static ItemInteractionResult result(InteractionResult result) {
      return switch (result) {
         case SUCCESS, SUCCESS_NO_ITEM_USED -> ItemInteractionResult.SUCCESS;
         case CONSUME -> ItemInteractionResult.CONSUME;
         case CONSUME_PARTIAL -> ItemInteractionResult.CONSUME_PARTIAL;
         case PASS -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         case FAIL -> ItemInteractionResult.FAIL;
         default -> throw new MatchException(null, null);
      };
   }

   @Override
   public int getPriority() {
      return -50;
   }

   @Override
   public int getColor(ItemStack stack) {
      return 12539049;
   }
}
