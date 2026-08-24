package at.petrak.hexcasting.common.items;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.lib.HexAttributes;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemLens extends Item implements HexBaubleItem {
   public static final AttributeModifier GRID_ZOOM = new AttributeModifier(HexAPI.modLoc("scrying_lens_zoom"), 0.33, Operation.ADD_MULTIPLIED_BASE);
   public static final AttributeModifier SCRY_SIGHT = new AttributeModifier(HexAPI.modLoc("scrying_lens_sight"), 1.0, Operation.ADD_VALUE);

   public ItemLens(Properties pProperties) {
      super(pProperties);
      DispenserBlock.registerBehavior(this, new OptionalDispenseItemBehavior() {
         @NotNull
         protected ItemStack execute(@NotNull BlockSource world, @NotNull ItemStack stack) {
            this.setSuccess(ArmorItem.dispenseArmor(world, stack));
            return stack;
         }
      });
   }

   @Override
   public Multimap<Attribute, AttributeModifier> getHexBaubleAttrs(ItemStack stack) {
      HashMultimap<Attribute, AttributeModifier> out = HashMultimap.create();
      out.put(HexAttributes.GRID_ZOOM, GRID_ZOOM);
      out.put(HexAttributes.SCRY_SIGHT, SCRY_SIGHT);
      return out;
   }

   @Nullable
   public EquipmentSlot getEquipmentSlot(ItemStack stack) {
      return EquipmentSlot.HEAD;
   }
}
