package net.joefoxe.hexerei.block.custom;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.container.CofferContainer;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.items.ItemStackHandler;

public class Coffer extends BaseEntityBlock implements ITileEntity<CofferTile>, SimpleWaterloggedBlock {
   public static final MapCodec<Coffer> CODEC = simpleCodec(Coffer::new);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final VoxelShape SHAPE = Optional.of(Block.box(2.0, 0.0, 4.0, 14.0, 4.0, 12.0)).get();
   public static final VoxelShape SHAPE_TURNED = Optional.of(Block.box(4.0, 0.0, 2.0, 12.0, 4.0, 14.0)).get();

   public Coffer(Properties properties) {
      super(properties.noOcclusion());
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false));
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public static int getColorValue(BlockState state, BlockPos pos, BlockGetter level) {
      if (level.getBlockEntity(pos) instanceof CofferTile cofferTile) {
         int var6 = cofferTile.dyeColor;
         DyeColor color = getDyeColorNamed(cofferTile.customName != null ? cofferTile.customName.getString() : "");
         return color == null ? var6 : color.getTextureDiffuseColor();
      } else {
         return 4337438;
      }
   }

   public static int getColorStatic(ItemStack stack) {
      return ((DyedItemColor)stack.getOrDefault(
            DataComponents.DYED_COLOR, new DyedItemColor(stack.getItem() == ModItems.ENTANGLED_COFFER.get() ? 856599 : 4337438, true)
         ))
         .rgb();
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate((Direction)pState.getValue(HorizontalDirectionalBlock.FACING)));
   }

   public static DyeColor getDyeColorNamed(String name) {
      return HexereiUtil.getDyeColorNamed(name, 0);
   }

   public void tick(BlockState p_60462_, ServerLevel p_60463_, BlockPos p_60464_, RandomSource p_60465_) {
      updateOrDestroy(p_60462_, p_60462_, p_60463_, p_60464_, 0);
      super.tick(p_60462_, p_60463_, p_60464_, p_60465_);
   }

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
         .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
      return p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) != Direction.EAST
            && p_220053_1_.getValue(HorizontalDirectionalBlock.FACING) != Direction.WEST
         ? SHAPE
         : SHAPE_TURNED;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.COFFER_TILE.get() ? (world2, pos, state2, entity) -> ((CofferTile)entity).tick() : null;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (stack.is((Item)ModItems.CROW_FLUTE.get()) && ((FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 2) {
         stack.useOn(new UseOnContext(player, hand, hitResult));
         return ItemInteractionResult.SUCCESS;
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (player instanceof ServerPlayer serverPlayer) {
         BlockEntity tileEntity = level.getBlockEntity(pos);
         if (!(tileEntity instanceof CofferTile)) {
            throw new IllegalStateException("Our Container provider is missing!");
         }

         MenuProvider containerProvider = this.createContainerProvider(level, pos);
         serverPlayer.openMenu(containerProvider, b -> b.writeBoolean(true).writeLong(tileEntity.getBlockPos().asLong()));
      }

      return InteractionResult.SUCCESS;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, WATERLOGGED});
   }

   public void attack(BlockState state, Level world, BlockPos pos, Player player) {
      if (!(player instanceof FakePlayer)) {
         if (world instanceof ServerLevel) {
            ItemStack cloneItemStack = this.getCloneItemStack(state, new BlockHitResult(pos.getCenter(), Direction.UP, pos, true), world, pos, player);
            world.destroyBlock(pos, false);
            if (world.getBlockState(pos) != state && !world.isClientSide()) {
               if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == Items.AIR) {
                  player.setItemInHand(InteractionHand.MAIN_HAND, cloneItemStack);
               } else {
                  player.getInventory().placeItemBackInInventory(cloneItemStack);
               }
            }
         }
      }
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return !(Boolean)state.getValue(WATERLOGGED);
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return super.getCloneItemStack(level, pos, state);
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
      ItemStack item = new ItemStack(this);
      Optional<CofferTile> tileEntityOptional = Optional.ofNullable(this.getBlockEntity(level, pos));
      CompoundTag tag = ((CustomData)item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (tileEntityOptional.isPresent() && tileEntityOptional.get().cofferId != null) {
         tag.putUUID("CofferId", tileEntityOptional.get().cofferId);
      }

      tag.putInt("ButtonToggled", tileEntityOptional.<Integer>map(cofferTile -> cofferTile.buttonToggled).orElse(0));
      if (tileEntityOptional.isPresent() && !tileEntityOptional.get().isWhitelistEmpty()) {
         ListTag itemsTag = new ListTag();

         for (int slot = 0; slot < tileEntityOptional.get().whitelist.size(); slot++) {
            ItemStack stack = (ItemStack)tileEntityOptional.get().whitelist.get(slot);
            if (!stack.isEmpty()) {
               CompoundTag slotTag = new CompoundTag();
               slotTag.putInt("Slot", slot);
               Tag itemTag = stack.save(Hexerei.DynamicRegistries.get(), slotTag);
               slotTag.put("Item", itemTag);
               itemsTag.add(slotTag);
            }
         }

         tag.put("WhitelistItems", itemsTag);
      }

      if (tileEntityOptional.isPresent() && tileEntityOptional.get().mode != null) {
         tag.putInt("WhitelistMode", tileEntityOptional.get().mode.ordinal());
      }

      item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      item.set(DataComponents.DYED_COLOR, new DyedItemColor(tileEntityOptional.<Integer>map(cofferTile -> cofferTile.dyeColor).orElse(4337438), true));
      Component customName = tileEntityOptional.map(CofferTile::getCustomName).orElse(null);
      if (customName != null && !customName.getString().isEmpty()) {
         item.set(DataComponents.CUSTOM_NAME, customName);
      }

      return item;
   }

   protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
      super.onPlace(state, level, pos, oldState, movedByPiston);
   }

   public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      this.withTileEntityDo(worldIn, pos, te -> {
         CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         if (tag.contains("CofferId")) {
            te.cofferId = tag.getUUID("CofferId");
         }

         if (tag.contains("WhitelistItems", 9)) {
            ListTag itemsTag = tag.getList("WhitelistItems", 10);

            for (int i = 0; i < itemsTag.size(); i++) {
               CompoundTag slotTag = itemsTag.getCompound(i);
               int slot = slotTag.getInt("Slot");
               if (slot >= 0 && slot < te.whitelist.size()) {
                  te.whitelist.set(slot, ItemStack.parse(Hexerei.DynamicRegistries.get(), slotTag.getCompound("Item")).orElse(ItemStack.EMPTY));
               }
            }
         }

         if (tag.contains("WhitelistMode")) {
            te.mode = CofferTile.WhitelistMode.byId(tag.getInt("WhitelistMode"));
         }

         te.setDyeColor(getColorStatic(stack));
         te.buttonToggled = tag.contains("ButtonToggled") ? tag.getInt("ButtonToggled") : 0;
         te.syncCofferInventory();
         te.sync();
      });
      super.setPlacedBy(worldIn, pos, state, placer, stack);
      if (stack.has(DataComponents.CUSTOM_NAME)) {
         BlockEntity tileentity = worldIn.getBlockEntity(pos);
         if (tileentity != null) {
            ((CofferTile)tileentity).customName = (Component)stack.get(DataComponents.CUSTOM_NAME);
         }
      }
   }

   public boolean isEmpty(ItemStackHandler handler) {
      boolean empty = true;

      for (int i = 0; i < handler.getSlots(); i++) {
         if (!handler.getStackInSlot(i).isEmpty()) {
            empty = false;
            break;
         }
      }

      return empty;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      boolean isEntangled = stack.is(ModItems.ENTANGLED_COFFER);
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      if (Screen.hasShiftDown()) {
         if (!tag.contains("CofferId") || this.isEmpty(new CofferTile.CofferInvWrapper(tag.getUUID("CofferId"), null))) {
            if (!isEntangled) {
               tooltipComponents.add(Component.translatable("tooltip.hexerei.coffer_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.coffer_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.coffer_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
               tooltipComponents.add(Component.translatable("tooltip.hexerei.dyeable").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            } else {
               tooltipComponents.add(
                  Component.translatable("tooltip.hexerei.entangled_coffer_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
               );
               tooltipComponents.add(Component.translatable("tooltip.hexerei.dyeable").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
            }
         }
      } else if (!isEntangled) {
         tooltipComponents.add(Component.translatable("tooltip.hexerei.coffer").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      } else {
         tooltipComponents.add(Component.translatable("tooltip.hexerei.entangled_coffer").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   private MenuProvider createContainerProvider(final Level worldIn, final BlockPos pos) {
      return new MenuProvider() {
         public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return new CofferContainer(i, worldIn, pos, playerInventory, playerEntity);
         }

         public Component getDisplayName() {
            return worldIn.getBlockEntity(pos) instanceof CofferTile cofferTile && cofferTile.customName != null
               ? Component.translatable(cofferTile.customName.getString())
               : Component.translatable("screen.hexerei.coffer");
         }
      };
   }

   @Override
   public Class<CofferTile> getTileEntityClass() {
      return CofferTile.class;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new CofferTile((BlockEntityType<?>)ModTileEntities.COFFER_TILE.get(), pos, state);
   }
}
