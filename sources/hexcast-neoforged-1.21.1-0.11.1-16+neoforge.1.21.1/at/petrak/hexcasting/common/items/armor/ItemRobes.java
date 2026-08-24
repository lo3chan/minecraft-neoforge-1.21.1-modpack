package at.petrak.hexcasting.common.items.armor;

import at.petrak.hexcasting.api.HexAPI;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class ItemRobes extends ArmorItem {
   public final Type type;

   public ItemRobes(Type type, Properties properties) {
      super(Holder.direct(HexAPI.instance().robesMaterial()), type, properties);
      this.type = type;
   }
}
