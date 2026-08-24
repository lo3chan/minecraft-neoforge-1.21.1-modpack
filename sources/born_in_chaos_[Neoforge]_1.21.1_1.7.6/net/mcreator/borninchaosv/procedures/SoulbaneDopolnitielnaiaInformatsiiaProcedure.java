package net.mcreator.borninchaosv.procedures;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SoulbaneDopolnitielnaiaInformatsiiaProcedure {
   public static String execute() {
      return Screen.hasShiftDown()
         ? Component.translatable("item.born_in_chaos_v1.soulbane.description_0").getString()
            + "\n\n"
            + Component.translatable("item.born_in_chaos_v1.soulbane.description_1").getString()
            + "\n"
            + Component.translatable("item.born_in_chaos_v1.soul_cutlass.description_1").getString()
         : Component.translatable("item.borninchaos.tooltip").getString();
   }
}
