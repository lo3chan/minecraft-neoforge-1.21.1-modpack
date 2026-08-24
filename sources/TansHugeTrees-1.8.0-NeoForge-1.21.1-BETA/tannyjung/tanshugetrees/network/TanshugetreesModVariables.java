package tannyjung.tanshugetrees.network;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import tannyjung.tanshugetrees.TanshugetreesMod;

@EventBusSubscriber
public class TanshugetreesModVariables {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(Keys.ATTACHMENT_TYPES, "tanshugetrees");

   @SubscribeEvent
   public static void init(FMLCommonSetupEvent event) {
      TanshugetreesMod.addNetworkMessage(
         TanshugetreesModVariables.SavedDataSyncMessage.TYPE,
         TanshugetreesModVariables.SavedDataSyncMessage.STREAM_CODEC,
         TanshugetreesModVariables.SavedDataSyncMessage::handleData
      );
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         SavedData mapdata = TanshugetreesModVariables.MapVariables.get(event.getEntity().level());
         SavedData worlddata = TanshugetreesModVariables.WorldVariables.get(event.getEntity().level());
         if (mapdata != null) {
            PacketDistributor.sendToPlayer(player, new TanshugetreesModVariables.SavedDataSyncMessage(0, mapdata), new CustomPacketPayload[0]);
         }

         if (worlddata != null) {
            PacketDistributor.sendToPlayer(player, new TanshugetreesModVariables.SavedDataSyncMessage(1, worlddata), new CustomPacketPayload[0]);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         SavedData worlddata = TanshugetreesModVariables.WorldVariables.get(event.getEntity().level());
         if (worlddata != null) {
            PacketDistributor.sendToPlayer(player, new TanshugetreesModVariables.SavedDataSyncMessage(1, worlddata), new CustomPacketPayload[0]);
         }
      }
   }

   @SubscribeEvent
   public static void onWorldTick(Post event) {
      if (event.getLevel() instanceof ServerLevel level) {
         TanshugetreesModVariables.WorldVariables worldVariables = TanshugetreesModVariables.WorldVariables.get(level);
         if (worldVariables._syncDirty) {
            PacketDistributor.sendToPlayersInDimension(level, new TanshugetreesModVariables.SavedDataSyncMessage(1, worldVariables), new CustomPacketPayload[0]);
            worldVariables._syncDirty = false;
         }

         TanshugetreesModVariables.MapVariables mapVariables = TanshugetreesModVariables.MapVariables.get(level);
         if (mapVariables._syncDirty) {
            PacketDistributor.sendToAllPlayers(new TanshugetreesModVariables.SavedDataSyncMessage(0, mapVariables), new CustomPacketPayload[0]);
            mapVariables._syncDirty = false;
         }
      }
   }

   public static class MapVariables extends SavedData {
      public static final String DATA_NAME = "tanshugetrees_mapvars";
      boolean _syncDirty = false;
      public boolean shape_file_converter = false;
      public double shape_file_converter_count = 0.0;
      public String season = "Summer";
      static TanshugetreesModVariables.MapVariables clientSide = new TanshugetreesModVariables.MapVariables();

      public static TanshugetreesModVariables.MapVariables load(CompoundTag tag, Provider lookupProvider) {
         TanshugetreesModVariables.MapVariables data = new TanshugetreesModVariables.MapVariables();
         data.read(tag, lookupProvider);
         return data;
      }

      public void read(CompoundTag nbt, Provider lookupProvider) {
         this.shape_file_converter = nbt.getBoolean("shape_file_converter");
         this.shape_file_converter_count = nbt.getDouble("shape_file_converter_count");
         this.season = nbt.getString("season");
      }

      public CompoundTag save(CompoundTag nbt, Provider lookupProvider) {
         nbt.putBoolean("shape_file_converter", this.shape_file_converter);
         nbt.putDouble("shape_file_converter_count", this.shape_file_converter_count);
         nbt.putString("season", this.season);
         return nbt;
      }

