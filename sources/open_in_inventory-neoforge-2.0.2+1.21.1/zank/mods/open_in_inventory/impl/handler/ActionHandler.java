package zank.mods.open_in_inventory.impl.handler;

import dev.architectury.event.EventResult;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.Nullable;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.mixin.AccessHandledScreen;

public class ActionHandler {
   private int swapFrom;
   private int swapTo;
   private ActionHandler.ActionStage stage;
   private long itemUseAtTime;
   private OpenAction action;
   private boolean shouldUpdateSneak;

   public ActionHandler() {
      this.reset();
   }

   public void reset() {
      this.stage = ActionHandler.ActionStage.IDLE;
      this.itemUseAtTime = -1L;
      this.action = null;
      this.shouldUpdateSneak = false;
   }

   public EventResult beforeMouseClicked(Minecraft client, Screen _screen, double mouseX, double mouseY, int button) {
      if (button != 1) {
         return EventResult.pass();
      } else {
         LocalPlayer player = client.player;
         ClientLevel world = client.level;
         if (player != null && world != null && _screen instanceof AbstractContainerScreen<?> screen) {
            Slot focused = ((AccessHandledScreen)screen).getHoveredSlot();
            OpenAction matched = this.matchAction(_screen, player, focused);
            if (matched == null) {
               return EventResult.pass();
            } else {
               this.swapFrom = focused.getContainerSlot();
               this.swapTo = player.getInventory().selected;
               ItemStack stackBeforeSwap = focused.getItem();
               if (OpenInInventory.CONFIG.debug()) {
                  OpenInInventory.LOGGER
                     .info(
                        "IDLE -> SWAPPED, attempt to swap slot(index {}, id {}) with hotbar {} in gui {}",
                        focused.getContainerSlot(),
                        focused.index,
                        this.swapTo,
                        screen
                     );
               }

               if (this.swapFrom < 9) {
                  player.getInventory().selected = this.swapFrom;
               } else {
                  int actualSwapFrom = screen instanceof EffectRenderingInventoryScreen ? focused.getContainerSlot() : focused.index;
                  this.performSwap(client, screen, actualSwapFrom, player);
               }

               if (player.getMainHandItem() != stackBeforeSwap) {
                  return EventResult.pass();
               } else {
                  this.shouldUpdateSneak = matched.sneak() != client.options.keyShift.isDown();
                  if (this.shouldUpdateSneak) {
                     if ((Boolean)client.options.toggleCrouch().get()) {
                        client.options.keyShift.setDown(true);
                     } else {
                        client.options.keyShift.setDown(matched.sneak());
                     }
                  }

                  this.itemUseAtTime = world.getGameTime() + OpenInInventory.CONFIG.openDelay();
                  this.stage = ActionHandler.ActionStage.SWAPPED;
                  this.action = matched;
                  return EventResult.interruptFalse();
               }
            }
         } else {
            return EventResult.pass();
         }
      }
   }

   private void performSwap(Minecraft client, AbstractContainerScreen<?> screen, int swapFrom, LocalPlayer player) {
      assert client.gameMode != null;

      client.gameMode.handleInventoryMouseClick(screen.getMenu().containerId, swapFrom, this.swapTo, ClickType.SWAP, player);
   }

   public void tick(ClientLevel world) {
      Minecraft client = Minecraft.getInstance();
      LocalPlayer player = client.player;
      if (this.stage == ActionHandler.ActionStage.SWAPPED && player != null && world.getGameTime() >= this.itemUseAtTime) {
         OpenAction action = this.action;
         if (action != null && action.match(player.getMainHandItem())) {
            player.connection.send(new ServerboundContainerClosePacket(player.containerMenu.containerId));
            player.containerMenu = player.inventoryMenu;

            assert client.gameMode != null;

            client.gameMode.useItem(player, InteractionHand.MAIN_HAND);
            if (this.shouldUpdateSneak) {
               if ((Boolean)client.options.toggleCrouch().get()) {
                  client.options.keyShift.setDown(true);
               } else {
                  client.options.keyShift.setDown(!action.sneak());
               }
            }

            if (OpenInInventory.CONFIG.debug()) {
               OpenInInventory.LOGGER.info("SWAPPED -> USED, action match: {}", action);
            }
         } else if (OpenInInventory.CONFIG.debug()) {
            OpenInInventory.LOGGER.info("SWAPPED -> USED, skipped using");
         }

         this.stage = ActionHandler.ActionStage.USED;
      } else if (this.stage == ActionHandler.ActionStage.SWAP_BACK_SCREEN && player != null && client.screen instanceof EffectRenderingInventoryScreen<?> inv) {
         NonNullList<Slot> slots = inv.getMenu().slots;
         if (this.swapFrom < 9) {
            player.getInventory().selected = this.swapTo;
         } else if (this.swapFrom < slots.size() && inv.getMenu().canDragTo((Slot)slots.get(this.swapFrom))) {
            this.performSwap(client, inv, this.swapFrom, player);
            if (OpenInInventory.CONFIG.debug()) {
               OpenInInventory.LOGGER.info("SWAP_BACK_SCREEN -> IDLE, from {}, to {}, screen {}", this.swapFrom, this.swapTo, client.screen);
            }
         } else if (OpenInInventory.CONFIG.debug()) {
            OpenInInventory.LOGGER.info("SWAP_BACK_SCREEN -> IDLE, swap skipped");
         }

         this.stage = ActionHandler.ActionStage.IDLE;
      }
   }

   public void screenClosed(Minecraft client) {
      if (this.stage == ActionHandler.ActionStage.USED) {
         this.stage = ActionHandler.ActionStage.SWAP_BACK_SCREEN;
         LocalPlayer player = client.player;
         if (player != null) {
            if (OpenInInventory.CONFIG.debug()) {
               OpenInInventory.LOGGER.info("USED -> SWAP_BACK_SCREEN");
            }

            client.setScreen(new InventoryScreen(player));
         }
      }
   }

   public void tooltip(ItemStack stack, List<Component> lines, TooltipContext ignored1, TooltipFlag ignored2) {
      Minecraft client = Minecraft.getInstance();
      if (client.screen instanceof AccessHandledScreen access) {
         OpenAction matched = this.matchAction(client.screen, client.player, access.getHoveredSlot());
         if (matched != null) {
            lines.add(Component.translatable("open_in_inventory.tooltip.use"));
         }
      }
   }

   private OpenAction matchAction(@Nullable Screen screen, @Nullable Player player, @Nullable Slot focused) {
      return this.stage == ActionHandler.ActionStage.IDLE
            && screen != null
            && player != null
            && focused != null
            && !OpenInInventory.isShiftPressed(Minecraft.getInstance())
            && (!OpenInInventory.CONFIG.requireSingleStack() || focused.getItem().getCount() == 1)
            && (!OpenInInventory.CONFIG.requireEmptyMainHand() || player.getMainHandItem().isEmpty())
            && !OpenInInventory.isScreenBlackListed(screen)
            && focused.container == player.getInventory()
            && screen instanceof AbstractContainerScreen<?> handled
            && handled.getMenu().getCarried().isEmpty()
         ? OpenInInventory.ACTION_REGISTRY.get(focused.getItem())
         : null;
   }

   static enum ActionStage {
      IDLE,
      SWAPPED,
      USED,
      SWAP_BACK_SCREEN;
   }
}
