package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface LevelCapabilityKey<C extends CapabilityComponent<Level>> extends CapabilityKey<Level, C> {
   @Override
   default void setChanged(C capabilityComponent, @Nullable PlayerSet playerSet) {
   }

   @Override
   default ClientboundMessage<?> toPacket(C capabilityComponent) {
      throw new UnsupportedOperationException();
   }
}
