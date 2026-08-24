package vazkii.psi.client.core.handler;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.cad.ISocketableController;
import vazkii.psi.client.gui.GuiSocketSelect;
import vazkii.psi.common.lib.LibResources;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "psi"
)
@OnlyIn(Dist.CLIENT)
public class KeybindHandler {
   public static final KeyMapping keybind = new KeyMapping("psimisc.keybind", 67, "key.categories.psi");

   @SubscribeEvent
   public static void register(RegisterKeyMappingsEvent event) {
      event.register(keybind);
   }

   public static void keyDown() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.screen == null && mc.player != null) {
         ItemStack stack = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
         if (isSocketable(mc.player, stack)) {
            mc.setScreen(new GuiSocketSelect(stack));
         } else {
            stack = mc.player.getItemInHand(InteractionHand.OFF_HAND);
            if (isSocketable(mc.player, stack)) {
               mc.setScreen(new GuiSocketSelect(stack));
            } else {
               PatchouliAPI.get().openBookGUI(LibResources.PATCHOULI_BOOK);
            }
         }
      }
   }

   private static boolean isSocketableController(Player player, ItemStack stack) {
      if (!(stack.getItem() instanceof ISocketableController)) {
         return false;
      } else {
         ItemStack[] stacks = ((ISocketableController)stack.getItem()).getControlledStacks(player, stack);

         for (ItemStack controlled : stacks) {
            if (!controlled.isEmpty() && ISocketable.isSocketable(controlled)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean isSocketable(@NotNull Player player, @NotNull ItemStack stack) {
      return !stack.isEmpty() && (ISocketable.isSocketable(stack) || isSocketableController(player, stack));
   }
}
