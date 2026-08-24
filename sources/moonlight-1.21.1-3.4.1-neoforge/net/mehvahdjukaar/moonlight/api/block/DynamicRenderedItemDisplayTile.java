package net.mehvahdjukaar.moonlight.api.block;

import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.mehvahdjukaar.moonlight.api.client.model.IExtraModelDataProvider;
import net.mehvahdjukaar.moonlight.api.client.model.ModelDataKey;
import net.mehvahdjukaar.moonlight.api.client.util.LOD;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class DynamicRenderedItemDisplayTile extends ItemDisplayTile implements IExtraModelDataProvider {
   public static final ModelDataKey<Boolean> IS_FANCY = DynamicRenderedBlockTile.IS_FANCY;
   private boolean isFancy = false;
   private int extraFancyTicks = 0;

   protected DynamicRenderedItemDisplayTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int capacity) {
      super(tileEntityTypeIn, pos, state, capacity);
   }

   protected DynamicRenderedItemDisplayTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
      super(tileEntityTypeIn, pos, state);
   }

   @Override
   public void addExtraModelData(ExtraModelData.Builder builder) {
      builder.with(IS_FANCY, this.isFancy);
   }

   public abstract boolean isNeverFancy();

   public void onFancyChanged(boolean fancy) {
   }

   public boolean rendersFancy() {
      return this.isFancy;
   }

   @Deprecated(
      forRemoval = true
   )
   public boolean shouldRenderFancy(Vec3 cameraPos) {
      return this.shouldRenderFancy();
   }

   public boolean shouldRenderFancy() {
      if (this.isNeverFancy()) {
         return false;
      } else {
         boolean newFancyStatus = this.getFancyDistance();
         boolean oldStatus = this.isFancy;
         if (oldStatus != newFancyStatus) {
            this.isFancy = newFancyStatus;
            this.onFancyChanged(this.isFancy);
            if (this.level == Minecraft.getInstance().level) {
               this.requestModelReload();
               this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 8);
            }

            if (!this.isFancy) {
               this.extraFancyTicks = 4;
            }
         }

         if (this.extraFancyTicks > 0) {
            this.extraFancyTicks--;
            return true;
         } else {
            return this.isFancy;
         }
      }
   }

   protected boolean getFancyDistance() {
      LOD lod = LOD.at(this);
      return lod.isNear();
   }
}
