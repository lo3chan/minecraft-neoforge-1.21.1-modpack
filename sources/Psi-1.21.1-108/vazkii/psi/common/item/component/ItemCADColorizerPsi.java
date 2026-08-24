package vazkii.psi.common.item.component;

import java.awt.Color;
import java.util.Locale;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vazkii.psi.client.core.handler.ClientTickHandler;
import vazkii.psi.client.core.handler.ColorHandler;
import vazkii.psi.common.core.handler.ContributorSpellCircleHandler;

public class ItemCADColorizerPsi extends ItemCADColorizer {
   public ItemCADColorizerPsi(Properties properties) {
      super(properties);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public int getColor(ItemStack stack) {
      if (!this.getContributorName(stack).isEmpty() && ContributorSpellCircleHandler.isContributor(this.getContributorName(stack).toLowerCase(Locale.ROOT))) {
         return ColorHandler.slideColor(ContributorSpellCircleHandler.getColors(this.getContributorName(stack).toLowerCase(Locale.ROOT)), 0.0125F);
      } else {
         float time = ClientTickHandler.total;
         float w = (float)(Math.sin(time * 0.4) * 0.5 + 0.5) * 0.1F;
         float r = (float)(Math.sin(time * 0.1) * 0.5 + 0.5) * 0.5F + 0.25F + w;
         float g = 0.5F + w;
         float b = 1.0F;
         return new Color((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F)).getRGB();
      }
   }
}
