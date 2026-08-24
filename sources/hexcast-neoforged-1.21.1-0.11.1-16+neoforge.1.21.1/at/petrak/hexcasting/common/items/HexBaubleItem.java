package at.petrak.hexcasting.common.items;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public interface HexBaubleItem {
   Multimap<Attribute, AttributeModifier> getHexBaubleAttrs(ItemStack var1);
}
