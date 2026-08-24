package com.finndog.moogs_structures.misc.trialspawnerconfig;

import com.finndog.moogs_structures.MoogsStructuresCommon;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

public class TrialSpawnerConfigManager extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = new Gson();
   public static final TrialSpawnerConfigManager INSTANCE = new TrialSpawnerConfigManager();
   private Map<ResourceLocation, CompoundTag> configs = new HashMap<>();

   public TrialSpawnerConfigManager() {
      super(GSON, "trial_spawner");
   }

   protected void apply(Map<ResourceLocation, JsonElement> loader, ResourceManager manager, ProfilerFiller profiler) {
      Map<ResourceLocation, CompoundTag> builder = new HashMap<>();
      loader.forEach((id, json) -> {
         try {
            Tag asNbt = (Tag)new Dynamic(JsonOps.INSTANCE, json).convert(NbtOps.INSTANCE).getValue();
            if (asNbt instanceof CompoundTag compound) {
               builder.put(id, compound);
            } else {
               MoogsStructuresCommon.LOGGER.error("Moog's Structure Lib Error: trial_spawner config {} is not a JSON object", id);
            }
         } catch (Exception var5) {
            MoogsStructuresCommon.LOGGER.error("Moog's Structure Lib Error: couldn't parse trial_spawner config {} - JSON: {}", id, json, var5);
         }
      });
      this.configs = builder;
   }

   @Nullable
   public CompoundTag get(ResourceLocation id) {
      return this.configs.get(id);
   }
}
