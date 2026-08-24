package top.theillusivec4.curios.client.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Tuple;
import net.minecraft.world.inventory.Slot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent.Init.Post;
import net.neoforged.neoforge.client.event.ScreenEvent.Render.Pre;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.client.CuriosClientConfig;
import top.theillusivec4.curios.common.network.client.CPacketDestroy;

public class GuiEventHandler {
   @SubscribeEvent
   public void onInventoryGuiInit(Post evt) {
      Screen screen = evt.getScreen();
      if ((Boolean)CuriosClientConfig.CLIENT.enableButton.get()) {
         if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            AbstractContainerScreen<?> gui = (AbstractContainerScreen<?>)screen;
            boolean isCreative = screen instanceof CreativeModeInventoryScreen;
            Tuple<Integer, Integer> offsets = CuriosScreen.getButtonOffset(isCreative);
            int x = (Integer)offsets.getA();
            int y = (Integer)offsets.getB();
            int size = isCreative ? 8 : 10;
            int yOffset = isCreative ? 67 : 81;
            evt.addListener(
               new CuriosButton(gui, gui.getGuiLeft() + x - 2, gui.getGuiTop() + y + yOffset, size, size, isCreative ? CuriosButton.SMALL : CuriosButton.BIG)
            );
         }
      }
   }

   @SubscribeEvent
   public void onInventoryGuiDrawBackground(Pre evt) {
      if (evt.getScreen() instanceof InventoryScreen gui) {
         gui.xMouse = evt.getMouseX();
         gui.yMouse = evt.getMouseY();
      }
   }

   @SubscribeEvent
   public void onMouseClick(net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonPressed.Pre evt) {
      long handle = Minecraft.getInstance().getWindow().getWindow();
      boolean isLeftShiftDown = InputConstants.isKeyDown(handle, 340);
      boolean isRightShiftDown = InputConstants.isKeyDown(handle, 344);
      boolean isShiftDown = isLeftShiftDown || isRightShiftDown;
      if (evt.getScreen() instanceof CreativeModeInventoryScreen gui && isShiftDown) {
         if (gui.isInventoryOpen()) {
            Slot destroyItemSlot = gui.destroyItemSlot;
            Slot slot = gui.findSlot(evt.getMouseX(), evt.getMouseY());
            if (destroyItemSlot != null && slot == destroyItemSlot) {
               PacketDistributor.sendToServer(new CPacketDestroy(), new CustomPacketPayload[0]);
            }
         }
      }
   }
}
