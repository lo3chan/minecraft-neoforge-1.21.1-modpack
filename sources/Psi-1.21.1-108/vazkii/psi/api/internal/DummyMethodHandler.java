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

public final class DummyMethodHandler implements IInternalMethodHandler {
   @Override
   public IPlayerData getDataForPlayer(Player player) {
      return new DummyPlayerData();
   }

   @Override
   public ResourceLocation getProgrammerTexture() {
      return ResourceLocation.withDefaultNamespace("");
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public RenderType getProgrammerLayer() {
      return null;
   }

   @Override
   public ISpellCompiler getCompiler() {
      return null;
   }

   @Override
   public ISpellCache getSpellCache() {
      return null;
   }

   @Override
   public void delayContext(SpellContext context) {
   }

   @Override
   public void setCrashData(CompiledSpell spell, SpellPiece piece) {
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void renderTooltip(GuiGraphics graphics, int x, int y, List<Component> tooltipData, int color, int color2, int width, int height) {
   }

   @Override
   public ItemStack createDefaultCAD(List<ItemStack> components) {
      return ItemStack.EMPTY;
   }

   @Override
   public ItemStack createCAD(ItemStack base, List<ItemStack> components) {
      return ItemStack.EMPTY;
   }
}
