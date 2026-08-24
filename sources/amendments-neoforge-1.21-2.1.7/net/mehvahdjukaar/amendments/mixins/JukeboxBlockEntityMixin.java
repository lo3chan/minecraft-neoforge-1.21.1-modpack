package net.mehvahdjukaar.amendments.mixins;

import net.mehvahdjukaar.amendments.common.IBetterJukebox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ContainerSingleItem.BlockContainerSingleItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({JukeboxBlockEntity.class})
public abstract class JukeboxBlockEntityMixin extends BlockEntity implements IBetterJukebox, BlockContainerSingleItem {
   @Shadow
   @Final
   private JukeboxSongPlayer jukeboxSongPlayer;
   @Unique
   private float amendments$rot = 0.0F;
   @Unique
   private float amendments$prevRot = 0.0F;

   protected JukeboxBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
      super(blockEntityType, blockPos, blockState);
   }

   @Inject(
      method = {"onSongChanged"},
      at = {@At("TAIL")}
   )
   public void amendments$notifySongChanged(CallbackInfo ci) {
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private static void amendments$tickAnimation(Level level, BlockPos pos, BlockState state, JukeboxBlockEntity jukebox, CallbackInfo ci) {
      if (level.isClientSide) {
         ((IBetterJukebox)jukebox).amendments$tickAnimation();
      }
   }

   @Override
   public void amendments$tickAnimation() {
      this.amendments$prevRot = this.amendments$rot;
      if (this.jukeboxSongPlayer.isPlaying()) {
         this.amendments$rot++;
         this.amendments$rot %= 360.0F;
      } else if (this.amendments$rot > 0.0) {
         this.amendments$rot -= 5.0F;
         if (this.amendments$rot < 0.0F) {
            this.amendments$rot = 0.0F;
         }

         this.amendments$rot %= 360.0F;
      }
   }

   public CompoundTag getUpdateTag(Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public float amendments$getRotation(float partialTicks) {
      return Mth.rotLerp(partialTicks, this.amendments$prevRot, this.amendments$rot);
   }
}
