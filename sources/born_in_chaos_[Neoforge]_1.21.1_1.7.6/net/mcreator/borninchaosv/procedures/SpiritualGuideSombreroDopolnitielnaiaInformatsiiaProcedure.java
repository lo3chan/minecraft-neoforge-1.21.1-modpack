package net.mcreator.borninchaosv.procedures;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SpiritualGuideSombreroDopolnitielnaiaInformatsiiaProcedure {
   public static String execute() {
      return Screen.hasShiftDown()
         ? Component.translatable("item.born_in_chaos_v1.spiritual_guide_sombrero_helmet.description_0").getString()
         : Component.translatable("item.borninchaos.tooltip").getString();
   }
}
