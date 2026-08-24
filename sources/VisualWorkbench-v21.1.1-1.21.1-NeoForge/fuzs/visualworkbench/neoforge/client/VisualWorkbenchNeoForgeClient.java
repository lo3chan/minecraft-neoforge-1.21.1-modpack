package fuzs.visualworkbench.neoforge.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext.Factory;
import fuzs.visualworkbench.client.VisualWorkbenchClient;
import fuzs.visualworkbench.data.client.ModLanguageProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(
   value = "visualworkbench",
   dist = {Dist.CLIENT}
)
public class VisualWorkbenchNeoForgeClient {
   public VisualWorkbenchNeoForgeClient() {
      ClientModConstructor.construct("visualworkbench", VisualWorkbenchClient::new);
      DataProviderHelper.registerDataProviders("visualworkbench", new Factory[]{ModLanguageProvider::new});
   }
}
