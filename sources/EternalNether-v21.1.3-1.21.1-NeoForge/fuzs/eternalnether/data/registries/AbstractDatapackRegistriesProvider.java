package fuzs.eternalnether.data.registries;

import fuzs.eternalnether.init.ResourceKeyHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.core.RegistriesDataProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder.PatchedRegistries;
import net.minecraft.core.RegistrySetBuilder.RegistryBootstrap;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;

@Deprecated(
   forRemoval = true
)
public abstract class AbstractDatapackRegistriesProvider extends RegistriesDatapackGenerator implements RegistriesDataProvider {
   private final CompletableFuture<Provider> fullRegistries;

   public AbstractDatapackRegistriesProvider(DataProviderContext context) {
      this(context.getPackOutput(), context.getRegistries());
   }

   public AbstractDatapackRegistriesProvider(PackOutput output, CompletableFuture<Provider> registries) {
      super(output, CompletableFuture.completedFuture(RegistryAccess.EMPTY));
      CompletableFuture<PatchedRegistries> patchedRegistries = RegistryPatchGenerator.createLookup(
         registries, (RegistrySetBuilder)Util.make(new RegistrySetBuilder(), registrySetBuilder -> this.addBootstrap(registrySetBuilder::add))
      );
      this.registries = patchedRegistries.thenApply(PatchedRegistries::patches);
      this.fullRegistries = patchedRegistries.thenApply(PatchedRegistries::full);
   }

   public abstract void addBootstrap(AbstractDatapackRegistriesProvider.RegistryBoostrapConsumer var1);

   public CompletableFuture<Provider> getRegistries() {
      return this.fullRegistries;
   }

   public static void registerJukeboxSong(
      BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> resourceKey, Holder<SoundEvent> soundEvent, float lengthInSeconds, int comparatorOutput
   ) {
      context.register(resourceKey, new JukeboxSong(soundEvent, ResourceKeyHelper.getComponent(resourceKey), lengthInSeconds, comparatorOutput));
   }

   @FunctionalInterface
   public interface RegistryBoostrapConsumer {
      <T> void add(ResourceKey<? extends Registry<T>> var1, RegistryBootstrap<T> var2);
   }
}
