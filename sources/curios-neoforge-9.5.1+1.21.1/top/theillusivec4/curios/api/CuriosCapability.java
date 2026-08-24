package top.theillusivec4.curios.api;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.items.IItemHandler;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class CuriosCapability {
   public static final ResourceLocation ID_INVENTORY = ResourceLocation.fromNamespaceAndPath("curios", "inventory");
   public static final ResourceLocation ID_ITEM_HANDLER = ResourceLocation.fromNamespaceAndPath("curios", "item_handler");
   public static final ResourceLocation ID_ITEM = ResourceLocation.fromNamespaceAndPath("curios", "item");
   public static final EntityCapability<ICuriosItemHandler, Void> INVENTORY = EntityCapability.createVoid(ID_INVENTORY, ICuriosItemHandler.class);
   public static final EntityCapability<IItemHandler, Void> ITEM_HANDLER = EntityCapability.createVoid(ID_ITEM_HANDLER, IItemHandler.class);
   public static final ItemCapability<ICurio, Void> ITEM = ItemCapability.createVoid(ID_ITEM, ICurio.class);
}
