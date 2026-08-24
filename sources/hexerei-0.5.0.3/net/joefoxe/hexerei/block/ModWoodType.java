package net.joefoxe.hexerei.block;

import java.util.List;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModWoodType {
   public static WoodType WILLOW = WoodType.register(new WoodType("hexerei:willow", BlockSetType.OAK));
   public static WoodType POLISHED_WILLOW = WoodType.register(new WoodType("hexerei:polished_willow", BlockSetType.OAK));
   public static WoodType WITCH_HAZEL = WoodType.register(new WoodType("hexerei:witch_hazel", BlockSetType.OAK));
   public static WoodType POLISHED_WITCH_HAZEL = WoodType.register(new WoodType("hexerei:polished_witch_hazel", BlockSetType.OAK));
   public static WoodType MAHOGANY = WoodType.register(new WoodType("hexerei:mahogany", BlockSetType.OAK));
   public static WoodType POLISHED_MAHOGANY = WoodType.register(new WoodType("hexerei:polished_mahogany", BlockSetType.OAK));
   public static List<WoodType> woodTypes = List.of(WILLOW, MAHOGANY, WITCH_HAZEL, POLISHED_WILLOW, POLISHED_MAHOGANY, POLISHED_WITCH_HAZEL);
}
