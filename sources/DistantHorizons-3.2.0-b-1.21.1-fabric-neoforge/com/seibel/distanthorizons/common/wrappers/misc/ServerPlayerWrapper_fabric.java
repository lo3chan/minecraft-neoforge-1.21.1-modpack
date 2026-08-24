package com.seibel.distanthorizons.common.wrappers.misc;

import com.google.common.base.Objects;
import com.google.common.collect.MapMaker;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_fabric;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3244;

public class ServerPlayerWrapper_fabric implements IServerPlayerWrapper {
   private static final ConcurrentMap<class_3244, ServerPlayerWrapper_fabric> serverPlayerWrapperMap = new MapMaker().weakKeys().weakValues().makeMap();
   private final class_3244 connection;

   public static ServerPlayerWrapper_fabric getWrapper(class_3222 serverPlayer) {
      return serverPlayerWrapperMap.computeIfAbsent(serverPlayer.field_13987, ignored -> new ServerPlayerWrapper_fabric(serverPlayer.field_13987));
   }

   private ServerPlayerWrapper_fabric(class_3244 connection) {
      this.connection = connection;
   }

   private class_3222 getServerPlayer() {
      return this.connection.field_14140;
   }

   @Override
   public String getName() {
      return this.getServerPlayer().method_5477().getString();
   }

   @Override
   public IServerLevelWrapper getLevel() {
      class_3218 level = ((IMixinServerPlayer_fabric)this.getServerPlayer()).distantHorizons$getDimensionChangeDestination();
      if (level == null) {
         level = this.getServerPlayer().method_51469();
      }

      return ServerLevelWrapper_fabric.getWrapper(level);
   }

   @Override
   public DhVec3d getPosition() {
      class_243 position = this.getServerPlayer().method_19538();
      return new DhVec3d(position.field_1352, position.field_1351, position.field_1350);
   }

   @Override
   public Object getWrappedMcObject() {
      return this.getServerPlayer();
   }

   @Override
   public String toString() {
      return "Wrapped{" + this.getServerPlayer() + "}";
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return !(obj instanceof ServerPlayerWrapper_fabric that) ? false : Objects.equal(this.connection, that.connection);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(new Object[]{this.connection});
   }
}
