package cc.cosmetica.cosmetica.mixin.textures;

import java.awt.Rectangle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
   targets = {"cc/cosmetica/include/twelvemonkeys/imageio/plugins/webp/AnimationFrame"},
   remap = false
)
@Pseudo
public interface AnimationFrameProdAccessor {
   @Accessor("bounds")
   Rectangle getBounds();

   @Accessor("blend")
   boolean getBlend();

   @Accessor("dispose")
   boolean getDispose();
}
