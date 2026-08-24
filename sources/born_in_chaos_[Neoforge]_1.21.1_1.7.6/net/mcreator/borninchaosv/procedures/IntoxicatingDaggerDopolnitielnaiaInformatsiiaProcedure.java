package net.mcreator.borninchaosv.procedures;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class IntoxicatingDaggerDopolnitielnaiaInformatsiiaProcedure {
   public static String execute() {
      return Screen.hasShiftDown()
         ? Component.translatable("item.born_in_chaos_v1.intoxicating_dagger.description_0").getString()
         : Component.translatable("item.borninchaos.tooltip").getString();
   }
}
