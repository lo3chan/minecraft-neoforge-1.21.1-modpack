package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface BlockEntityCapabilityKey<T extends BlockEntity, C extends CapabilityComponent<T>> extends CapabilityKey<T, C> {
   @Override
   default void setChanged(C capabilityComponent, @Nullable PlayerSet playerSet) {
      capabilityComponent.getHolder().setChanged();
   }

   @Override
   default ClientboundMessage<?> toPacket(C capabilityComponent) {
      throw new UnsupportedOperationException();
   }
}
