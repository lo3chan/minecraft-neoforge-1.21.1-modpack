package io.wispforest.owo.config;

import com.google.common.collect.HashMultimap;
import io.netty.buffer.Unpooled;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.Owo;
import io.wispforest.owo.mixin.ServerCommonNetworkHandlerAccessor;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.serialization.CodecUtils;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

public class ConfigSynchronizer {
   public static final ResourceLocation CONFIG_SYNC_CHANNEL = ResourceLocation.fromNamespaceAndPath("owo", "config_sync");
   private static final Map<Connection, Map<String, Map<Option.Key, Object>>> CLIENT_OPTION_STORAGE = new WeakHashMap<>();
   private static final Map<String, ConfigWrapper<?>> KNOWN_CONFIGS = new HashMap<>();
   private static final MutableComponent PREFIX = TextOps.concat(Owo.PREFIX, Component.nullToEmpty("§cunrecoverable config mismatch\n\n"));

   static void register(ConfigWrapper<?> config) {
      KNOWN_CONFIGS.put(config.name(), config);
   }

   @Nullable
   public static Map<Option.Key, ?> getClientOptions(ServerPlayer player, String configName) {
      Map<String, Map<Option.Key, Object>> storage = CLIENT_OPTION_STORAGE.get(((ServerCommonNetworkHandlerAccessor)player.connection).owo$getConnection());
      return storage == null ? null : storage.get(configName);
   }

   @Nullable
   public static Map<Option.Key, ?> getClientOptions(ServerPlayer player, ConfigWrapper<?> config) {
      return getClientOptions(player, config.name());
   }

   private static ConfigSynchronizer.ConfigSyncPacket toPacket(Option.SyncMode targetMode) {
      Map<String, ConfigSynchronizer.ConfigEntry> configs = new HashMap<>();
      KNOWN_CONFIGS.forEach((configName, config) -> {
         ConfigSynchronizer.ConfigEntry entry = new ConfigSynchronizer.ConfigEntry(new HashMap<>());
         config.allOptions().forEach((key, option) -> {
            if (option.syncMode().ordinal() >= targetMode.ordinal()) {
               FriendlyByteBuf optionBuf = new FriendlyByteBuf(Unpooled.buffer());
               option.write(optionBuf);
               entry.options().put(key.asString(), optionBuf);
            }
         });
         configs.put(configName, entry);
      });
      return new ConfigSynchronizer.ConfigSyncPacket(configs);
   }

