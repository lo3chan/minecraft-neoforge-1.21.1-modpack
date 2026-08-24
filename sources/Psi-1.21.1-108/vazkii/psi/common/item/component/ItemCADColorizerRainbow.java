package vazkii.psi.common.item.component;

import java.awt.Color;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vazkii.psi.client.core.handler.ClientTickHandler;

public class ItemCADColorizerRainbow extends ItemCADColorizer {
   public ItemCADColorizerRainbow(Properties properties) {
      super(properties);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public int getColor(ItemStack stack) {
      float time = ClientTickHandler.total;
      return Color.HSBtoRGB(time * 0.005F, 1.0F, 1.0F);
   }
}
