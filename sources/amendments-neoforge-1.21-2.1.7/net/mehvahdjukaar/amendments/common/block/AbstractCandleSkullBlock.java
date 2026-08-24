package net.mehvahdjukaar.amendments.common.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import java.util.List;
import net.mehvahdjukaar.amendments.common.tile.CandleSkullBlockTile;
import net.mehvahdjukaar.amendments.configs.CommonConfigs;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.block.ILightable;
import net.mehvahdjukaar.moonlight.api.block.IWashable;
import net.mehvahdjukaar.moonlight.api.set.BlocksColorAPI;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCandleSkullBlock extends AbstractCandleBlock implements EntityBlock, ILightable, IWashable {
   protected static final Int2ObjectMap<List<Vec3>> PARTICLE_OFFSETS = (Int2ObjectMap<List<Vec3>>)Util.make(() -> {
      Int2ObjectMap<List<Vec3>> map = new Int2ObjectArrayMap();
      map.defaultReturnValue(List.of());
      map.put(1, List.of(new Vec3(0.5, 1.0, 0.5)));
      map.put(2, List.of(new Vec3(0.375, 0.94, 0.5), new Vec3(0.625, 1.0, 0.44)));
      map.put(3, List.of(new Vec3(0.5, 0.813, 0.625), new Vec3(0.375, 0.94, 0.5), new Vec3(0.56, 1.0, 0.44)));
      map.put(4, List.of(new Vec3(0.44, 0.813, 0.56), new Vec3(0.625, 0.94, 0.56), new Vec3(0.375, 0.94, 0.375), new Vec3(0.56, 1.0, 0.375)));
      return Int2ObjectMaps.unmodifiable(map);
   });
   protected static final VoxelShape BASE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
   protected static final VoxelShape ONE_AABB = Shapes.or(BASE, Block.box(7.0, 8.0, 7.0, 9.0, 14.0, 9.0));
   protected static final VoxelShape TWO_AABB = Shapes.or(BASE, Block.box(5.0, 8.0, 6.0, 11.0, 14.0, 9.0));
   protected static final VoxelShape THREE_AABB = Shapes.or(BASE, Block.box(5.0, 8.0, 6.0, 10.0, 14.0, 11.0));
   protected static final VoxelShape FOUR_AABB = Shapes.or(BASE, Block.box(5.0, 8.0, 5.0, 11.0, 14.0, 10.0));
   public static final IntegerProperty CANDLES = BlockStateProperties.CANDLES;
   public static final BooleanProperty LIT = AbstractCandleBlock.LIT;

   protected AbstractCandleSkullBlock(Properties properties) {
      super(properties.lightLevel(CandleBlock.LIGHT_EMISSION));
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(LIT, false)).setValue(CANDLES, 1));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      super.createBlockStateDefinition(pBuilder);
      pBuilder.add(new Property[]{CANDLES, LIT});
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.INVISIBLE;
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new CandleSkullBlockTile(pPos, pState);
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof CandleSkullBlockTile tile
         && tile.getCandle().getBlock() instanceof CandleBlock) {
         List<ItemStack> loot = ((BlockState)tile.getCandle().setValue(CANDLES, (Integer)state.getValue(CANDLES))).getDrops(builder);
         BlockEntity skullTile = tile.getSkullTile();
         if (skullTile != null) {
            BlockState skull = skullTile.getBlockState();
            builder = builder.withOptionalParameter(LootContextParams.BLOCK_ENTITY, skullTile);
            loot.addAll(skull.getDrops(builder));
         }

         return loot;
      } else {
         return super.getDrops(state, builder);
      }
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return level.getBlockEntity(pos) instanceof CandleSkullBlockTile tile ? tile.getSkullItem() : super.getCloneItemStack(level, pos, state);
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult hitResult, LevelReader world, BlockPos pos, Player player) {
      if (world.getBlockEntity(pos) instanceof CandleSkullBlockTile tile) {
         double y = hitResult.getLocation().y;
         boolean up = y % (int)y > 0.5;
         return up ? tile.getCandle().getBlock().getCloneItemStack(world, pos, state) : tile.getSkullItem();
      } else {
         return super.getCloneItemStack(world, pos, state);
      }
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return switch (pState.getValue(CANDLES)) {
         case 2 -> TWO_AABB;
         case 3 -> THREE_AABB;
         case 4 -> FOUR_AABB;
         default -> ONE_AABB;
      };
   }

   protected Iterable<Vec3> getParticleOffsets(BlockState pState) {
      return (Iterable<Vec3>)PARTICLE_OFFSETS.get((Integer)pState.getValue(CANDLES));
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      if (Utils.mayPerformBlockAction(player, pos, stack)) {
         if (stack.is(ItemTags.CANDLES) && stack.getItem() instanceof BlockItem blockItem) {
            int count = (Integer)state.getValue(CANDLES);
            if (count < 4
               && CommonConfigs.SKULL_CANDLES_MULTIPLE.get()
               && level.getBlockEntity(pos) instanceof CandleSkullBlockTile tile
               && tile.getCandle().getBlock().asItem() == stack.getItem()) {
               SoundType sound = blockItem.getBlock().defaultBlockState().getSoundType();
               level.playSound(player, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
               stack.consume(1, player);
               if (player instanceof ServerPlayer serverPlayer) {
                  CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
               }

               player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
               level.setBlock(pos, (BlockState)state.setValue(CANDLES, count + 1), 2);
               level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
               return ItemInteractionResult.sidedSuccess(level.isClientSide);
            } else {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
         } else {
            return super.lightableInteractWithPlayerItem(state, level, pos, player, hand, stack);
         }
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      }
   }

   public boolean isLitUp(BlockState state, BlockGetter level, BlockPos pos) {
      return (Boolean)state.getValue(LIT);
   }

   public void setLitUp(BlockState state, LevelAccessor world, BlockPos pos, @Nullable Entity player, boolean lit) {
      world.setBlock(pos, (BlockState)state.setValue(LIT, lit), 3);
   }

   public boolean canBeExtinguishedBy(ItemStack item) {
      return item.isEmpty() || super.canBeExtinguishedBy(item);
   }

   public void playExtinguishSound(LevelAccessor world, BlockPos pos) {
      world.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public void spawnSmokeParticles(BlockState state, BlockPos pos, LevelAccessor level) {
      this.getParticleOffsets(state)
         .forEach(
            vec3 -> level.addParticle(ParticleTypes.SMOKE, pos.getX() + vec3.x(), pos.getY() + vec3.y(), pos.getZ() + vec3.z(), 0.0, 0.10000000149011612, 0.0)
         );
   }

   public boolean tryWash(Level level, BlockPos pos, BlockState state, Vec3 hit) {
      if (level.getBlockEntity(pos) instanceof CandleSkullBlockTile tile) {
         BlockState c = tile.getCandle();
         if (c != null) {
            Block n = BlocksColorAPI.changeColor(c.getBlock(), null);
            if (n != null && n != c.getBlock()) {
               tile.setCandle(n.withPropertiesOf(c));
               tile.setChanged();
               level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
               return true;
            }
         }
      }

      return false;
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
      return Utils.getTicker(type, ModRegistry.SKULL_CANDLE_TILE.get(), CandleSkullBlockTile::tick);
   }

   public void animateTick(BlockState state, Level level, BlockPos blockPos, RandomSource randomSource) {
      if ((Boolean)state.getValue(LIT) && level.getBlockEntity(blockPos) instanceof CandleSkullBlockTile tile) {
         this.getParticleOffsets(state)
            .forEach(vec3 -> this.addParticlesAndSound(tile.getParticle(), level, vec3.add(blockPos.getX(), blockPos.getY(), blockPos.getZ()), randomSource));
      }
   }

   protected void addParticlesAndSound(ParticleType<?> particle, Level level, Vec3 vec3, RandomSource randomSource) {
      float f = randomSource.nextFloat();
      if (f < 0.3F) {
         level.addParticle(ParticleTypes.SMOKE, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
         if (f < 0.17F) {
            level.playLocalSound(
               vec3.x + 0.5,
               vec3.y + 0.5,
               vec3.z + 0.5,
               SoundEvents.CANDLE_AMBIENT,
               SoundSource.BLOCKS,
               1.0F + randomSource.nextFloat(),
               randomSource.nextFloat() * 0.7F + 0.3F,
               false
            );
         }
      }

      level.addParticle((ParticleOptions)particle, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
   }
}
