package vazkii.psi.api.internal;

import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import vazkii.psi.api.spell.CompiledSpell;
import vazkii.psi.api.spell.ISpellCache;
import vazkii.psi.api.spell.ISpellCompiler;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;

public interface IInternalMethodHandler {
   IPlayerData getDataForPlayer(Player var1);

   ResourceLocation getProgrammerTexture();

   @OnlyIn(Dist.CLIENT)
   RenderType getProgrammerLayer();

   ISpellCompiler getCompiler();

   ISpellCache getSpellCache();

   void delayContext(SpellContext var1);

   void setCrashData(CompiledSpell var1, SpellPiece var2);

   @OnlyIn(Dist.CLIENT)
   void renderTooltip(GuiGraphics var1, int var2, int var3, List<Component> var4, int var5, int var6, int var7, int var8);

   ItemStack createDefaultCAD(List<ItemStack> var1);

   ItemStack createCAD(ItemStack var1, List<ItemStack> var2);
}
