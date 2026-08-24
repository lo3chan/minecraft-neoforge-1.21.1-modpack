package com.github.alexthe666.citadel.server.message;

import com.github.alexthe666.citadel.client.render.pathfinding.PathfindingDebugRenderer;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.MNode;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncePathMessage implements CustomPacketPayload {
   public static final Type<SyncePathMessage> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("citadel", "sync_path"));
   public static final StreamCodec<FriendlyByteBuf, SyncePathMessage> CODEC = StreamCodec.ofMember(SyncePathMessage::write, SyncePathMessage::read);
   public Set<MNode> lastDebugNodesVisited = new HashSet<>();
   public Set<MNode> lastDebugNodesNotVisited = new HashSet<>();
   public Set<MNode> lastDebugNodesPath = new HashSet<>();

   public SyncePathMessage(Set<MNode> lastDebugNodesVisited, Set<MNode> lastDebugNodesNotVisited, Set<MNode> lastDebugNodesPath) {
      this.lastDebugNodesVisited = lastDebugNodesVisited;
      this.lastDebugNodesNotVisited = lastDebugNodesNotVisited;
      this.lastDebugNodesPath = lastDebugNodesPath;
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeInt(this.lastDebugNodesVisited.size());

      for (MNode MNode : this.lastDebugNodesVisited) {
         MNode.serializeToBuf(buf);
      }

      buf.writeInt(this.lastDebugNodesNotVisited.size());

      for (MNode MNode : this.lastDebugNodesNotVisited) {
         MNode.serializeToBuf(buf);
      }

      buf.writeInt(this.lastDebugNodesPath.size());

      for (MNode MNode : this.lastDebugNodesPath) {
         MNode.serializeToBuf(buf);
      }
   }

   public static SyncePathMessage read(FriendlyByteBuf buf) {
      int size = buf.readInt();
      Set<MNode> lastDebugNodesVisited = new HashSet<>();

      for (int i = 0; i < size; i++) {
         lastDebugNodesVisited.add(new MNode(buf));
      }

      size = buf.readInt();
      Set<MNode> lastDebugNodesNotVisited = new HashSet<>();

      for (int i = 0; i < size; i++) {
         lastDebugNodesNotVisited.add(new MNode(buf));
      }

      size = buf.readInt();
      Set<MNode> lastDebugNodesPath = new HashSet<>();

      for (int i = 0; i < size; i++) {
         lastDebugNodesPath.add(new MNode(buf));
      }

      return new SyncePathMessage(lastDebugNodesVisited, lastDebugNodesNotVisited, lastDebugNodesPath);
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(SyncePathMessage message, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.flow().isClientbound()) {
            PathfindingDebugRenderer.lastDebugNodesVisited = message.lastDebugNodesVisited;
            PathfindingDebugRenderer.lastDebugNodesNotVisited = message.lastDebugNodesNotVisited;
            PathfindingDebugRenderer.lastDebugNodesPath = message.lastDebugNodesPath;
         }
      });
   }
}
