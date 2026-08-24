package io.wispforest.owo.moddata;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.wispforest.owo.Owo;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;
import org.apache.commons.io.FilenameUtils;

public final class ModDataLoader {
   private static final Gson GSON = new Gson();
   private static final Path DATA_PATH = FMLLoader.getGamePath().resolve("moddata");

   private ModDataLoader() {
   }

   public static void load(ModDataConsumer consumer) {
      Map<ResourceLocation, JsonObject> foundFiles = new HashMap<>();
      LoadingModList.get()
         .getMods()
         .forEach(
            modInfo -> {
               Path targetPath = modInfo.getOwningFile()
                  .getFile()
                  .getSecureJar()
                  .getRootPath()
                  .resolve(String.format("data/%s/%s", modInfo.getModId(), consumer.getDataSubdirectory()));
               tryLoadFilesFrom(foundFiles, modInfo.getModId(), targetPath);
            }
         );

      try {
         Files.createDirectories(DATA_PATH);
         Files.list(DATA_PATH).forEach(nsPath -> {
            if (Files.isDirectory(nsPath)) {
               String namespace = nsPath.getFileName().toString();
               Path targetPath = nsPath.resolve(consumer.getDataSubdirectory());
               if (Files.exists(targetPath)) {
                  tryLoadFilesFrom(foundFiles, namespace, targetPath);
               }
            }
         });
      } catch (IOException var3) {
         Owo.LOGGER.error("### Unable to traverse global data tree ++ Stacktrace below ###", var3);
      }

      foundFiles.forEach(consumer::acceptParsedFile);
   }

   private static void tryLoadFilesFrom(Map<ResourceLocation, JsonObject> foundFiles, String namespace, Path targetPath) {
      try {
         if (!Files.exists(targetPath)) {
            return;
         }

         Files.walk(targetPath)
            .forEach(
               path -> {
                  if (path.toString().endsWith(".json")) {
                     try {
                        InputStreamReader tabData = new InputStreamReader(Files.newInputStream(path));
                        foundFiles.put(
                           ResourceLocation.fromNamespaceAndPath(namespace, FilenameUtils.removeExtension(targetPath.relativize(path).toString())),
                           (JsonObject)GSON.fromJson(tabData, JsonObject.class)
                        );
                     } catch (IOException var5) {
                        Owo.LOGGER.warn("### Unable to open data file {} ++ Stacktrace below ###", path, var5);
                        var5.printStackTrace();
                     }
                  }
               }
            );
      } catch (IOException var4) {
         Owo.LOGGER.error("### Unable to traverse data tree {} ++ Stacktrace below ###", targetPath, var4);
      }
   }
}
