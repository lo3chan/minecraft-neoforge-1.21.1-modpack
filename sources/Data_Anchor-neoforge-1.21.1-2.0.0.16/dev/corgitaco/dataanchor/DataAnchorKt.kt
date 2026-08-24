package dev.corgitaco.dataanchor

import dev.corgitaco.dataanchor.data.TrackedDataContainer
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistry.TrackedDataFactory
import dev.corgitaco.dataanchor.data.type.blockentity.BlockEntityTrackedData
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData
import dev.corgitaco.dataanchor.data.type.entity.EntityTrackedData
import dev.corgitaco.dataanchor.data.type.level.LevelTrackedData
import java.util.Optional
import kotlin.reflect.KClass
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.chunk.ChunkAccess

public final val container: TrackedDataContainer<Entity, EntityTrackedData>?
   public final get() {
      return TrackedDataRegistries.ENTITY.getContainer(`$this$container`);
   }


public final val container: TrackedDataContainer<BlockEntity, BlockEntityTrackedData>?
   public final get() {
      return TrackedDataRegistries.BLOCK_ENTITY.getContainer(`$this$container`);
   }


public final val container: TrackedDataContainer<Level, LevelTrackedData>?
   public final get() {
      return TrackedDataRegistries.LEVEL.getContainer(`$this$container`);
   }


public final val container: TrackedDataContainer<ChunkAccess, ChunkTrackedData>?
   public final get() {
      return TrackedDataRegistries.CHUNK.getContainer(`$this$container`);
   }


public operator fun <T : Entity, E : EntityTrackedData> Any.get(key: TrackedDataKey<Any>): Optional<Any>? {
   val var10000: TrackedDataContainer = getContainer(`$this$get`);
   return if (var10000 != null) var10000.dataAnchor$getTrackedData(key) else null;
}

public operator fun <T : BlockEntity, E : BlockEntityTrackedData> Any.get(key: TrackedDataKey<Any>): Optional<Any>? {
   val var10000: TrackedDataContainer = getContainer(`$this$get`);
   return if (var10000 != null) var10000.dataAnchor$getTrackedData(key) else null;
}

public operator fun <T : Level, E : LevelTrackedData> Any.get(key: TrackedDataKey<Any>): Optional<Any>? {
   val var10000: TrackedDataContainer = getContainer(`$this$get`);
   return if (var10000 != null) var10000.dataAnchor$getTrackedData(key) else null;
}

public operator fun <T : ChunkAccess, E : ChunkTrackedData> Any.get(key: TrackedDataKey<Any>): Optional<Any>? {
   val var10000: TrackedDataContainer = getContainer(`$this$get`);
   return if (var10000 != null) var10000.dataAnchor$getTrackedData(key) else null;
}

public fun <E : EntityTrackedData> KClass<Any>.entityDataOf(name: ResourceLocation, factory: TrackedDataFactory<Entity, Any>): TrackedDataKey<Any> {
   val var10000: TrackedDataKey = TrackedDataRegistries.ENTITY.register(name, JvmClassMappingKt.getJavaClass(`$this$entityDataOf`), factory);
   return var10000;
}

public fun <E : BlockEntityTrackedData> KClass<Any>.blockEntityDataOf(name: ResourceLocation, factory: TrackedDataFactory<BlockEntity, Any>): TrackedDataKey<
      Any
   > {
   val var10000: TrackedDataKey = TrackedDataRegistries.BLOCK_ENTITY.register(name, JvmClassMappingKt.getJavaClass(`$this$blockEntityDataOf`), factory);
   return var10000;
}

public fun <E : LevelTrackedData> KClass<Any>.levelDataOf(name: ResourceLocation, factory: TrackedDataFactory<Level, Any>): TrackedDataKey<Any> {
   val var10000: TrackedDataKey = TrackedDataRegistries.LEVEL.register(name, JvmClassMappingKt.getJavaClass(`$this$levelDataOf`), factory);
   return var10000;
}

public fun <E : ChunkTrackedData> KClass<Any>.chunkDataOf(name: ResourceLocation, factory: TrackedDataFactory<ChunkAccess, Any>): TrackedDataKey<Any> {
   val var10000: TrackedDataKey = TrackedDataRegistries.CHUNK.register(name, JvmClassMappingKt.getJavaClass(`$this$chunkDataOf`), factory);
   return var10000;
}
