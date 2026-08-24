package net.cibernet.alchemancy.properties.voidborn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import net.cibernet.alchemancy.item.InnatePropertyItem;
import net.cibernet.alchemancy.item.components.PropertyModifierComponent;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Nullable;

public class BlockVacuumProperty extends Property implements IDataHolder<BlockVacuumProperty.Data> {
   public static final DustColorTransitionOptions PARTICLES = new DustColorTransitionOptions(
      Vec3.fromRGB24(15502328).toVector3f(), Vec3.fromRGB24(0).toVector3f(), 0.75F
   );
   public static final InnatePropertyItem.Tooltip TOOL_TOOLTIP = (stack, context, tooltipComponents, tooltipFlag) -> {
      BlockVacuumProperty.Data data = ((BlockVacuumProperty)AlchemancyProperties.WORLD_OBLITERATOR.value()).getData(stack);
      if (data.filterBlock() != null) {
         tooltipComponents.add(
            Component.translatable("item.alchemancy.black_hole_tool.bound_to_block", new Object[]{data.filterBlock().getName()})
               .withColor(((BlockVacuumProperty)AlchemancyProperties.WORLD_OBLITERATOR.value()).getColor(stack))
         );
      }
   };

   @Override
   public void onStackedOverMe(
      ItemStack carriedItem, ItemStack stack, Player player, ClickAction clickAction, SlotAccess carriedSlot, Slot stackedOnSlot, AtomicBoolean isCancelled
   ) {
      if (clickAction == ClickAction.SECONDARY) {
         BlockVacuumProperty.Data data = this.getData(stack);
         if (carriedItem.getItem() instanceof BlockItem blockItem && (data.tag() == null || blockItem.getBlock().defaultBlockState().is(data.tag()))) {
            this.setData(stack, new BlockVacuumProperty.Data(data.tag(), blockItem.getBlock()));
            isCancelled.set(true);
         } else if (carriedItem.isEmpty()) {
            this.setData(stack, new BlockVacuumProperty.Data(data.tag()));
            isCancelled.set(true);
         }
      }
   }

   @Override
   public boolean cluelessCanReset() {
      return false;
   }

   @Override
   public void onItemUseTick(LivingEntity user, ItemStack stack, Tick event) {
      Level level = event.getEntity().level();
      BlockVacuumProperty.Data data = this.getData(event.getItem());
      if (!level.isClientSide()) {
         int radius = PropertyModifierComponent.getOrElse(stack, this.asHolder(), AlchemancyProperties.Modifiers.EFFECT_RADIUS, 5.0F).intValue();
         AABB aabb = new AABB(user.blockPosition()).inflate(radius);
         ArrayList<BlockPos> validPos = new ArrayList<>();

         for (BlockPos pos : BlockPos.betweenClosed(user.blockPosition().offset(radius, radius, radius), user.blockPosition().offset(-radius, -radius, -radius))) {
            if (user.blockPosition().distSqr(pos) <= radius * radius) {
               validPos.add(new BlockPos(pos));
            }
         }

         Collections.shuffle(validPos);
         if (!validPos.isEmpty()) {
            BlockPos posx = (BlockPos)validPos.getFirst();
            int durabilityConsumed = 0;

            for (int i = 5; i > 0 && posx != null; posx = validPos.isEmpty() ? null : (BlockPos)validPos.getFirst()) {
               validPos.removeFirst();
               BlockState state = level.getBlockState(posx);
               if (state.canEntityDestroy(level, posx, user) && state.getBlock().defaultDestroyTime() >= 0.0F && data.matches(state)) {
                  level.destroyBlock(posx, false, event.getEntity());
                  ItemEntity itemEntity = new ItemEntity(
                     level, posx.getX() + 0.5F, posx.getY() + 0.5F, posx.getZ() + 0.5F, state.getBlock().asItem().getDefaultInstance(), 0.0, 0.0, 0.0
                  );
                  itemEntity.setDefaultPickUpDelay();
                  level.addFreshEntity(itemEntity);
                  ((ServerLevel)level).sendParticles(PARTICLES, posx.getX() + 0.5F, posx.getY() + 0.5F, posx.getZ() + 0.5F, 5, 0.5, 0.5, 0.5, 0.0);
                  i--;
                  durabilityConsumed++;
               }
            }

            if (durabilityConsumed > 0) {
               this.damageItem(user, stack, EquipmentSlot.MAINHAND, durabilityConsumed);
            }
         }

         if (user instanceof Player player) {
            for (ItemEntity item : level.getEntitiesOfClass(
               ItemEntity.class,
               aabb,
               itemx -> itemx.getItem().getItem() instanceof BlockItem blockItem && data.matches(blockItem.getBlock().defaultBlockState())
            )) {
               item.playerTouch(player);
            }
         }
      }
   }

