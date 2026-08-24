package vazkii.psi.client.patchouli;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.UnaryOperator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.StringUtil;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellGrid;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.client.gui.GuiProgrammer;

public class SpellGridComponent implements ICustomComponent {
   public IVariable spell;
   private transient SpellGrid grid;
   private transient String spellName;

   public void build(int componentX, int componentY, int pageNum) {
      try {
         String spellstr = this.spell.asString("");
         if (StringUtil.isNullOrEmpty(spellstr)) {
            throw new IllegalArgumentException("Spell string is missing!");
         } else {
            CompoundTag cmp = TagParser.parseTag(spellstr);
            Spell fromNBT = Spell.createFromNBT(cmp);
            if (fromNBT == null) {
               throw new IllegalArgumentException("Invalid spell string: " + this.spell);
            } else {
               this.grid = fromNBT.grid;
               this.spellName = fromNBT.name;
            }
         }
      } catch (CommandSyntaxException var7) {
         throw new IllegalArgumentException("Invalid spell string: " + this.spell, var7);
      }
   }

   public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
      float scale = 0.65F;
      graphics.pose().pushPose();
      graphics.pose().scale(scale, scale, 0.0F);
      graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      graphics.blit(GuiProgrammer.texture, 0, 0, 0, 0, 174, 184);
      graphics.drawString(context.getGui().getMinecraft().font, I18n.get("psimisc.name", new Object[0]), 7, 171, 16777215, true);
      graphics.drawString(context.getGui().getMinecraft().font, this.spellName, 44, 170, 16777215, true);
      graphics.pose().translate(7.0F, 7.0F, 0.0F);
      BufferSource buffer = MultiBufferSource.immediate(new ByteBufferBuilder(1536));
      this.grid.draw(graphics.pose(), buffer, 15728880);
      buffer.endBatch();
      float scaledSize = 18.0F * scale;
      int scaledHoverSize = (int)(16.0F * scale);
      SpellPiece[][] gridData = this.grid.gridData;

      for (int i = 0; i < gridData.length; i++) {
         SpellPiece[] data = gridData[i];

         for (int j = 0; j < data.length; j++) {
            SpellPiece piece = data[j];
            if (piece != null
               && context.isAreaHovered(mouseX, mouseY, (int)(4.0F + i * scaledSize), (int)(4.0F + j * scaledSize), scaledHoverSize, scaledHoverSize)) {
               PatchouliUtils.setPieceTooltip(context, piece);
            }
         }
      }

      graphics.pose().popPose();
   }

   public void onVariablesAvailable(UnaryOperator<IVariable> lookup, Provider registries) {
      this.spell = lookup.apply(this.spell);
   }
}
