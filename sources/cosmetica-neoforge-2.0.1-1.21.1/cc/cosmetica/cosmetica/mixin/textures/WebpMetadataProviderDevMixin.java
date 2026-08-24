package cc.cosmetica.cosmetica.mixin.textures;

import cc.cosmetica.core.api.texture.FrameMetaData;
import cc.cosmetica.core.api.texture.FrameMetadataHolder;
import cc.cosmetica.core.impl.Logging;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Mixin(
   targets = {"cc/cosmetica/include/twelvemonkeys/imageio/plugins/webp/WebPImageReader"},
   remap = false
)
@Pseudo
public class WebpMetadataProviderDevMixin implements FrameMetadataHolder {
   public List<FrameMetaData> getFrameMetadata() {
      try {
         Field f = this.getClass().getDeclaredField("frames");
         f.setAccessible(true);
         List<Object> l = (List<Object>)f.get(this);
         return l.stream()
            .map(obj -> (AnimationFrameDevAccessor)obj)
            .map(meta -> new FrameMetaData(meta.getBounds(), meta.getBlend(), meta.getDispose()))
            .collect(Collectors.toList());
      } catch (IllegalAccessException | NoSuchFieldException var3) {
         Logging.getInstance().error("Error reading webp frame metadata (Dev/Fabric Prod)", var3);
         return ImmutableList.of();
      }
   }

   public Optional<int[]> getCanvasDimensions() {
      return Optional.empty();
   }
}
