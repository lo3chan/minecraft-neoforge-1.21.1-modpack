package vazkii.psi.client.core.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent.Post;
import net.neoforged.neoforge.client.event.RenderFrameEvent.Pre;
import vazkii.psi.api.exosuit.PsiArmorEvent;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageTriggerJumpSpell;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "psi"
)
public class ClientTickHandler {
   public static int ticksInGame = 0;
   public static float partialTicks = 0.0F;
   public static float total = 0.0F;
   private static boolean lastJumpKeyState = false;

   private static void calcDelta() {
      total = ticksInGame + partialTicks;
   }

   @SubscribeEvent
   public static void renderTick(Pre event) {
      partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);
   }

   @SubscribeEvent
   public static void renderTick(Post event) {
      calcDelta();
   }

   @SubscribeEvent
   public static void clientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Pre event) {
      Minecraft mc = Minecraft.getInstance();
      boolean pressed = mc.options.keyJump.consumeClick();
      if (mc.player != null && pressed && !lastJumpKeyState && !mc.player.onGround()) {
         PsiArmorEvent.post(new PsiArmorEvent(mc.player, "psi.event.jump"));
         MessageRegister.sendToServer(new MessageTriggerJumpSpell());
      }

      lastJumpKeyState = pressed;
   }

   @SubscribeEvent
   public static void clientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
      Minecraft mc = Minecraft.getInstance();
      HUDHandler.tick();
      Screen gui = mc.screen;
      if (gui == null && KeybindHandler.keybind.isDown()) {
         KeybindHandler.keyDown();
      }

      if (!mc.isPaused()) {
         ticksInGame++;
         partialTicks = 0.0F;
      }

      calcDelta();
   }
}
