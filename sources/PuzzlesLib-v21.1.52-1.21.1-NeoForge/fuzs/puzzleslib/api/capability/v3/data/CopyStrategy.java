package fuzs.puzzleslib.api.capability.v3.data;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.entity.Entity;

public enum CopyStrategy {
   ALWAYS {
      @Override
      public void copy(
         Entity oldEntity, CapabilityComponent<?> oldCapability, Entity newEntity, CapabilityComponent<?> newCapability, boolean originalStillAlive
      ) {
         this.copy(newEntity.registryAccess(), oldCapability, newCapability);
      }
   },
   NEVER {
      @Override
      public void copy(
         Entity oldEntity, CapabilityComponent<?> oldCapability, Entity newEntity, CapabilityComponent<?> newCapability, boolean originalStillAlive
      ) {
         if (originalStillAlive) {
            this.copy(newEntity.registryAccess(), oldCapability, newCapability);
         }
      }
   },
   @Deprecated
   KEEP_PLAYER_INVENTORY {
      @Override
      public void copy(
         Entity oldEntity, CapabilityComponent<?> oldCapability, Entity newEntity, CapabilityComponent<?> newCapability, boolean originalStillAlive
      ) {
         NEVER.copy(oldEntity, oldCapability, newEntity, newCapability, originalStillAlive);
      }
   };

   public abstract void copy(Entity var1, CapabilityComponent<?> var2, Entity var3, CapabilityComponent<?> var4, boolean var5);

   void copy(Provider registries, CapabilityComponent<?> oldCapability, CapabilityComponent<?> newCapability) {
      newCapability.read(oldCapability.toCompoundTag(registries), registries);
   }
}
