package vazkii.psi.common.spell.other;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.IGenericRedirector;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamAny;

public class PieceCrossConnector extends SpellPiece implements IGenericRedirector {
   private static final int LINE_ONE = 10526880;
   private static final int LINE_TWO = 10502399;
   SpellParam<SpellParam.Any> in1;
   SpellParam<SpellParam.Any> out1;
   SpellParam<SpellParam.Any> in2;
   SpellParam<SpellParam.Any> out2;

   public PieceCrossConnector(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.COMPLEXITY, new StatLabel(1.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.in1 = new ParamAny("psi.spellparam.from1", 10526880, false));
      this.addParam(this.out1 = new ParamAny("psi.spellparam.to1", 10526880, false, SpellParam.ArrowType.NONE));
      this.addParam(this.in2 = new ParamAny("psi.spellparam.from2", 10502399, false));
      this.addParam(this.out2 = new ParamAny("psi.spellparam.to2", 10502399, false, SpellParam.ArrowType.NONE));
   }

   @Override
   public boolean isInputSide(SpellParam.Side side) {
      return this.paramSides.get(this.in1) == side || this.paramSides.get(this.in2) == side;
   }

   @Override
   public String getSortingName() {
      return "00000000000";
   }

   @Override
   public Component getEvaluationTypeString() {
      return Component.translatable("psi.datatype.any");
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      meta.addStat(EnumSpellStat.COMPLEXITY, 1);
   }

   @Override
   public void drawAdditional(PoseStack pPoseStack, MultiBufferSource buffers, int light) {
      this.drawSide(pPoseStack, buffers, this.paramSides.get(this.in1), light, 10526880);
      this.drawSide(pPoseStack, buffers, this.paramSides.get(this.out1), light, 10526880);
      this.drawSide(pPoseStack, buffers, this.paramSides.get(this.in2), light, 10502399);
      this.drawSide(pPoseStack, buffers, this.paramSides.get(this.out2), light, 10502399);
   }

   @OnlyIn(Dist.CLIENT)
   private void drawSide(PoseStack pPoseStack, MultiBufferSource buffers, SpellParam.Side side, int light, int color) {
      if (side.isEnabled()) {
         Material material = new Material(InventoryMenu.BLOCK_ATLAS, ResourceLocation.parse("psi:spell/connector_lines"));
         VertexConsumer buffer = material.buffer(buffers, ignored -> SpellPiece.getLayer());
         float minU = 0.0F;
         float minV = 0.0F;
         switch (side) {
            case LEFT:
               minU = 0.5F;
               break;
            case TOP:
               minV = 0.5F;
               break;
            case BOTTOM:
               minU = 0.5F;
               minV = 0.5F;
         }

         float maxU = minU + 0.5F;
         float maxV = minV + 0.5F;
         float r = PsiRenderHelper.r(color) / 255.0F;
         float g = PsiRenderHelper.g(color) / 255.0F;
         float b = PsiRenderHelper.b(color) / 255.0F;
         Matrix4f mat = pPoseStack.last().pose();
         buffer.addVertex(mat, 0.0F, 16.0F, 0.0F).setColor(r, g, b, 1.0F);
         buffer.setUv(minU, maxV).setLight(light);
         buffer.addVertex(mat, 16.0F, 16.0F, 0.0F).setColor(r, g, b, 1.0F);
         buffer.setUv(maxU, maxV).setLight(light);
         buffer.addVertex(mat, 16.0F, 0.0F, 0.0F).setColor(r, g, b, 1.0F);
         buffer.setUv(maxU, minV).setLight(light);
         buffer.addVertex(mat, 0.0F, 0.0F, 0.0F).setColor(r, g, b, 1.0F);
         buffer.setUv(minU, minV).setLight(light);
      }
   }

   @Override
   public EnumPieceType getPieceType() {
      return EnumPieceType.CONNECTOR;
   }

   @Override
   public SpellParam.Side remapSide(SpellParam.Side inputSide) {
      if (this.paramSides.get(this.out1).getOpposite() == inputSide) {
         return this.paramSides.get(this.in1);
      } else {
         return this.paramSides.get(this.out2).getOpposite() == inputSide ? this.paramSides.get(this.in2) : SpellParam.Side.OFF;
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return null;
   }

   @Override
   public Object evaluate() {
      return null;
   }

   @Override
   public Object execute(SpellContext context) {
      return null;
   }
}
