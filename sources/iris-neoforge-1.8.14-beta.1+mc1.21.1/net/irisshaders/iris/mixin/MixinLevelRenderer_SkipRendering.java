package net.irisshaders.iris.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.Set;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LevelRenderer.class})
public class MixinLevelRenderer_SkipRendering {
   @Unique
   private static final ObjectArrayList<RenderSection> EMPTY_LIST = new ObjectArrayList();

   @WrapWithCondition(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;setupRender(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;ZZ)V"
      )}
   )
   private boolean skipSetupRender(LevelRenderer instance, Camera camera, Frustum frustum, boolean bl, boolean bl2) {
      return Iris.getPipelineManager().getPipelineNullable() instanceof IrisRenderingPipeline pipeline ? !pipeline.skipAllRendering() : true;
   }

   @WrapWithCondition(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;DDDLorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"
      )}
   )
   private boolean skipRenderChunks(LevelRenderer instance, RenderType renderType, double d, double e, double f, Matrix4f matrix4f, Matrix4f matrix4f2) {
      return Iris.getPipelineManager().getPipelineNullable() instanceof IrisRenderingPipeline pipeline ? !pipeline.skipAllRendering() : true;
   }

   @WrapOperation(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/ClientLevel;entitiesForRendering()Ljava/lang/Iterable;"
      )}
   )
   private Iterable<Entity> skipRenderEntities(ClientLevel instance, Operation<Iterable<Entity>> original) {
      return (Iterable<Entity>)(Iris.getPipelineManager().getPipelineNullable() instanceof IrisRenderingPipeline pipeline && pipeline.skipAllRendering()
         ? Collections.emptyList()
         : (Iterable)original.call(new Object[]{instance}));
   }

   @WrapOperation(
      method = {"renderLevel"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;visibleSections:Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"
      )}
   )
   private ObjectArrayList<RenderSection> skipLocalBlockEntities(LevelRenderer instance, Operation<ObjectArrayList<RenderSection>> original) {
      return Iris.getPipelineManager().getPipelineNullable() instanceof IrisRenderingPipeline pipeline && pipeline.skipAllRendering()
         ? EMPTY_LIST
         : (ObjectArrayList)original.call(new Object[]{instance});
   }

   @WrapOperation(
      method = {"renderLevel"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/renderer/LevelRenderer;globalBlockEntities:Ljava/util/Set;"
      )}
   )
   private Set<BlockEntity> skipGlobalBlockEntities(LevelRenderer instance, Operation<Set<BlockEntity>> original) {
      return Iris.getPipelineManager().getPipelineNullable() instanceof IrisRenderingPipeline pipeline && pipeline.skipAllRendering()
         ? Collections.emptySet()
         : (Set)original.call(new Object[]{instance});
   }
}