   @Override
   public int modifyUseDuration(ItemStack stack, int original, int result) {
      return 72000;
   }

   @Override
   public Optional<UseAnim> modifyUseAnimation(ItemStack stack, UseAnim original, Optional<UseAnim> current) {
      return Optional.of(UseAnim.BOW);
   }

   @Override
   public void onRightClickItem(RightClickItem event) {
      event.getEntity().startUsingItem(event.getHand());
      event.setCancellationResult(InteractionResult.CONSUME);
      event.setCanceled(true);
   }

   @Override
   public Collection<ItemStack> populateCreativeTab(DeferredItem<Item> capsuleItem, Holder<Property> holder) {
      return List.of();
   }

   @Override
   public int getColor(ItemStack stack) {
      return ColorUtils.interpolateColorsOverTime(4.0F, 15502328, 5192594);
   }

   public BlockVacuumProperty.Data readData(CompoundTag tag) {
      return new BlockVacuumProperty.Data(tag);
   }

   public CompoundTag writeData(BlockVacuumProperty.Data data) {
      return data.save();
   }

   public BlockVacuumProperty.Data getDefaultData() {
      return BlockVacuumProperty.Data.DEFAULT;
   }

   @Override
   public Component getDisplayText(ItemStack stack) {
      Component name = super.getDisplayText(stack).copy().withStyle(ChatFormatting.BOLD);
      BlockVacuumProperty.Data data = this.getData(stack);
      if (data.filterBlock() != null) {
         return Component.translatable("property.detail", new Object[]{name, data.filterBlock().getName()}).withColor(this.getColor(stack));
      } else {
         return (Component)(data.tag() != null
            ? Component.translatable("property.detail", new Object[]{name, "#%s".formatted(data.tag().toString())}).withColor(this.getColor(stack))
            : name);
      }
   }

   @Override
   public boolean hasJournalEntry() {
      return false;
   }

   public record Data(@Nullable TagKey<Block> tag, @Nullable Block filterBlock) {
      public static final BlockVacuumProperty.Data DEFAULT = new BlockVacuumProperty.Data(null, null);

      public Data(@Nullable TagKey<Block> tag) {
         this(tag, null);
      }

      public Data(CompoundTag nbt) {
         this(
            nbt.contains("tag", 8) ? TagKey.create(Registries.BLOCK, ResourceLocation.parse(nbt.getString("tag"))) : null,
            nbt.contains("block", 8) ? (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.parse(nbt.getString("block"))) : null
         );
      }

      public CompoundTag save() {
         return new CompoundTag() {
            {
               if (Data.this.tag != null) {
                  this.putString("tag", Data.this.tag.location().toString());
               }

               if (Data.this.filterBlock != null) {
                  this.putString("block", BuiltInRegistries.BLOCK.getKey(Data.this.filterBlock).toString());
               }
            }
         };
      }

      public boolean matches(BlockState blockState) {
         if (this.filterBlock() != null) {
            return blockState.is(this.filterBlock());
         } else {
            return this.tag() != null ? blockState.is(this.tag()) : true;
         }
      }
   }
}
