package dev.shadowsoffire.placebo.mixin;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import dev.shadowsoffire.placebo.datagen.FieldOrderingFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(
   value = {DataProvider.class},
   remap = false
)
public interface DataProviderMixin {
   @Overwrite
   static CompletableFuture<?> saveStable(CachedOutput output, JsonElement json, Path path) {
      return CompletableFuture.runAsync(() -> {
         try {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha1(), bytearrayoutputstream);
            JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8));

            try {
               jsonwriter.setSerializeNulls(false);
               jsonwriter.setIndent(" ".repeat(Math.max(0, DataProvider.INDENT_WIDTH.get())));
               GsonHelper.writeValue(jsonwriter, json, FieldOrderingFactory.Impl.getComparatorFor(json, path));
            } catch (Throwable var9) {
               try {
                  jsonwriter.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            jsonwriter.close();
            output.writeIfNeeded(path, bytearrayoutputstream.toByteArray(), hashingoutputstream.hash());
         } catch (IOException var10) {
            DataProvider.LOGGER.error("Failed to save file to {}", path, var10);
         }
      }, Util.backgroundExecutor());
   }
}
