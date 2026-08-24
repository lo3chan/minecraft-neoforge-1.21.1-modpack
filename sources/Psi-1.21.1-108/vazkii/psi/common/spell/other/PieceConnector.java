package vazkii.psi.common.spell.other;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.IRedirector;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.api.spell.param.ParamAny;

public class PieceConnector extends SpellPiece implements IRedirector {
   public static final ResourceLocation LINES_TEXTURE = ResourceLocation.parse("psi:spell/connector_lines");
   public SpellParam<SpellParam.Any> target;

   public PieceConnector(Spell spell) {
      super(spell);
   }

   @Override
   public String getSortingName() {
      return "00000000000";
   }

   @Override
   public Component getEvaluationTypeString() {
      return Component.translatable("psi.datatype.any");
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void drawAdditional(PoseStack pPoseStack, MultiBufferSource buffers, int light) {
      this.drawSide(pPoseStack, buffers, light, this.paramSides.get(this.target));
      if (this.isInGrid) {
         for (SpellParam.Side side : SpellParam.Side.class.getEnumConstants()) {
            if (side.isEnabled()) {
               SpellPiece piece = this.spell.grid.getPieceAtSideSafely(this.x, this.y, side);
               if (piece != null && piece.isInputSide(side.getOpposite())) {
                  this.drawSide(pPoseStack, buffers, light, side);
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void drawSide(PoseStack pPoseStack, MultiBufferSource buffers, int light, SpellParam.Side side) {
      if (side.isEnabled()) {
         Material material = new Material(InventoryMenu.BLOCK_ATLAS, LINES_TEXTURE);
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
         Matrix4f mat = pPoseStack.last().pose();
         buffer.addVertex(mat, 0.0F, 16.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.setUv(minU, maxV).setLight(light);
         buffer.addVertex(mat, 16.0F, 16.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.setUv(maxU, maxV).setLight(light);
         buffer.addVertex(mat, 16.0F, 0.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.setUv(maxU, minV).setLight(light);
         buffer.addVertex(mat, 0.0F, 0.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.setUv(minU, minV).setLight(light);
      }
   }

   @Override
   public void getShownPieces(List<SpellPiece> pieces) {
      for (SpellParam.Side side : SpellParam.Side.class.getEnumConstants()) {
         if (side.isEnabled()) {
            PieceConnector piece = (PieceConnector)SpellPiece.create(PieceConnector.class, new Spell());
            piece.paramSides.put(piece.target, side);
            pieces.add(piece);
         }
      }
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamAny("psi.spellparam.target", 7763574, false));
   }

   @Override
   public EnumPieceType getPieceType() {
      return EnumPieceType.CONNECTOR;
   }

   @Override
   public SpellParam.Side getRedirectionSide() {
      return this.paramSides.get(this.target);
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
