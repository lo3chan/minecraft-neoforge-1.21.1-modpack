package top.theillusivec4.curios.client;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.type.util.IIconHelper;

public class IconHelper implements IIconHelper {
   private final Map<String, ResourceLocation> idToIcon = new HashMap<>();

   @Override
   public void clearIcons() {
      this.idToIcon.clear();
   }

   @Override
   public void addIcon(String identifier, ResourceLocation resourceLocation) {
      this.idToIcon.putIfAbsent(identifier, resourceLocation);
   }

   @Override
   public ResourceLocation getIcon(String identifier) {
      return this.idToIcon.getOrDefault(identifier, ResourceLocation.fromNamespaceAndPath("curios", "slot/empty_curio_slot"));
   }
}
