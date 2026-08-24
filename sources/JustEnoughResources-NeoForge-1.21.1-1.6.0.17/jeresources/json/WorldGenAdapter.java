package jeresources.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import jeresources.api.distributions.DistributionBase;
import jeresources.api.distributions.DistributionCustom;
import jeresources.api.distributions.DistributionHelpers;
import jeresources.api.drop.LootDrop;
import jeresources.api.restrictions.DimensionRestriction;
import jeresources.api.restrictions.Restriction;
import jeresources.entry.WorldGenEntry;
import jeresources.platform.Services;
import jeresources.registry.WorldGenRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class WorldGenAdapter {
   private static final String worldGenFileName = "world-gen.json";
   private static Map<ResourceKey<Level>, Restriction> map = new HashMap<>();

   public static File getWorldGenFile() {
      return Services.PLATFORM.getConfigDir().resolve("world-gen.json").toFile();
   }

   public static boolean hasWorldGenDIYData() {
      return getWorldGenFile().exists();
   }

   public static boolean readDIYData() {
      try {
         JsonElement base = JsonParser.parseReader(new FileReader(getWorldGenFile()));
         if (!base.isJsonArray() || base.getAsJsonArray().size() == 0) {
            return false;
         }

         JsonArray array = base.getAsJsonArray();

         for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            JsonElement element = obj.get("mod");
            if (element == null || Services.PLATFORM.getModsList().isLoaded(element.getAsString())) {
               String block = obj.get("block").getAsString();
               JsonElement distribElement = obj.get("distrib");
               if (distribElement != null) {
                  String distrib = distribElement.getAsString();
                  JsonElement silk = obj.get("silktouch");
                  boolean silktouch = silk != null && silk.getAsBoolean();
                  JsonElement dimElement = obj.get("dim");
                  String dim = dimElement != null ? dimElement.getAsString() : "";
                  String[] blockParts = block.split(":");
                  Item itemBlock = (Item)BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(blockParts[0], blockParts[1]));
                  if (itemBlock == Items.AIR) {
                     itemBlock = ((Block)BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(blockParts[0], blockParts[1]))).asItem();
                  }

                  if (itemBlock != Items.AIR) {
                     ItemStack blockStack = itemBlock.getDefaultInstance();
                     List<DistributionHelpers.OrePoint> points = new ArrayList<>();

                     for (String point : distrib.split(";")) {
                        String[] split = point.split(",");
                        if (split.length == 2) {
                           points.add(new DistributionHelpers.OrePoint(Integer.parseInt(split[0]), Float.parseFloat(split[1])));
                        }
                     }

                     DistributionBase distribution = new DistributionCustom(
                        DistributionHelpers.getDistributionFromPoints(points.toArray(new DistributionHelpers.OrePoint[points.size()]))
                     );
                     JsonElement dropsListElement = obj.get("dropsList");
                     List<LootDrop> dropList = new LinkedList<>();
                     if (dropsListElement != null) {
                        for (JsonElement dropElement : dropsListElement.getAsJsonArray()) {
                           JsonObject drop = dropElement.getAsJsonObject();
                           JsonElement itemStackElement = drop.get("itemStack");
                           if (!itemStackElement.isJsonNull()) {
                              String itemStackString = itemStackElement.getAsString();
                              String[] stackStrings = itemStackString.split(":", 4);
                              Item item = (Item)BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(stackStrings[0], stackStrings[1]));
                              if (item != Items.AIR) {
                                 ItemStack itemStack = new ItemStack(item);
                                 if (stackStrings.length >= 3) {
                                    itemStack.setDamageValue(Integer.valueOf(stackStrings[2]));
                                 }

                                 if (stackStrings.length == 4) {
                                 }

                                 JsonElement fortuneElement = drop.get("fortunes");
                                 if (fortuneElement != null) {
                                    JsonObject fortunes = fortuneElement.getAsJsonObject();

                                    for (Entry<String, JsonElement> fortuneValue : fortunes.entrySet()) {
                                       int fortuneLevel = Integer.parseInt(fortuneValue.getKey());
                                       float dropAmount = fortuneValue.getValue().getAsFloat();
                                       dropList.add(new LootDrop(itemStack, dropAmount, fortuneLevel));
                                    }
                                 }
                              }
                           }
                        }
                     }

                     if ((blockStack.isEmpty() || blockStack.getItem() == Items.AIR) && dropList.size() > 0) {
                        blockStack = dropList.get(0).item.copy();
                        blockStack.setCount(1);
                     }

                     WorldGenRegistry.getInstance()
                        .registerEntry(
                           new WorldGenEntry(blockStack, distribution, getRestriction(dim), silktouch, dropList.toArray(new LootDrop[dropList.size()]))
                        );
                  }
               }
            }
         }
      } catch (FileNotFoundException var34) {
         var34.printStackTrace();
      }

      map.clear();
      return true;
   }

   private static Restriction getRestriction(String dim) {
      ResourceKey<Level> worldRegistryKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dim));
      return map.computeIfAbsent(worldRegistryKey, k -> new Restriction(new DimensionRestriction(worldRegistryKey)));
   }
}
