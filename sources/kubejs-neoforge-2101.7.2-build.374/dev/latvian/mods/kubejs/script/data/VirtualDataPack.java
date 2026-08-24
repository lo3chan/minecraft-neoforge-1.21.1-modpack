package dev.latvian.mods.kubejs.script.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.registries.datamaps.DataMapFile;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class VirtualDataPack extends VirtualResourcePack implements KubeDataGenerator {
   private final Map<DataMapType<?, ?>, VirtualDataMapFile<?, ?>> dataMaps = new HashMap<>();

   public VirtualDataPack(GeneratedDataStage stage, Supplier<RegistryAccessContainer> registries) {
      super(ScriptType.SERVER, PackType.SERVER_DATA, stage, registries);
   }

   @Override
   public <R, T> void dataMap(DataMapType<R, T> type, Consumer<VirtualDataMapFile<R, T>> consumer) {
      VirtualDataMapFile<R, T> map = Cast.to(this.dataMaps.computeIfAbsent(type, k -> new VirtualDataMapFile(type, this)));
      consumer.accept(map);
   }

   @Override
   public void reset() {
      super.reset();
      this.dataMaps.clear();
   }

   @Override
   public void flush() {
      super.flush();
      RegistryOps<JsonElement> jsonOps = this.getRegistries().json();

      for (Entry<DataMapType<?, ?>, VirtualDataMapFile<?, ?>> typeEntry : this.dataMaps.entrySet()) {
         DataMapType<?, ?> type = typeEntry.getKey();
         ResourceLocation id = type.id();
         ResourceKey<? extends Registry<?>> registry = type.registryKey();
         ResourceLocation regId = registry.location();
         Codec<? extends DataMapFile<?, ?>> codec = DataMapFile.codec(Cast.to(registry), type);
         VirtualDataMapFile<?, ?> data = typeEntry.getValue();
         DataMapFile<?, ?> file = data.toFile();
         JsonElement json = (JsonElement)codec.encodeStart(jsonOps, (DataMapFile)Cast.to(file))
            .getOrThrow(str -> new RuntimeException("Failed to encode data for %s / %s / %s: %s".formatted(regId, id, data, str)));
         this.add(GeneratedData.json(id.withPath("data_maps/%s/%s.json".formatted(ID.resourcePath(regId), id.getPath())), () -> json));
      }

      this.dataMaps.clear();
   }
}
