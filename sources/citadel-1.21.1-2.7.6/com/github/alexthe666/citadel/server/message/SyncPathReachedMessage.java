package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.client.render.pathfinding.PathfindingDebugRenderer;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.MNode;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncPathReachedMessage implements CustomPacketPayload {
   public static final Type<SyncPathReachedMessage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("citadel", "sync_path_reached"));
   public static final StreamCodec<FriendlyByteBuf, SyncPathReachedMessage> CODEC = StreamCodec.ofMember(
      SyncPathReachedMessage::write, SyncPathReachedMessage::read
   );
   public Set<BlockPos> reached = new HashSet<>();

   public SyncPathReachedMessage(Set<BlockPos> reached) {
      this.reached = reached;
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeInt(this.reached.size());

      for (BlockPos node : this.reached) {
         buf.writeBlockPos(node);
      }
   }

   public static SyncPathReachedMessage read(FriendlyByteBuf buf) {
      int size = buf.readInt();
      Set<BlockPos> reached = new HashSet<>();

      for (int i = 0; i < size; i++) {
         reached.add(buf.readBlockPos());
      }

      return new SyncPathReachedMessage(reached);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(SyncPathReachedMessage message, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.flow().isClientbound()) {
            for (MNode node : PathfindingDebugRenderer.lastDebugNodesPath) {
               if (message.reached.contains(node.pos)) {
                  node.setReachedByWorker(true);
               }
            }
         }
      });
   }
}
