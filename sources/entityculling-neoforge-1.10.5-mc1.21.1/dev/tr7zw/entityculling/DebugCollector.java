package dev.tr7zw.entityculling;

import com.google.gson.GsonBuilder;
import dev.tr7zw.transition.mc.ClientUtil;
import dev.tr7zw.transition.mc.ComponentProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DebugCollector {
   private boolean requestStart = false;
   private boolean running = false;
   private final DebugCollector.DataHolder dataHolder = new DebugCollector.DataHolder();

   public void requestStart() {
      this.requestStart = true;
   }

   public void tick() {
      if (this.running) {
         this.dumpData();
         this.running = false;
      }

      if (this.requestStart) {
         this.dataHolder.clear();
         this.running = true;
         this.requestStart = false;
      }
   }

   private void dumpData() {
      File file = new File("entityculling_debug_" + System.currentTimeMillis() + ".json");

      try (FileWriter writer = new FileWriter(file)) {
         writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(this.dataHolder));
      } catch (IOException var7) {
         var7.printStackTrace();
      }

      ClientUtil.sendChatMessage(ComponentProvider.literal("Debug data dumped to " + file.getAbsolutePath()).withStyle(ChatFormatting.GREEN));
      this.dataHolder.clear();
   }

   public void addEntity(Entity entity, boolean rendered, boolean ignoredCulling) {
      String id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
      if (rendered) {
         this.dataHolder.renderedEntityCounts.computeIfAbsent(id, k -> new AtomicInteger()).incrementAndGet();
         this.dataHolder.renderedEntities++;
      } else {
         this.dataHolder.skippedEntityCounts.computeIfAbsent(id, k -> new AtomicInteger()).incrementAndGet();
         this.dataHolder.skippedEntities++;
      }
   }

   public void addBlockEntity(BlockEntity blockEntity, boolean rendered) {
      String id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType()).toString();
      if (rendered) {
         this.dataHolder.renderedBlockEntityCounts.computeIfAbsent(id, k -> new AtomicInteger()).incrementAndGet();
         this.dataHolder.renderedBlockEntities++;
      } else {
         this.dataHolder.skippedBlockEntityCounts.computeIfAbsent(id, k -> new AtomicInteger()).incrementAndGet();
         this.dataHolder.skippedBlockEntities++;
      }
   }

   @Generated
   public boolean isRunning() {
      return this.running;
   }

   @Generated
   public DebugCollector.DataHolder getDataHolder() {
      return this.dataHolder;
   }

   public static class DataHolder {
      public int consideredEntities = 0;
      public int consideredBlockEntities = 0;
      public int renderedEntities = 0;
      public int skippedEntities = 0;
      public int tickedEntities = 0;
      public int skippedEntityTicks = 0;
      public int renderedBlockEntities = 0;
      public int skippedBlockEntities = 0;
      Map<String, AtomicInteger> renderedEntityCounts = new HashMap<>();
      Map<String, AtomicInteger> skippedEntityCounts = new HashMap<>();
      Map<String, AtomicInteger> renderedBlockEntityCounts = new HashMap<>();
      Map<String, AtomicInteger> skippedBlockEntityCounts = new HashMap<>();

      public void clear() {
         this.consideredEntities = 0;
         this.consideredBlockEntities = 0;
         this.renderedEntities = 0;
         this.skippedEntities = 0;
         this.tickedEntities = 0;
         this.skippedEntityTicks = 0;
         this.renderedBlockEntities = 0;
         this.skippedBlockEntities = 0;
         this.renderedEntityCounts.clear();
         this.skippedEntityCounts.clear();
         this.renderedBlockEntityCounts.clear();
         this.skippedBlockEntityCounts.clear();
      }
   }
}
