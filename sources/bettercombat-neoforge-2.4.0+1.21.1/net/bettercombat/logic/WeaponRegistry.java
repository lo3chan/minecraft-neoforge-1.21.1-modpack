package net.bettercombat.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.api.AttributesContainer;
import net.bettercombat.api.WeaponAttributes;
import net.bettercombat.api.WeaponAttributesHelper;
import net.bettercombat.api.component.BetterCombatDataComponents;
import net.bettercombat.network.Packets;
import net.bettercombat.utils.CompressionHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class WeaponRegistry {
   static final Logger LOGGER = LogUtils.getLogger();
   static Map<ResourceLocation, WeaponAttributes> registrations = new HashMap<>();
   static Map<ResourceLocation, AttributesContainer> containers = new HashMap<>();
   private static WeaponRegistry.Encoded encodedRegistrations = new WeaponRegistry.Encoded(true, List.of());
   private static final int CHUNK_SIZE = 10000;
   private static final Gson gson = new GsonBuilder().create();

   public static void register(ResourceLocation itemId, WeaponAttributes attributes) {
      registrations.put(itemId, attributes);
   }

   static WeaponAttributes getAttributes(ResourceLocation itemId) {
      return registrations.get(itemId);
   }

   public static WeaponAttributes getAttributes(ItemStack itemStack) {
      if (itemStack == null) {
         return null;
      } else {
         ResourceLocation component = (ResourceLocation)itemStack.get(BetterCombatDataComponents.WEAPON_PRESET_ID);
         if (component != null) {
            AttributesContainer container = containers.get(component);
            if (container != null) {
               return container.attributes();
            }
         }

         Item item = itemStack.getItem();
         ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
         return getAttributes(id);
      }
   }

   public static void loadAttributes(ResourceManager resourceManager) {
      loadContainers(resourceManager);
      containers.forEach((itemId, container) -> {
         if (BuiltInRegistries.ITEM.containsKey(itemId)) {
            resolveAndRegisterAttributes(itemId, container);
         }
      });
   }

   private static void loadContainers(ResourceManager resourceManager) {
      Map<ResourceLocation, AttributesContainer> containers = new HashMap<>();
      boolean logging = BetterCombatMod.config.weapon_registry_logging;

      for (Entry<ResourceLocation, Resource> entry : resourceManager.listResources("weapon_attributes", fileName -> fileName.getPath().endsWith(".json"))
         .entrySet()) {
         ResourceLocation identifier = entry.getKey();
         Resource resource = entry.getValue();

         try {
            JsonReader reader = new JsonReader(new InputStreamReader(resource.open()));
            AttributesContainer container = WeaponAttributesHelper.decode(reader);
            String id = identifier.toString().replace("weapon_attributes/", "");
            id = id.substring(0, id.lastIndexOf(46));
            containers.put(ResourceLocation.parse(id), container);
            if (logging) {
               System.out.println("Loaded container: " + id);
            }
         } catch (Exception var10) {
            System.err.println("Failed to parse: " + identifier);
            var10.printStackTrace();
         }
      }

      WeaponRegistry.containers = containers;
      Map<ResourceLocation, AttributesContainer> resolvedContainers = new HashMap<>();

      for (Entry<ResourceLocation, AttributesContainer> entry : containers.entrySet()) {
         ResourceLocation id = entry.getKey();
         AttributesContainer container = entry.getValue();
         if (container.parent() != null) {
            WeaponAttributes resolvedAttributes = resolveAttributes(id, container);
            if (resolvedAttributes != null) {
               container = new AttributesContainer(null, resolvedAttributes);
            }
         }

         resolvedContainers.put(id, container);
      }

      WeaponRegistry.containers = resolvedContainers;
   }

   public static WeaponAttributes resolveAttributes(ResourceLocation itemId, AttributesContainer container) {
      try {
         ArrayList<WeaponAttributes> resolutionChain = new ArrayList<>();
         AttributesContainer current = container;

         while (current != null) {
            resolutionChain.add(0, current.attributes());
            if (current.parent() != null) {
               current = containers.get(ResourceLocation.parse(current.parent()));
            } else {
               current = null;
            }
         }

         WeaponAttributes empty = WeaponAttributes.empty();
         WeaponAttributes resolvedAttributes = resolutionChain.stream().reduce(empty, (a, b) -> b == null ? a : WeaponAttributesHelper.override(a, b));
         WeaponAttributesHelper.validate(resolvedAttributes);
         return resolvedAttributes;
      } catch (Exception var6) {
         LOGGER.error("Failed to resolve weapon attributes for: " + itemId + ". Reason: " + var6.getMessage());
         var6.printStackTrace();
         return null;
      }
   }

   public static void resolveAndRegisterAttributes(ResourceLocation itemId, AttributesContainer container) {
      WeaponAttributes resolvedAttributes = resolveAttributes(itemId, container);
      if (resolvedAttributes != null) {
         register(itemId, resolvedAttributes);
      }
   }

   public static void encodeRegistry() {
      boolean compressed = BetterCombatMod.config.weapon_registry_compression;
      List<String> chunks = new ArrayList<>();
      WeaponRegistry.SyncFormat syncContent = new WeaponRegistry.SyncFormat();
      containers.forEach((key, value) -> syncContent.attributes.put(key.toString(), value));
      registrations.forEach((key, value) -> syncContent.registrations.put(key.toString(), value));
      String json = gson.toJson(syncContent);
      if (compressed) {
         json = CompressionHelper.gzipCompress(json);
      }

      if (BetterCombatMod.config.weapon_registry_logging) {
         LOGGER.info("Weapon Attribute assignments loaded: " + json);
      }

      for (int i = 0; i < json.length(); i += 10000) {
         chunks.add(json.substring(i, Math.min(json.length(), i + 10000)));
      }

      encodedRegistrations = new WeaponRegistry.Encoded(compressed, chunks);
      Packets.WeaponRegistrySync referencePacket = new Packets.WeaponRegistrySync(compressed, chunks);
      FriendlyByteBuf buffer = Platform.createByteBuffer();
      referencePacket.write(buffer);
      LOGGER.info(
         "Encoded Weapon Attribute registry size (with package overhead): "
            + buffer.readableBytes()
            + " bytes (in "
            + chunks.size()
            + " string chunks with the size of 10000)"
      );
   }

   public static void decodeRegistry(Packets.WeaponRegistrySync syncPacket) {
      boolean compressed = syncPacket.compressed();
      String json = "";

      for (String chunk : syncPacket.chunks()) {
         json = json.concat(chunk);
      }

      if (compressed) {
         json = CompressionHelper.gzipDecompress(json);
      }

      LOGGER.info("Decoded Weapon Attribute registry in " + syncPacket.chunks().size() + " string chunks");
      if (BetterCombatMod.config.weapon_registry_logging) {
         LOGGER.info("Weapon Attribute registry received: " + json);
      }

      WeaponRegistry.SyncFormat sync = (WeaponRegistry.SyncFormat)gson.fromJson(json, WeaponRegistry.SyncFormat.class);
      containers.clear();
      sync.attributes.forEach((key, value) -> containers.put(ResourceLocation.parse(key), value));
      registrations.clear();
      sync.registrations.forEach((key, value) -> registrations.put(ResourceLocation.parse(key), value));
   }

   public static WeaponRegistry.Encoded getEncodedRegistry() {
      return encodedRegistrations;
   }

   public record Encoded(boolean compressed, List<String> chunks) {
   }

   public static class SyncFormat {
      public Map<String, AttributesContainer> attributes = new HashMap<>();
      public Map<String, WeaponAttributes> registrations = new HashMap<>();
   }
}
