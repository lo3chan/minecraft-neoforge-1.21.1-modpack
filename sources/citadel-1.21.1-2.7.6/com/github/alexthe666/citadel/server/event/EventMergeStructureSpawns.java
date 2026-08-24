package com.github.alexthe666.citadel.server.event;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.util.TriState;

public class EventMergeStructureSpawns extends Event {
   private StructureManager structureManager;
   private BlockPos pos;
   private MobCategory category;
   private WeightedRandomList<SpawnerData> structureSpawns;
   private WeightedRandomList<SpawnerData> biomeSpawns;
   private TriState result = TriState.DEFAULT;

   public EventMergeStructureSpawns(
      StructureManager structureManager,
      BlockPos pos,
      MobCategory category,
      WeightedRandomList<SpawnerData> structureSpawns,
      WeightedRandomList<SpawnerData> biomeSpawns
   ) {
      this.structureManager = structureManager;
      this.pos = pos;
      this.category = category;
      this.structureSpawns = structureSpawns;
      this.biomeSpawns = biomeSpawns;
   }

   public StructureManager getStructureManager() {
      return this.structureManager;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public MobCategory getCategory() {
      return this.category;
   }

   public boolean isStructureTagged(TagKey<Structure> tagKey) {
      return this.structureManager.getStructureWithPieceAt(this.pos, tagKey).isValid();
   }

   public WeightedRandomList<SpawnerData> getStructureSpawns() {
      return this.structureSpawns;
   }

   public void setStructureSpawns(WeightedRandomList<SpawnerData> spawns) {
      this.structureSpawns = spawns;
   }

   public void mergeSpawns() {
      List<SpawnerData> list = new ArrayList<>(this.biomeSpawns.unwrap());

      for (SpawnerData structureSpawn : this.structureSpawns.unwrap()) {
         if (!list.contains(structureSpawn)) {
            list.add(structureSpawn);
         }
      }

      this.setStructureSpawns(WeightedRandomList.create(list));
   }

   public WeightedRandomList<SpawnerData> getBiomeSpawns() {
      return this.biomeSpawns;
   }

   public TriState getResult() {
      return this.result;
   }

   public void setResult(TriState result) {
      this.result = result;
   }
}
