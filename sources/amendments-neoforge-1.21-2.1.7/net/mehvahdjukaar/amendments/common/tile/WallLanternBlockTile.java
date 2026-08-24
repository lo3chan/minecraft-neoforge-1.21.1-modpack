package net.mehvahdjukaar.amendments.common.tile;

import java.util.Optional;
import net.mehvahdjukaar.amendments.common.LanternRegistry;
import net.mehvahdjukaar.amendments.common.block.WallLanternBlock;
import net.mehvahdjukaar.amendments.configs.ClientConfigs;
import net.mehvahdjukaar.amendments.integration.CompatHandler;
import net.mehvahdjukaar.amendments.integration.ThinAirCompat;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.block.IBlockHolder;
import net.mehvahdjukaar.moonlight.api.block.MimicBlockTile;
import net.mehvahdjukaar.moonlight.api.client.model.IExtraModelDataProvider;
import net.mehvahdjukaar.moonlight.api.client.model.ModelDataKey;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData.Builder;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class WallLanternBlockTile extends SwayingBlockTile implements IBlockHolder, IExtraModelDataProvider {
   public static final ModelDataKey<BlockState> MIMIC_KEY = MimicBlockTile.MIMIC_KEY;
   @Nullable
   private BlockState pendingLegacyLantern;
   private boolean pendingLegacyRedstone;

   public WallLanternBlockTile(BlockPos pos, BlockState state) {
      super(ModRegistry.WALL_LANTERN_TILE.get(), pos, state);
   }

   public boolean isNeverFancy() {
      return ClientConfigs.FAST_LANTERNS.get();
   }

   public boolean isRedstoneLantern() {
      return this.getOwnBlock().type.getId().toString().equals("charm:redstone_lantern");
   }

   public double getAttachmentOffset() {
      return this.getOwnBlock().type.attachmentOffset;
   }

   public WallLanternBlock getOwnBlock() {
      return (WallLanternBlock)this.getBlockState().getBlock();
   }

   public BlockState getLanternState() {
      return this.getOwnBlock().getLanternState(this.getBlockState());
   }

   public BlockState getHeldBlock(int index) {
      return this.getLanternState();
   }

   public boolean setHeldBlock(BlockState state, int index) {
      return false;
   }

   @Override
   public Vector3f getRotationAxis(BlockState state) {
      return ((Direction)state.getValue(WallLanternBlock.FACING)).step();
   }

   public void addExtraModelData(Builder builder) {
      super.addExtraModelData(builder);
      builder.with(MIMIC_KEY, this.getLanternState());
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (tag.contains("Lantern")) {
         this.pendingLegacyLantern = Utils.readBlockState(tag.getCompound("Lantern"), this.level);
         this.pendingLegacyRedstone = tag.getBoolean("IsRedstone");
         this.tryMigrateLegacyLantern();
      }
   }

   private void tryMigrateLegacyLantern() {
      if (this.pendingLegacyLantern != null && this.level != null && !this.level.isClientSide) {
         BlockState legacyLantern = this.pendingLegacyLantern;
         boolean legacyRedstone = this.pendingLegacyRedstone;
         this.pendingLegacyLantern = null;
         if (!legacyLantern.isAir()) {
            Optional<LanternRegistry.LanternType> type = LanternRegistry.INSTANCE
               .detectTypeFromBlock(legacyLantern.getBlock(), Utils.getID(legacyLantern.getBlock()));
            if (!type.isEmpty()) {
               WallLanternBlock targetWall = ModRegistry.WALL_LANTERNS.get(type.get());
               if (targetWall != null) {
                  WallLanternBlock currentWall = this.getOwnBlock();
                  BlockState wallState = this.getBlockState();
                  if (currentWall.type != targetWall.type) {
                     wallState = copyWallState(wallState, targetWall.defaultBlockState());
                  }

                  wallState = this.applyLegacyLanternState(wallState, legacyLantern, legacyRedstone, targetWall.type);
                  if (wallState != this.getBlockState()) {
                     this.level.setBlock(this.worldPosition, wallState, 3);
                  }

                  if (CompatHandler.THIN_AIR && ThinAirCompat.isAirLantern(legacyLantern)) {
                     this.updateThinAir(legacyLantern);
                  }

                  this.setChanged();
               }
            }
         }
      }
   }

   private static BlockState copyWallState(BlockState from, BlockState to) {
      return (BlockState)((BlockState)((BlockState)to.setValue(WallLanternBlock.FACING, (Direction)from.getValue(WallLanternBlock.FACING)))
            .setValue(WallLanternBlock.ATTACHMENT, (ModBlockProperties.BlockAttachment)from.getValue(WallLanternBlock.ATTACHMENT)))
         .setValue(WallLanternBlock.WATERLOGGED, (Boolean)from.getValue(WallLanternBlock.WATERLOGGED));
   }

   private BlockState applyLegacyLanternState(BlockState wallState, BlockState legacyLantern, boolean legacyRedstone, LanternRegistry.LanternType type) {
      int light = ForgeHelper.getLightEmission(legacyLantern, this.level, this.worldPosition);
      boolean lit = true;
      if (legacyRedstone || type.getId().toString().equals("charm:redstone_lantern")) {
         lit = legacyLantern.hasProperty(WallLanternBlock.LIT) && (Boolean)legacyLantern.getValue(WallLanternBlock.LIT);
         light = 15;
      } else if (legacyLantern.hasProperty(WallLanternBlock.LIT)) {
         lit = (Boolean)legacyLantern.getValue(WallLanternBlock.LIT);
      }

      if (light == 0) {
         lit = false;
      }

      return (BlockState)((BlockState)wallState.setValue(WallLanternBlock.LIT, lit)).setValue(WallLanternBlock.LIGHT_LEVEL, Math.max(light, 5));
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
   }

   public void updateThinAir(BlockState lantern) {
      if (CompatHandler.THIN_AIR && this.level != null && ThinAirCompat.isAirLantern(lantern)) {
         BlockState newState = ThinAirCompat.maybeSetAirQuality(lantern, Vec3.atCenterOf(this.worldPosition), this.level);
         if (newState != null) {
            this.level.scheduleTick(this.worldPosition, this.getBlockState().getBlock(), 20, TickPriority.NORMAL);
         }
      }
   }
}
