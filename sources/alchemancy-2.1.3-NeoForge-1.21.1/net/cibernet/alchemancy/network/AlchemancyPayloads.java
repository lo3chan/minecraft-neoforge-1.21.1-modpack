package net.cibernet.alchemancy.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class AlchemancyPayloads {
   @SubscribeEvent
   public static void registerPayloads(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("2.1");
      registrar.playToClient(S2CPlayFireworksPayload.TYPE, S2CPlayFireworksPayload.STREAM_CODEC, S2CPlayFireworksPayload::handleDataOnMain);
      registrar.playToClient(
         S2CPlayGustBasketEffectsPayload.TYPE, S2CPlayGustBasketEffectsPayload.STREAM_CODEC, S2CPlayGustBasketEffectsPayload::handleDataOnMain
      );
      registrar.playToClient(S2CAddPlayerMovementPayload.TYPE, S2CAddPlayerMovementPayload.STREAM_CODEC, S2CAddPlayerMovementPayload::handleDataOnMain);
      registrar.playToClient(S2CUnlockCodexEntriesPayload.TYPE, S2CUnlockCodexEntriesPayload.STREAM_CODEC, S2CUnlockCodexEntriesPayload::handleDataOnMain);
      registrar.playToClient(
         S2CDiscoverCodexIngredientsPayload.TYPE, S2CDiscoverCodexIngredientsPayload.STREAM_CODEC, S2CDiscoverCodexIngredientsPayload::handleDataOnMain
      );
      registrar.playToClient(S2CEntitySyncTintColorPayload.TYPE, S2CEntitySyncTintColorPayload.STREAM_CODEC, S2CEntitySyncTintColorPayload::handleDataOnMain);
      registrar.playToClient(S2CInventoryTickPayload.TYPE, S2CInventoryTickPayload.STREAM_CODEC, S2CInventoryTickPayload::handleDataOnMain);
      registrar.playToClient(S2CDeathWardEffectsPayload.TYPE, S2CDeathWardEffectsPayload.STREAM_CODEC, S2CDeathWardEffectsPayload::handleDataOnMain);
      registrar.playToClient(S2CRidePlayerPayload.TYPE, S2CRidePlayerPayload.STREAM_CODEC, S2CRidePlayerPayload::handleDataOnMain);
      registrar.playToClient(S2CTickGlobalTimerPayload.TYPE, S2CTickGlobalTimerPayload.STREAM_CODEC, S2CTickGlobalTimerPayload::handleDataOnMain);
      registrar.playToClient(S2CSetGlobalTimerPayload.TYPE, S2CSetGlobalTimerPayload.STREAM_CODEC, S2CSetGlobalTimerPayload::handleDataOnMain);
      registrar.playToClient(S2CTriggerEchoPacket.TYPE, S2CTriggerEchoPacket.STREAM_CODEC, S2CTriggerEchoPacket::handleDataOnMain);
      registrar.playToClient(S2CRotatePlayerPayload.TYPE, S2CRotatePlayerPayload.STREAM_CODEC, S2CRotatePlayerPayload::handleDataOnMain);
      registrar.playToServer(C2SChromatizePayload.TYPE, C2SChromatizePayload.STREAM_CODEC, C2SChromatizePayload::handleDataOnMain);
      registrar.playToServer(C2SResetItemTintPayload.TYPE, C2SResetItemTintPayload.STREAM_CODEC, C2SResetItemTintPayload::handleDataOnMain);
      registrar.playToServer(C2SDispenseFromItemPayload.TYPE, C2SDispenseFromItemPayload.STREAM_CODEC, C2SDispenseFromItemPayload::handleDataOnMain);
   }
}
