package fuzs.visualworkbench.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext.Factory;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.data.ModBlockTagsProvider;
import net.neoforged.fml.common.Mod;

@Mod("visualworkbench")
public class VisualWorkbenchNeoForge {
   public VisualWorkbenchNeoForge() {
      ModConstructor.construct("visualworkbench", VisualWorkbench::new);
      DataProviderHelper.registerDataProviders("visualworkbench", new Factory[]{ModBlockTagsProvider::new});
   }
}
