package net.mcreator.undeadrevamp.network;

import java.util.function.Supplier;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
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
public class UndeadRevamp2ModVariables {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(Keys.ATTACHMENT_TYPES, "undead_revamp2");
   public static final Supplier<AttachmentType<UndeadRevamp2ModVariables.PlayerVariables>> PLAYER_VARIABLES = ATTACHMENT_TYPES.register(
      "player_variables", () -> AttachmentType.serializable(() -> new UndeadRevamp2ModVariables.PlayerVariables()).build()
   );

   @SubscribeEvent
   public static void init(FMLCommonSetupEvent event) {
      UndeadRevamp2Mod.addNetworkMessage(
         UndeadRevamp2ModVariables.PlayerVariablesSyncMessage.TYPE,
         UndeadRevamp2ModVariables.PlayerVariablesSyncMessage.STREAM_CODEC,
         UndeadRevamp2ModVariables.PlayerVariablesSyncMessage::handleData
      );
   }

   @EventBusSubscriber
   public static class EventBusVariableHandlers {
      @SubscribeEvent
      public static void onPlayerLoggedInSyncPlayerVariables(PlayerLoggedInEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            ((UndeadRevamp2ModVariables.PlayerVariables)player.getData(UndeadRevamp2ModVariables.PLAYER_VARIABLES)).syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void onPlayerRespawnedSyncPlayerVariables(PlayerRespawnEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            ((UndeadRevamp2ModVariables.PlayerVariables)player.getData(UndeadRevamp2ModVariables.PLAYER_VARIABLES)).syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerChangedDimensionEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            ((UndeadRevamp2ModVariables.PlayerVariables)player.getData(UndeadRevamp2ModVariables.PLAYER_VARIABLES)).syncPlayerVariables(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void clonePlayer(Clone event) {
         UndeadRevamp2ModVariables.PlayerVariables original = (UndeadRevamp2ModVariables.PlayerVariables)event.getOriginal()
            .getData(UndeadRevamp2ModVariables.PLAYER_VARIABLES);
         UndeadRevamp2ModVariables.PlayerVariables clone = new UndeadRevamp2ModVariables.PlayerVariables();
         clone.fallinmylove = original.fallinmylove;
         if (!event.isWasDeath()) {
         }

         event.getEntity().setData(UndeadRevamp2ModVariables.PLAYER_VARIABLES, clone);
      }
   }

   public static class PlayerVariables implements INBTSerializable<CompoundTag> {
      public double fallinmylove = 0.0;

      public CompoundTag serializeNBT(Provider lookupProvider) {
         CompoundTag nbt = new CompoundTag();
         nbt.putDouble("fallinmylove", this.fallinmylove);
         return nbt;
      }

      public void deserializeNBT(Provider lookupProvider, CompoundTag nbt) {
         this.fallinmylove = nbt.getDouble("fallinmylove");
      }

      public void syncPlayerVariables(Entity entity) {
         if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new UndeadRevamp2ModVariables.PlayerVariablesSyncMessage(this), new CustomPacketPayload[0]);
         }
      }
   }

   public record PlayerVariablesSyncMessage(UndeadRevamp2ModVariables.PlayerVariables data) implements CustomPacketPayload {
      public static final Type<UndeadRevamp2ModVariables.PlayerVariablesSyncMessage> TYPE = new Type(
         ResourceLocation.fromNamespaceAndPath("undead_revamp2", "player_variables_sync")
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, UndeadRevamp2ModVariables.PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec.of(
         (buffer, message) -> buffer.writeNbt(message.data().serializeNBT(buffer.registryAccess())),
         buffer -> {
            UndeadRevamp2ModVariables.PlayerVariablesSyncMessage message = new UndeadRevamp2ModVariables.PlayerVariablesSyncMessage(
               new UndeadRevamp2ModVariables.PlayerVariables()
            );
            message.data.deserializeNBT(buffer.registryAccess(), buffer.readNbt());
            return message;
         }
      );

      public Type<UndeadRevamp2ModVariables.PlayerVariablesSyncMessage> type() {
         return TYPE;
      }

      public static void handleData(UndeadRevamp2ModVariables.PlayerVariablesSyncMessage message, IPayloadContext context) {
         if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
            context.enqueueWork(
                  () -> ((UndeadRevamp2ModVariables.PlayerVariables)context.player().getData(UndeadRevamp2ModVariables.PLAYER_VARIABLES))
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
