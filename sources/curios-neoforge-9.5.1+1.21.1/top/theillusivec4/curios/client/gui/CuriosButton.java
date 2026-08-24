package top.theillusivec4.curios.client.gui;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.client.ICuriosScreen;
import top.theillusivec4.curios.common.network.client.CPacketOpenCurios;
import top.theillusivec4.curios.common.network.client.CPacketOpenVanilla;

public class CuriosButton extends ImageButton {
   public static final WidgetSprites BIG = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("curios", "button"), ResourceLocation.fromNamespaceAndPath("curios", "button_highlighted")
   );
   public static final WidgetSprites SMALL = new WidgetSprites(
      ResourceLocation.fromNamespaceAndPath("curios", "button_small"), ResourceLocation.fromNamespaceAndPath("curios", "button_small_highlighted")
   );
   private final AbstractContainerScreen<?> parentGui;

   CuriosButton(AbstractContainerScreen<?> parentGui, int xIn, int yIn, int widthIn, int heightIn, WidgetSprites sprites) {
      super(xIn, yIn, widthIn, heightIn, sprites, button -> {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null) {
            ItemStack stack = mc.player.containerMenu.getCarried();
            mc.player.containerMenu.setCarried(ItemStack.EMPTY);
            if (parentGui instanceof ICuriosScreen) {
               InventoryScreen inventory = new InventoryScreen(mc.player);
               mc.setScreen(inventory);
               mc.player.containerMenu.setCarried(stack);
               PacketDistributor.sendToServer(new CPacketOpenVanilla(stack), new CustomPacketPayload[0]);
            } else {
               if (parentGui instanceof InventoryScreen inventory) {
                  RecipeBookComponent recipeBookGui = inventory.getRecipeBookComponent();
                  if (recipeBookGui.isVisible()) {
                     recipeBookGui.toggleVisibility();
                  }
               }

               PacketDistributor.sendToServer(new CPacketOpenCurios(stack), new CustomPacketPayload[0]);
            }
         }
      });
      this.parentGui = parentGui;
   }

   public void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      Tuple<Integer, Integer> offsets = CuriosScreen.getButtonOffset(this.parentGui instanceof CreativeModeInventoryScreen);
      this.setX(this.parentGui.getGuiLeft() + (Integer)offsets.getA() + 2);
      int yOffset = this.parentGui instanceof CreativeModeInventoryScreen ? 70 : 85;
      this.setY(this.parentGui.getGuiTop() + (Integer)offsets.getB() + yOffset);
      if (this.parentGui instanceof CreativeModeInventoryScreen gui) {
         boolean isInventoryTab = gui.isInventoryOpen();
         this.active = isInventoryTab;
         if (!isInventoryTab) {
            return;
         }
      }

      super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
   }
}
