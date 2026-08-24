package com.seibel.distanthorizons.common.wrappers.level;

import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper_neoforge;
import com.seibel.distanthorizons.core.level.IKeyedClientLevelManager;
import com.seibel.distanthorizons.core.level.IServerKeyedClientLevel;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;

public class KeyedClientLevelManager_neoforge implements IKeyedClientLevelManager {
   private final Map<String, KeyedClientLevelManager$KeyInfo_neoforge> keysByDimensionName = new ConcurrentHashMap<>();
   private final Map<ClientLevel, IServerKeyedClientLevel> keyedLevelsCache = Collections.synchronizedMap(new WeakHashMap<>());
   private volatile boolean enabled = false;

   @Nullable
   @Override
   public IServerKeyedClientLevel getServerKeyedLevel(IClientLevelWrapper levelWrapper) {
      if (levelWrapper == null) {
         return null;
      } else {
         synchronized (this.keyedLevelsCache) {
            ClientLevel level = (ClientLevel)levelWrapper.getWrappedMcObject();
            IServerKeyedClientLevel cached = this.keyedLevelsCache.get(level);
            if (cached != null) {
               return cached;
            } else {
               IClientLevelWrapper wrappedLevel = ClientLevelWrapper_neoforge.getWrapper(level, true);
               if (wrappedLevel == null) {
                  return null;
               } else {
                  String dimensionName = wrappedLevel.getDimensionName();
                  KeyedClientLevelManager$KeyInfo_neoforge info = this.keysByDimensionName.get(dimensionName);
                  if (info == null) {
                     return null;
                  } else {
                     IServerKeyedClientLevel keyedLevel = new ServerKeyedClientLevelWrapper_neoforge(level, info.serverKey, info.levelKey);
                     this.keyedLevelsCache.put(level, keyedLevel);
                     return keyedLevel;
                  }
               }
            }
         }
      }
   }

   @Override
   public IServerKeyedClientLevel setServerKeyedLevel(IClientLevelWrapper clientLevel, String dimensionResource, String serverKey, String levelKey) {
      this.keysByDimensionName.put(dimensionResource, new KeyedClientLevelManager$KeyInfo_neoforge(serverKey, levelKey));
      this.enabled = true;
      synchronized (this.keyedLevelsCache) {
         this.keyedLevelsCache.keySet().removeIf(level -> {
            String levelDim = level.dimension().location().toString();
            return levelDim.equals(dimensionResource);
         });
      }

      return clientLevel != null && clientLevel.getDimensionName().equals(dimensionResource) ? this.getServerKeyedLevel(clientLevel) : null;
   }

   @Override
   public void clearKeyedLevel() {
      synchronized (this.keyedLevelsCache) {
         this.keyedLevelsCache.clear();
         this.keysByDimensionName.clear();
      }
   }

   @Override
   public boolean isEnabled() {
      return this.enabled;
   }

   @Override
   public void disable() {
      this.enabled = false;
      this.clearKeyedLevel();
   }
}