   private static void read(ConfigSynchronizer.ConfigSyncPacket packet, BiConsumer<Option<?>, FriendlyByteBuf> optionConsumer) {
      for (Entry<String, ConfigSynchronizer.ConfigEntry> configEntry : packet.configs().entrySet()) {
         String configName = configEntry.getKey();
         ConfigWrapper<?> config = KNOWN_CONFIGS.get(configName);
         if (config == null) {
            Owo.LOGGER.error("Received overrides for unknown config '{}', skipping", configName);
         } else {
            for (Entry<String, FriendlyByteBuf> optionEntry : configEntry.getValue().options().entrySet()) {
               Option.Key optionKey = new Option.Key(optionEntry.getKey());
               Option<Object> option = config.optionForKey(optionKey);
               if (option == null) {
                  Owo.LOGGER.error("Received override for unknown option '{}' in config '{}', skipping", optionKey, configName);
               } else {
                  optionConsumer.accept(option, optionEntry.getValue());
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static void applyClient(ConfigSynchronizer.ConfigSyncPacket payload, IPayloadContext context) {
      Minecraft client = Minecraft.getInstance();
      Owo.LOGGER.info("Applying server overrides");
      HashMap<Option<?>, Object> mismatchedOptions = new HashMap<>();
      if (!client.hasSingleplayerServer() || !client.getSingleplayerServer().isSingleplayer()) {
         read(payload, (optionx, packetByteBuf) -> {
            Object mismatchedValue = optionx.read(packetByteBuf);
            if (mismatchedValue != null) {
               mismatchedOptions.put(optionx, mismatchedValue);
            }
         });
         if (!mismatchedOptions.isEmpty()) {
            Owo.LOGGER.error("Aborting connection, non-syncable config values were mismatched");
            mismatchedOptions.forEach(
               (optionx, serverValue) -> Owo.LOGGER
                  .error(
                     "- Option {} in config '{}' has value '{}' but server requires '{}'",
                     optionx.key().asString(),
                     optionx.configName(),
                     optionx.value(),
                     serverValue
                  )
            );
            MutableComponent errorMessage = Component.empty();
            HashMultimap<String, Tuple<Option<?>, Object>> optionsByConfig = HashMultimap.create();
            mismatchedOptions.forEach((optionx, serverValue) -> optionsByConfig.put(optionx.configName(), new Tuple(optionx, serverValue)));

            for (String configName : optionsByConfig.keys()) {
               errorMessage.append(TextOps.withFormatting("in config ", ChatFormatting.GRAY)).append(configName).append("\n");

               for (Tuple<Option<?>, Object> option : optionsByConfig.get(configName)) {
                  errorMessage.append(Component.translatable(((Option)option.getA()).translationKey()).withStyle(ChatFormatting.YELLOW)).append(" -> ");
                  errorMessage.append(((Option)option.getA()).value().toString()).append(TextOps.withFormatting(" (client)", ChatFormatting.GRAY));
                  errorMessage.append(TextOps.withFormatting(" / ", ChatFormatting.DARK_GRAY));
                  errorMessage.append(option.getB().toString()).append(TextOps.withFormatting(" (server)", ChatFormatting.GRAY)).append("\n");
               }

               errorMessage.append("\n");
            }

            errorMessage.append(TextOps.withFormatting("these options could not be synchronized because\n", ChatFormatting.GRAY));
            errorMessage.append(TextOps.withFormatting("they require your client to be restarted\n", ChatFormatting.GRAY));
            errorMessage.append(TextOps.withFormatting("change them manually and restart if you want to join this server", ChatFormatting.GRAY));
            ((LocalPlayer)context.player()).connection.getConnection().disconnect(TextOps.concat(PREFIX, errorMessage));
            return;
         }
      }

      Owo.LOGGER.info("Responding with client values");
      context.reply(toPacket(Option.SyncMode.INFORM_SERVER));
   }

   private static void applyServer(ConfigSynchronizer.ConfigSyncPacket payload, IPayloadContext context) {
      Owo.LOGGER.info("Receiving client config");
      Connection connection = ((ServerCommonNetworkHandlerAccessor)((ServerPlayer)context.player()).connection).owo$getConnection();
      read(
         payload,
         (option, optionBuf) -> {
            Map<Option.Key, Object> config = CLIENT_OPTION_STORAGE.computeIfAbsent(connection, $ -> new HashMap<>())
               .computeIfAbsent(option.configName(), s -> new HashMap<>());
            config.put(option.key(), optionBuf.read(option.endec()));
         }
      );
   }

   public static void register(PayloadRegistrar registrar) {
      StreamCodec<FriendlyByteBuf, ConfigSynchronizer.ConfigSyncPacket> packetCodec = CodecUtils.toPacketCodec(ConfigSynchronizer.ConfigSyncPacket.ENDEC);
      registrar.playBidirectional(ConfigSynchronizer.ConfigSyncPacket.ID, packetCodec, (payload, context) -> context.enqueueWork(() -> {
         if (context.player().level().isClientSide()) {
            applyClient(payload, context);
         } else {
            applyServer(payload, context);
         }
      }));
      NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, event -> {
         if (event.getPlayer() != null) {
            Owo.LOGGER.info("Sending server config values to client");
            event.getPlayer().connection.send(toPacket(Option.SyncMode.OVERRIDE_CLIENT));
         }
      });
   }

   public static void onDisconnect() {
      if (FMLLoader.getDist() == Dist.CLIENT) {
         KNOWN_CONFIGS.forEach((name, config) -> config.forEachOption(Option::reattach));
      }
   }

   private record ConfigEntry(Map<String, FriendlyByteBuf> options) {
      public static final Endec<ConfigSynchronizer.ConfigEntry> ENDEC = StructEndecBuilder.of(
         MinecraftEndecs.PACKET_BYTE_BUF.mapOf().fieldOf("options", ConfigSynchronizer.ConfigEntry::options), ConfigSynchronizer.ConfigEntry::new
      );
   }

   private record ConfigSyncPacket(Map<String, ConfigSynchronizer.ConfigEntry> configs) implements CustomPacketPayload {
      public static final Type<ConfigSynchronizer.ConfigSyncPacket> ID = new Type(ConfigSynchronizer.CONFIG_SYNC_CHANNEL);
      public static final Endec<ConfigSynchronizer.ConfigSyncPacket> ENDEC = StructEndecBuilder.of(
         ConfigSynchronizer.ConfigEntry.ENDEC.mapOf().fieldOf("configs", ConfigSynchronizer.ConfigSyncPacket::configs),
         ConfigSynchronizer.ConfigSyncPacket::new
      );

      public Type<? extends CustomPacketPayload> type() {
         return ID;
      }
   }
}
