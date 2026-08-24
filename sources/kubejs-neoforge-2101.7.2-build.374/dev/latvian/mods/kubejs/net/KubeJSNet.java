package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   modid = "kubejs"
)
public interface KubeJSNet {
   Type<WebServerUpdateJSONPayload> WEB_SERVER_JSON_UPDATE = type("web_server_json_update");
   Type<WebServerUpdateNBTPayload> WEB_SERVER_NBT_UPDATE = type("web_server_nbt_update");
   Type<SendDataFromClientPayload> SEND_DATA_FROM_CLIENT = type("send_data_from_client");
   Type<SendDataFromServerPayload> SEND_DATA_FROM_SERVER = type("send_data_from_server");
   Type<AddStagePayload> ADD_STAGE = type("add_stage");
   Type<RemoveStagePayload> REMOVE_STAGE = type("remove_stage");
   Type<SyncStagesPayload> SYNC_STAGES = type("sync_stages");
   Type<FirstClickPayload> FIRST_CLICK = type("first_click");
   Type<NotificationPayload> NOTIFICATION = type("toast");
   Type<ReloadStartupScriptsPayload> RELOAD_STARTUP_SCRIPTS = type("reload_startup_scripts");
   Type<DisplayServerErrorsPayload> DISPLAY_SERVER_ERRORS = type("display_server_errors");
   Type<DisplayClientErrorsPayload> DISPLAY_CLIENT_ERRORS = type("display_client_errors");
   Type<SyncServerDataPayload> SYNC_SERVER_DATA = type("sync_server_data");
   Type<SetActivePostShaderPayload> SET_ACTIVE_POST_SHADER = type("set_active_post_shader");

   private static <T extends CustomPacketPayload> Type<T> type(String id) {
      return new Type(KubeJS.id(id));
   }

   @SubscribeEvent
   static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar reg = event.registrar("1").optional();
      reg.playToClient(WEB_SERVER_JSON_UPDATE, WebServerUpdateJSONPayload.STREAM_CODEC, WebServerUpdateJSONPayload::handle);
      reg.playToClient(WEB_SERVER_NBT_UPDATE, WebServerUpdateNBTPayload.STREAM_CODEC, WebServerUpdateNBTPayload::handle);
      reg.playToServer(SEND_DATA_FROM_CLIENT, SendDataFromClientPayload.STREAM_CODEC, SendDataFromClientPayload::handle);
      reg.playToClient(SEND_DATA_FROM_SERVER, SendDataFromServerPayload.STREAM_CODEC, SendDataFromServerPayload::handle);
      reg.playToClient(ADD_STAGE, AddStagePayload.STREAM_CODEC, AddStagePayload::handle);
      reg.playToClient(REMOVE_STAGE, RemoveStagePayload.STREAM_CODEC, RemoveStagePayload::handle);
      reg.playToClient(SYNC_STAGES, SyncStagesPayload.STREAM_CODEC, SyncStagesPayload::handle);
      reg.playToServer(FIRST_CLICK, FirstClickPayload.STREAM_CODEC, FirstClickPayload::handle);
      reg.playToClient(NOTIFICATION, NotificationPayload.STREAM_CODEC, NotificationPayload::handle);
      reg.playToClient(RELOAD_STARTUP_SCRIPTS, ReloadStartupScriptsPayload.STREAM_CODEC, ReloadStartupScriptsPayload::handle);
      reg.playToClient(DISPLAY_SERVER_ERRORS, DisplayServerErrorsPayload.STREAM_CODEC, DisplayServerErrorsPayload::handle);
      reg.playToClient(DISPLAY_CLIENT_ERRORS, DisplayClientErrorsPayload.STREAM_CODEC, DisplayClientErrorsPayload::handle);
      reg.playToClient(SYNC_SERVER_DATA, SyncServerDataPayload.STREAM_CODEC, SyncServerDataPayload::handle);
      reg.playToClient(SET_ACTIVE_POST_SHADER, SetActivePostShaderPayload.STREAM_CODEC, SetActivePostShaderPayload::handle);
      reg.playToServer(KubeJSNet.Kubedex.REQUEST_INVENTORY, RequestInventoryKubedexPayload.STREAM_CODEC, RequestInventoryKubedexPayload::handle);
      reg.playToServer(KubeJSNet.Kubedex.REQUEST_BLOCK, RequestBlockKubedexPayload.STREAM_CODEC, RequestBlockKubedexPayload::handle);
      reg.playToServer(KubeJSNet.Kubedex.REQUEST_ENTITY, RequestEntityKubedexPayload.STREAM_CODEC, RequestEntityKubedexPayload::handle);
   }

   static void safeSendToPlayer(ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... payloads) {
      if (!CommonProperties.get().serverOnly) {
         PacketDistributor.sendToPlayer(player, payload, payloads);
      }
   }

   static void sendToAllPlayers(CustomPacketPayload payload, CustomPacketPayload... payloads) {
      if (!CommonProperties.get().serverOnly) {
         PacketDistributor.sendToAllPlayers(payload, payloads);
      }
   }

   public interface Kubedex {
      Type<RequestInventoryKubedexPayload> REQUEST_INVENTORY = KubeJSNet.type("kubedex/request_inventory");
      Type<RequestBlockKubedexPayload> REQUEST_BLOCK = KubeJSNet.type("kubedex/request_block");
      Type<RequestEntityKubedexPayload> REQUEST_ENTITY = KubeJSNet.type("kubedex/request_entity");
   }
}
