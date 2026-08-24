package net.irisshaders.iris.mixin;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Objects;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.option.IrisVideoSettings;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DebugScreenOverlay.class})
public abstract class MixinDebugScreenOverlay {
   @Unique
   private static final List<BufferPoolMXBean> iris$pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
   @Unique
   private static final BufferPoolMXBean iris$directPool;

   @Unique
   private static String iris$humanReadableByteCountBin(long bytes) {
      long absB = bytes == -9223372036854775808L ? 9223372036854775807L : Math.abs(bytes);
      if (absB < 1024L) {
         return bytes + " B";
      } else {
         long value = absB;
         CharacterIterator ci = new StringCharacterIterator("KMGTPE");

         for (int i = 40; i >= 0 && absB > 1152865209611504844L >> i; i -= 10) {
            value >>= 10;
            ci.next();
         }

         value *= Long.signum(bytes);
         return String.format("%.3f %ciB", value / 1024.0, ci.current());
      }
   }

   @Unique
   private static long iris$getNativeMemoryUsage() {
      return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
   }

   @Inject(
      method = {"getSystemInformation"},
      at = {@At("RETURN")}
   )
   private void iris$appendShaderPackText(CallbackInfoReturnable<List<String>> cir) {
      List<String> messages = (List<String>)cir.getReturnValue();
      messages.add("");
      messages.add("[Iris] Version: " + Iris.getFormattedVersion());
      messages.add("");
      if (Iris.getIrisConfig().areShadersEnabled()) {
         messages.add("[Iris] Shaderpack: " + Iris.getCurrentPackName() + (Iris.isFallback() ? " (fallback)" : ""));
         Iris.getCurrentPack().ifPresent(pack -> messages.add("[Iris] " + pack.getProfileInfo()));
         messages.add("[Iris] Color space: " + IrisVideoSettings.colorSpace.name());
      } else {
         messages.add("[Iris] Shaders are disabled");
      }

      messages.add(3, "Direct Buffers: +" + iris$humanReadableByteCountBin(iris$directPool.getMemoryUsed()));
   }

   @Inject(
      method = {"getGameInformation"},
      at = {@At("RETURN")}
   )
   private void iris$appendShadowDebugText(CallbackInfoReturnable<List<String>> cir) {
      List<String> messages = (List<String>)cir.getReturnValue();
      Iris.getPipelineManager().getPipeline().ifPresent(pipeline -> pipeline.addDebugText(messages));
   }

   static {
      BufferPoolMXBean found = null;

      for (BufferPoolMXBean pool : iris$pools) {
         if (pool.getName().equals("direct")) {
            found = pool;
            break;
         }
      }

      iris$directPool = Objects.requireNonNull(found);
   }
}
