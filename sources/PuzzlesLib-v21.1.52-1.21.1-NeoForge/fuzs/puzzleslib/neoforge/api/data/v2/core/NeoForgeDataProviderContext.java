package fuzs.puzzleslib.neoforge.api.data.v2.core;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class NeoForgeDataProviderContext extends DataProviderContext {
   private final ResourceManager clientResourceManager;
   private final ResourceManager serverResourceManager;
   private final ExistingFileHelper fileHelper;

   public NeoForgeDataProviderContext(
      String modId,
      PackOutput packOutput,
      CompletableFuture<Provider> registries,
      ResourceManager clientResourceManager,
      ResourceManager serverResourceManager,
      ExistingFileHelper fileHelper
   ) {
      super(modId, packOutput, registries);
      this.clientResourceManager = clientResourceManager;
      this.serverResourceManager = serverResourceManager;
      this.fileHelper = fileHelper;
   }

   public static NeoForgeDataProviderContext fromEvent(GatherDataEvent event) {
      return fromEvent(event, event.getGenerator().getPackOutput(), event.getLookupProvider());
   }

   public static NeoForgeDataProviderContext fromEvent(GatherDataEvent event, PackOutput packOutput, CompletableFuture<Provider> registries) {
      return new NeoForgeDataProviderContext(
         event.getModContainer().getModId(),
         packOutput,
         registries,
         event.getResourceManager(PackType.CLIENT_RESOURCES),
         event.getResourceManager(PackType.SERVER_DATA),
         event.getExistingFileHelper()
      );
   }

   @Override
   public ResourceManager getClientResourceManager() {
      return this.clientResourceManager;
   }

   @Override
   public ResourceManager getServerResourceManager() {
      return this.serverResourceManager;
   }

   public NeoForgeDataProviderContext withRegistries(CompletableFuture<Provider> registries) {
      return new NeoForgeDataProviderContext(
         this.getModId(), this.getPackOutput(), registries, this.clientResourceManager, this.serverResourceManager, this.fileHelper
      );
   }

   public ExistingFileHelper getFileHelper() {
      return this.fileHelper;
   }

   @FunctionalInterface
   public interface Factory extends Function<NeoForgeDataProviderContext, DataProvider> {
   }

   @FunctionalInterface
   public interface LegacyFactory extends BiFunction<GatherDataEvent, String, DataProvider> {
   }
}
