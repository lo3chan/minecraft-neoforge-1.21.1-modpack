package net.joefoxe.hexerei.block.custom;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.block.ITileEntity;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.custom.CandleItem;
import net.joefoxe.hexerei.particle.ModParticleTypes;
import net.joefoxe.hexerei.tileentity.CandleTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.EntityBlock;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.EventHooks;

public class Candle extends AbstractCandleBlock implements ITileEntity<CandleTile>, EntityBlock, SimpleWaterloggedBlock {
   public static final MapCodec<Candle> CODEC = simpleCodec(Candle::new);
   public static final IntegerProperty CANDLES = IntegerProperty.create("candles", 1, 4);
   public static final IntegerProperty CANDLES_LIT = IntegerProperty.create("candles_lit", 0, 4);
   public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final int BASE_COLOR = 13419416;
   private static final Int2ObjectMap<List<Vec3>> PARTICLE_OFFSETS = (Int2ObjectMap<List<Vec3>>)Util.make(
      () -> {
         Int2ObjectMap<List<Vec3>> int2objectmap = new Int2ObjectOpenHashMap();
         int2objectmap.defaultReturnValue(ImmutableList.of());
         int2objectmap.put(1, ImmutableList.of(new Vec3(0.5, 0.5, 0.5)));
         int2objectmap.put(2, ImmutableList.of(new Vec3(0.375, 0.44, 0.5), new Vec3(0.625, 0.5, 0.44)));
         int2objectmap.put(3, ImmutableList.of(new Vec3(0.5, 0.313, 0.625), new Vec3(0.375, 0.44, 0.5), new Vec3(0.56, 0.5, 0.44)));
         int2objectmap.put(
            4, ImmutableList.of(new Vec3(0.44, 0.313, 0.56), new Vec3(0.625, 0.44, 0.56), new Vec3(0.375, 0.44, 0.375), new Vec3(0.56, 0.5, 0.375))
         );
         return Int2ObjectMaps.unmodifiable(int2objectmap);
      }
   );
   public static final VoxelShape ONE_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 9.0, 10.0);
   public static final VoxelShape TWO_SHAPE = Block.box(3.5, 0.0, 3.5, 12.5, 9.0, 12.5);
   public static final VoxelShape THREE_SHAPE = Block.box(3.5, 0.0, 3.5, 12.5, 9.0, 12.5);
   public static final VoxelShape FOUR_SHAPE = Block.box(3.5, 0.0, 3.5, 12.5, 9.0, 12.5);
   public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new OptionalDispenseItemBehavior() {
      public ItemStack execute(BlockSource source, ItemStack stack) {
         this.setSuccess(false);
         Item item = stack.getItem();
         if (item instanceof BlockItem) {
            Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
            BlockPos blockpos = source.pos().relative(direction);

            try {
               this.setSuccess(((BlockItem)item).place(new DirectionalPlaceContext(source.level(), blockpos, direction, stack, direction)).consumesAction());
            } catch (Exception var9) {
               LOGGER.error("Error trying to place candle at {}", blockpos, var9);
            }

            BlockEntity blockEntity = source.level().getBlockEntity(blockpos);
            BlockState blockState = source.level().getBlockState(blockpos);
            if (blockEntity instanceof CandleTile candleTile) {
               source.level().scheduleTick(blockpos, blockState.getBlock(), 1);
            }
         }

         return stack;
      }

      protected void playSound(BlockSource source) {
         source.level().levelEvent(1000, source.pos(), 0);
      }
   };

   protected MapCodec<? extends AbstractCandleBlock> codec() {
      return CODEC;
   }

   public RenderShape getRenderShape(BlockState iBlockState) {
      return RenderShape.MODEL;
   }

   public void animateTick(BlockState p_220697_, Level p_220698_, BlockPos p_220699_, RandomSource p_220700_) {
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(HorizontalDirectionalBlock.FACING, pRot.rotate((Direction)pState.getValue(HorizontalDirectionalBlock.FACING)));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
      if (blockstate.is((Block)ModBlocks.CANDLE.get())) {
         return (BlockState)blockstate.setValue(CANDLES, Math.min(4, (Integer)blockstate.getValue(CANDLES) + 1));
      } else {
         FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
         boolean flag = fluidstate.getType() == Fluids.WATER;
         return (BlockState)((BlockState)((BlockState)super.getStateForPlacement(context).setValue(WATERLOGGED, flag))
               .setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection()))
            .setValue(CANDLES_LIT, 0);
      }
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
      if (pLevel.getBlockEntity(pPos) instanceof CandleTile candleTile) {
         int analog = candleTile.updateAnalog();
         pLevel.updateNeighborsAt(pPos, pState.getBlock());
      }

      super.tick(pState, pLevel, pPos, pRandom);
   }

   public static boolean isLit(BlockState p_151934_) {
      return p_151934_.hasProperty(LIT) && (p_151934_.is(BlockTags.CANDLES) || p_151934_.is(BlockTags.CANDLE_CAKES)) && (Boolean)p_151934_.getValue(LIT);
   }

   public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
      return useContext.getItemInHand().getItem() == ModItems.CANDLE.get() && (Integer)state.getValue(CANDLES) < 4 || super.canBeReplaced(state, useContext);
   }

   public void dropCandles(Level level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof CandleTile candleTile && !level.isClientSide()) {
         for (int i = 0; i < 4; i++) {
            CandleData candleData = (CandleData)candleTile.candles.get(i);
            if (candleData.hasCandle) {
               ItemStack itemStack = new ItemStack((ItemLike)ModBlocks.CANDLE.get());
               CompoundTag tag = ((CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
               candleData.save(tag, level.registryAccess(), true, false);
               itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
               if (candleData.dyeColor != 13419416) {
                  itemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(candleData.dyeColor, true));
               }

               popResource(level, pos, itemStack);
            }
         }
      }
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
      ItemStack item = new ItemStack((ItemLike)ModItems.CANDLE.get());
      Optional<CandleTile> tileEntityOptional = Optional.ofNullable(this.getBlockEntity(level, pos));
      tileEntityOptional.ifPresent(candleTile -> {
         CompoundTag tag = ((CustomData)item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         CandleData candleData = (CandleData)candleTile.candles.get(0);
         candleData.save(tag, level.registryAccess(), true, false);
         item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
         if (candleData.dyeColor != 13419416) {
            item.set(DataComponents.DYED_COLOR, new DyedItemColor(candleData.dyeColor, true));
         }
      });
      return item;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         BlockEntity tileentity = level.getBlockEntity(pos);
         if (tileentity != null) {
            this.dropCandles(level, pos);
         }

         for (Direction direction : Direction.values()) {
            level.updateNeighborsAt(pos.relative(direction), this);
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return switch (state.getValue(CANDLES)) {
         case 2 -> TWO_SHAPE;
         case 3 -> THREE_SHAPE;
         case 4 -> FOUR_SHAPE;
         default -> ONE_SHAPE;
      };
   }

   public static VoxelShape getShape(BlockState state) {
      return switch (state.getValue(CANDLES)) {
         case 2 -> TWO_SHAPE;
         case 3 -> THREE_SHAPE;
         case 4 -> FOUR_SHAPE;
         default -> ONE_SHAPE;
      };
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      ItemStack itemstack = player.getItemInHand(hand);
      Random random = new Random();
      if (itemstack.getItem() == Items.FLINT_AND_STEEL && canBeLit(state, pos, level)) {
         CandleTile tile = (CandleTile)level.getBlockEntity(pos);
         if (tile == null) {
            return ItemInteractionResult.FAIL;
         } else {
            if (!((CandleData)tile.candles.get(0)).lit) {
               ((CandleData)tile.candles.get(0)).lit = true;
            } else if (!((CandleData)tile.candles.get(1)).lit) {
               ((CandleData)tile.candles.get(1)).lit = true;
            } else if (!((CandleData)tile.candles.get(2)).lit) {
               ((CandleData)tile.candles.get(2)).lit = true;
            } else {
               if (((CandleData)tile.candles.get(3)).lit) {
                  return ItemInteractionResult.FAIL;
               }

               ((CandleData)tile.candles.get(3)).lit = true;
            }

            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
            itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
         }
      } else if (itemstack.getItem() == Items.FIRE_CHARGE && canBeLit(state, pos, level)) {
         CandleTile tile = (CandleTile)level.getBlockEntity(pos);
         if (tile == null) {
            return ItemInteractionResult.FAIL;
         } else {
            if (!((CandleData)tile.candles.get(0)).hasCandle) {
               ((CandleData)tile.candles.get(0)).lit = true;
            }

            if (!((CandleData)tile.candles.get(1)).hasCandle) {
               ((CandleData)tile.candles.get(1)).lit = true;
            }

            if (!((CandleData)tile.candles.get(2)).hasCandle) {
               ((CandleData)tile.candles.get(2)).lit = true;
            }

            if (!((CandleData)tile.candles.get(3)).hasCandle) {
               ((CandleData)tile.candles.get(3)).lit = true;
            }

            level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
            itemstack.shrink(1);
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
         }
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }

   public Candle(Properties properties) {
      super(properties.noCollission());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false)).setValue(POWER, 0))
               .setValue(CANDLES_LIT, 0))
            .setValue(LIT, false)
      );
   }

   protected Iterable<Vec3> getParticleOffsets(BlockState blockState) {
      return (Iterable<Vec3>)PARTICLE_OFFSETS.get((Integer)blockState.getValue(CANDLES));
   }

   public static void spawnSmokeParticles(Level level, BlockPos pos, boolean spawnExtraSmoke) {
      RandomSource random = level.getRandom();
      SimpleParticleType basicparticletype = (SimpleParticleType)ModParticleTypes.EXTINGUISH.get();
      Vec3 offset = new Vec3(random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() / 3.0 * (random.nextBoolean() ? 1 : -1));
      level.addParticle(
         basicparticletype,
         true,
         pos.getX() + 0.5 + offset.x,
         pos.getY() + random.nextDouble() * 0.15000000596046448,
         pos.getZ() + 0.5 + offset.z,
         offset.x / 8.0,
         random.nextDouble() * 0.1 + 0.1,
         offset.z / 8.0
      );
      if (spawnExtraSmoke) {
         level.addParticle(
            basicparticletype,
            true,
            pos.getX() + 0.5 + offset.x,
            pos.getY() + random.nextDouble() * 0.15000000596046448,
            pos.getZ() + 0.5 + offset.z,
            offset.x / 8.0,
            random.nextDouble() * 0.1 + 0.1,
            offset.z / 8.0
         );
      }
   }

   public static void spawnParticleWave(Level level, BlockPos pos, boolean spawnExtraSmoke, List<String> particle, int amount) {
      RandomSource random = level.getRandom();

      for (int i = 0; i < amount; i++) {
         float rotation = random.nextFloat() * 30.0F + 360.0F / amount * i;
         float ran = (float)random.nextDouble() * 0.15F + 0.15F;
         Vec3 offset = new Vec3(ran * Math.cos(rotation), 0.0, ran * Math.sin(rotation));
         if (!particle.isEmpty()) {
            try {
               ParticleOptions options = ParticleArgument.readParticle(new StringReader(particle.get(random.nextInt(particle.size()))), level.registryAccess());
               level.addParticle(
                  options,
                  true,
                  pos.getX() + 0.5 + offset.x,
                  pos.getY() + random.nextDouble() * 0.15000000596046448,
                  pos.getZ() + 0.5 + offset.z,
                  offset.x / 8.0,
                  random.nextDouble() * 0.025,
                  offset.z / 8.0
               );
               if (spawnExtraSmoke) {
                  level.addParticle(
                     options,
                     true,
                     pos.getX() + 0.5 + offset.x,
                     pos.getY() + random.nextDouble() * 0.15000000596046448,
                     pos.getZ() + 0.5 + offset.z,
                     offset.x / 8.0,
                     random.nextDouble() * 0.025,
                     offset.z / 8.0
                  );
               }
            } catch (CommandSyntaxException var11) {
            }
         }
      }
   }

   public static void extinguish(LevelAccessor level, BlockPos pos, BlockState state, CandleTile tile) {
      int numLit = 0;

      for (int i = 0; i < 4; i++) {
         if (((CandleData)tile.candles.get(i)).lit) {
            numLit++;
         }
      }

      ((CandleData)tile.candles.get(0)).lit = false;
      ((CandleData)tile.candles.get(1)).lit = false;
      ((CandleData)tile.candles.get(2)).lit = false;
      ((CandleData)tile.candles.get(3)).lit = false;
      if (!level.isClientSide()) {
         level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      if (level.isClientSide()) {
         for (int ix = 0; ix < 10 * numLit; ix++) {
            spawnSmokeParticles((Level)level, pos, true);
         }
      }
   }

   public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidStateIn) {
      if (!(Boolean)state.getValue(BlockStateProperties.WATERLOGGED) && fluidStateIn.getType() == Fluids.WATER) {
         CandleTile tile = (CandleTile)level.getBlockEntity(pos);
         boolean flag = ((CandleData)tile.candles.get(0)).lit
            || ((CandleData)tile.candles.get(1)).lit
            || ((CandleData)tile.candles.get(2)).lit
            || ((CandleData)tile.candles.get(3)).lit;
         if (flag) {
            extinguish(level, pos, state, tile);
         }

         level.setBlock(pos, (BlockState)state.setValue(WATERLOGGED, Boolean.TRUE), 3);
         level.scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(level));
         return true;
      } else {
         return false;
      }
   }

   public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
      if (projectile.isOnFire()) {
         CandleTile tile = (CandleTile)level.getBlockEntity(hit.getBlockPos());
         boolean flagLit = ((CandleData)tile.candles.get(0)).lit
            && ((CandleData)tile.candles.get(1)).lit
            && ((CandleData)tile.candles.get(2)).lit
            && ((CandleData)tile.candles.get(3)).lit;
         Entity entity = projectile.getOwner();
         boolean flag = entity == null || entity instanceof Player || EventHooks.canEntityGrief(level, entity);
         if (flag && !flagLit && !(Boolean)state.getValue(WATERLOGGED)) {
            if (((CandleData)tile.candles.get(0)).hasCandle) {
               ((CandleData)tile.candles.get(0)).lit = true;
            }

            if (((CandleData)tile.candles.get(1)).hasCandle) {
               ((CandleData)tile.candles.get(1)).lit = true;
            }

            if (((CandleData)tile.candles.get(2)).hasCandle) {
               ((CandleData)tile.candles.get(2)).lit = true;
            }

            if (((CandleData)tile.candles.get(3)).hasCandle) {
               ((CandleData)tile.candles.get(3)).lit = true;
            }
         }
      }
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if (stack != null) {
         this.withTileEntityDo(level, pos, te -> {
            int newCandlePos = 0;

            for (int i = 0; i < 4; i++) {
               if (!((CandleData)te.candles.get(i)).hasCandle && stack.getItem() instanceof CandleItem candleItem) {
                  CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
                  ((CandleData)te.candles.get(i)).load(tag, level.registryAccess(), true);
                  if (stack.has(DataComponents.DYED_COLOR)) {
                     ((CandleData)te.candles.get(0)).dyeColor = ((DyedItemColor)stack.get(DataComponents.DYED_COLOR)).rgb();
                  }

                  te.setOffsetPos(true);
                  newCandlePos = i;
                  break;
               }
            }

            for (int ix = 0; ix < 4; ix++) {
               if (((CandleData)te.candles.get(ix)).returnToBlock || ix == newCandlePos) {
                  te.setOffsetPos(ix);
                  ((CandleData)te.candles.get(ix)).moveInstantlyToTarget();
               }
            }

            te.sync();
         });

         for (Direction direction : Direction.values()) {
            level.updateNeighborsAt(pos.relative(direction), this);
         }

         super.setPlacedBy(level, pos, state, placer, stack);
      }
   }

   public boolean hasAnalogOutputSignal(BlockState pState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
      AtomicInteger toReturn = new AtomicInteger();
      if (pLevel.getBlockEntity(pPos) instanceof CandleTile candleTile) {
         candleTile.updateAnalog();
         return candleTile.redstoneAnalogSignal;
      } else {
         return toReturn.get();
      }
   }

   public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
      return (Integer)pBlockState.getValue(POWER);
   }

   public boolean isSignalSource(BlockState pState) {
      return (Integer)pState.getValue(POWER) > 0;
   }

   public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
      return (Integer)pBlockState.getValue(POWER);
   }

   public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
      for (Direction direction : Direction.values()) {
         pLevel.updateNeighborsAt(pPos.relative(direction), this);
      }

      super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HorizontalDirectionalBlock.FACING, CANDLES, WATERLOGGED, POWER, CANDLES_LIT, LIT});
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entityIn) {
      if (level.getBlockEntity(pos) instanceof CandleTile tile) {
         tile.entityInside(entityIn);
      }
   }

   public static boolean canBeLit(BlockState state, BlockPos pos, Level world) {
      return !(world.getBlockEntity(pos) instanceof CandleTile tile)
         ? false
         : !(Boolean)state.getValue(BlockStateProperties.WATERLOGGED)
            && (
               !((CandleData)tile.candles.get(0)).lit
                  || !((CandleData)tile.candles.get(1)).lit && ((CandleData)tile.candles.get(1)).hasCandle
                  || !((CandleData)tile.candles.get(2)).lit && ((CandleData)tile.candles.get(2)).hasCandle
                  || !((CandleData)tile.candles.get(3)).lit && ((CandleData)tile.candles.get(3)).hasCandle
            );
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
      return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      return canSupportCenter(level, pos.below(), Direction.UP);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "<%s>", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11167232)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_shift_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
         tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329))));
      } else {
         tooltipComponents.add(
            Component.translatable(
                  "[%s]", new Object[]{Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184640)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)))
         );
         String str = CandleItem.getEffectLocation(stack);
         if (str != null && str.length() > 0 && !str.equals("hexerei:no_effect")) {
            String translateEffect = "effect." + ResourceLocation.parse(str).getNamespace() + "." + ResourceLocation.parse(str).getPath();
            MutableComponent component = Component.translatable(translateEffect).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(10066329)));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.candle_effect", new Object[]{component}));
         }
      }

      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
   }

   @Override
   public Class<CandleTile> getTileEntityClass() {
      return CandleTile.class;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new CandleTile((BlockEntityType<?>)ModTileEntities.CANDLE_TILE.get(), pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> entityType) {
      return entityType == ModTileEntities.CANDLE_TILE.get() ? (world2, pos, state2, entity) -> ((CandleTile)entity).tick() : null;
   }
}
