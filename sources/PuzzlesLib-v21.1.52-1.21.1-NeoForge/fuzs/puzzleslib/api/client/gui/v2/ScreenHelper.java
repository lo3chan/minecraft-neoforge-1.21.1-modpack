package fuzs.puzzleslib.api.client.gui.v2;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;

public final class ScreenHelper {
   private ScreenHelper() {
   }

   public static float getPartialTick() {
      return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
   }

   public static int getMouseX() {
      Minecraft minecraft = Minecraft.getInstance();
      return (int)(minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth());
   }

   public static int getMouseY() {
      Minecraft minecraft = Minecraft.getInstance();
      return (int)(minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight());
   }

   public static boolean isHovering(int posX, int posY, int width, int height, double mouseX, double mouseY) {
      return mouseX >= posX && mouseX < posX + width && mouseY >= posY && mouseY < posY + height;
   }

   public static boolean isEffectVisibleInInventory(MobEffectInstance mobEffect) {
      return ClientProxyImpl.get().isEffectVisibleInInventory(mobEffect);
   }

   public static boolean isEffectVisibleInGui(MobEffectInstance mobEffect) {
      return ClientProxyImpl.get().isEffectVisibleInGui(mobEffect);
   }
}
