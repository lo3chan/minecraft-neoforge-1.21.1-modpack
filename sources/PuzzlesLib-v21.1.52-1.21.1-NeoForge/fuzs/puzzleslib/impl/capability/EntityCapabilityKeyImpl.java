package fuzs.puzzleslib.impl.capability;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v3.data.CopyStrategy;
import fuzs.puzzleslib.api.capability.v3.data.EntityCapabilityKey;
import fuzs.puzzleslib.api.capability.v3.data.SyncStrategy;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingConversionCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.AfterChangeDimensionCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerCopyEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerNetworkEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTrackingEvents;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import java.util.Optional;
import net.minecraft.world.entity.Entity;

public interface EntityCapabilityKeyImpl<T extends Entity, C extends CapabilityComponent<T>> extends EntityCapabilityKey.Mutable<T, C> {
   @Override
   default EntityCapabilityKey.Mutable<T, C> setSyncStrategy(SyncStrategy syncStrategy) {
      if (this.getSyncStrategy() != SyncStrategy.MANUAL) {
         throw new IllegalStateException("Sync strategy has already been set!");
      } else {
         if (syncStrategy != SyncStrategy.MANUAL) {
            PlayerNetworkEvents.LOGGED_IN
               .register(
                  serverPlayer -> this.getIfProvided(serverPlayer)
                     .ifPresent(capabilityComponent -> capabilityComponent.setChanged(PlayerSet.ofPlayer(serverPlayer)))
               );
            AfterChangeDimensionCallback.EVENT
               .register(
                  (serverPlayer, from, to) -> this.getIfProvided(serverPlayer)
                     .ifPresent(capabilityComponent -> capabilityComponent.setChanged(PlayerSet.ofPlayer(serverPlayer)))
               );
            PlayerCopyEvents.RESPAWN
               .register(
                  (serverPlayer, originalStillAlive) -> this.getIfProvided(serverPlayer)
                     .ifPresent(capabilityComponent -> capabilityComponent.setChanged(PlayerSet.ofPlayer(serverPlayer)))
               );
            if (syncStrategy == SyncStrategy.TRACKING) {
               PlayerTrackingEvents.START
                  .register(
                     (trackedEntity, serverPlayer) -> this.getIfProvided(trackedEntity)
                        .ifPresent(capabilityComponent -> capabilityComponent.setChanged(PlayerSet.ofPlayer(serverPlayer)))
                  );
            }
         }

         return this;
      }
   }

   @Override
   default EntityCapabilityKey.Mutable<T, C> setCopyStrategy(CopyStrategy copyStrategy) {
      if (this.getCopyStrategy() != CopyStrategy.NEVER) {
         throw new IllegalStateException("Copy strategy has already been set!");
      } else {
         if (copyStrategy == CopyStrategy.ALWAYS) {
            LivingConversionCallback.EVENT.register((originalEntity, newEntity) -> {
               Optional<C> originalCapability = this.getIfProvided(originalEntity);
               Optional<C> newCapability = this.getIfProvided(newEntity);
               if (originalCapability.isPresent() && newCapability.isPresent()) {
                  this.getCopyStrategy().copy(originalEntity, originalCapability.get(), newEntity, newCapability.get(), false);
               }
            });
         }

         return this;
      }
   }

   default void initialize() {
      PlayerCopyEvents.COPY.register((originalPlayer, newPlayer, originalStillAlive) -> {
         Optional<C> originalCapability = this.getIfProvided(originalPlayer);
         Optional<C> newCapability = this.getIfProvided(newPlayer);
         if (originalCapability.isPresent() && newCapability.isPresent()) {
            this.getCopyStrategy().copy(originalPlayer, originalCapability.get(), newPlayer, newCapability.get(), originalStillAlive);
         }
      });
   }
}
