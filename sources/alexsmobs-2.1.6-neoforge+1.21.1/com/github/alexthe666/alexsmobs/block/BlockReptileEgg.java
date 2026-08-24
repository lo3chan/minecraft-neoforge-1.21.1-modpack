package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.EntityCaiman;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityPlatypus;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockReptileEgg extends Block {
   public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
   public static final IntegerProperty EGGS = BlockStateProperties.EGGS;
   private static final VoxelShape ONE_EGG_SHAPE = Block.box(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
   private static final VoxelShape MULTI_EGG_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);
   private final Supplier<EntityType> births;

   public BlockReptileEgg(Supplier births) {
      super(Properties.of().mapColor(MapColor.SAND).strength(0.5F).sound(SoundType.METAL).randomTicks().noOcclusion());
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HATCH, 0)).setValue(EGGS, 1));
      this.births = births;
   }

   public static boolean hasProperHabitat(BlockGetter reader, BlockPos blockReader) {
      return isProperHabitat(reader, blockReader.below());
   }

   public static boolean isProperHabitat(BlockGetter reader, BlockPos pos) {
      return reader.getBlockState(pos).is(BlockTags.SAND) || reader.getBlockState(pos).is(AMTagRegistry.CROCODILE_SPAWNS);
   }

   public void stepOn(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
      this.tryTrample(worldIn, pos, entityIn, 100);
      super.stepOn(worldIn, pos, state, entityIn);
   }

   public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
      if (!(entityIn instanceof Zombie)) {
         this.tryTrample(worldIn, pos, entityIn, 3);
      }

      super.fallOn(worldIn, state, pos, entityIn, fallDistance);
   }

   private void tryTrample(Level worldIn, BlockPos pos, Entity trampler, int chances) {
      if (this.canTrample(worldIn, trampler) && !worldIn.isClientSide() && worldIn.getRandom().nextInt(chances) == 0) {
         AABB bb = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(25.0, 25.0, 25.0);
         if (trampler instanceof LivingEntity) {
            for (Mob living : worldIn.getEntitiesOfClass(Mob.class, bb, livingx -> livingx.isAlive() && livingx.getType() == this.births.get())) {
               if (!(living instanceof TamableAnimal) || !((TamableAnimal)living).isTame() || !((TamableAnimal)living).isOwnedBy((LivingEntity)trampler)) {
                  living.setTarget((LivingEntity)trampler);
               }
            }
         }

         BlockState blockstate = worldIn.getBlockState(pos);
         this.removeOneEgg(worldIn, pos, blockstate);
      }
   }

   private void removeOneEgg(Level worldIn, BlockPos pos, BlockState state) {
      worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + worldIn.getRandom().nextFloat() * 0.2F);
      int i = (Integer)state.getValue(EGGS);
      if (i <= 1) {
         worldIn.destroyBlock(pos, false);
      } else {
         worldIn.setBlock(pos, (BlockState)state.setValue(EGGS, i - 1), 2);
         worldIn.gameEvent(GameEvent.BLOCK_DESTROY, pos, Context.of(state));
         worldIn.levelEvent(2001, pos, Block.getId(state));
      }
   }

   public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
      if (this.canGrow(worldIn, pos) && hasProperHabitat(worldIn, pos)) {
         int i = (Integer)state.getValue(HATCH);
         if (i < 2) {
            worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            worldIn.gameEvent(GameEvent.BLOCK_DESTROY, pos, Context.of(state));
            worldIn.setBlock(pos, (BlockState)state.setValue(HATCH, i + 1), 2);
         } else {
            worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            worldIn.gameEvent(GameEvent.BLOCK_DESTROY, pos, Context.of(state));
            worldIn.removeBlock(pos, false);

            for (int j = 0; j < state.getValue(EGGS); j++) {
               worldIn.levelEvent(2001, pos, Block.getId(state));
               Entity fromType = AMCompat.create(this.births.get(), worldIn);
               if (fromType instanceof Animal animal) {
                  animal.setAge(-24000);
                  animal.restrictTo(pos, 20);
               }

               Holder<Biome> biome = worldIn.getBiome(pos);
               fromType.moveTo(pos.getX() + 0.3 + j * 0.2, pos.getY(), pos.getZ() + 0.3, 0.0F, 0.0F);
               if (!worldIn.isClientSide()) {
                  Player closest = worldIn.getNearestPlayer(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 20.0, EntitySelector.NO_SPECTATORS);
                  if (closest != null) {
                     if (fromType instanceof TamableAnimal tamableAnimal) {
                        AMCompat.setTame(tamableAnimal, true);
                        tamableAnimal.setOrderedToSit(true);
                        tamableAnimal.tame(closest);
                     }

                     if (fromType instanceof EntityCrocodile crocodile) {
                        crocodile.setDesert(biome.is(AMTagRegistry.SPAWNS_DESERT_CROCODILES));
                     }
                  }

                  worldIn.addFreshEntity(fromType);
               }
            }
         }
      }
   }

   public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (hasProperHabitat(worldIn, pos) && !worldIn.isClientSide()) {
         worldIn.levelEvent(2005, pos, 0);
      }
   }

   private boolean canGrow(Level worldIn, BlockPos pos) {
      float f = AMCompat.timeOfDay(worldIn, pos);
      return f < 0.8 && f > 0.65 ? true : worldIn.getRandom().nextInt(15) == 0;
   }

   public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
      super.playerDestroy(worldIn, player, pos, state, te, stack);
      this.removeOneEgg(worldIn, pos, state);
   }

   public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
      return useContext.getItemInHand().getItem() == this.asItem() && (Integer)state.getValue(EGGS) < 4 || super.canBeReplaced(state, useContext);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
      return blockstate.getBlock() == this
         ? (BlockState)blockstate.setValue(EGGS, Math.min(4, (Integer)blockstate.getValue(EGGS) + 1))
         : super.getStateForPlacement(context);
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return state.getValue(EGGS) > 1 ? MULTI_EGG_SHAPE : ONE_EGG_SHAPE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HATCH, EGGS});
   }

   private boolean canTrample(Level worldIn, Entity trampler) {
      if (trampler instanceof EntityCrocodile || trampler instanceof EntityCaiman || trampler instanceof EntityPlatypus || trampler instanceof Bat) {
         return false;
      } else {
         return !(trampler instanceof LivingEntity) ? false : trampler instanceof Player || AMPlatform.mobGriefing(worldIn, trampler);
      }
   }
}
