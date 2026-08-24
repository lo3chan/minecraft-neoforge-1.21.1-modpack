package yalter.mousetweaks.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonPressed.Pre;
import net.neoforged.neoforge.client.event.ScreenEvent.MouseScrolled.Post;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import yalter.mousetweaks.Logger;
import yalter.mousetweaks.Main;
import yalter.mousetweaks.MouseButton;

@Mod("mousetweaks")
public class MouseTweaksNeo {
   public MouseTweaksNeo() {
      if (FMLEnvironment.dist != Dist.CLIENT) {
         Logger.Log("Disabled because not running on the client.");
      } else {
         Main.initialize();
         NeoForge.EVENT_BUS.register(this);
         ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, ClientHelper::new);
      }
   }

   @SubscribeEvent
   public void onGuiMouseClickedPre(Pre event) {
      Logger.DebugLog("onGuiMouseClickedPre button = " + event.getButton());
      MouseButton button = MouseButton.fromEventButton(event.getButton());
      if (button != null && Main.onMouseClicked(event.getScreen(), event.getMouseX(), event.getMouseY(), button)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public void onGuiMouseReleasedPre(net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonReleased.Pre event) {
      Logger.DebugLog("onGuiMouseReleasedPre button = " + event.getButton());
      MouseButton button = MouseButton.fromEventButton(event.getButton());
      if (button != null && Main.onMouseReleased(event.getScreen(), event.getMouseX(), event.getMouseY(), button)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public void onGuiMouseScrollPost(Post event) {
      Logger.DebugLog("onGuiMouseScrollPost delta = " + event.getScrollDeltaY());
      Main.onMouseScrolled(event.getScreen(), event.getMouseX(), event.getMouseY(), event.getScrollDeltaY());
   }

   @SubscribeEvent
   public void onGuiMouseDragPre(net.neoforged.neoforge.client.event.ScreenEvent.MouseDragged.Pre event) {
      Logger.DebugLog("onGuiMouseDragPre button = " + event.getMouseButton() + ", dx = " + event.getDragX() + ", dy = " + event.getDragY());
      MouseButton button = MouseButton.fromEventButton(event.getMouseButton());
      if (button != null && Main.onMouseDrag(event.getScreen(), event.getMouseX(), event.getMouseY(), button)) {
         event.setCanceled(true);
      }
   }
}
