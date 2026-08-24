package vazkii.psi.common.item.component;

import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vazkii.psi.api.cad.ICADColorizer;
import vazkii.psi.common.item.base.ModDataComponents;

public class ItemCADColorizer extends ItemCADComponent implements ICADColorizer {
   private final DyeColor color;

   public ItemCADColorizer(Properties properties, DyeColor color) {
      super(properties);
      this.color = color;
   }

   public ItemCADColorizer(Properties properties) {
      super(properties);
      this.color = DyeColor.BLACK;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public int getColor(ItemStack stack) {
      return ARGB32.opaque(this.color.getTextColor());
   }

   @Override
   public String getContributorName(ItemStack stack) {
      return (String)stack.getOrDefault(ModDataComponents.CONTRIBUTOR, "");
   }

   @Override
   public void setContributorName(ItemStack stack, String name) {
      stack.set(ModDataComponents.CONTRIBUTOR, name);
   }
}