      public void markSyncDirty() {
         this.setDirty();
         this._syncDirty = true;
      }

      public static TanshugetreesModVariables.MapVariables get(LevelAccessor world) {
         return world instanceof ServerLevelAccessor serverLevelAcc
            ? (TanshugetreesModVariables.MapVariables)serverLevelAcc.getLevel()
               .getServer()
               .getLevel(Level.OVERWORLD)
               .getDataStorage()
               .computeIfAbsent(new Factory(TanshugetreesModVariables.MapVariables::new, TanshugetreesModVariables.MapVariables::load), "tanshugetrees_mapvars")
            : clientSide;
      }
   }

   public record SavedDataSyncMessage(int dataType, SavedData data) implements CustomPacketPayload {
      public static final Type<TanshugetreesModVariables.SavedDataSyncMessage> TYPE = new Type(
         ResourceLocation.fromNamespaceAndPath("tanshugetrees", "saved_data_sync")
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, TanshugetreesModVariables.SavedDataSyncMessage> STREAM_CODEC = StreamCodec.of(
         (buffer, message) -> {
            buffer.writeInt(message.dataType);
            if (message.data != null) {
               buffer.writeNbt(message.data.save(new CompoundTag(), buffer.registryAccess()));
            }
         }, buffer -> {
            int dataType = buffer.readInt();
            CompoundTag nbt = buffer.readNbt();
            SavedData data = null;
            if (nbt != null) {
               data = (SavedData)(dataType == 0 ? new TanshugetreesModVariables.MapVariables() : new TanshugetreesModVariables.WorldVariables());
               if (data instanceof TanshugetreesModVariables.MapVariables mapVariables) {
                  mapVariables.read(nbt, buffer.registryAccess());
               } else if (data instanceof TanshugetreesModVariables.WorldVariables worldVariables) {
                  worldVariables.read(nbt, buffer.registryAccess());
               }
            }

            return new TanshugetreesModVariables.SavedDataSyncMessage(dataType, data);
         }
      );

      public Type<TanshugetreesModVariables.SavedDataSyncMessage> type() {
         return TYPE;
      }

      public static void handleData(TanshugetreesModVariables.SavedDataSyncMessage message, IPayloadContext context) {
         if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
            context.enqueueWork(
                  () -> {
                     if (message.dataType == 0) {
                        TanshugetreesModVariables.MapVariables.clientSide
                           .read(message.data.save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
                     } else {
                        TanshugetreesModVariables.WorldVariables.clientSide
                           .read(message.data.save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
                     }
                  }
               )
               .exceptionally(e -> {
                  context.connection().disconnect(Component.literal(e.getMessage()));
                  return null;
               });
         }
      }
   }

   public static class WorldVariables extends SavedData {
      public static final String DATA_NAME = "tanshugetrees_worldvars";
      boolean _syncDirty = false;
      static TanshugetreesModVariables.WorldVariables clientSide = new TanshugetreesModVariables.WorldVariables();

      public static TanshugetreesModVariables.WorldVariables load(CompoundTag tag, Provider lookupProvider) {
         TanshugetreesModVariables.WorldVariables data = new TanshugetreesModVariables.WorldVariables();
         data.read(tag, lookupProvider);
         return data;
      }

      public void read(CompoundTag nbt, Provider lookupProvider) {
      }

      public CompoundTag save(CompoundTag nbt, Provider lookupProvider) {
         return nbt;
      }

      public void markSyncDirty() {
         this.setDirty();
         this._syncDirty = true;
      }

      public static TanshugetreesModVariables.WorldVariables get(LevelAccessor world) {
         return world instanceof ServerLevel level
            ? (TanshugetreesModVariables.WorldVariables)level.getDataStorage()
               .computeIfAbsent(
                  new Factory(TanshugetreesModVariables.WorldVariables::new, TanshugetreesModVariables.WorldVariables::load), "tanshugetrees_worldvars"
               )
            : clientSide;
      }
   }
}
