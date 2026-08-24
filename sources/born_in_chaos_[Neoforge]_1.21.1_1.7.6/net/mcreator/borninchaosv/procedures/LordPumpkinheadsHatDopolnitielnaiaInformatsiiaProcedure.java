package net.mcreator.borninchaosv.procedures;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LordPumpkinheadsHatDopolnitielnaiaInformatsiiaProcedure {
   public static String execute() {
      return Screen.hasShiftDown()
         ? Component.translatable("item.born_in_chaos_v1.lord_pumpkinheads_hat_helmet.description_0").getString()
            + "\n"
            + Component.translatable("item.born_in_chaos_v1.lord_pumpkinheads_hat_helmet.description_1").getString()
            + "\n"
            + Component.translatable("item.born_in_chaos_v1.lord_pumpkinheads_hat_helmet.description_2").getString()
         : Component.translatable("item.borninchaos.tooltip").getString();
   }
}
