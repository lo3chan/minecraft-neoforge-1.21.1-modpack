package at.petrak.hexcasting.server;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.math.EulerPathFinder;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import org.jetbrains.annotations.Nullable;

public class ScrungledPatternsSave extends SavedData {
   public static final String DATA_VERSION = "0.1.0";
   public static final String TAG_SAVED_DATA = "hexcasting.per-world-patterns.0.1.0";
   private static final String TAG_DIR = "startDir";
   private static final String TAG_KEY = "key";
   private final Map<String, ScrungledPatternsSave.PerWorldEntry> lookup;
   private final Map<ResourceKey<ActionRegistryEntry>, String> reverseLookup;

   private ScrungledPatternsSave(Map<String, ScrungledPatternsSave.PerWorldEntry> lookup) {
      this.lookup = lookup;
      this.reverseLookup = new HashMap<>();
      this.lookup.forEach((sig, entry) -> this.reverseLookup.put(entry.key, sig));
   }

   @Nullable
   public ScrungledPatternsSave.PerWorldEntry lookup(String signature) {
      return this.lookup.get(signature);
   }

   @Nullable
   public Pair<String, ScrungledPatternsSave.PerWorldEntry> lookupReverse(ResourceKey<ActionRegistryEntry> key) {
      String sig = this.reverseLookup.get(key);
      return sig == null ? null : Pair.of(sig, this.lookup.get(sig));
   }

   public CompoundTag save(CompoundTag tag, Provider provider) {
      this.lookup.forEach((sig, entry) -> {
         CompoundTag inner = new CompoundTag();
         inner.putByte("startDir", (byte)entry.canonicalStartDir.ordinal());
         inner.putString("key", entry.key().location().toString());
         tag.put(sig, inner);
      });
      return tag;
   }

   private static ScrungledPatternsSave load(CompoundTag tag, Provider provider) {
      ResourceKey<? extends Registry<ActionRegistryEntry>> registryKey = IXplatAbstractions.INSTANCE.getActionRegistry().key();
      HashMap<String, ScrungledPatternsSave.PerWorldEntry> map = new HashMap<>();

      for (String sig : tag.getAllKeys()) {
         CompoundTag inner = tag.getCompound(sig);
         byte rawDir = inner.getByte("startDir");
         String rawKey = inner.getString("key");
         HexDir dir = HexDir.values()[rawDir];
         ResourceKey<ActionRegistryEntry> key = ResourceKey.create(registryKey, ResourceLocation.parse(rawKey));
         map.put(sig, new ScrungledPatternsSave.PerWorldEntry(key, dir));
      }

      return new ScrungledPatternsSave(map);
   }

   public static ScrungledPatternsSave createFromScratch(long seed) {
      ScrungledPatternsSave out = new ScrungledPatternsSave(new HashMap<>());
      out.addMissingPatterns(seed);
      out.setDirty();
      return out;
   }

   private boolean addMissingPatterns(long seed) {
      boolean changed = false;
      Registry<ActionRegistryEntry> registry = IXplatAbstractions.INSTANCE.getActionRegistry();

      for (ResourceKey<ActionRegistryEntry> key : registry.registryKeySet()) {
         ActionRegistryEntry entry = (ActionRegistryEntry)registry.get(key);
         if (HexUtils.isOfTag(registry, key, HexTags.Actions.PER_WORLD_PATTERN) && !this.reverseLookup.containsKey(key)) {
            HexPattern scrungledPat = EulerPathFinder.findAltDrawing(entry.prototype(), seed);
            String signature = scrungledPat.anglesSignature();
            ScrungledPatternsSave.PerWorldEntry perWorldEntry = new ScrungledPatternsSave.PerWorldEntry(key, scrungledPat.getStartDir());
            this.lookup.put(signature, perWorldEntry);
            this.reverseLookup.put(key, signature);
            changed = true;
         }
      }

      return changed;
   }

   public static ScrungledPatternsSave open(ServerLevel overworld) {
      ScrungledPatternsSave save = (ScrungledPatternsSave)overworld.getDataStorage()
         .computeIfAbsent(
            new Factory(() -> createFromScratch(overworld.getSeed()), ScrungledPatternsSave::load, DataFixTypes.LEVEL), "hexcasting.per-world-patterns.0.1.0"
         );
      if (save.addMissingPatterns(overworld.getSeed())) {
         save.setDirty();
      }

      return save;
   }

   public record PerWorldEntry(ResourceKey<ActionRegistryEntry> key, HexDir canonicalStartDir) {
   }
}
