package net.joefoxe.hexerei.block.custom;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.container.MixingCauldronContainer;
import net.joefoxe.hexerei.data.recipes.CauldronEmptyingRecipe;
import net.joefoxe.hexerei.data.recipes.CauldronFillingRecipe;
import net.joefoxe.hexerei.data.recipes.ModRecipeTypes;
import net.joefoxe.hexerei.fluid.ModFluids;
import net.joefoxe.hexerei.fluid.PotionFluid;
import net.joefoxe.hexerei.fluid.PotionFluidHandler;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.particle.CauldronParticleData;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.tileentity.MixingCauldronTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiTags;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.EmitParticlesPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class MixingCauldron extends BaseEntityBlock implements ITileEntity<MixingCauldronTile> {
   public static final int POTION_MB_AMOUNT = 250;
   public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);
   public static final IntegerProperty CRAFT_DELAY = IntegerProperty.create("delay", 0, 100);
   public static final BooleanProperty GUI_RENDER = BooleanProperty.create("gui_render");
   public static final BooleanProperty DYED = BooleanProperty.create("dyed");
   public int emitParticles;
   public static final MapCodec<MixingCauldron> CODEC = simpleCodec(MixingCauldron::new);
   public static final VoxelShape SHAPE = Stream.of(
         Block.box(0.0, 1.0, 0.0, 16.0, 16.0, 2.0),
         Block.box(0.0, 1.0, 14.0, 16.0, 16.0, 16.0),
         Block.box(0.0, 1.0, 2.0, 2.0, 16.0, 14.0),
         Block.box(14.0, 1.0, 2.0, 16.0, 16.0, 14.0),
         Block.box(-1.0, 3.0, -1.0, 17.0, 14.0, 2.0),
         Block.box(-1.0, 3.0, 2.0, 2.0, 14.0, 14.0),
         Block.box(0.0, 0.0, 13.0, 3.0, 1.0, 16.0),
         Block.box(0.0, 0.0, 0.0, 3.0, 1.0, 3.0),
         Block.box(13.0, 0.0, 0.0, 16.0, 1.0, 3.0),
         Block.box(13.0, 0.0, 13.0, 16.0, 1.0, 16.0),
         Block.box(2.0, 3.0, 2.0, 14.0, 4.0, 14.0),
         Block.box(14.0, 3.0, 2.0, 17.0, 14.0, 14.0),
         Block.box(-1.0, 3.0, 14.0, 17.0, 14.0, 17.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   protected MapCodec<? extends MixingCauldron> codec() {
      return CODEC;
   }

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(CRAFT_DELAY, 0)).setValue(DYED, false)).setValue(GUI_RENDER, false);
   }

   protected boolean canReceiveStalactiteDrip(Fluid pFluid) {
      return true;
   }

   protected void receiveStalactiteDrip(BlockState pState, Level pLevel, BlockPos pPos, Fluid pFluid) {
      if (pLevel.getBlockEntity(pPos) instanceof MixingCauldronTile mixingCauldron) {
         if (mixingCauldron.getFluidStack().isEmpty()) {
            mixingCauldron.fill(new FluidStack(pFluid, 100), FluidAction.EXECUTE);
         } else if (mixingCauldron.getFluidStack().getFluid() == pFluid && mixingCauldron.getFluidStack().getAmount() < 2000) {
            mixingCauldron.getFluidStack().grow(100);
            if (mixingCauldron.getFluidStack().getAmount() > 2000) {
               mixingCauldron.getFluidStack().setAmount(2000);
            }
         }

         mixingCauldron.sync();
      }
   }

   public boolean useShapeForLightOcclusion(BlockState p_220074_1_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_220053_1_, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
      return SHAPE;
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (!(level.getBlockEntity(pos) instanceof MixingCauldronTile cauldronTile)) {
         return ItemInteractionResult.FAIL;
      } else if (stack.is((Item)ModItems.CROW_FLUTE.get()) && ((FluteData)stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY)).commandMode() == 2) {
         stack.useOn(new UseOnContext(player, hand, hitResult));
         return ItemInteractionResult.SUCCESS;
      } else {
         if (stack.is(HexereiTags.Items.SIGILS)) {
            if (cauldronTile.getItemStackInSlot(9).isEmpty()) {
               stack.setCount(1);
               cauldronTile.setItem(9, stack);
               player.getItemInHand(hand).shrink(1);
               cauldronTile.setChanged();
               return ItemInteractionResult.SUCCESS;
            }

            if (!cauldronTile.getItemStackInSlot(9).is(stack.getItem())) {
               player.getInventory().placeItemBackInInventory(cauldronTile.getItemStackInSlot(9));
               stack.setCount(1);
               cauldronTile.setItem(9, stack);
               player.getItemInHand(hand).shrink(1);
               cauldronTile.setChanged();
               return ItemInteractionResult.SUCCESS;
            }
         }

         FluidStack cauldronFluid = cauldronTile.getFluidStack();
         Optional<RecipeHolder<CauldronFillingRecipe>> fillingOptional = level.getRecipeManager()
            .getRecipeFor((RecipeType)ModRecipeTypes.CAULDRON_FILLING_TYPE.get(), new SingleRecipeInput(stack), level);
         if (fillingOptional.isPresent()) {
            CauldronFillingRecipe recipe = (CauldronFillingRecipe)fillingOptional.get().value();
            ItemStack output = recipe.getResultItem(level.registryAccess());
            FluidStack fluidOut = recipe.getResultingFluid();
            if (cauldronFluid.getFluid().isSame(Fluids.EMPTY)
               || fluidOut.getFluid().isSame(cauldronFluid.getFluid()) && cauldronFluid.getAmount() + fluidOut.getAmount() <= cauldronTile.getTankCapacity(0)) {
               return this.fillFromItem(cauldronTile, level, player, hand, player.getItemInHand(hand), output, fluidOut);
            }
         }

         Optional<RecipeHolder<CauldronEmptyingRecipe>> emptyingOptional = level.getRecipeManager()
            .getRecipeFor((RecipeType)ModRecipeTypes.CAULDRON_EMPTYING_TYPE.get(), new CauldronEmptyingRecipe.Wrapper(stack, cauldronFluid), level);
         if (emptyingOptional.isPresent()) {
            CauldronEmptyingRecipe recipe = (CauldronEmptyingRecipe)emptyingOptional.get().value();
            ItemStack output = recipe.getResultItem(level.registryAccess());
            FluidStack fluidIn = recipe.getFluid();
            if (FluidStack.isSameFluidSameComponents(fluidIn, cauldronFluid) && cauldronFluid.getAmount() >= fluidIn.getAmount()) {
               return this.emptyToItem(
                  cauldronTile, level, player, hand, player.getItemInHand(hand), new FluidStack(cauldronFluid.getFluid(), fluidIn.getAmount()), output
               );
            }
         }

         if (cauldronFluid.getFluid() instanceof PotionFluid && stack.getItem() == Items.GLASS_BOTTLE) {
            ItemStack potionOut = PotionFluidHandler.fillBottle(cauldronFluid);
            this.shrinkItem(player, hand, player.getItemInHand(hand), potionOut);
            cauldronFluid.shrink(250);
            cauldronTile.setChanged();
            Random random = new Random();
            if (!level.isClientSide) {
               HexereiPacketHandler.sendToNearbyClient(level, cauldronTile.getPos(), new EmitParticlesPacket(cauldronTile.getPos(), 3, false));
            }

            level.playSound(
               null,
               cauldronTile.getPos().getX() + 0.5F,
               cauldronTile.getPos().getY() + 0.5F,
               cauldronTile.getPos().getZ() + 0.5F,
               SoundEvents.BOTTLE_FILL,
               SoundSource.BLOCKS,
               1.0F,
               0.8F + 0.4F * random.nextFloat()
            );
            return ItemInteractionResult.SUCCESS;
         } else if (stack.getItem() != Items.POTION && stack.getItem() != Items.LINGERING_POTION && stack.getItem() != Items.SPLASH_POTION
            || (
                  !FluidStack.isSameFluidSameComponents(cauldronFluid, PotionFluidHandler.getFluidFromPotionItem(stack))
                     || cauldronFluid.getAmount() + 250 > cauldronTile.getTankCapacity(0)
               )
               && !cauldronFluid.isEmpty()) {
            ItemStack fillStack = stack.copy();
            fillStack.setCount(1);
            Optional<IFluidHandlerItem> fluidHandlerOptional = FluidUtil.getFluidHandler(fillStack);
            if (fluidHandlerOptional.isPresent()) {
               IFluidHandlerItem fluidHandler = fluidHandlerOptional.get();
               if (cauldronTile.interactWithFluid(fluidHandler)) {
                  if (!player.isCreative()) {
                     stack.shrink(1);
                     if (stack.isEmpty()) {
                        player.setItemInHand(hand, fluidHandler.getContainer());
                     } else {
                        player.setItemInHand(hand, stack);
                        if (!player.getInventory().add(fluidHandler.getContainer())) {
                           player.drop(fluidHandler.getContainer(), false);
                        }
                     }
                  }

                  if (!level.isClientSide) {
                     HexereiPacketHandler.sendToNearbyClient(level, cauldronTile.getPos(), new EmitParticlesPacket(cauldronTile.getPos(), 3, false));
                  }

                  return ItemInteractionResult.sidedSuccess(level.isClientSide);
               } else {
                  return ItemInteractionResult.SUCCESS;
               }
            } else {
               if (!level.isClientSide()) {
                  MenuProvider containerProvider = this.createContainerProvider(level, pos);
                  player.openMenu(containerProvider, cauldronTile.getBlockPos());
               }

               return ItemInteractionResult.SUCCESS;
            }
         } else {
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            player.awardStat(Stats.USE_CAULDRON);
            this.shrinkItem(player, hand, player.getItemInHand(hand), bottle);
            if (cauldronFluid.isEmpty()) {
               cauldronTile.fill(PotionFluidHandler.getFluidFromPotionItem(stack), FluidAction.EXECUTE);
            } else {
               cauldronFluid.grow(250);
            }

            cauldronTile.setChanged();
            Random random = new Random();
            if (!level.isClientSide) {
               HexereiPacketHandler.sendToNearbyClient(level, cauldronTile.getPos(), new EmitParticlesPacket(cauldronTile.getPos(), 3, false));
            }

            level.playSound(
               null,
               cauldronTile.getPos().getX() + 0.5F,
               cauldronTile.getPos().getY() + 0.5F,
               cauldronTile.getPos().getZ() + 0.5F,
               SoundEvents.BOTTLE_EMPTY,
               SoundSource.BLOCKS,
               1.0F,
               0.8F + 0.4F * random.nextFloat()
            );
            return ItemInteractionResult.SUCCESS;
         }
      }
   }

   private ItemInteractionResult fillFromItem(
      MixingCauldronTile mixingCauldron, Level level, Player player, InteractionHand hand, ItemStack stackIn, ItemStack stackOut, FluidStack fluid
   ) {
      player.awardStat(Stats.USE_CAULDRON);
      this.shrinkItem(player, hand, stackIn, stackOut);
      if (mixingCauldron.getFluidStack().isEmpty()) {
         mixingCauldron.fill(fluid, FluidAction.EXECUTE);
      } else {
         mixingCauldron.getFluidStack().grow(fluid.getAmount());
      }

      mixingCauldron.normalizeTank();
      Random random = new Random();
      if (!level.isClientSide) {
         HexereiPacketHandler.sendToNearbyClient(level, mixingCauldron.getPos(), new EmitParticlesPacket(mixingCauldron.getPos(), 3, false));
      }

      level.playSound(
         null,
         mixingCauldron.getPos().getX() + 0.5F,
         mixingCauldron.getPos().getY() + 0.5F,
         mixingCauldron.getPos().getZ() + 0.5F,
         SoundEvents.BOTTLE_EMPTY,
         SoundSource.BLOCKS,
         1.0F,
         0.8F + 0.4F * random.nextFloat()
      );
      mixingCauldron.setChanged();
      return ItemInteractionResult.SUCCESS;
   }

   private ItemInteractionResult emptyToItem(
      MixingCauldronTile mixingCauldron, Level level, Player player, InteractionHand hand, ItemStack stackIn, FluidStack fluid, ItemStack stackOut
   ) {
      player.awardStat(Stats.USE_CAULDRON);
      this.shrinkItem(player, hand, stackIn, stackOut);
      mixingCauldron.getFluidStack().shrink(fluid.getAmount());
      mixingCauldron.normalizeTank();
      mixingCauldron.setChanged();
      Random random = new Random();
      if (!level.isClientSide) {
         HexereiPacketHandler.sendToNearbyClient(level, mixingCauldron.getPos(), new EmitParticlesPacket(mixingCauldron.getPos(), 3, false));
      }

      level.playSound(
         null,
         mixingCauldron.getPos().getX() + 0.5F,
         mixingCauldron.getPos().getY() + 0.5F,
         mixingCauldron.getPos().getZ() + 0.5F,
         SoundEvents.BOTTLE_FILL,
         SoundSource.BLOCKS,
         1.0F,
         0.8F + 0.4F * random.nextFloat()
      );
      return ItemInteractionResult.SUCCESS;
   }

   private void shrinkItem(Player player, InteractionHand hand, ItemStack stackIn, ItemStack stackOut) {
      if (!player.isCreative()) {
         stackIn.shrink(1);
         if (stackIn.isEmpty()) {
            player.setItemInHand(hand, stackOut);
         } else {
            player.getInventory().placeItemBackInInventory(stackOut);
         }
      }
   }

   public MixingCauldron(Properties properties) {
      super(properties.noOcclusion());
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(GUI_RENDER, false)).setValue(DYED, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{CRAFT_DELAY, GUI_RENDER, DYED});
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         if (level.getBlockEntity(pos) instanceof MixingCauldronTile te) {
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, (ItemStack)te.items.get(0)));
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, (ItemStack)te.items.get(1)));
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, (ItemStack)te.items.get(2)));
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, (ItemStack)te.items.get(3)));
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, (ItemStack)te.items.get(4)));
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, (ItemStack)te.items.get(5)));
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, (ItemStack)te.items.get(6)));
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, (ItemStack)te.items.get(7)));
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, (ItemStack)te.items.get(8)));
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, (ItemStack)te.items.get(9)));
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      this.withTileEntityDo(
         worldIn, pos, te -> te.setDyeColor(((DyedItemColor)stack.getOrDefault(DataComponents.DYED_COLOR, new DyedItemColor(16760348, true))).rgb())
      );
      super.setPlacedBy(worldIn, pos, state, placer, stack);
      if (stack.has(DataComponents.CUSTOM_NAME) && worldIn.getBlockEntity(pos) instanceof MixingCauldronTile mixingCauldronTile) {
         mixingCauldronTile.customName = stack.getHoverName();
      }
   }

   protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
      super.onPlace(state, level, pos, oldState, movedByPiston);
      this.withTileEntityDo(
         level,
         pos,
         te -> {
            if (!level.isClientSide()) {
               boolean old = te.hasHeatSource;
               te.hasHeatSource = false;
               BlockState heatSource = level.getBlockState(pos.below());
               if (heatSource.is(HexereiTags.Blocks.HEAT_SOURCES)
                  && (!heatSource.hasProperty(BlockStateProperties.LIT) || (Boolean)heatSource.getValue(BlockStateProperties.LIT))) {
                  te.hasHeatSource = true;
               }

               te.setChanged();
            }
         }
      );
   }

   protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      this.withTileEntityDo(
         level,
         pos,
         te -> {
            if (!level.isClientSide()) {
               boolean old = te.hasHeatSource;
               te.hasHeatSource = false;
               BlockState heatSource = level.getBlockState(pos.below());
               if (heatSource.is(HexereiTags.Blocks.HEAT_SOURCES)
                  && (!heatSource.hasProperty(BlockStateProperties.LIT) || (Boolean)heatSource.getValue(BlockStateProperties.LIT))) {
                  te.hasHeatSource = true;
               }

               te.setChanged();
            }
         }
      );
      return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
      ItemStack item = new ItemStack(this);
      Optional<MixingCauldronTile> tileEntityOptional = Optional.ofNullable(this.getBlockEntity(level, pos));
      item.set(DataComponents.DYED_COLOR, new DyedItemColor(tileEntityOptional.<Integer>map(cauldron -> cauldron.dyeColor).orElse(16760348), true));
      Component customName = tileEntityOptional.map(MixingCauldronTile::getCustomName).orElse(null);
      if (customName != null && !customName.getString().isEmpty()) {
         item.set(DataComponents.CUSTOM_NAME, customName);
      }

      return item;
   }

   public static DyeColor getDyeColorNamed(ItemStack stack) {
      return HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);
   }

   public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
      float height = 0.25F;
      if (world.getBlockEntity(pos) instanceof MixingCauldronTile cauldronTile) {
         height = 0.25F + 0.6875F * Math.min(1.0F, (float)cauldronTile.getFluidStack().getAmount() / cauldronTile.getTankCapacity(0)) + 0.0625F;
         int num = cauldronTile.getNumberOfItems() / 4;
         if (cauldronTile.getFluidStack().getAmount() > 0) {
            for (int i = 0; i < Mth.floor(cauldronTile.getFluidStack().getAmount() / 1000.0F + 0.5F); i++) {
               if (rand.nextDouble() > 0.5) {
                  world.addParticle(
                     new CauldronParticleData(cauldronTile.getFluidStack()),
                     pos.getX() + 0.2 + 0.6 * rand.nextDouble(),
                     pos.getY() + height,
                     pos.getZ() + 0.2 + 0.6 * rand.nextDouble(),
                     (rand.nextDouble() - 0.5) / 50.0,
                     (rand.nextDouble() + 0.5) * 0.004,
                     (rand.nextDouble() - 0.5) / 50.0
                  );
               }
            }

            for (int ix = 0; ix < num; ix++) {
               if (rand.nextDouble() > 0.5) {
                  world.addParticle(
                     new CauldronParticleData(cauldronTile.getFluidStack()),
                     pos.getX() + 0.2 + 0.6 * rand.nextDouble(),
                     pos.getY() + height,
                     pos.getZ() + 0.2 + 0.6 * rand.nextDouble(),
                     (rand.nextDouble() - 0.5) / 50.0,
                     (rand.nextDouble() + 0.5) * 0.004,
                     (rand.nextDouble() - 0.5) / 50.0
                  );
               }
            }

            if (cauldronTile.hasHeatSource) {
               for (int ixx = 0; ixx < num + 5; ixx++) {
                  if (rand.nextDouble() > 0.5) {
                     world.addParticle(
                        new CauldronParticleData(cauldronTile.getFluidStack()),
                        pos.getX() + 0.2 + 0.6 * rand.nextDouble(),
                        pos.getY() + height,
                        pos.getZ() + 0.2 + 0.6 * rand.nextDouble(),
                        (rand.nextDouble() - 0.5) / 50.0,
                        (rand.nextDouble() + 0.5) * 0.014,
                        (rand.nextDouble() - 0.5) / 50.0
                     );
                  }
               }
            }

            if (FluidStack.isSameFluidSameComponents(cauldronTile.getFluidStack(), new FluidStack(Fluids.WATER, 1))
               || FluidStack.isSameFluidSameComponents(cauldronTile.getFluidStack(), new FluidStack((Fluid)ModFluids.TALLOW_FLUID.get(), 1))) {
               world.addParticle(
                  ParticleTypes.BUBBLE,
                  pos.getX() + 0.2 + 0.6 * rand.nextDouble(),
                  pos.getY() + height,
                  pos.getZ() + 0.2 + 0.6 * rand.nextDouble(),
                  (rand.nextDouble() - 0.5) / 50.0,
                  (rand.nextDouble() + 0.5) * 0.005,
                  (rand.nextDouble() - 0.5) / 50.0
               );
            } else if (FluidStack.isSameFluidSameComponents(cauldronTile.getFluidStack(), new FluidStack((Fluid)ModFluids.BLOOD_FLUID.get(), 1))
               && rand.nextInt(20) == 0) {
               world.addParticle(
                  (ParticleOptions)ModParticleTypes.BLOOD.get(),
                  pos.getX() + 0.2 + 0.6 * rand.nextDouble(),
                  pos.getY() + height,
                  pos.getZ() + 0.2 + 0.6 * rand.nextDouble(),
                  (rand.nextDouble() - 0.5) / 75.0,
                  (rand.nextDouble() + 0.5) * 5.0E-4,
                  (rand.nextDouble() - 0.5) / 75.0
               );
            }
         }

         if (((Integer)state.getValue(CRAFT_DELAY)).intValue() >= 80.0 && !world.isClientSide) {
            HexereiPacketHandler.sendToNearbyClient(world, cauldronTile.getPos(), new EmitParticlesPacket(cauldronTile.getPos(), 3, false));
         }
      }

      super.animateTick(state, world, pos, rand);
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      BlockPos blockpos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(pLevel, pPos);
      if (blockpos != null) {
         Fluid fluid = PointedDripstoneBlock.getCauldronFillFluidType(pLevel, blockpos);
         if (fluid != Fluids.EMPTY && this.canReceiveStalactiteDrip(fluid)) {
            this.receiveStalactiteDrip(pState, pLevel, pPos, fluid);
         }
      }
   }

   private MenuProvider createContainerProvider(final Level worldIn, final BlockPos pos) {
      return new MenuProvider() {
         @org.jetbrains.annotations.Nullable
         public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return new MixingCauldronContainer(i, worldIn, pos, playerInventory, playerEntity);
         }

         public Component getDisplayName() {
            return worldIn.getBlockEntity(pos) instanceof MixingCauldronTile mixingCauldronTile && mixingCauldronTile.customName != null
               ? Component.translatable(mixingCauldronTile.customName.getString())
               : Component.translatable("screen.hexerei.mixing_cauldron");
         }
      };
   }

   public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
      BlockEntity tileentity = worldIn.getBlockEntity(pos);
      if (tileentity instanceof MixingCauldronTile) {
         ((MixingCauldronTile)tileentity).entityInside(entityIn);
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.mixing_cauldron_shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   @Override
   public Class<MixingCauldronTile> getTileEntityClass() {
      return MixingCauldronTile.class;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new MixingCauldronTile((BlockEntityType<?>)ModTileEntities.MIXING_CAULDRON_TILE.get(), pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.MIXING_CAULDRON_TILE.get() ? (world2, pos, state2, entity) -> ((MixingCauldronTile)entity).tick() : null;
   }
}
