package net.blay09.mods.balm.api.network;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Predicate;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.LoadedConfig;
import net.blay09.mods.balm.api.config.LoadedTableConfig;
import net.blay09.mods.balm.api.config.MutableLoadedConfig;
import net.blay09.mods.balm.api.config.PropertyAwareConfig;
import net.blay09.mods.balm.api.config.schema.BalmConfigSchema;
import net.blay09.mods.balm.api.config.schema.ConfiguredProperty;
import net.blay09.mods.balm.api.config.schema.builder.ConfigCategory;
import net.blay09.mods.balm.common.config.AbstractBalmConfig;
import net.blay09.mods.balm.common.config.ConfigSync;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record ClientboundConfigPacket(BalmConfigSchema schema, LoadedConfig config) implements CustomPacketPayload {
   public static final Type<ClientboundConfigPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("balm", "config"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundConfigPacket> STREAM_CODEC = StreamCodec.of(
      ClientboundConfigPacket::encode, ClientboundConfigPacket::decode
   );

   private static ClientboundConfigPacket decode(RegistryFriendlyByteBuf buf) {
      ResourceLocation identifier = (ResourceLocation)ResourceLocation.STREAM_CODEC.decode(buf);
      BalmConfigSchema schema = Balm.getConfig().getSchema(identifier);
      if (schema == null) {
         throw new RuntimeException("Received config packet for unknown schema: " + identifier);
      } else {
         LoadedTableConfig config = new LoadedTableConfig();
         int rootPropertyCount = buf.readVarInt();

         for (int j = 0; j < rootPropertyCount; j++) {
            String property = buf.readUtf();
            ConfiguredProperty<?> propertySchema = schema.findRootProperty(property);
            decodePropertyInto(propertySchema, buf, config);
         }

         int categoryCount = buf.readVarInt();

         for (int i = 0; i < categoryCount; i++) {
            String category = buf.readUtf();
            int propertyCount = buf.readVarInt();

            for (int j = 0; j < propertyCount; j++) {
               String property = buf.readUtf();
               ConfiguredProperty<?> propertySchema = schema.findProperty(category, property);
               decodePropertyInto(propertySchema, buf, config);
            }
         }

         return new ClientboundConfigPacket(schema, config);
      }
   }

   private static void encode(RegistryFriendlyByteBuf buf, ClientboundConfigPacket packet) {
      ResourceLocation.STREAM_CODEC.encode(buf, packet.schema.identifier());
      List<ConfiguredProperty<?>> rootProperties = packet.schema.rootProperties().stream().filter(ConfiguredProperty::synced).toList();
      buf.writeVarInt(rootProperties.size());

      for (ConfiguredProperty<?> rootProperty : rootProperties) {
         buf.writeUtf(rootProperty.name());
         encodeProperty(rootProperty, buf, packet.config);
      }

      List<ConfigCategory> categories = packet.schema.categories().stream().filter(ConfigSync::hasSyncedProperties).toList();
      buf.writeVarInt(categories.size());

      for (ConfigCategory category : categories) {
         buf.writeUtf(category.name());
         List<ConfiguredProperty<?>> properties = category.properties().stream().filter(ConfiguredProperty::synced).toList();
         buf.writeVarInt(properties.size());

         for (ConfiguredProperty<?> property : properties) {
            buf.writeUtf(property.name());
            encodeProperty(property, buf, packet.config);
         }
      }
   }

   private static <T> void decodePropertyInto(ConfiguredProperty<T> property, ByteBuf buf, MutableLoadedConfig config) {
      T value = (T)property.streamCodec().decode(buf);
      config.setRaw(property, value);
   }

   private static <T> void encodeProperty(ConfiguredProperty<T> property, ByteBuf buf, LoadedConfig config) {
      T value = config.getRaw(property);
      property.streamCodec().encode(buf, value);
   }

   public static void handle(Player player, ClientboundConfigPacket packet) {
      MutableLoadedConfig localConfig = Balm.getConfig().getLocalConfig(packet.schema);
      MutableLoadedConfig newConfig = localConfig.copy();
      Predicate<ConfiguredProperty<?>> propertyFilter = packet.config instanceof PropertyAwareConfig propertyAwareConfig
         ? propertyAwareConfig::hasProperty
         : it -> true;
      newConfig.applyFrom(packet.schema, packet.config, propertyFilter);
      if (Balm.getConfig() instanceof AbstractBalmConfig config) {
         config.setActiveConfig(packet.schema, newConfig);
      }
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
