package vazkii.psi.api.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import vazkii.psi.api.internal.Vector3;

public class SpellHelpers {
   public static Number getBoundedNumber(SpellPiece piece, SpellContext context, SpellParam<Number> param, double def) {
      double val = piece.getParamValueOrDefault(context, param, def).doubleValue();
      return Math.min(val, def);
   }

   public static double ensurePositiveOrZero(SpellPiece piece, SpellParam<Number> param) throws SpellCompilationException {
      double val = piece.getNotNullParamEvaluation(param).doubleValue();
      if (val < 0.0) {
         throw new SpellCompilationException("psi.spellerror.nonpositivevalue", piece.x, piece.y);
      } else {
         return val;
      }
   }

   public static double ensurePositiveAndNonzero(SpellPiece piece, SpellParam<Number> param) throws SpellCompilationException {
      double val = piece.getNotNullParamEvaluation(param).doubleValue();
      if (val <= 0.0) {
         throw new SpellCompilationException("psi.spellerror.nonpositivevalue", piece.x, piece.y);
      } else {
         return val;
      }
   }

   public static double rangeLimitParam(SpellPiece piece, SpellContext context, SpellParam<Number> param, double max) throws SpellRuntimeException {
      Number numberVal = piece.getParamValue(context, param);
      return numberVal == null ? max : Math.min(max, Math.max(-max, numberVal.doubleValue()));
   }

   public static Direction getFacing(SpellPiece piece, SpellContext context, SpellParam<Vector3> param) throws SpellRuntimeException {
      Vector3 face = getVector3(piece, context, param, false, true);
      return Direction.getNearest((float)face.x, (float)face.y, (float)face.z);
   }

   public static boolean isBlockPosInRadius(SpellContext context, BlockPos pos) {
      return context.isInRadius(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
   }

   public static double ensurePositiveOrZero(SpellPiece piece, SpellParam<Number> param, double def) throws SpellCompilationException {
      double val = piece.getParamEvaluationeOrDefault(param, def).doubleValue();
      if (val < 0.0) {
         throw new SpellCompilationException("psi.spellerror.nonpositivevalue", piece.x, piece.y);
      } else {
         return val;
      }
   }

   public static Vector3 getVector3(SpellPiece piece, SpellContext context, SpellParam<Vector3> param, boolean check, boolean shouldBeAxial) throws SpellRuntimeException {
      return checkPos(piece, context, param, check, shouldBeAxial);
   }

   public static Vector3 getVector3(SpellPiece piece, SpellContext context, SpellParam<Vector3> param, boolean NotNull, boolean check, boolean shouldBeAxial) throws SpellRuntimeException {
      return checkPos(piece, context, param, NotNull, check, shouldBeAxial);
   }

   public static BlockPos getBlockPos(SpellPiece piece, SpellContext context, SpellParam<Vector3> param, boolean check, boolean shouldBeAxial) throws SpellRuntimeException {
      return checkPos(piece, context, param, check, shouldBeAxial).toBlockPos();
   }

   public static Vector3 checkPos(SpellPiece piece, SpellContext context, SpellParam<Vector3> param, boolean check, boolean shouldBeAxial) throws SpellRuntimeException {
      return checkPos(piece, context, param, true, check, shouldBeAxial);
   }

   public static Vector3 checkPos(SpellPiece piece, SpellContext context, SpellParam<Vector3> param, boolean NotNull, boolean check, boolean shouldBeAxial) throws SpellRuntimeException {
      Vector3 position = piece.getParamValue(context, param);
      if (!NotNull || position != null && !position.isZero()) {
         if (check && !context.isInRadius(position)) {
            throw new SpellRuntimeException("psi.spellerror.outsideradius");
         } else if (shouldBeAxial && !position.isAxial()) {
            throw new SpellRuntimeException("psi.spellerror.nonaxial");
         } else {
            return position;
         }
      } else {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      }
   }

   public static Vector3 getDefaultedVector(
      SpellPiece piece, SpellContext context, SpellParam<Vector3> param, boolean check, boolean shouldBeAxial, Vector3 def
   ) throws SpellRuntimeException {
      Vector3 position = piece.getParamValue(context, param);
      if (position != null && !position.isZero()) {
         return checkPos(piece, context, param, false, check, shouldBeAxial);
      } else if (def != null && !def.isZero()) {
         return def;
      } else {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      }
   }
}
