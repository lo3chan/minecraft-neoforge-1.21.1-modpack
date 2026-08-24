package net.joefoxe.hexerei.item.custom;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.entity.custom.BroomEntity;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.CachedMap;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BroomItem extends BroomStickItem {
   private static final Predicate<Entity> field_219989_a = EntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith);
   private final String type;
   private final CachedMap<ItemStack, BroomEntity> cachedBroom;
   public static final Comparator<ItemStack> ITEM_COMPARATOR = (item1, item2) -> {
      int cmp = item2.getItem().hashCode() - item1.getItem().hashCode();
      if (cmp != 0) {
         return cmp;
      } else {
         cmp = item2.getDamageValue() - item1.getDamageValue();
         return cmp != 0 ? cmp : item1.getComponents().hashCode() - item2.getComponents().hashCode();
      }
   };

   private ItemStackHandler createHandler() {
      return new ItemStackHandler(30) {};
   }

   public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
      ItemStackHandler handler = this.createHandler();
      handler.deserializeNBT(
         Hexerei.DynamicRegistries.get(), ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag().getCompound("Inventory")
      );
      return Optional.of(new BroomItem.BroomItemToolTip(handler, stack));
   }

   public BroomItem(String broomType, Properties properties) {
      super(properties);
      this.type = broomType;
      this.cachedBroom = new CachedMap<>(10000L, ITEM_COMPARATOR);
   }

   public Vec3 getBrushOffset() {
      return new Vec3(0.0, 0.0, 0.0);
   }

   public Vec3 getSatchelOffset() {
      return new Vec3(0.0, 0.0, 0.0);
   }

   public Vec3 getTipOffset() {
      return new Vec3(0.0, 0.0, 0.0);
   }

   public static UUID getUUID(ItemStack stack) {
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (tag.contains("broomUUID")) {
         return tag.getUUID("broomUUID");
      } else {
         UUID newUUID = UUID.randomUUID();
         tag.putUUID("broomUUID", newUUID);
         return newUUID;
      }
   }

   public BroomEntity getBroom(Level world, ItemStack stack, Vec3 pos) {
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      BroomEntity broom = new BroomEntity(world, pos.x, pos.y, pos.z);
      if (tag.contains("floatMode")) {
         broom.itemHandler.deserializeNBT(world.registryAccess(), tag.getCompound("Inventory"));
         broom.floatMode = tag.getBoolean("floatMode");
      } else {
         broom.itemHandler.setStackInSlot(2, new ItemStack((ItemLike)ModItems.BROOM_BRUSH.get()));
      }

      if (stack.getItem() instanceof BroomItem broomItem) {
         broom.setBroomType(broomItem.type);
      }

      broom.isItem = true;
      broom.selfItem = stack.copy();
      broom.broomUUID = getUUID(stack);
      if (stack.get(DataComponents.CUSTOM_NAME) != null) {
         broom.setCustomName(stack.getHoverName());
      }

      return broom;
   }

   public void onActivate(BroomEntity broom, RandomSource random) {
   }

   public BroomEntity getBroomFast(Level world, ItemStack stack) {
      return this.cachedBroom.get(stack, () -> this.getBroom(world, stack, new Vec3(0.0, 0.0, 0.0)));
   }

   public static BlockHitResult getPlayerPOVHitResult(Level level, Player player, Fluid fluidMode, float range) {
      Vec3 vec3 = player.getEyePosition();
      Vec3 vec31 = vec3.add(player.calculateViewVector(player.getXRot(), player.getYRot()).scale(range));
      return level.clip(new ClipContext(vec3, vec31, Block.OUTLINE, fluidMode, player));
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.getItemInHand(handIn);
      HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, Fluid.ANY, (float)Math.min(3.0, playerIn.blockInteractionRange()));
      Vec3 vector3d = playerIn.getLookAngle();
      List<Entity> list = worldIn.getEntities(playerIn, playerIn.getBoundingBox().expandTowards(vector3d.scale(5.0)).inflate(1.0), field_219989_a);
      if (!list.isEmpty()) {
         Vec3 vector3d1 = playerIn.getEyePosition(1.0F);

         for (Entity entity : list) {
            AABB axisalignedbb = entity.getBoundingBox().inflate(entity.getPickRadius());
            if (axisalignedbb.contains(vector3d1)) {
               return InteractionResultHolder.pass(itemstack);
            }
         }
      }

      BroomEntity broom = this.getBroom(worldIn, itemstack, raytraceresult.getLocation());
      if (!worldIn.noCollision(broom, broom.getBoundingBox().inflate(-0.1))) {
         return InteractionResultHolder.fail(itemstack);
      } else {
         if (!worldIn.isClientSide) {
            worldIn.addFreshEntity(broom);
            broom.setRotation(playerIn.getYRot());
            if (raytraceresult.getType() == Type.MISS) {
               broom.setFloatMode(true);
            }

            if (!playerIn.getAbilities().instabuild) {
               itemstack.shrink(1);
            }
         }

         playerIn.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResultHolder.sidedSuccess(itemstack, worldIn.isClientSide());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "tooltip.hexerei.broom_shift_2",
                  new Object[]{
                     Component.translatable(ModKeyBindings.broomDown.getKey().getName()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(13421568)))
                  }
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.broom_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         if (stack.is((Item)ModItems.MAHOGANY_BROOM.get())) {
            tooltipComponents.add(Component.translatable(""));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.mahogany_broom_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(
               Component.translatable("tooltip.hexerei.mahogany_broom_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
            );
         } else {
            tooltipComponents.add(Component.translatable(""));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.willow_broom_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.willow_broom_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         }
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   public record BroomItemToolTip(ItemStackHandler handler, ItemStack self) implements TooltipComponent {
   }
}
