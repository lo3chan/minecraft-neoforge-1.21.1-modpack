package forge.me.thosea.badoptimizations.mixin.renderer.entity;

import forge.me.thosea.badoptimizations.other.PlayerModelRendererHolder;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.PlayerSkin.Model;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({AbstractClientPlayer.class})
public abstract class MixinClientPlayer extends MixinEntity {
   @Shadow
   public abstract PlayerSkin getSkin();

   @Override
   public EntityRenderer<?> bo$getRenderer() {
      Model model = this.getSkin().model();
      if (model == Model.WIDE) {
         return PlayerModelRendererHolder.WIDE_RENDERER;
      } else {
         return model == Model.SLIM ? PlayerModelRendererHolder.SLIM_RENDERER : null;
      }
   }
}
