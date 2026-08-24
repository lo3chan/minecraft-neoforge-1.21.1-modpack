package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

public class KubeJSResourcePackFinder implements RepositorySource {
   public void loadPacks(Consumer<Pack> nameToPackMap) {
      if (KubeJSPaths.FIRST_RUN.getValue()) {
         Path blockTextures = KubeJSPaths.dir(KubeJSPaths.ASSETS.resolve("kubejs/textures/block"));
         Path itemTextures = KubeJSPaths.dir(KubeJSPaths.ASSETS.resolve("kubejs/textures/item"));

         try (
            InputStream in = Files.newInputStream(
               KubeJS.thisMod.getModInfo().getOwningFile().getFile().findResource(new String[]{"data", "kubejs", "example_block_texture.png"})
            );
            OutputStream out = Files.newOutputStream(blockTextures.resolve("example_block.png"));
         ) {
            in.transferTo(out);
         } catch (Exception var17) {
            var17.printStackTrace();
         }

         try (
            InputStream in = Files.newInputStream(
               KubeJS.thisMod.getModInfo().getOwningFile().getFile().findResource(new String[]{"data", "kubejs", "example_item_texture.png"})
            );
            OutputStream out = Files.newOutputStream(itemTextures.resolve("example_item.png"));
         ) {
            in.transferTo(out);
         } catch (Exception var14) {
            var14.printStackTrace();
         }
      }
   }
}
