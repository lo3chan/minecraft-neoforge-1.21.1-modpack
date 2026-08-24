package net.mehvahdjukaar.moonlight.core.set;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.misc.BlockAndItem;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class BlocksColorInternal extends SimplePreparableReloadListener<List<JsonElement>> {
   public static final BlocksColorInternal INSTANCE = new BlocksColorInternal();
   public static final List<DyeColor> VANILLA_COLORS = List.of(
      DyeColor.WHITE,
      DyeColor.ORANGE,
      DyeColor.MAGENTA,
      DyeColor.LIGHT_BLUE,
      DyeColor.YELLOW,
      DyeColor.LIME,
      DyeColor.PINK,
      DyeColor.GRAY,
      DyeColor.LIGHT_GRAY,
      DyeColor.CYAN,
      DyeColor.PURPLE,
      DyeColor.BLUE,
      DyeColor.BROWN,
      DyeColor.GREEN,
      DyeColor.RED,
      DyeColor.BLACK
   );
   public static final List<DyeColor> MODDED_COLORS = List.of(
      Arrays.stream(DyeColor.values()).filter(v -> !VANILLA_COLORS.contains(v)).toArray(DyeColor[]::new)
   );
   private static final List<String> KNOWN_COLOR_MODS = Stream.of("tinted", "dye_depot", "dyenamics", "delicate_dyes", "mint")
      .filter(PlatHelper::isModLoaded)
      .toList();
   private BlocksColorInternal.State defaultState;
   private BlocksColorInternal.State state;
   private final Gson gson = new Gson();

   protected List<JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
      List<JsonElement> output = new ArrayList<>();
      String directory = "color_sets";
      FileToIdConverter filetoidconverter = FileToIdConverter.json(directory);

      for (Entry<ResourceLocation, List<Resource>> entry : filetoidconverter.listMatchingResourceStacks(resourceManager).entrySet()) {
         ResourceLocation resourcelocation = entry.getKey();
         ResourceLocation resourcelocation1 = filetoidconverter.fileToId(resourcelocation);

         for (Resource r : entry.getValue()) {
            try (Reader reader = r.openAsReader()) {
               JsonElement jsonelement = (JsonElement)GsonHelper.fromJson(this.gson, reader, JsonElement.class);
               output.add(jsonelement);
            } catch (JsonParseException | IllegalArgumentException | IOException var18) {
               Moonlight.LOGGER.error("Couldn't parse data file {} from {}", resourcelocation1, resourcelocation, var18);
            }
         }
      }

      return output;
   }

   protected void apply(List<JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
      List<ColorSetModification> colorSets = new ArrayList<>();

      for (JsonElement json : object) {
         try {
            ColorSetModification cs = (ColorSetModification)((Pair)ColorSetModification.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow()).getFirst();
            colorSets.add(cs);
         } catch (Exception var8) {
            Moonlight.LOGGER.info("Failed to load custom color set definition {}. Ignoring", json);
         }
      }

      this.state = this.defaultState.cloneModified(colorSets);
   }

   public void setup() {
      Stopwatch sw = Stopwatch.createStarted();
      Map<String, DyeColor> colors = new HashMap<>();
      VANILLA_COLORS.forEach(d -> colors.put(d.getName(), d));
      List<String> colorPriority = new ArrayList<>(colors.keySet().stream().toList());
      Map<String, BlocksColorInternal.ColoredSet<Block>> blockSets = this.scanRegistryAndDetectSets(colors, colorPriority, BuiltInRegistries.BLOCK);
      Map<String, BlocksColorInternal.ColoredSet<Item>> itemSets = this.scanRegistryAndDetectSets(colors, colorPriority, BuiltInRegistries.ITEM);
      this.defaultState = new BlocksColorInternal.State(blockSets, itemSets);
      this.state = this.defaultState;
      Moonlight.LOGGER.info("Initialized color sets in {}ms", sw.elapsed().toMillis());
   }

   private <T> Map<String, BlocksColorInternal.ColoredSet<T>> scanRegistryAndDetectSets(
      Map<String, DyeColor> colors, List<String> colorPriority, Registry<T> registry
   ) {
      Map<ResourceLocation, BlocksColorInternal.ColorSetBuilder<T>> groupedByType = new HashMap<>();
      colorPriority.sort(Comparator.comparingInt(String::length));
      Collections.reverse(colorPriority);

      for (Entry<ResourceKey<T>, T> e : registry.entrySet()) {
         ResourceLocation id = e.getKey().location();
         String name = id.getPath();
         if (name.contains("_")) {
            for (String c : colorPriority) {
               ResourceLocation newId = null;
               if (name.startsWith(c + "_")) {
                  newId = id.withPath(name.substring((c + "_").length()));
               }

               if (name.endsWith("_" + c)) {
                  newId = id.withPath(name.substring(0, name.length() - ("_" + c).length()));
               }

               if (newId != null) {
                  DyeColor dyeColor = colors.get(c);
                  groupedByType.computeIfAbsent(newId, a -> new BlocksColorInternal.ColorSetBuilder<>()).setColor(dyeColor, e.getValue());
                  break;
               }
            }
         }
      }

      Map<String, BlocksColorInternal.ColoredSet<T>> result = new HashMap<>();

      for (Entry<ResourceLocation, BlocksColorInternal.ColorSetBuilder<T>> j : groupedByType.entrySet()) {
         BlocksColorInternal.ColorSetBuilder<T> set = j.getValue();
         ResourceLocation id = j.getKey();
         if (!this.isHardcodedBlacklisted(id) && set.hasAllVanilla()) {
            this.addExtraEntries(id, registry, set);
            result.put(id.toString(), set.build());
         }
      }

      return result;
   }

   private <T> void addExtraEntries(ResourceLocation id, Registry<T> registry, BlocksColorInternal.ColorSetBuilder<T> colorsToObj) {
      label32:
      for (DyeColor c : MODDED_COLORS) {
         String namespace = id.getNamespace();
         String path = id.getPath();

         for (String mod : KNOWN_COLOR_MODS) {
            for (String s : new String[]{namespace + ":" + path + "_%s", namespace + ":%s_" + path, mod + ":" + path + "_%s", mod + ":%s_" + path}) {
               Optional<T> o = registry.getOptional(ResourceLocation.parse(String.format(s, c.getName())));
               if (o.isPresent()) {
                  colorsToObj.setColor(c, o.get());
                  continue label32;
               }
            }
         }
      }

      Optional<T> o = registry.getOptional(id);
      T def = o.orElseGet(() -> registry.getOptional(ResourceLocation.parse(id.getPath())).orElseGet(() -> colorsToObj.getColor(DyeColor.WHITE)));
      colorsToObj.setColor(null, def);
   }

   private boolean isHardcodedBlacklisted(ResourceLocation id) {
      String modId = id.getNamespace();
      return modId.equals("energeticsheep") || modId.equals("xycraft_world") || modId.equals("botania") || modId.equals("spectrum");
   }

   @Nullable
   public DyeColor getColor(Block block) {
      return (DyeColor)this.state.obj2Colors.get(block);
   }

   @Nullable
   public DyeColor getColor(Item item) {
      return (DyeColor)this.state.obj2Colors.get(item);
   }

   @Nullable
   public Item getColoredItem(String key, @Nullable DyeColor color) {
      BlocksColorInternal.ColoredSet<Item> set = this.getItemSet(key);
      return set != null ? set.with(color) : null;
   }

   @Nullable
   public Block getColoredBlock(String key, @Nullable DyeColor color) {
      BlocksColorInternal.ColoredSet<Block> set = this.getBlockSet(key);
      return set != null ? set.with(color) : null;
   }

   public Set<String> getBlockKeys() {
      return this.state.blockColorSets.keySet();
   }

   public Set<String> getItemKeys() {
      return this.state.itemColorSets.keySet();
   }

   @Nullable
   public Block changeColor(Block old, @Nullable DyeColor newColor) {
      String key = this.getKey(old);
      if (key != null) {
         BlocksColorInternal.ColoredSet<Block> set = this.getBlockSet(key);
         if (set != null) {
            Block b = set.with(newColor);
            if (b != old) {
               return b;
            }
         }
      }

      return null;
   }

   @Nullable
   public Item changeColor(Item old, @Nullable DyeColor newColor) {
      String key = this.getKey(old);
      if (key != null) {
         BlocksColorInternal.ColoredSet<Item> set = this.getItemSet(key);
         if (set != null) {
            Item i = set.with(newColor);
            if (i != old) {
               return i;
            }
         }
      }

      return null;
   }

   @Nullable
   public String getKey(Block block) {
      return (String)this.state.obj2Type.get(block);
   }

   @Nullable
   public String getKey(Item item) {
      return (String)this.state.obj2Type.get(item);
   }

   @Nullable
   private BlocksColorInternal.ColoredSet<Block> getBlockSet(String key) {
      key = ResourceLocation.parse(key).toString();
      return this.state.blockColorSets.get(key);
   }

   @Nullable
   private BlocksColorInternal.ColoredSet<Item> getItemSet(String key) {
      key = ResourceLocation.parse(key).toString();
      return this.state.itemColorSets.get(key);
   }

   @Nullable
   public HolderSet<Block> getBlockHolderSet(String key) {
      BlocksColorInternal.ColoredSet<Block> set = this.getBlockSet(key);
      return set != null ? set.makeHolderSet(BuiltInRegistries.BLOCK) : null;
   }

   @Nullable
   public HolderSet<Item> getItemHolderSet(String key) {
      BlocksColorInternal.ColoredSet<Item> set = this.getItemSet(key);
      return set != null ? set.makeHolderSet(BuiltInRegistries.ITEM) : null;
   }

   private static class ColorSetBuilder<T> {
      private final Map<DyeColor, T> colorsToObj = new HashMap<>();

      private static <T> BlocksColorInternal.ColorSetBuilder<T> from(BlocksColorInternal.ColoredSet<T> other) {
         BlocksColorInternal.ColorSetBuilder<T> b = new BlocksColorInternal.ColorSetBuilder<>();
         b.colorsToObj.putAll(other.colorsToObj);
         return b;
      }

      private void setColor(@Nullable DyeColor color, T b) {
         this.colorsToObj.put(color, b);
      }

      private boolean isEmpty() {
         return this.colorsToObj.isEmpty();
      }

      private BlocksColorInternal.ColoredSet<T> build() {
         return new BlocksColorInternal.ColoredSet<>(this.colorsToObj);
      }

      public boolean hasAllVanilla() {
         return BlocksColorInternal.VANILLA_COLORS.stream().allMatch(this.colorsToObj::containsKey);
      }

      @Nullable
      public T getColor(DyeColor dyeColor) {
         return this.colorsToObj.get(dyeColor);
      }
   }

   private record ColoredSet<T>(Map<DyeColor, T> colorsToObj) {
      private ColoredSet(Map<DyeColor, T> colorsToObj) {
         this.colorsToObj = new HashMap<>(colorsToObj);
      }

      private HolderSet<T> makeHolderSet(Registry<T> registry) {
         return HolderSet.direct(registry::wrapAsHolder, new ArrayList<>(this.colorsToObj.values()));
      }

      @Nullable
      private T with(@Nullable DyeColor newColor) {
         return newColor != null && !this.colorsToObj.containsKey(newColor) ? null : this.colorsToObj.getOrDefault(newColor, this.colorsToObj.get(null));
      }
   }

   static class State {
      private final Map<String, BlocksColorInternal.ColoredSet<Block>> blockColorSets;
      private final Map<String, BlocksColorInternal.ColoredSet<Item>> itemColorSets;
      private final Object2ObjectOpenHashMap<Object, DyeColor> obj2Colors = new Object2ObjectOpenHashMap();
      private final Object2ObjectOpenHashMap<Object, String> obj2Type = new Object2ObjectOpenHashMap();

      private State(Map<String, BlocksColorInternal.ColoredSet<Block>> blockColorSets, Map<String, BlocksColorInternal.ColoredSet<Item>> itemColorSets) {
         this.blockColorSets = blockColorSets;
         this.itemColorSets = itemColorSets;

         for (Entry<String, BlocksColorInternal.ColoredSet<Block>> e : blockColorSets.entrySet()) {
            String id = e.getKey();
            BlocksColorInternal.ColoredSet<Block> set = e.getValue();

            for (Entry<DyeColor, Block> v : set.colorsToObj.entrySet()) {
               this.obj2Colors.put(v.getValue(), v.getKey());
               this.obj2Type.put(v.getValue(), id);
            }
         }

         for (Entry<String, BlocksColorInternal.ColoredSet<Item>> e : itemColorSets.entrySet()) {
            String id = e.getKey();
            BlocksColorInternal.ColoredSet<Item> set = e.getValue();

            for (Entry<DyeColor, Item> v : set.colorsToObj.entrySet()) {
               this.obj2Colors.put(v.getValue(), v.getKey());
               this.obj2Type.put(v.getValue(), id);
            }
         }
      }

      private BlocksColorInternal.State cloneModified(List<ColorSetModification> mods) {
         Map<String, BlocksColorInternal.ColorSetBuilder<Block>> blockBuilder = this.blockColorSets
            .entrySet()
            .stream()
            .map(ex -> Map.entry((String)ex.getKey(), BlocksColorInternal.ColorSetBuilder.from((BlocksColorInternal.ColoredSet)ex.getValue())))
            .collect(HashMap::new, (m, ex) -> m.put((String)ex.getKey(), (BlocksColorInternal.ColorSetBuilder)ex.getValue()), HashMap::putAll);
         Map<String, BlocksColorInternal.ColorSetBuilder<Item>> itemBuilder = this.itemColorSets
            .entrySet()
            .stream()
            .map(ex -> Map.entry((String)ex.getKey(), BlocksColorInternal.ColorSetBuilder.from((BlocksColorInternal.ColoredSet)ex.getValue())))
            .collect(HashMap::new, (m, ex) -> m.put((String)ex.getKey(), (BlocksColorInternal.ColorSetBuilder)ex.getValue()), HashMap::putAll);
         mods.sort(Comparator.comparingInt(m -> m.replace() ? 1 : 0));

         for (ColorSetModification mod : mods) {
            String id = mod.getId().toString();
            BlocksColorInternal.ColorSetBuilder<Block> blockSet = null;
            BlocksColorInternal.ColorSetBuilder<Item> itemSet = null;
            if (mod.hasBlocks()) {
               if (mod.replace()) {
                  blockBuilder.put(id, new BlocksColorInternal.ColorSetBuilder<>());
               }

               blockSet = blockBuilder.get(id);
            }

            if (mod.hasItems()) {
               if (mod.replace()) {
                  itemBuilder.put(id, new BlocksColorInternal.ColorSetBuilder<>());
               }

               itemSet = itemBuilder.get(id);
            }

            for (Entry<DyeColor, BlockAndItem> e : mod.entrySet()) {
               DyeColor color = e.getKey();
               Block b = e.getValue().block();
               Item i = e.getValue().item();
               if (b != null && blockSet != null) {
                  blockSet.setColor(color, b);
               }

               if (i != null && itemSet != null) {
                  itemSet.setColor(color, i);
               }
            }
         }

         Map<String, BlocksColorInternal.ColoredSet<Block>> newBlockSets = blockBuilder.entrySet()
            .stream()
            .filter(ex -> !((BlocksColorInternal.ColorSetBuilder)ex.getValue()).isEmpty())
            .collect(HashMap::new, (m, ex) -> m.put((String)ex.getKey(), ((BlocksColorInternal.ColorSetBuilder)ex.getValue()).build()), HashMap::putAll);
         Map<String, BlocksColorInternal.ColoredSet<Item>> newItemSets = itemBuilder.entrySet()
            .stream()
            .filter(ex -> !((BlocksColorInternal.ColorSetBuilder)ex.getValue()).isEmpty())
            .collect(HashMap::new, (m, ex) -> m.put((String)ex.getKey(), ((BlocksColorInternal.ColorSetBuilder)ex.getValue()).build()), HashMap::putAll);
         return new BlocksColorInternal.State(newBlockSets, newItemSets);
      }
   }
}
