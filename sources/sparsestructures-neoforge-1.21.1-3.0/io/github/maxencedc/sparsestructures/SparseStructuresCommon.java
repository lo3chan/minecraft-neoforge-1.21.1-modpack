package io.github.maxencedc.sparsestructures;

import com.google.gson.Gson;
import io.github.maxencedc.sparsestructures.platform.Services;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SparseStructuresCommon {
   public static SparseStructuresConfig config;

   public static void init() {
      if (Services.PLATFORM.isModLoaded("sparsestructures")) {
         if (!Constants.CONFIG_FILE_PATH.toFile().exists()) {
            try (InputStream in = SparseStructuresCommon.class.getClassLoader().getResourceAsStream("sparse-structures-default-config.json5")) {
               if (in == null) {
                  throw new IllegalStateException("Failed to load SparseStructure's default config \"sparse-structures-default-config.json5\"");
               }

               Files.createDirectories(Constants.CONFIG_FILE_PATH);
               Files.copy(in, Constants.CONFIG_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception var8) {
               throw new RuntimeException(var8);
            }
         }

         try (InputStream in = Files.newInputStream(Constants.CONFIG_FILE_PATH)) {
            config = (SparseStructuresConfig)new Gson().fromJson(new InputStreamReader(in), SparseStructuresConfig.class);
         } catch (Exception var6) {
            throw new RuntimeException(
               "SparseStructure's config file is malformed! If you don't know what's causing this, delete the config file and restart the game."
            );
         }
      }
   }
}
