package vazkii.psi.client.core.handler;

import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.ItemExosuitSensor;
import vazkii.psi.common.item.armor.ItemPsimetalArmor;
import vazkii.psi.common.item.base.ModItems;
import vazkii.psi.common.item.component.ItemCADColorizer;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "psi"
)
public class ColorHandler {
   @SubscribeEvent
   public static void init(Item event) {
      event.register(
         (stack, tintIndex) -> tintIndex == 1 ? ((ItemPsimetalArmor)stack.getItem()).getColor(stack) : -1,
         new ItemLike[]{
            (ItemLike)ModItems.psimetalExosuitBoots.get(),
            (ItemLike)ModItems.psimetalExosuitChestplate.get(),
            (ItemLike)ModItems.psimetalExosuitHelmet.get(),
            (ItemLike)ModItems.psimetalExosuitLeggings.get()
         }
      );
      event.register(
         (stack, tintIndex) -> tintIndex == 1 ? ((ItemExosuitSensor)stack.getItem()).getColor(stack) : -1,
         new ItemLike[]{
            (ItemLike)ModItems.exosuitSensorHeat.get(),
            (ItemLike)ModItems.exosuitSensorLight.get(),
            (ItemLike)ModItems.exosuitSensorStress.get(),
            (ItemLike)ModItems.exosuitSensorWater.get(),
            (ItemLike)ModItems.exosuitSensorTrigger.get()
         }
      );
      event.register((stack, tintIndex) -> tintIndex == 1 ? ((ItemCAD)stack.getItem()).getSpellColor(stack) : -1, new ItemLike[]{(ItemLike)ModItems.cad.get()});
      event.register(
         (stack, tintIndex) -> tintIndex != 1 ? -1 : ((ItemCADColorizer)stack.getItem()).getColor(stack),
         new ItemLike[]{
            (ItemLike)ModItems.cadColorizerWhite.get(),
            (ItemLike)ModItems.cadColorizerOrange.get(),
            (ItemLike)ModItems.cadColorizerMagenta.get(),
            (ItemLike)ModItems.cadColorizerLightBlue.get(),
            (ItemLike)ModItems.cadColorizerYellow.get(),
            (ItemLike)ModItems.cadColorizerLime.get(),
            (ItemLike)ModItems.cadColorizerPink.get(),
            (ItemLike)ModItems.cadColorizerGray.get(),
            (ItemLike)ModItems.cadColorizerLightGray.get(),
            (ItemLike)ModItems.cadColorizerCyan.get(),
            (ItemLike)ModItems.cadColorizerPurple.get(),
            (ItemLike)ModItems.cadColorizerBlue.get(),
            (ItemLike)ModItems.cadColorizerBrown.get(),
            (ItemLike)ModItems.cadColorizerGreen.get(),
            (ItemLike)ModItems.cadColorizerRed.get(),
            (ItemLike)ModItems.cadColorizerBlack.get()
         }
      );
   }

   public static int slideColor(int[] color, float speed) {
      int n = color.length;
      double t = ClientTickHandler.total * speed * n / 3.141592653589793 % n;
      int phase = (int)t;
      double dt = t - phase;
      if (dt == 0.0) {
         return color[phase];
      } else {
         int nextPhase = (phase + 1) % n;
         return slideColorTime(color[phase], color[nextPhase], (float)(dt * 3.141592653589793));
      }
   }

   public static int pulseColor(int source, float speed, int magnitude) {
      return pulseColor(source, 1.0F, speed, magnitude);
   }

   public static int pulseColor(int source, float multiplier, float speed, int magnitude) {
      int add = (int)(Mth.sin(ClientTickHandler.ticksInGame * speed) * magnitude);
      int red = (0xFF0000 & source) >> 16;
      int green = (0xFF00 & source) >> 8;
      int blue = 0xFF & source;
      int addedRed = Mth.clamp((int)(multiplier * (red + add)), 0, 255);
      int addedGreen = Mth.clamp((int)(multiplier * (green + add)), 0, 255);
      int addedBlue = Mth.clamp((int)(multiplier * (blue + add)), 0, 255);
      return 0xFF000000 | addedRed << 16 | addedGreen << 8 | addedBlue;
   }

   public static int slideColorTime(int color, int secondColor, float t) {
      float shift = (1.0F - Mth.cos(t)) / 2.0F;
      if (shift == 0.0F) {
         return color;
      } else if (shift == 1.0F) {
         return secondColor;
      } else {
         int redA = (0xFF0000 & color) >> 16;
         int greenA = (0xFF00 & color) >> 8;
         int blueA = 0xFF & color;
         int redB = (0xFF0000 & secondColor) >> 16;
         int greenB = (0xFF00 & secondColor) >> 8;
         int blueB = 0xFF & secondColor;
         int newRed = (int)(redA * (1.0F - shift) + redB * shift);
         int newGreen = (int)(greenA * (1.0F - shift) + greenB * shift);
         int newBlue = (int)(blueA * (1.0F - shift) + blueB * shift);
         return 0xFF000000 | newRed << 16 | newGreen << 8 | newBlue;
      }
   }
}
