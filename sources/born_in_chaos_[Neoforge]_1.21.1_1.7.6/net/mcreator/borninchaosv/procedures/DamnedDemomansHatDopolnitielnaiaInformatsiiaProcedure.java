package net.mcreator.borninchaosv.procedures;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DamnedDemomansHatDopolnitielnaiaInformatsiiaProcedure {
   public static String execute() {
      return Screen.hasShiftDown()
         ? Component.translatable("item.born_in_chaos_v1.damned_demomans_hat_helmet.description_0").getString()
            + "\n"
            + Component.translatable("item.born_in_chaos_v1.damned_demomans_hat_helmet.description_1").getString()
         : Component.translatable("item.borninchaos.tooltip").getString();
   }
}
