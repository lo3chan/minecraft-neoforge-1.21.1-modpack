package at.petrak.hexcasting.common.lib;

import at.petrak.hexcasting.api.HexAPI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class HexAttributes {
   private static final Map<ResourceLocation, Attribute> ATTRIBUTES = new LinkedHashMap<>();
   public static final Attribute GRID_ZOOM = ((RangedAttribute)make("grid_zoom", new RangedAttribute("hexcasting.attributes.grid_zoom", 1.0, 0.5, 4.0)))
      .setSyncable(true);
   public static final Attribute SCRY_SIGHT = ((RangedAttribute)make("scry_sight", new RangedAttribute("hexcasting.attributes.scry_sight", 0.0, 0.0, 1.0)))
      .setSyncable(true);

   public static void register(BiConsumer<Attribute, ResourceLocation> r) {
      for (Entry<ResourceLocation, Attribute> e : ATTRIBUTES.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   private static <T extends Attribute> T make(String id, T attr) {
      Attribute old = ATTRIBUTES.put(HexAPI.modLoc(id), attr);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + id);
      } else {
         return attr;
      }
   }
}
