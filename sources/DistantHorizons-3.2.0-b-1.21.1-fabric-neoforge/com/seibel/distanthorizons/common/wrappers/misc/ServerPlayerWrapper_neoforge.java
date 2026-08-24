package com.seibel.distanthorizons.common.wrappers.misc;

import com.google.common.base.Objects;
import com.google.common.collect.MapMaker;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_neoforge;
import com.seibel.distanthorizons.core.util.math.DhVec3d;
import com.seibel.distanthorizons.core.wrapperInterfaces.misc.IServerPlayerWrapper;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IServerLevelWrapper;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;

public class ServerPlayerWrapper_neoforge implements IServerPlayerWrapper {
   private static final ConcurrentMap<ServerGamePacketListenerImpl, ServerPlayerWrapper_neoforge> serverPlayerWrapperMap = new MapMaker()
      .weakKeys()
      .weakValues()
      .makeMap();
   private final ServerGamePacketListenerImpl connection;

   public static ServerPlayerWrapper_neoforge getWrapper(ServerPlayer serverPlayer) {
      return serverPlayerWrapperMap.computeIfAbsent(serverPlayer.connection, ignored -> new ServerPlayerWrapper_neoforge(serverPlayer.connection));
   }

   private ServerPlayerWrapper_neoforge(ServerGamePacketListenerImpl connection) {
      this.connection = connection;
   }

   private ServerPlayer getServerPlayer() {
      return this.connection.player;
   }

   @Override
   public String getName() {
      return this.getServerPlayer().getName().getString();
   }

   @Override
   public IServerLevelWrapper getLevel() {
      ServerLevel level = ((IMixinServerPlayer_neoforge)this.getServerPlayer()).distantHorizons$getDimensionChangeDestination();
      if (level == null) {
         level = this.getServerPlayer().serverLevel();
      }

      return ServerLevelWrapper_neoforge.getWrapper(level);
   }

   @Override
   public DhVec3d getPosition() {
      Vec3 position = this.getServerPlayer().position();
      return new DhVec3d(position.x, position.y, position.z);
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
         return !(obj instanceof ServerPlayerWrapper_neoforge that) ? false : Objects.equal(this.connection, that.connection);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(new Object[]{this.connection});
   }
}
