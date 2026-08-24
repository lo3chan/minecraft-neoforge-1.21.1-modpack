package fuzs.puzzleslib.api.core.v1.context;

import com.google.common.base.Preconditions;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.RepositorySource;

public interface PackRepositorySourcesContext {
   @Deprecated(
      forRemoval = true
   )
   default void addRepositorySource(RepositorySource repositorySource) {
      this.registerRepositorySource(repositorySource);
   }

   @Deprecated(
      forRemoval = true
   )
   default void addRepositorySource(RepositorySource... repositorySources) {
      Objects.requireNonNull(repositorySources, "repository sources is null");
      Preconditions.checkState(repositorySources.length > 0, "repository sources is empty");

      for (RepositorySource repositorySource : repositorySources) {
         this.registerRepositorySource(repositorySource);
      }
   }

   void registerRepositorySource(RepositorySource var1);

   default void registerBuiltInPack(ResourceLocation resourceLocation) {
      Objects.requireNonNull(resourceLocation, "resource location is null");
      this.registerBuiltInPack(resourceLocation, Component.literal(resourceLocation.toString()), false);
   }

   void registerBuiltInPack(ResourceLocation var1, Component var2, boolean var3);
}
