package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.block.AMBlockRegistry;
import com.github.alexthe666.alexsmobs.block.BlockLeafcutterAntChamber;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TileEntityLeafcutterAnthill extends BlockEntity {
   private static final Direction[] DIRECTIONS_UP = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
   private final List<TileEntityLeafcutterAnthill.Ant> ants = Lists.newArrayList();
   private int leafFeedings = 0;

   public TileEntityLeafcutterAnthill(BlockPos pos, BlockState state) {
      super(AMTileEntityRegistry.LEAFCUTTER_ANTHILL.get(), pos, state);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityLeafcutterAnthill entity) {
      entity.tickAnts();
   }

   @Nullable
   public static Entity loadEntityAndExecute(CompoundTag compound, Level worldIn, Function<Entity, Entity> p_220335_2_) {
      return loadEntity(compound, worldIn).map(p_220335_2_).map(p_220346_3_ -> {
         if (AMCompat.contains(compound, "Passengers", 9)) {
            ListTag listnbt = AMCompat.getList(compound, "Passengers", 10);

            for (int i = 0; i < listnbt.size(); i++) {
               Entity entity = loadEntityAndExecute(AMCompat.getCompound(listnbt, i), worldIn, p_220335_2_);
               if (entity != null) {
                  AMCompat.startRiding(entity, p_220346_3_, true);
               }
            }
         }

         return (Entity)p_220346_3_;
      }).orElse(null);
   }

   private static Optional<Entity> loadEntity(CompoundTag compound, Level worldIn) {
      try {
         return loadEntityUnchecked(compound, worldIn);
      } catch (RuntimeException var3) {
         return Optional.empty();
      }
   }

   public static Optional<Entity> loadEntityUnchecked(CompoundTag compound, Level worldIn) {
      EntityLeafcutterAnt leafcutterAnt = AMCompat.create(AMEntityRegistry.LEAFCUTTER_ANT.get(), worldIn);
      AMCompat.loadEntity(leafcutterAnt, compound);
      return Optional.of(leafcutterAnt);
   }

   public boolean hasNoAnts() {
      return this.ants.isEmpty();
   }

   public boolean hasAtleastThisManyAnts(int antCount) {
      return this.ants.size() >= antCount;
   }

   public boolean isFullOfAnts() {
      return this.ants.size() == AMConfig.leafcutterAntColonySize;
   }

   public void angerAnts(@Nullable LivingEntity p_226963_1_, BlockState p_226963_2_, BeeReleaseStatus p_226963_3_) {
      List<Entity> list = this.tryReleaseAnt(p_226963_2_, p_226963_3_);
      if (p_226963_1_ != null) {
         for (Entity entity : list) {
            if (entity instanceof EntityLeafcutterAnt entityLeafcutterAnt) {
               if (p_226963_1_.position().distanceToSqr(entity.position()) <= 16.0) {
                  entityLeafcutterAnt.setTarget(p_226963_1_);
               }

               entityLeafcutterAnt.setStayOutOfHiveCountdown(400);
            }
         }
      }
   }

   public void angerAntsBecauseAnteater(@Nullable LivingEntity p_226963_1_, BlockState p_226963_2_, BeeReleaseStatus p_226963_3_) {
      List<Entity> list = this.tryReleaseAntAnteater(p_226963_2_, p_226963_3_);
      if (p_226963_1_ != null) {
         for (Entity entity : list) {
            if (entity instanceof EntityLeafcutterAnt entityLeafcutterAnt) {
               if (p_226963_1_.position().distanceToSqr(entity.position()) <= 16.0) {
                  entityLeafcutterAnt.setTarget(p_226963_1_);
               }

               entityLeafcutterAnt.setStayOutOfHiveCountdown(400);
            }
         }
      }
   }

   private List<Entity> tryReleaseAnt(BlockState p_226965_1_, BeeReleaseStatus p_226965_2_) {
      List<Entity> list = Lists.newArrayList();
      this.ants.removeIf(p_226966_4_ -> this.addAntToWorld(p_226965_1_, p_226966_4_, list, p_226965_2_));
      return list;
   }

   private List<Entity> tryReleaseAntAnteater(BlockState p_226965_1_, BeeReleaseStatus p_226965_2_) {
      List<Entity> list = Lists.newArrayList();
      this.ants.removeIf(ant -> !ant.queen && this.addAntToWorld(p_226965_1_, ant, list, p_226965_2_));
      return list;
   }

   private boolean addAntToWorld(
      BlockState p_235651_1_, TileEntityLeafcutterAnthill.Ant p_235651_2_, @Nullable List<Entity> p_235651_3_, BeeReleaseStatus p_235651_4_
   ) {
      BlockPos blockpos = this.getBlockPos();
      CompoundTag compoundnbt = p_235651_2_.entityData;
      compoundnbt.remove("Passengers");
      compoundnbt.remove("Leash");
      compoundnbt.remove("UUID");
      BlockPos blockpos1 = blockpos.above();
      boolean flag = !this.level.getBlockState(blockpos1).getBlockSupportShape(this.level, blockpos1).isEmpty();
      if (flag && p_235651_4_ != BeeReleaseStatus.EMERGENCY) {
         return false;
      } else {
         Entity entity = loadEntityAndExecute(compoundnbt, this.level, p_226960_0_ -> p_226960_0_);
         if (entity != null) {
            if (entity instanceof EntityLeafcutterAnt entityLeafcutterAnt) {
               entityLeafcutterAnt.setLeaf(false);
               if (p_235651_4_ == BeeReleaseStatus.HONEY_DELIVERED) {
               }

               if (p_235651_3_ != null) {
                  p_235651_3_.add(entityLeafcutterAnt);
               }

               float f = entity.getBbWidth();
               double d0 = blockpos.getX() + 0.5;
               double d1 = blockpos.getY() + 1.0;
               double d2 = blockpos.getZ() + 0.5;
               entity.moveTo(d0, d1, d2, entity.getYRot(), entity.getXRot());
               if (((EntityLeafcutterAnt)entity).isQueen()) {
                  entityLeafcutterAnt.setStayOutOfHiveCountdown(400);
               }
            }

            this.level.gameEvent(GameEvent.BLOCK_ACTIVATE, this.getBlockPos(), Context.of(this.getBlockState()));
            this.level.playSound(null, blockpos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
            return this.level.addFreshEntity(entity);
         } else {
            return false;
         }
      }
   }

   public void tryEnterHive(EntityLeafcutterAnt p_226962_1_, boolean p_226962_2_, int p_226962_3_) {
      if (this.ants.size() < AMConfig.leafcutterAntColonySize) {
         p_226962_1_.stopRiding();
         p_226962_1_.ejectPassengers();
         CompoundTag compoundnbt = new CompoundTag();
         AMCompat.saveEntity(p_226962_1_, compoundnbt);
         if (p_226962_2_) {
            if (!this.level.isClientSide() && p_226962_1_.getRandom().nextFloat() < AMConfig.leafcutterAntFungusGrowChance) {
               this.growFungus();
            }

            this.leafFeedings++;
            if (this.leafFeedings >= AMConfig.leafcutterAntRepopulateFeedings
               && this.getAntsInAreaCount(32.0) < Mth.ceil(AMConfig.leafcutterAntColonySize * 0.5F)
               && this.hasQueen()) {
               this.leafFeedings = 0;
               this.ants.add(new TileEntityLeafcutterAnthill.Ant(new CompoundTag(), 0, 100, false));
            }
         }

         this.ants.add(new TileEntityLeafcutterAnthill.Ant(compoundnbt, p_226962_3_, p_226962_2_ ? 100 : 200, p_226962_1_.isQueen()));
         if (this.level != null) {
            BlockPos blockpos = this.getBlockPos();
            this.level.gameEvent(GameEvent.BLOCK_ACTIVATE, this.getBlockPos(), Context.of(this.getBlockState()));
            this.level.playSound(null, blockpos.getX(), blockpos.getY(), blockpos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         p_226962_1_.remove(RemovalReason.DISCARDED);
      }
   }

   private int getAntsInAreaCount(double size) {
      int ants = this.getAntCount();
      Vec3 vec = Vec3.atCenterOf(this.getBlockPos());
      AABB box = new AABB(vec.add(-size, -size, -size), vec.add(size, size, size));
      return ants + this.level.getEntitiesOfClass(EntityLeafcutterAnt.class, box).size();
   }

   public boolean hasQueen() {
      for (TileEntityLeafcutterAnthill.Ant ant : this.ants) {
         if (ant.queen) {
            return true;
         }
      }

      return false;
   }

   public void releaseQueens() {
      this.ants.removeIf(p_226966_4_ -> p_226966_4_.queen && this.addAntToWorld(this.getBlockState(), p_226966_4_, null, BeeReleaseStatus.BEE_RELEASED));
   }

   public void tryEnterHive(EntityLeafcutterAnt p_226961_1_, boolean p_226961_2_) {
      this.tryEnterHive(p_226961_1_, p_226961_2_, 0);
   }

   public int getAntCount() {
      return this.ants.size();
   }

   public void setChanged() {
      if (this.isNearFire()) {
         this.angerAnts(null, this.level.getBlockState(this.getBlockPos()), BeeReleaseStatus.EMERGENCY);
      }

      super.setChanged();
   }

   public boolean isNearFire() {
      if (this.level == null) {
         return false;
      } else {
         for (BlockPos blockpos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
            if (this.level.getBlockState(blockpos).getBlock() instanceof FireBlock) {
               return true;
            }
         }

         return false;
      }
   }

   public BlockState shrinkFungus() {
      BlockPos bottomChamber = this.getBlockPos().below();

      while (this.level.getBlockState(bottomChamber.below()).getBlock() == AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get() && bottomChamber.getY() > 0) {
         bottomChamber = bottomChamber.below();
      }

      BlockPos chamber = bottomChamber;
      if (!this.isUnfilledChamber(bottomChamber)) {
         BlockState prev = this.level.getBlockState(bottomChamber);
         if (prev.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())) {
            int fungalLevel = (Integer)prev.getValue(BlockLeafcutterAntChamber.FUNGUS);
            this.level
               .setBlockAndUpdate(
                  bottomChamber,
                  (BlockState)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER
                     .get()
                     .defaultBlockState()
                     .setValue(BlockLeafcutterAntChamber.FUNGUS, Math.min(0, fungalLevel - 1))
               );
            return prev;
         }
      } else {
         boolean flag = false;
         List<BlockPos> possibleChambers = new ArrayList<>();

         while (!flag) {
            for (BlockPos blockpos : BlockPos.betweenClosed(chamber.offset(-4, 0, -4), chamber.offset(4, 0, 4))) {
               if (this.isUnfilledChamber(blockpos)) {
                  possibleChambers.add(blockpos.immutable());
                  flag = true;
               }
            }

            if (!flag) {
               chamber = chamber.above();
               if (chamber.getY() > this.worldPosition.getY()) {
                  return null;
               }
            }
         }

         Collections.shuffle(possibleChambers);
         if (!possibleChambers.isEmpty()) {
            BlockPos newChamber = possibleChambers.get(0);
            if (newChamber != null && !this.isUnfilledChamber(newChamber)) {
               BlockState prev = this.level.getBlockState(newChamber);
               if (prev.is(AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get())) {
                  int fungalLevel = (Integer)prev.getValue(BlockLeafcutterAntChamber.FUNGUS);
                  this.level
                     .setBlockAndUpdate(
                        newChamber,
                        (BlockState)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER
                           .get()
                           .defaultBlockState()
                           .setValue(BlockLeafcutterAntChamber.FUNGUS, Math.min(fungalLevel - 1, 0))
                     );
                  return prev;
               }
            }
         }
      }

      return null;
   }

   public void growFungus() {
      if (!this.hasNoAnts()) {
         BlockPos bottomChamber = this.getBlockPos().below();

         while (this.level.getBlockState(bottomChamber.below()).getBlock() == AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get() && bottomChamber.getY() > 0) {
            bottomChamber = bottomChamber.below();
         }

         BlockPos chamber = bottomChamber;
         if (this.isUnfilledChamber(bottomChamber)) {
            int fungalLevel = (Integer)this.level.getBlockState(bottomChamber).getValue(BlockLeafcutterAntChamber.FUNGUS);
            int fungalLevel2 = Mth.clamp(fungalLevel + 1 + this.level.getRandom().nextInt(1), 0, 5);
            this.level
               .setBlockAndUpdate(
                  bottomChamber,
                  (BlockState)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get().defaultBlockState().setValue(BlockLeafcutterAntChamber.FUNGUS, fungalLevel2)
               );
         } else {
            boolean flag = false;
            List<BlockPos> possibleChambers = new ArrayList<>();

            while (!flag) {
               for (BlockPos blockpos : BlockPos.betweenClosed(chamber.offset(-4, 0, -4), chamber.offset(4, 0, 4))) {
                  if (this.isUnfilledChamber(blockpos)) {
                     possibleChambers.add(blockpos.immutable());
                     flag = true;
                  }
               }

               if (!flag) {
                  chamber = chamber.above();
                  if (chamber.getY() > this.worldPosition.getY()) {
                     return;
                  }
               }
            }

            Collections.shuffle(possibleChambers);
            if (!possibleChambers.isEmpty()) {
               BlockPos newChamber = possibleChambers.get(0);
               if (newChamber != null && this.isUnfilledChamber(newChamber)) {
                  int fungalLevel = (Integer)this.level.getBlockState(newChamber).getValue(BlockLeafcutterAntChamber.FUNGUS);
                  int fungalLevel2 = Mth.clamp(fungalLevel + 1 + this.level.getRandom().nextInt(1), 0, 5);
                  this.level
                     .setBlockAndUpdate(
                        newChamber,
                        (BlockState)AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get().defaultBlockState().setValue(BlockLeafcutterAntChamber.FUNGUS, fungalLevel2)
                     );
               }
            }
         }
      }
   }

   private boolean isUnfilledChamber(BlockPos pos) {
      return this.level.getBlockState(pos).getBlock() == AMBlockRegistry.LEAFCUTTER_ANT_CHAMBER.get()
         && (Integer)this.level.getBlockState(pos).getValue(BlockLeafcutterAntChamber.FUNGUS) < 5;
   }

   private void tickAnts() {
      Iterator<TileEntityLeafcutterAnthill.Ant> iterator = this.ants.iterator();
      BlockState blockstate = this.getBlockState();

      while (iterator.hasNext()) {
         TileEntityLeafcutterAnthill.Ant ant = iterator.next();
         if (ant.ticksInHive > ant.minOccupationTicks && !ant.queen) {
            BeeReleaseStatus beehivetileentity$state = AMCompat.getBoolean(ant.entityData, "HasNectar")
               ? BeeReleaseStatus.HONEY_DELIVERED
               : BeeReleaseStatus.BEE_RELEASED;
            if (this.addAntToWorld(blockstate, ant, null, beehivetileentity$state)) {
               iterator.remove();
            }
         }

         ant.ticksInHive++;
      }
   }

   protected void loadAdditional(CompoundTag nbt, Provider provider) {
      super.loadAdditional(nbt, provider);
      this.ants.clear();
      this.leafFeedings = AMCompat.getInt(nbt, "LeafFeedings");
      ListTag listnbt = AMCompat.getList(nbt, "Ants", 10);

      for (int i = 0; i < listnbt.size(); i++) {
         CompoundTag compoundnbt = AMCompat.getCompound(listnbt, i);
         TileEntityLeafcutterAnthill.Ant beehiveTileEntity$ant = new TileEntityLeafcutterAnthill.Ant(
            AMCompat.getCompound(compoundnbt, "EntityData"),
            AMCompat.getInt(compoundnbt, "TicksInHive"),
            AMCompat.getInt(compoundnbt, "MinOccupationTicks"),
            AMCompat.getBoolean(compoundnbt, "Queen")
         );
         this.ants.add(beehiveTileEntity$ant);
      }
   }

   public ListTag getAnts() {
      ListTag listnbt = new ListTag();

      for (TileEntityLeafcutterAnthill.Ant beehiveTileEntity$ant : this.ants) {
         beehiveTileEntity$ant.entityData.remove("UUID");
         CompoundTag compoundnbt = new CompoundTag();
         compoundnbt.put("EntityData", beehiveTileEntity$ant.entityData);
         compoundnbt.putInt("TicksInHive", beehiveTileEntity$ant.ticksInHive);
         compoundnbt.putInt("MinOccupationTicks", beehiveTileEntity$ant.minOccupationTicks);
         listnbt.add(compoundnbt);
      }

      return listnbt;
   }

   protected void saveAdditional(CompoundTag compound, Provider provider) {
      super.saveAdditional(compound, provider);
      AMCompat.put(compound, "Ants", this.getAnts());
      compound.putInt("LeafFeedings", this.leafFeedings);
   }

   static class Ant {
      private final CompoundTag entityData;
      private final int minOccupationTicks;
      private int ticksInHive;
      private final boolean queen;

      private Ant(CompoundTag p_i225767_1_, int p_i225767_2_, int p_i225767_3_, boolean queen) {
         p_i225767_1_.remove("UUID");
         this.entityData = p_i225767_1_;
         this.ticksInHive = p_i225767_2_;
         this.minOccupationTicks = p_i225767_3_;
         this.queen = queen;
      }
   }
}
