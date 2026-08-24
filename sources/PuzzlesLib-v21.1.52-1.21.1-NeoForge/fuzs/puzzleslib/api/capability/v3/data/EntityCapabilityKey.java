package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.capability.ClientboundEntityCapabilityMessage;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface EntityCapabilityKey<T extends Entity, C extends CapabilityComponent<T>> extends CapabilityKey<T, C> {
   SyncStrategy getSyncStrategy();

   CopyStrategy getCopyStrategy();

   @Override
   default void setChanged(C capabilityComponent, @Nullable PlayerSet playerSet) {
      if (capabilityComponent.getHolder().level().isClientSide) {
         playerSet = PlayerSet.ofNone();
      } else if (playerSet == null) {
         playerSet = this.getSyncStrategy().getPlayerSet(capabilityComponent.getHolder());
      }

      PuzzlesLibMod.NETWORK.sendMessage(playerSet, this.toPacket(capabilityComponent));
   }

   default ClientboundEntityCapabilityMessage toPacket(C capabilityComponent) {
      return new ClientboundEntityCapabilityMessage(
         this.identifier(), capabilityComponent.getHolder().getId(), capabilityComponent.toCompoundTag(capabilityComponent.getHolder().registryAccess())
      );
   }

   public interface Mutable<T extends Entity, C extends CapabilityComponent<T>> extends EntityCapabilityKey<T, C> {
      EntityCapabilityKey.Mutable<T, C> setSyncStrategy(SyncStrategy var1);

      EntityCapabilityKey.Mutable<T, C> setCopyStrategy(CopyStrategy var1);
   }
}
