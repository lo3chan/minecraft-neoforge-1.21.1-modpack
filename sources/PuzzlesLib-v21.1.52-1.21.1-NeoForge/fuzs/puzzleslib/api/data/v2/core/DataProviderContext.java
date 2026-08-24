package fuzs.puzzleslib.api.data.v2.core;

import com.google.common.base.Suppliers;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

public class DataProviderContext {
   private final String modId;
   private final PackOutput packOutput;
   private final Supplier<CompletableFuture<Provider>> registries;

   public DataProviderContext(String modId, PackOutput packOutput, CompletableFuture<Provider> registries) {
      this(modId, packOutput, (Supplier<CompletableFuture<Provider>>)(() -> registries));
   }

   private DataProviderContext(String modId, PackOutput packOutput, Supplier<CompletableFuture<Provider>> registries) {
      this.modId = modId;
      this.packOutput = packOutput;
      this.registries = registries;
   }

   public static DataProviderContext fromModId(String modId) {
      return fromModId(modId, Path.of(""));
   }

   public static DataProviderContext fromModId(String modId, Path path) {
      return new DataProviderContext(
         modId, new PackOutput(path), Suppliers.memoize(() -> CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor()))
      );
   }

   public String getModId() {
      return this.modId;
   }

   public PackOutput getPackOutput() {
      return this.packOutput;
   }

   public CompletableFuture<Provider> getRegistries() {
      return this.registries.get();
   }

   @Nullable
   public ResourceManager getClientResourceManager() {
      return null;
   }

   @Nullable
   public ResourceManager getServerResourceManager() {
      return null;
   }

   public DataProviderContext withRegistries(CompletableFuture<Provider> registries) {
      return new DataProviderContext(this.modId, this.packOutput, registries);
   }

   @FunctionalInterface
   public interface Factory extends Function<DataProviderContext, DataProvider> {
   }
}
