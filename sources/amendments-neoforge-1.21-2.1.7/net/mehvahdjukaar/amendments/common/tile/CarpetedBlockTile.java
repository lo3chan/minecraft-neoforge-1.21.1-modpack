package net.mehvahdjukaar.amendments.common.tile;

import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.Map;
import net.mehvahdjukaar.amendments.common.block.CarpetSlabBlock;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.block.MimicBlockTile;
import net.mehvahdjukaar.moonlight.api.client.model.ModelDataKey;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData.Builder;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CarpetedBlockTile extends MimicBlockTile {
   private static final Map<Pair<SoundType, SoundType>, SoundType> MIXED_SOUND_MAP = new HashMap<>();
   public static final ModelDataKey<BlockState> CARPET_KEY = new ModelDataKey(BlockState.class);
   private BlockState carpet = Blocks.WHITE_CARPET.defaultBlockState();
   private SoundType soundType = null;

   public CarpetedBlockTile(BlockPos pos, BlockState state) {
      super(ModRegistry.CARPET_STAIRS_TILE.get(), pos, state);
   }

   public void addExtraModelData(Builder builder) {
      super.addExtraModelData(builder);
      builder.with(CARPET_KEY, this.carpet);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      HolderGetter<Block> holderGetter = (HolderGetter<Block>)(this.level != null
         ? this.level.holderLookup(Registries.BLOCK)
         : BuiltInRegistries.BLOCK.asLookup());
      this.setCarpet(NbtUtils.readBlockState(holderGetter, tag.getCompound("Carpet")));
   }

   public void setCarpet(BlockState carpet) {
      this.setHeldBlock(carpet, 1);
   }

   public BlockState getCarpet() {
      return this.getHeldBlock(1);
   }

   public void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.put("Carpet", NbtUtils.writeBlockState(this.carpet));
   }

   public BlockState getHeldBlock(int index) {
      return index == 1 ? this.carpet : super.getHeldBlock(index);
   }

   public boolean setHeldBlock(BlockState state, int index) {
      if (index == 0) {
         this.mimic = state;
      } else if (index == 1) {
         this.carpet = state;
      }

      this.soundType = null;
      if (this.level instanceof ServerLevel) {
         this.setChanged();
         int newLight = Math.max(
            ForgeHelper.getLightEmission(this.getCarpet(), this.level, this.worldPosition),
            ForgeHelper.getLightEmission(this.getHeldBlock(), this.level, this.worldPosition)
         );
         this.level
            .setBlock(
               this.worldPosition,
               (BlockState)((BlockState)this.getBlockState().setValue(ModBlockProperties.LIGHT_LEVEL, newLight))
                  .setValue(CarpetSlabBlock.SOLID, this.getHeldBlock().canOcclude()),
               3
            );
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
      } else {
         this.requestModelReload();
      }

      return true;
   }

   public void initialize(BlockState stairs, BlockState carpet) {
      this.setHeldBlock(carpet, 1);
      this.setHeldBlock(stairs, 0);
   }

   @Nullable
   public SoundType getSoundType() {
      if (this.soundType == null) {
         BlockState stairs = this.getHeldBlock();
         BlockState carpet = this.getHeldBlock(1);
         if (stairs.isAir() || carpet.isAir()) {
            return null;
         }

         SoundType stairsSound = stairs.getSoundType();
         SoundType carpetSound = carpet.getSoundType();
         this.soundType = MIXED_SOUND_MAP.computeIfAbsent(
            Pair.of(stairsSound, carpetSound),
            p -> new SoundType(
               1.0F,
               1.0F,
               stairsSound.getBreakSound(),
               carpetSound.getStepSound(),
               stairsSound.getPlaceSound(),
               stairsSound.getHitSound(),
               carpetSound.getFallSound()
            )
         );
      }

      return this.soundType;
   }
}
