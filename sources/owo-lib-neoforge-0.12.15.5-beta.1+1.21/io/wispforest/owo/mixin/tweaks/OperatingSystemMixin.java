package io.wispforest.owo.mixin.tweaks;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.Util.OS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({OS.class})
public abstract class OperatingSystemMixin {
   @Shadow
   protected abstract String[] getOpenUriArguments(URI var1);

   @Overwrite
   public void openUri(URI uri) {
      CompletableFuture.runAsync(() -> {
         try {
            String[] command = this.getOpenUriArguments(uri);
            new ProcessBuilder(command).redirectError(Redirect.DISCARD).redirectOutput(Redirect.DISCARD).start();
         } catch (IOException var3) {
            LogUtils.getLogger().error("Couldn't open uri '{}'", uri, var3);
         }
      }, Util.backgroundExecutor());
   }
}
