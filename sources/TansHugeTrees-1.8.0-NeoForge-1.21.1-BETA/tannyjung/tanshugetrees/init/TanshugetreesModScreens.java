package tannyjung.tanshugetrees.init;

import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import tannyjung.tanshugetrees.client.gui.TreeSummonerStaffGUIScreen;

@EventBusSubscriber({Dist.CLIENT})
public class TanshugetreesModScreens {
   @SubscribeEvent
   public static void clientLoad(RegisterMenuScreensEvent event) {
      event.register((MenuType)TanshugetreesModMenus.TREE_SUMMONER_STAFF_GUI.get(), TreeSummonerStaffGUIScreen::new);
   }

   public interface ScreenAccessor {
      void updateMenuState(int var1, String var2, Object var3);
   }
}
