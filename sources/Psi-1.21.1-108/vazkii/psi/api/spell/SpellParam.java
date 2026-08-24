package vazkii.psi.api.spell;

import com.google.common.base.CaseFormat;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class SpellParam<T> {
   public static final String PSI_PREFIX = "psi.spellparam.";
   public static final String GENERIC_NAME_TARGET = "psi.spellparam.target";
   public static final String GENERIC_NAME_NUMBER = "psi.spellparam.number";
   public static final String GENERIC_NAME_NUMBER1 = "psi.spellparam.number1";
   public static final String GENERIC_NAME_NUMBER2 = "psi.spellparam.number2";
   public static final String GENERIC_NAME_NUMBER3 = "psi.spellparam.number3";
   public static final String GENERIC_NAME_NUMBER4 = "psi.spellparam.number4";
   public static final String GENERIC_NAME_VECTOR1 = "psi.spellparam.vector1";
   public static final String GENERIC_NAME_VECTOR2 = "psi.spellparam.vector2";
   public static final String GENERIC_NAME_VECTOR3 = "psi.spellparam.vector3";
   public static final String GENERIC_NAME_VECTOR4 = "psi.spellparam.vector4";
   public static final String GENERIC_NAME_POSITION = "psi.spellparam.position";
   public static final String GENERIC_NAME_MIN = "psi.spellparam.min";
   public static final String GENERIC_NAME_MAX = "psi.spellparam.max";
   public static final String GENERIC_NAME_POWER = "psi.spellparam.power";
   public static final String GENERIC_NAME_X = "psi.spellparam.x";
   public static final String GENERIC_NAME_Y = "psi.spellparam.y";
   public static final String GENERIC_NAME_Z = "psi.spellparam.z";
   public static final String GENERIC_NAME_RADIUS = "psi.spellparam.radius";
   public static final String GENERIC_NAME_DISTANCE = "psi.spellparam.distance";
   public static final String GENERIC_NAME_TIME = "psi.spellparam.time";
   public static final String GENERIC_NAME_BASE = "psi.spellparam.base";
   public static final String GENERIC_NAME_RAY = "psi.spellparam.ray";
   public static final String GENERIC_NAME_VECTOR = "psi.spellparam.vector";
   public static final String GENERIC_NAME_AXIS = "psi.spellparam.axis";
   public static final String GENERIC_NAME_ANGLE = "psi.spellparam.angle";
   public static final String GENERIC_NAME_PITCH = "psi.spellparam.pitch";
   public static final String GENERIC_NAME_INSTRUMENT = "psi.spellparam.instrument";
   public static final String GENERIC_NAME_VOLUME = "psi.spellparam.volume";
   public static final String GENERIC_NAME_LIST1 = "psi.spellparam.list1";
   public static final String GENERIC_NAME_LIST2 = "psi.spellparam.list2";
   public static final String GENERIC_NAME_LIST = "psi.spellparam.list";
   public static final String GENERIC_NAME_DIRECTION = "psi.spellparam.direction";
   public static final String CONNECTOR_NAME_FROM1 = "psi.spellparam.from1";
   public static final String CONNECTOR_NAME_FROM2 = "psi.spellparam.from2";
   public static final String CONNECTOR_NAME_TO1 = "psi.spellparam.to1";
   public static final String CONNECTOR_NAME_TO2 = "psi.spellparam.to2";
   public static final String GENERIC_NAME_ROOT = "psi.spellparam.root";
   public static final String GENERIC_NAME_TOGGLE = "psi.spellparam.toggle";
   public static final String GENERIC_NAME_MASK = "psi.spellparam.mask";
   public static final String GENERIC_NAME_CHANNEL = "psi.spellparam.channel";
   public static final String GENERIC_NAME_SLOT = "psi.spellparam.slot";
   public static final String GENERIC_NAME_RAY_END = "psi.spellparam.ray_end";
   public static final String GENERIC_NAME_RAY_START = "psi.spellparam.ray_start";
   public static final int RED = 13773354;
   public static final int GREEN = 4117034;
   public static final int BLUE = 2774482;
   public static final int PURPLE = 7678674;
   public static final int CYAN = 2805970;
   public static final int YELLOW = 13814826;
   public static final int GRAY = 7763574;
   public static final int BROWN = 6701056;
   public final String name;
   public final int color;
   public final boolean canDisable;
   public final SpellParam.ArrowType arrowType;

   public SpellParam(String name, int color, boolean canDisable) {
      this(name, color, canDisable, SpellParam.ArrowType.IN);
   }

   public SpellParam(String name, int color, boolean canDisable, SpellParam.ArrowType arrowType) {
      this.name = name;
      this.color = color;
      this.canDisable = canDisable;
      this.arrowType = arrowType;
   }

   protected abstract Class<T> getRequiredType();

   protected boolean requiresConstant() {
      return false;
   }

   public Component getRequiredTypeString() {
      Class<T> evalType = this.getRequiredType();
      String evalStr = evalType == null ? "null" : CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, evalType.getSimpleName());
      MutableComponent s = Component.translatable("psi.datatype." + evalStr);
      if (this.requiresConstant()) {
         s.append(" ").append(Component.translatable("psimisc.constant"));
      }

      return s;
   }

   public boolean canAccept(SpellPiece piece) {
      return (this.getRequiredType() == SpellParam.Any.class || this.getRequiredType().isAssignableFrom(piece.getEvaluationType()))
         && (!this.requiresConstant() || piece.getPieceType() == EnumPieceType.CONSTANT);
   }

   public SpellParam.ArrowType getArrowType() {
      return this.arrowType;
   }

   public static class Any {
   }

   public static enum ArrowType {
      NONE,
      OUT,
      IN;
   }

   public static enum Side {
      OFF(0, 0, 0, 0, 0, 0, 238, 0),
      TOP(0, -1, 4, -9, -4, -9, 222, 8),
      BOTTOM(0, 1, 4, 9, -4, 9, 230, 8),
      LEFT(-1, 0, -9, 4, -9, -4, 230, 0),
      RIGHT(1, 0, 9, 4, 9, -4, 222, 0);

      public static final SpellParam.Side[] DIRECTIONS = new SpellParam.Side[]{TOP, BOTTOM, LEFT, RIGHT};
      public final int offx;
      public final int offy;
      public final int minx;
      public final int miny;
      public final int maxx;
      public final int maxy;
      public final int u;
      public final int v;

      private Side(int offx, int offy, int minx, int miny, int maxx, int maxy, int u, int v) {
         this.offx = offx;
         this.offy = offy;
         this.minx = minx;
         this.miny = miny;
         this.maxx = maxx;
         this.maxy = maxy;
         this.u = u;
         this.v = v;
      }

      public static SpellParam.Side fromInt(int i) {
         return SpellParam.Side.class.getEnumConstants()[i];
      }

      public boolean isEnabled() {
         return this != OFF;
      }

      public int asInt() {
         return this.ordinal();
      }

      public SpellParam.Side getOpposite() {
         return this.mapSides(BOTTOM, TOP, RIGHT, LEFT);
      }

      public SpellParam.Side mirrorVertical() {
         return this.mapSides(BOTTOM, TOP, LEFT, RIGHT);
      }

      public SpellParam.Side rotateCW() {
         return this.mapSides(LEFT, RIGHT, BOTTOM, TOP);
      }

      public SpellParam.Side rotateCCW() {
         return this.mapSides(RIGHT, LEFT, TOP, BOTTOM);
      }

      private SpellParam.Side mapSides(SpellParam.Side whenUp, SpellParam.Side whenDown, SpellParam.Side whenL, SpellParam.Side whenR) {
         return switch (this) {
            case TOP -> whenUp;
            case BOTTOM -> whenDown;
            case LEFT -> whenL;
            case RIGHT -> whenR;
            default -> OFF;
         };
      }
   }
}
