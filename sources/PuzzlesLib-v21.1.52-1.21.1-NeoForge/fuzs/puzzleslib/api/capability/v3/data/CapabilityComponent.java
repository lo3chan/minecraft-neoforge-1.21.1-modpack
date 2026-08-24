package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.core.v1.utility.NbtSerializable;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import java.util.Objects;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public abstract class CapabilityComponent<T> implements NbtSerializable {
   private boolean initialized;
   private CapabilityKey<T, CapabilityComponent<T>> capabilityKey;
   private T holder;

   protected final T getHolder() {
      Objects.requireNonNull(this.holder, "holder is null");
      return this.holder;
   }

   @Internal
   public final void initialize(CapabilityKey<T, CapabilityComponent<T>> capabilityKey, T holder) {
      if (!this.initialized) {
         this.initialized = true;
         Objects.requireNonNull(capabilityKey, "capability key is null");
         this.capabilityKey = capabilityKey;
         Objects.requireNonNull(holder, "capability holder is null");
         this.holder = holder;
         this.initialize();
      }
   }

   protected void initialize() {
   }

   public final void setChanged() {
      this.setChanged(null);
   }

   @MustBeInvokedByOverriders
   public void setChanged(@Nullable PlayerSet playerSet) {
      this.capabilityKey.setChanged(this, playerSet);
   }

   @Override
   public void write(CompoundTag compoundTag, Provider registries) {
   }

   @Override
   public void read(CompoundTag compoundTag, Provider registries) {
   }
}
