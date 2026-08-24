package top.theillusivec4.curios.common.network.client;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.common.network.server.SPacketBreak;
import top.theillusivec4.curios.common.network.server.SPacketGrabbedItem;
import top.theillusivec4.curios.common.network.server.SPacketPage;
import top.theillusivec4.curios.common.network.server.SPacketQuickMove;
import top.theillusivec4.curios.common.network.server.SPacketSetIcons;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncActiveState;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncCurios;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncData;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncModifiers;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncRender;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncStack;

public class CuriosClientPayloadHandler {
   private static final CuriosClientPayloadHandler INSTANCE = new CuriosClientPayloadHandler();

   public static CuriosClientPayloadHandler getInstance() {
      return INSTANCE;
   }

   private static void handle(IPayloadContext ctx, Runnable handler) {
      ctx.enqueueWork(handler).exceptionally(e -> {
         ctx.disconnect(Component.translatable("curios.networking.failed"));
         return null;
      });
   }

   public void handle(SPacketSetIcons data, IPayloadContext ctx) {
      handle(ctx, () -> CuriosClientPackets.handle(data));
   }

   public void handle(SPacketQuickMove data, IPayloadContext ctx) {
      handle(ctx, () -> CuriosClientPackets.handle(data));
   }

   public void handle(SPacketPage data, IPayloadContext ctx) {
      handle(ctx, () -> CuriosClientPackets.handle(data));
   }

   public void handle(SPacketBreak data, IPayloadContext ctx) {
      handle(ctx, () -> CuriosClientPackets.handle(data));
   }

   public void handle(SPacketSyncRender data, IPayloadContext ctx) {
      handle(ctx, () -> CuriosClientPackets.handle(data));
   }

   public void handle(SPacketSyncModifiers data, IPayloadContext ctx) {
      handle(ctx, () -> CuriosClientPackets.handle(data));
   }

   public void handle(SPacketSyncData data, IPayloadContext ctx) {
      handle(ctx, () -> CuriosClientPackets.handle(data));
   }

   public void handle(SPacketSyncCurios data, IPayloadContext ctx) {
      handle(ctx, () -> CuriosClientPackets.handle(data));
   }

   public void handle(SPacketGrabbedItem data, IPayloadContext ctx) {
      handle(ctx, () -> CuriosClientPackets.handle(data));
   }

   public void handle(SPacketSyncStack data, IPayloadContext ctx) {
      handle(ctx, () -> CuriosClientPackets.handle(data));
   }

   public void handle(SPacketSyncActiveState data, IPayloadContext ctx) {
      handle(ctx, () -> CuriosClientPackets.handle(data));
   }
}
