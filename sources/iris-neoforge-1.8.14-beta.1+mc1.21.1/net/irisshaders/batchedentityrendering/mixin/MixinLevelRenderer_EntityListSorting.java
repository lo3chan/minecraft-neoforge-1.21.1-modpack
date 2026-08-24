package net.irisshaders.batchedentityrendering.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
   value = {LevelRenderer.class},
   priority = 999
)
public class MixinLevelRenderer_EntityListSorting {
   @Shadow
   private ClientLevel level;

   @WrapOperation(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;"
      )}
   )
   private Iterator<Entity> batchedentityrendering$sortEntityList(Iterable<Entity> instance, Operation<Iterator<Entity>> original) {
      this.level.getProfiler().push("sortEntityList");
      Map<EntityType<?>, List<Entity>> sortedEntities = new HashMap<>();
      List<Entity> entities = new ArrayList<>();
      ((Iterator)original.call(new Object[]{instance}))
         .forEachRemaining(entity -> sortedEntities.computeIfAbsent(entity.getType(), entityType -> new ArrayList<>(32)).add(entity));
      sortedEntities.values().forEach(entities::addAll);
      this.level.getProfiler().pop();
      return entities.iterator();
   }
}
