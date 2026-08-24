package top.theillusivec4.curios.common.network;

import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.theillusivec4.curios.common.network.client.CPacketDestroy;
import top.theillusivec4.curios.common.network.client.CPacketOpenCurios;
import top.theillusivec4.curios.common.network.client.CPacketOpenVanilla;
import top.theillusivec4.curios.common.network.client.CPacketPage;
import top.theillusivec4.curios.common.network.client.CPacketToggleCosmetics;
import top.theillusivec4.curios.common.network.client.CPacketToggleRender;
import top.theillusivec4.curios.common.network.client.CuriosClientPayloadHandler;
import top.theillusivec4.curios.common.network.server.CuriosServerPayloadHandler;
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

public class NetworkHandler {
   public static void register(PayloadRegistrar registrar) {
      registrar.playToServer(CPacketDestroy.TYPE, CPacketDestroy.STREAM_CODEC, CuriosServerPayloadHandler.getInstance()::handleDestroyPacket);
      registrar.playToServer(CPacketOpenCurios.TYPE, CPacketOpenCurios.STREAM_CODEC, CuriosServerPayloadHandler.getInstance()::handleOpenCurios);
      registrar.playToServer(CPacketOpenVanilla.TYPE, CPacketOpenVanilla.STREAM_CODEC, CuriosServerPayloadHandler.getInstance()::handleOpenVanilla);
      registrar.playToServer(CPacketPage.TYPE, CPacketPage.STREAM_CODEC, CuriosServerPayloadHandler.getInstance()::handlePage);
      registrar.playToServer(CPacketToggleRender.TYPE, CPacketToggleRender.STREAM_CODEC, CuriosServerPayloadHandler.getInstance()::handlerToggleRender);
      registrar.playToServer(CPacketToggleCosmetics.TYPE, CPacketToggleCosmetics.STREAM_CODEC, CuriosServerPayloadHandler.getInstance()::handlerToggleCosmetics);
      registrar.playToClient(SPacketSyncStack.TYPE, SPacketSyncStack.STREAM_CODEC, CuriosClientPayloadHandler.getInstance()::handle);
      registrar.playToClient(SPacketGrabbedItem.TYPE, SPacketGrabbedItem.STREAM_CODEC, CuriosClientPayloadHandler.getInstance()::handle);
      registrar.playToClient(SPacketSyncCurios.TYPE, SPacketSyncCurios.STREAM_CODEC, CuriosClientPayloadHandler.getInstance()::handle);
      registrar.playToClient(SPacketSyncData.TYPE, SPacketSyncData.STREAM_CODEC, CuriosClientPayloadHandler.getInstance()::handle);
      registrar.playToClient(SPacketSyncModifiers.TYPE, SPacketSyncModifiers.STREAM_CODEC, CuriosClientPayloadHandler.getInstance()::handle);
      registrar.playToClient(SPacketSyncRender.TYPE, SPacketSyncRender.STREAM_CODEC, CuriosClientPayloadHandler.getInstance()::handle);
      registrar.playToClient(SPacketSyncActiveState.TYPE, SPacketSyncActiveState.STREAM_CODEC, CuriosClientPayloadHandler.getInstance()::handle);
      registrar.playToClient(SPacketBreak.TYPE, SPacketBreak.STREAM_CODEC, CuriosClientPayloadHandler.getInstance()::handle);
      registrar.playToClient(SPacketPage.TYPE, SPacketPage.STREAM_CODEC, CuriosClientPayloadHandler.getInstance()::handle);
      registrar.playToClient(SPacketSetIcons.TYPE, SPacketSetIcons.STREAM_CODEC, CuriosClientPayloadHandler.getInstance()::handle);
      registrar.playToClient(SPacketQuickMove.TYPE, SPacketQuickMove.STREAM_CODEC, CuriosClientPayloadHandler.getInstance()::handle);
   }
}
