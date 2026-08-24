package vazkii.psi.common.spell.constant;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import vazkii.psi.api.spell.EnumPieceType;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellPiece;
import vazkii.psi.common.Psi;

public class PieceConstantNumber extends SpellPiece {
   private static final String TAG_CONSTANT_VALUE = "constantValue";
   public String valueStr;

   public PieceConstantNumber(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      super.initParams();
      this.valueStr = "0";
   }

   @Override
   public void drawAdditional(PoseStack pPoseStack, MultiBufferSource buffers, int light) {
      if (this.valueStr == null || this.valueStr.isEmpty() || this.valueStr.length() > 5) {
         this.valueStr = "0";
      }

      Minecraft mc = Minecraft.getInstance();
      int color = Psi.magical ? 0 : 16777215;
      float efflen = mc.font.width(this.valueStr);

      float scale;
      for (scale = 1.0F; efflen > 16.0F; efflen = mc.font.width(this.valueStr) / scale) {
         scale++;
      }

      pPoseStack.pushPose();
      pPoseStack.scale(1.0F / scale, 1.0F / scale, 1.0F);
      pPoseStack.translate((9.0F - efflen / 2.0F) * scale, 4.0F * scale, 0.0F);
      mc.font.drawInBatch(this.valueStr, 0.0F, 0.0F, color, false, pPoseStack.last().pose(), buffers, DisplayMode.NORMAL, 0, 15728880);
      pPoseStack.popPose();
   }

   @Override
   public boolean interceptKeystrokes() {
      return true;
   }

   @Override
   public boolean onCharTyped(char character, int keyCode, boolean doit) {
      if ("FDfd".indexOf(character) >= 0) {
         return false;
      } else {
         String oldStr = this.valueStr;
         String newStr = this.valueStr;
         if ((newStr.equals("0") || newStr.equals("-0")) && "+-.".indexOf(character) < 0) {
            newStr = newStr.replace("0", "");
         }

         if (character == '+') {
            newStr = newStr.replace("-", "");
         } else if (character == '-') {
            if (!newStr.startsWith("-")) {
               newStr = "-" + newStr;
            }
         } else {
            newStr = newStr + character;
         }

         if (newStr.isEmpty()) {
            newStr = "0";
         }

         newStr = newStr.trim();
         if (newStr.length() > 5) {
            return false;
         } else {
            String newValueStr;
            try {
               Double.parseDouble(newStr);
               newValueStr = newStr;
            } catch (NumberFormatException var8) {
               return false;
            }

            if (doit) {
               this.valueStr = newValueStr;
            }

            return !newValueStr.equals(oldStr);
         }
      }
   }

   @Override
   public boolean onKeyPressed(int keyCode, int scanCode, boolean doit) {
      String oldStr = this.valueStr;
      String newStr = this.valueStr;
      if (keyCode == 259) {
         if (newStr.length() == 2 && newStr.startsWith("-")) {
            newStr = "-0";
         } else if (newStr.equals("-")) {
            newStr = "0";
         } else if (!newStr.isEmpty()) {
            newStr = newStr.substring(0, newStr.length() - 1);
         }
      }

      if (newStr.isEmpty()) {
         newStr = "0";
      }

      newStr = newStr.trim();
      if (newStr.length() > 5) {
         return false;
      } else {
         String newValueStr;
         try {
            Double.parseDouble(newStr);
            newValueStr = newStr;
         } catch (NumberFormatException var8) {
            return false;
         }

         if (doit) {
            this.valueStr = newValueStr;
         }

         return !newValueStr.equals(oldStr);
      }
   }

   @Override
   public EnumPieceType getPieceType() {
      return EnumPieceType.CONSTANT;
   }

   @Override
   public void writeToNBT(CompoundTag cmp) {
      super.writeToNBT(cmp);
      cmp.putString("constantValue", this.valueStr);
   }

   @Override
   public void readFromNBT(CompoundTag cmp) {
      super.readFromNBT(cmp);
      this.valueStr = cmp.getString("constantValue");
   }

   @Override
   public Class<?> getEvaluationType() {
      return Double.class;
   }

   @Override
   public Object evaluate() {
      if (this.valueStr == null || this.valueStr.isEmpty() || this.valueStr.length() > 5) {
         this.valueStr = "0";
      }

      try {
         return Double.parseDouble(this.valueStr);
      } catch (NumberFormatException var2) {
         return 0.0;
      }
   }

   @Override
   public Object execute(SpellContext context) {
      return this.evaluate();
   }
}
