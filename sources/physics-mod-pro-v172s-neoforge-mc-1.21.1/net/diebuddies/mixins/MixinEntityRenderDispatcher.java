package net.diebuddies.mixins;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.minecraft.EggItemRenderer;
import net.diebuddies.minecraft.EnderpearItemRenderer;
import net.diebuddies.minecraft.SnowballItemRenderer;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({EntityRenderDispatcher.class})
public class MixinEntityRenderDispatcher {
   @Shadow
   private Map<EntityType<?>, EntityRenderer<?>> renderers;

   @Inject(
      at = {@At("TAIL")},
      method = {"onResourceManagerReload"},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void onResourceManagerReload(ResourceManager manager, CallbackInfo ci, Context context) {
      Builder<EntityType<?>, EntityRenderer<?>> builder = ImmutableMap.builder();

      for (Entry<EntityType<?>, EntityRenderer<?>> entry : this.renderers.entrySet()) {
         if (entry.getKey() != EntityType.SNOWBALL && entry.getKey() != EntityType.ENDER_PEARL && entry.getKey() != EntityType.EGG) {
            builder.put(entry.getKey(), entry.getValue());
         }
      }

      builder.put(EntityType.SNOWBALL, new SnowballItemRenderer(context));
      builder.put(EntityType.ENDER_PEARL, new EnderpearItemRenderer(context));
      builder.put(EntityType.EGG, new EggItemRenderer(context));
      this.renderers = builder.build();

      for (Entry<EntityType<?>, EntityRenderer<?>> entryx : this.renderers.entrySet()) {
         PhysicsMod.renderers.put(entryx.getKey(), entryx.getValue());
      }

      PhysicsMod.renderers.put(EntityType.PLAYER, null);
   }
}
