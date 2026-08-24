package vazkii.psi.client.core.handler;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.client.event.InputEvent.Key;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.psi.common.core.handler.PsiSoundHandler;
import vazkii.psi.common.lib.LibResources;

@EventBusSubscriber(
   modid = "psi",
   value = {Dist.CLIENT}
)
public class BookSoundHandler {
   private static final int[] SECRET_CODE = new int[]{81, 85, 65, 84, 49, 48, 50, 52};
   private static int nextLetter = 0;
   private static int bookTime = 0;

   private static boolean isBookOpen() {
      return Objects.equals(PatchouliAPI.get().getOpenBookGui(), LibResources.PATCHOULI_BOOK);
   }

   @SubscribeEvent
   public static void clientTick(Pre evt) {
      if (bookTime > 0) {
         bookTime--;
      }

      if (!isBookOpen()) {
         nextLetter = 0;
      }
   }

   @SubscribeEvent
   public static void handleInput(Key evt) {
      Minecraft mc = Minecraft.getInstance();
      if (evt.getModifiers() == 0 && evt.getAction() == 1 && isBookOpen()) {
         if (bookTime == 0 && evt.getKey() == SECRET_CODE[nextLetter]) {
            nextLetter++;
            if (nextLetter >= SECRET_CODE.length) {
               mc.getSoundManager().play(SimpleSoundInstance.forUI(PsiSoundHandler.book, 1.0F));
               nextLetter = 0;
               bookTime = 320;
            }
         } else {
            nextLetter = 0;
         }
      }
   }
}
