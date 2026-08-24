package net.cibernet.alchemancy.blocks.blockentities;

import net.cibernet.alchemancy.registries.AlchemancyBlockEntities;
import net.cibernet.alchemancy.registries.AlchemancySoundEvents;
import net.cibernet.alchemancy.util.ColorUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AlchemancyCatalystBlockEntity extends BlockEntity {
   private float spinOffset = 0.0F;
   private String crystalTexture = DyeColor.LIME.getName();
   private int[] tint = null;
   private float rotationTime = 0.0F;
   private float prevRotationTime = 0.0F;
   private int animationTicks = 0;
   public boolean silent = false;
   private final int ANIMATION_LENGTH = 10;

   public AlchemancyCatalystBlockEntity(BlockPos pos, BlockState blockState) {
      super((BlockEntityType)AlchemancyBlockEntities.ALCHEMANCY_CATALYST.get(), pos, blockState);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      if (tag.contains("spin_offset", 5)) {
         this.spinOffset = tag.getFloat("spin_offset");
      }

      if (tag.contains("crystal_texture", 8)) {
         this.setCrystalTexture(tag.getString("crystal_texture"));
      }

      if (tag.contains("tint", 11)) {
         this.setTint((int[])tag.getIntArray("tint").clone());
      } else if (tag.contains("tint", 3)) {
         this.setTint(tag.getInt("tint"));
      }

      this.animationTicks = tag.getInt("animation_ticks");
      this.silent = tag.getBoolean("silent");
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putFloat("spin_offset", this.spinOffset);
      tag.putString("crystal_texture", this.getCrystalTexture());
      if (this.tint != null) {
         tag.putIntArray("tint", (int[])this.tint.clone());
      }

      tag.putInt("animation_ticks", this.animationTicks);
      tag.putBoolean("silent", this.silent);
   }

   public float getSpinOffset() {
      return this.spinOffset;
   }

   public void randomizeSpinOffset(RandomSource randomSource) {
      this.spinOffset = randomSource.nextFloat() * 360.0F;
   }

   public int getTint() {
      return this.tint != null && this.tint.length != 0 ? ColorUtils.interpolateColorsOverTime(1.0F, this.tint) : -1;
   }

   public String getCrystalTexture() {
      return this.crystalTexture;
   }

   public void setTint(int... tint) {
      this.tint = tint;
      this.notifyColorUpdate();
   }

   public int[] getTintColors() {
      return this.tint;
   }

   public void setCrystalTexture(String crystalTexture) {
      this.crystalTexture = crystalTexture;
      this.notifyColorUpdate();
   }

   public void setCrystalTexture(DyeColor dyeColor) {
      this.setCrystalTexture(dyeColor.getName());
   }

   public void notifyColorUpdate() {
      if (this.hasLevel()) {
         this.level.markAndNotifyBlock(this.getBlockPos(), this.level.getChunkAt(this.getBlockPos()), this.getBlockState(), this.getBlockState(), 2, 1);
      }
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = new CompoundTag();
      this.saveAdditional(tag, registries);
      return tag;
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public static void clientTick(Level level, BlockPos pos, BlockState state, AlchemancyCatalystBlockEntity crystal) {
      crystal.prevRotationTime = crystal.rotationTime;
      crystal.rotationTime = crystal.rotationTime + crystal.getAnimationSpeed();
      crystal.animationTicks = Math.max(0, crystal.animationTicks - 1);
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, AlchemancyCatalystBlockEntity crystal) {
      crystal.animationTicks = Math.max(0, crystal.animationTicks - 1);
   }

   public void playAnimation(boolean startup) {
      this.animationTicks = 10;
      if (this.level != null) {
         this.level.markAndNotifyBlock(this.getBlockPos(), this.level.getChunkAt(this.getBlockPos()), this.getBlockState(), this.getBlockState(), 2, 1);
         if (!this.silent) {
            this.level
               .playSound(
                  null,
                  this.getBlockPos(),
                  startup
                     ? (SoundEvent)AlchemancySoundEvents.ALCHEMANCY_CRYSTAL_ACTIVATE.value()
                     : (SoundEvent)AlchemancySoundEvents.ALCHEMANCY_CRYSTAL_FIRE.value(),
                  SoundSource.BLOCKS,
                  1.0F,
                  1.0F
               );
         }
      }
   }

   public float getAnimationSpeed() {
      return Mth.lerp(this.getAnimationProgressLeft(0.0F), 0.05F, 0.5F);
   }

   @OnlyIn(Dist.CLIENT)
   public float getRotationTime(float partialTicks) {
      return Mth.lerp(partialTicks, this.prevRotationTime, this.rotationTime);
   }

   public float getAnimationProgressLeft(float partialTick) {
      return Math.max(0.0F, this.animationTicks - partialTick) / 10.0F;
   }
}
