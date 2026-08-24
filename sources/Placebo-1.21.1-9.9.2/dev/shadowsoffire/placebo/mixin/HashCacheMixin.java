package dev.shadowsoffire.placebo.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import net.minecraft.data.HashCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {HashCache.class},
   remap = false
)
public class HashCacheMixin {
   @Shadow
   @Final
   private Path rootDir;

   @Inject(
      method = {"purgeStaleAndWrite"},
      at = {@At(
         value = "CONSTANT",
         args = {"stringValue=version.json"}
      )}
   )
   public void placebo_skipSomeMetadata(CallbackInfo ci, @Local Set<Path> paths) throws IOException {
      paths.add(this.rootDir.resolve("META-INF" + File.separator + "coremods.json"));
      paths.add(this.rootDir.resolve("META-INF" + File.separator + "neoforge.mods.toml"));

      for (Path p : Files.newDirectoryStream(this.rootDir, "*.mixins.json")) {
         paths.add(p);
      }
   }
}
