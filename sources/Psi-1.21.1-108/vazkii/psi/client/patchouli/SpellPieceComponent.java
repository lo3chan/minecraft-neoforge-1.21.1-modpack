package vazkii.psi.client.patchouli;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import java.util.function.UnaryOperator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellPiece;

public class SpellPieceComponent implements ICustomComponent {
   private transient int x;
   private transient int y;
   private transient SpellPiece piece;
   private IVariable name;

   public void build(int componentX, int componentY, int pageNum) {
      this.x = componentX;
      this.y = componentY;
      this.piece = PsiAPI.SPELL_PIECE_REGISTRY
         .getOptional(ResourceLocation.parse(this.name.asString()))
         .map(clazz -> SpellPiece.create((Class<? extends SpellPiece>)clazz, new Spell()))
         .orElseThrow(() -> new IllegalArgumentException("Invalid spell piece name: " + this.name));
   }

   public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
      BufferSource buffer = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
      graphics.pose().pushPose();
      graphics.pose().translate(this.x, this.y, 0.0F);
      this.piece.draw(graphics.pose(), buffer, 15728880);
      buffer.endBatch();
      if (context.isAreaHovered(mouseX, mouseY, this.x - 1, this.y - 1, 16, 16)) {
         PatchouliUtils.setPieceTooltip(context, this.piece);
      }

      graphics.pose().popPose();
   }

   public void onVariablesAvailable(UnaryOperator<IVariable> function, Provider registries) {
      this.name = function.apply(this.name);
   }
}
