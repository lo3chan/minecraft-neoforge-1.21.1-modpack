package fuzs.puzzleslib.neoforge.api.data.v2.client;

import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

@Deprecated
public abstract class AbstractSpriteSourceProvider extends SpriteSourceProvider {
   public AbstractSpriteSourceProvider(NeoForgeDataProviderContext context) {
      this(context.getModId(), context.getPackOutput(), context.getRegistries(), context.getFileHelper());
   }

   public AbstractSpriteSourceProvider(String modId, PackOutput packOutput, CompletableFuture<Provider> lookupProvider, ExistingFileHelper fileHelper) {
      super(packOutput, lookupProvider, modId, fileHelper);
   }

   protected final void gather() {
      this.addSpriteSources();
   }

   public abstract void addSpriteSources();

   public String getName() {
      return "Sprite Sources";
   }
}
