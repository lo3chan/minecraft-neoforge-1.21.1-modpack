package net.mcreator.borninchaosv.procedures;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DeathTotemDopolnitielnaiaInformatsiiaProcedure {
   public static String execute() {
      return Screen.hasShiftDown()
         ? Component.translatable("item.born_in_chaos_v1.death_totem.description_0").getString()
            + "\n"
            + Component.translatable("item.born_in_chaos_v1.death_totem.description_1").getString()
            + "\n\n"
            + Component.translatable("item.born_in_chaos_v1.death_totem.description_2").getString()
            + "\n"
            + Component.translatable("item.born_in_chaos_v1.death_totem.description_3").getString()
         : Component.translatable("item.borninchaos.tooltip").getString();
   }
}
