package net.mcreator.borninchaosv.procedures;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class StormcallersHornDopolnitielnaiaInformatsiiaProcedure {
   public static String execute() {
      return Screen.hasShiftDown()
         ? Component.translatable("item.born_in_chaos_v1.stormcallers_horn.description_0").getString()
            + "\n\n"
            + Component.translatable("item.born_in_chaos_v1.death_totem.description_2").getString()
            + "\n"
            + Component.translatable("item.born_in_chaos_v1.stormcallers_horn.description_1").getString()
         : Component.translatable("item.borninchaos.tooltip").getString();
   }
}
