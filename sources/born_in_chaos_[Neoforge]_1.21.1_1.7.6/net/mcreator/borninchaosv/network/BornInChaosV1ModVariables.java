package net.mcreator.borninchaosv.network;

import java.util.function.Supplier;
import net.mcreator.borninchaosv.BornInChaosV1Mod;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class BornInChaosV1ModVariables {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(Keys.ATTACHMENT_TYPES, "born_in_chaos_v1");
   public static final Supplier<AttachmentType<BornInChaosV1ModVariables.PlayerVariables>> PLAYER_VARIABLES = ATTACHMENT_TYPES.register(
      "player_variables", () -> AttachmentType.serializable(() -> new BornInChaosV1ModVariables.PlayerVariables()).build()
   );

   @SubscribeEvent
   public static void init(FMLCommonSetupEvent event) {
      BornInChaosV1Mod.addNetworkMessage(
         BornInChaosV1ModVariables.PlayerVariablesSyncMessage.TYPE,
         BornInChaosV1ModVariables.PlayerVariablesSyncMessage.STREAM_CODEC,
         BornInChaosV1ModVariables.PlayerVariablesSyncMessage::handleData
      );
   }

   @EventBusSubscriber
   public static class EventBusVariableHandlers {
      @SubscribeEvent
      public static void onPlayerLoggedInSyncPlayerVariables(PlayerLoggedInEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            ((BornInChaosV1ModVariables.PlayerVariables)player.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void onPlayerRespawnedSyncPlayerVariables(PlayerRespawnEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            ((BornInChaosV1ModVariables.PlayerVariables)player.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerChangedDimensionEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            ((BornInChaosV1ModVariables.PlayerVariables)player.getData(BornInChaosV1ModVariables.PLAYER_VARIABLES)).syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void clonePlayer(Clone event) {
         BornInChaosV1ModVariables.PlayerVariables original = (BornInChaosV1ModVariables.PlayerVariables)event.getOriginal()
            .getData(BornInChaosV1ModVariables.PLAYER_VARIABLES);
         BornInChaosV1ModVariables.PlayerVariables clone = new BornInChaosV1ModVariables.PlayerVariables();
         if (!event.isWasDeath()) {
            clone.naughtiness = original.naughtiness;
         }

         event.getEntity().setData(BornInChaosV1ModVariables.PLAYER_VARIABLES, clone);
      }
   }

   public static class PlayerVariables implements INBTSerializable<CompoundTag> {
      public double naughtiness = 0.0;

      public CompoundTag serializeNBT(Provider lookupProvider) {
         CompoundTag nbt = new CompoundTag();
         nbt.putDouble("naughtiness", this.naughtiness);
         return nbt;
      }

      public void deserializeNBT(Provider lookupProvider, CompoundTag nbt) {
         this.naughtiness = nbt.getDouble("naughtiness");
      }

      public void syncPlayerVariables(Entity entity) {
         if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new BornInChaosV1ModVariables.PlayerVariablesSyncMessage(this), new CustomPacketPayload[0]);
         }
      }
   }

   public record PlayerVariablesSyncMessage(BornInChaosV1ModVariables.PlayerVariables data) implements CustomPacketPayload {
      public static final Type<BornInChaosV1ModVariables.PlayerVariablesSyncMessage> TYPE = new Type(
         ResourceLocation.fromNamespaceAndPath("born_in_chaos_v1", "player_variables_sync")
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, BornInChaosV1ModVariables.PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec.of(
         (buffer, message) -> buffer.writeNbt(message.data().serializeNBT(buffer.registryAccess())),
         buffer -> {
            BornInChaosV1ModVariables.PlayerVariablesSyncMessage message = new BornInChaosV1ModVariables.PlayerVariablesSyncMessage(
               new BornInChaosV1ModVariables.PlayerVariables()
            );
            message.data.deserializeNBT(buffer.registryAccess(), buffer.readNbt());
            return message;
         }
      );

      public Type<BornInChaosV1ModVariables.PlayerVariablesSyncMessage> type() {
         return TYPE;
      }

      public static void handleData(BornInChaosV1ModVariables.PlayerVariablesSyncMessage message, IPayloadContext context) {
         if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
            context.enqueueWork(
                  () -> ((BornInChaosV1ModVariables.PlayerVariables)context.player().getData(BornInChaosV1ModVariables.PLAYER_VARIABLES))
                     .deserializeNBT(context.player().registryAccess(), message.data.serializeNBT(context.player().registryAccess()))
               )
               .exceptionally(e -> {
                  context.connection().disconnect(Component.literal(e.getMessage()));
                  return null;
               });
         }
      }
   }
}
