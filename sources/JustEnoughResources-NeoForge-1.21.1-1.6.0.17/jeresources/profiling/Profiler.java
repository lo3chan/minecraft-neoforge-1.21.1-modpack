package jeresources.profiling;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jeresources.config.Settings;
import jeresources.json.ProfilingAdapter;
import jeresources.util.DimensionHelper;
import jeresources.util.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class Profiler implements Runnable {
   private final ConcurrentMap<ResourceKey<Level>, ProfiledDimensionData> allDimensionData;
   private final ProfilingTimer timer;
   private final Entity sender;
   private final ProfilingBlacklist blacklist;
   private final int chunkCount;
   private final boolean allDimensions;
   private ProfilingExecutor currentExecutor;
   private static Profiler currentProfiler;

   private Profiler(Entity sender, int chunkCount, boolean allDimensions) {
      this.sender = sender;
      this.allDimensionData = new ConcurrentHashMap<>();
      this.chunkCount = chunkCount;
      this.timer = new ProfilingTimer(sender, chunkCount);
      this.allDimensions = allDimensions;
      this.blacklist = new ProfilingBlacklist();
   }

   @Override
   public void run() {
      if (!this.allDimensions) {
         ResourceKey<Level> worldKey = this.sender.level().dimension();
         this.profileWorld(worldKey);
      } else {
         for (ResourceKey<Level> worldKey : this.sender.getServer().levelKeys()) {
            this.profileWorld(worldKey);
         }
      }

      this.writeData();
      this.timer.complete();
   }

   private void profileWorld(ResourceKey<Level> dimensionKey) {
      MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
      ServerLevel world = server.getLevel(dimensionKey);
      if (world == null) {
         String msg = "Unable to profile dimension " + DimensionHelper.getDimensionName(dimensionKey) + ".  There is no world for it.";
         LogHelper.error(msg);
         this.sender.sendSystemMessage(Component.literal(msg));
      } else {
         String msg = "Inspecting dimension " + DimensionHelper.getDimensionName(dimensionKey) + ". ";
         this.sender.sendSystemMessage(Component.literal(msg));
         LogHelper.info(msg);
         if (Settings.excludedDimensions.contains(dimensionKey.location().toString())) {
            msg = "Skipped dimension " + DimensionHelper.getDimensionName(dimensionKey) + " during profiling";
            LogHelper.info(msg);
            this.sender.sendSystemMessage(Component.literal(msg));
         } else {
            ProfilingExecutor executor = new ProfilingExecutor(this);
            this.currentExecutor = executor;
            this.allDimensionData.put(dimensionKey, new ProfiledDimensionData());
            DummyWorld dummyWorld = new DummyWorld(world);
            ChunkGetter chunkGetter = new ChunkGetter(this.chunkCount, dummyWorld, executor);
            world.getServer().addTickable(chunkGetter);
            executor.awaitTermination();
            this.currentExecutor = null;
         }
      }
   }

   public ProfilingTimer getTimer() {
      return this.timer;
   }

   public ProfilingBlacklist getBlacklist() {
      return this.blacklist;
   }

   public ConcurrentMap<ResourceKey<Level>, ProfiledDimensionData> getAllDimensionData() {
      return this.allDimensionData;
   }

   private void writeData() {
      Map<ResourceKey<Level>, ProfilingAdapter.DimensionData> allData = new HashMap<>();

      for (ResourceKey<Level> worldRegistryKey : this.allDimensionData.keySet()) {
         ProfiledDimensionData profiledData = this.allDimensionData.get(worldRegistryKey);
         ProfilingAdapter.DimensionData data = new ProfilingAdapter.DimensionData();
         data.dropsMap = profiledData.dropsMap;
         data.silkTouchMap = profiledData.silkTouchMap;

         for (Entry<String, Integer[]> entry : profiledData.distributionMap.entrySet()) {
            Float[] array = new Float[256];

            for (int i = 0; i < 256; i++) {
               array[i] = entry.getValue()[i].intValue() * 1.0F / (float)this.timer.getBlocksPerLayer(worldRegistryKey);
            }

            data.distribution.put(entry.getKey(), array);
         }

         allData.put(worldRegistryKey, data);
      }

      ProfilingAdapter.write(allData);
   }

   public static boolean init(Entity sender, int chunks, boolean allWorlds) {
      sender.sendSystemMessage(Component.literal("Command not yet re-implemented, profiling will be re-added in the future"));
      return true;
   }

   public static boolean stop(Entity sender) {
      sender.sendSystemMessage(Component.literal("Command not yet re-implemented, profiling will be re-added in the future"));
      return true;
   }
}
