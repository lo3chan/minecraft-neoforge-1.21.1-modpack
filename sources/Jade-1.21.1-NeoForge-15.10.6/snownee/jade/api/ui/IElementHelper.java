package snownee.jade.api.ui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.JadeInternals;
import snownee.jade.api.ITooltip;
import snownee.jade.api.fluid.JadeFluidObject;

public interface IElementHelper {
   static IElementHelper get() {
      return JadeInternals.getElementHelper();
   }

   ITextElement text(Component var1);

   IElement spacer(int var1, int var2);

   IElement item(ItemStack var1);

   IElement item(ItemStack var1, float var2);

   IElement item(ItemStack var1, float var2, @Nullable String var3);

   IElement smallItem(ItemStack var1);

   IElement fluid(JadeFluidObject var1);

   IElement progress(float var1, @Nullable Component var2, ProgressStyle var3, BoxStyle var4, boolean var5);

   IElement progress(float var1);

   IElement progress(float var1, ResourceLocation var2, ResourceLocation var3, int var4, int var5, boolean var6);

   IBoxElement box(ITooltip var1, BoxStyle var2);

   ITooltip tooltip();

   ProgressStyle progressStyle();

   IElement sprite(ResourceLocation var1, int var2, int var3);
}
