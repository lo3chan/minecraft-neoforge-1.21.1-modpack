package net.diebuddies.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.World;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TextureManager.class})
public class MixinTextureManager {
   @Unique
   private boolean loadedCloth = false;

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   private void constructor(ResourceManager resourceManager, CallbackInfo info) {
      if (!this.loadedCloth && RenderSystem.isOnRenderThreadOrInit()) {
         this.loadedCloth = true;
         PhysicsMod.loadCloth();
         new World(new Vec2(0.0F, 0.0F));
      }
   }
}
