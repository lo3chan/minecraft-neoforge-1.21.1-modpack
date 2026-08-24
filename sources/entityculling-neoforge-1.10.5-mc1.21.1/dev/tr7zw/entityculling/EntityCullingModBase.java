package dev.tr7zw.entityculling;

import com.logisticscraft.occlusionculling.OcclusionCullingInstance;
import dev.tr7zw.entityculling.versionless.EntityCullingVersionlessBase;
import dev.tr7zw.transition.manager.CodeManager;
import dev.tr7zw.transition.mc.ClientUtil;
import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.transition.mc.GeneralUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;

public abstract class EntityCullingModBase extends EntityCullingVersionlessBase {
   public static EntityCullingModBase instance;
   public final DebugCollector debugCollector = new DebugCollector();
   public Set<BlockEntityType<?>> blockEntityWhitelist = new HashSet<>();
   public Set<EntityType<?>> entityWhitelist = new HashSet<>();
   public Set<EntityType<?>> tickCullWhitelists = new HashSet<>();
   public CullTask cullTask;
   private Set<Function<BlockEntity, Boolean>> dynamicBlockEntityWhitelist = new HashSet<>();
   private Set<Function<Entity, Boolean>> dynamicEntityWhitelist = new HashSet<>();
   private int tickCounter = 0;
   public double lastTickTime = 0.0;
   private boolean freezeCamera = false;

   @Override
   public void onInitialize() {
      instance = this;
      super.onInitialize();
      this.culling = new OcclusionCullingInstance(this.config.tracingDistance, new Provider());
      this.cullTask = new CullTask(this.culling, this.blockEntityWhitelist, this.entityWhitelist);
      this.cullThread = new Thread(this.cullTask, "CullThread");
      this.cullThread.setUncaughtExceptionHandler((thread, ex) -> LOGGER.error("The CullingThread has crashed! Please report the following stacktrace!", ex));
      CodeManager.getInstance().registerCode("entityculling_debug", () -> this.debugCollector.requestStart());
      CodeManager.getInstance().registerCode("entityculling_freezecam", () -> {
         this.freezeCamera = !this.freezeCamera;
         ClientUtil.sendChatMessage(ComponentProvider.literal("Toggle freeze camera: " + this.freezeCamera));
      });
      this.initModloader();
   }

   public void worldTick() {
      this.cullTask.requestCull = true;
   }

   public void clientTick() {
      if (!this.lateInit) {
         this.lateInit = true;
         this.cullThread.start();

         for (String blockId : this.config.blockEntityWhitelist) {
            Optional<BlockEntityType<?>> block = BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(GeneralUtil.getResourceLocation(blockId));
            block.ifPresent(b -> this.blockEntityWhitelist.add((BlockEntityType<?>)b));
         }

         for (String entityType : this.config.tickCullingWhitelist) {
            Optional<EntityType<?>> entity = BuiltInRegistries.ENTITY_TYPE.getOptional(GeneralUtil.getResourceLocation(entityType));
            entity.ifPresent(e -> this.tickCullWhitelists.add((EntityType<?>)e));
         }

         for (String entityType : this.config.entityWhitelist) {
            Optional<EntityType<?>> entity = BuiltInRegistries.ENTITY_TYPE.getOptional(GeneralUtil.getResourceLocation(entityType));
            entity.ifPresent(e -> this.entityWhitelist.add((EntityType<?>)e));
         }
      }

      if (KeybindHolder.INSTANCE.keybind.isDown()) {
         if (this.pressed) {
            return;
         }

         this.pressed = true;
         enabled = !enabled;
         if (enabled) {
            ClientUtil.sendChatMessage(ComponentProvider.literal("Culling on").withStyle(ChatFormatting.GREEN));
         } else {
            ClientUtil.sendChatMessage(ComponentProvider.literal("Culling off").withStyle(ChatFormatting.RED));
         }
      } else {
         this.pressed = false;
      }

      if (KeybindHolder.INSTANCE.keybindBoxes.isDown()) {
         if (this.pressedBox) {
            return;
         }

         this.pressedBox = true;
         this.debugHitboxes = !this.debugHitboxes;
         if (this.debugHitboxes) {
            ClientUtil.sendChatMessage(ComponentProvider.literal("Debug Cullboxes on").withStyle(ChatFormatting.GREEN));
         } else {
            ClientUtil.sendChatMessage(ComponentProvider.literal("Debug Cullboxes off").withStyle(ChatFormatting.RED));
         }
      } else {
         this.pressedBox = false;
      }

      long start = System.nanoTime();
      this.debugCollector.tick();
      Minecraft client = Minecraft.getInstance();
      boolean ingame = client.level != null && client.player != null && client.player.tickCount > 10;
      if (ingame && enabled) {
         boolean changed = false;
         if (this.tickCounter++ % this.config.captureRate == 0) {
            if (!this.config.skipEntityCulling) {
               List<Entity> entities = StreamSupport.<Entity>stream(client.level.entitiesForRendering().spliterator(), false).toList();
               this.cullTask.setEntitiesForRendering(entities);
               this.debugCollector.getDataHolder().consideredEntities = entities.size();
            }

            if (!this.config.skipBlockEntityCulling) {
               Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();

               for (int x = -8; x <= 8; x++) {
                  for (int z = -8; z <= 8; z++) {
                     LevelChunk chunk = client.level.getChunk(client.player.chunkPosition().x + x, client.player.chunkPosition().z + z);
                     blockEntities.putAll(chunk.getBlockEntities());
                  }
               }

               this.cullTask.setBlockEntities(blockEntities);
               this.debugCollector.getDataHolder().consideredBlockEntities = blockEntities.size();
            }

            changed = true;
         }

         this.cullTask.setIngame(true);
         if (!this.freezeCamera) {
            this.cullTask.setCameraMC(instance.config.debugMode ? client.player.getEyePosition(0.0F) : client.gameRenderer.getMainCamera().getPosition());
         }

         this.cullTask.requestCull = true;
         if (changed) {
            this.lastTickTime = (System.nanoTime() - start) / 1000000.0;
         }
      } else {
         this.cullTask.setIngame(false);
         this.cullTask.setEntitiesForRendering(Collections.emptyList());
         this.cullTask.setBlockEntities(Collections.emptyMap());
         this.lastTickTime = (System.nanoTime() - start) / 1000000.0;
      }
   }

   public abstract AABB setupAABB(BlockEntity var1, BlockPos var2);

   public boolean isDynamicWhitelisted(BlockEntity entity) {
      for (Function<BlockEntity, Boolean> fun : this.dynamicBlockEntityWhitelist) {
         if (fun.apply(entity)) {
            return true;
         }
      }

      return false;
   }

   public boolean isDynamicWhitelisted(Entity entity) {
      for (Function<Entity, Boolean> fun : this.dynamicEntityWhitelist) {
         if (fun.apply(entity)) {
            return true;
         }
      }

      return false;
   }

   public void addDynamicBlockEntityWhitelist(Function<BlockEntity, Boolean> function) {
      this.dynamicBlockEntityWhitelist.add(function);
   }

   public void addDynamicEntityWhitelist(Function<Entity, Boolean> function) {
      this.dynamicEntityWhitelist.add(function);
   }
}
