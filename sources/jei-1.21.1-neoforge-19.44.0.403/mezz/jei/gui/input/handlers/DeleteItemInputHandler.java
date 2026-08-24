package mezz.jei.gui.input.handlers;

import java.util.Optional;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.GiveMode;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.common.network.packets.PacketDeletePlayerItem;
import mezz.jei.common.util.ServerCommandUtil;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.overlay.ingredients.IIngredientGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DeleteItemInputHandler implements IUserInputHandler {
   private final IIngredientGrid ingredientGrid;
   private final IClientToggleState toggleState;
   private final IClientConfig clientConfig;
   private final IConnectionToServer serverConnection;
   private final IIngredientManager ingredientManager;

   public DeleteItemInputHandler(
      IIngredientGrid ingredientGrid,
      IClientToggleState toggleState,
      IClientConfig clientConfig,
      IConnectionToServer serverConnection,
      IIngredientManager ingredientManager
   ) {
      this.ingredientGrid = ingredientGrid;
      this.toggleState = toggleState;
      this.clientConfig = clientConfig;
      this.serverConnection = serverConnection;
      this.ingredientManager = ingredientManager;
   }

   @Override
   public Optional<IUserInputHandler> handleUserInput(Screen screen, UserInput userInput, IInternalKeyMappings keyBindings) {
      if (!userInput.is(keyBindings.getLeftClick())) {
         return Optional.empty();
      } else {
         double mouseX = userInput.getMouseX();
         double mouseY = userInput.getMouseY();
         if (!this.ingredientGrid.isMouseOver(mouseX, mouseY)) {
            return Optional.empty();
         } else {
            Minecraft minecraft = Minecraft.getInstance();
            if (!this.shouldDeleteItemOnClick(minecraft, mouseX, mouseY)) {
               return Optional.empty();
            } else {
               LocalPlayer player = minecraft.player;
               if (player == null) {
                  return Optional.empty();
               } else {
                  ItemStack itemStack = player.containerMenu.getCarried();
                  if (itemStack.isEmpty()) {
                     return Optional.empty();
                  } else {
                     if (!userInput.isSimulate()) {
                        player.containerMenu.setCarried(ItemStack.EMPTY);
                        PacketDeletePlayerItem packet = new PacketDeletePlayerItem(itemStack);
                        this.serverConnection.sendPacketToServer(packet);
                     }

                     return Optional.of(this);
                  }
               }
            }
         }
      }
   }

   public void drawTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      JeiTooltip tooltip = new JeiTooltip();
      tooltip.add(Component.translatable("jei.tooltip.delete.item"));
      tooltip.draw(guiGraphics, mouseX, mouseY);
   }

   public boolean shouldDeleteItemOnClick(Minecraft minecraft, double mouseX, double mouseY) {
      if (this.toggleState.isCheatItemsEnabled() && this.serverConnection.isJeiOnServer()) {
         Player player = minecraft.player;
         if (player == null) {
            return false;
         } else {
            ItemStack itemStack = player.containerMenu.getCarried();
            if (itemStack.isEmpty()) {
               return false;
            } else {
               GiveMode giveMode = this.clientConfig.giveMode().getValue();
               return giveMode == GiveMode.MOUSE_PICKUP
                  ? this.ingredientGrid
                     .getIngredientUnderMouse(mouseX, mouseY)
                     .findFirst()
                     .map(c -> c.getCheatItemStack(this.ingredientManager))
                     .map(i -> !ServerCommandUtil.canStack(itemStack, i))
                     .orElse(true)
                  : true;
            }
         }
      } else {
         return false;
      }
   }
}
