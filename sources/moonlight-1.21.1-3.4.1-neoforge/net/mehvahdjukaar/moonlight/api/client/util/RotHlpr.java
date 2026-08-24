package net.mehvahdjukaar.moonlight.api.client.util;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.mojang.math.Axis;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;

public class RotHlpr {
   public static final Quaternionf Y180 = Axis.YP.rotationDegrees(180.0F);
   public static final Quaternionf Y90 = Axis.YP.rotationDegrees(90.0F);
   public static final Quaternionf Y45 = Axis.YP.rotationDegrees(45.0F);
   public static final Quaternionf YN45 = Axis.YP.rotationDegrees(-45.0F);
   public static final Quaternionf YN90 = Axis.YP.rotationDegrees(-90.0F);
   public static final Quaternionf YN180 = Axis.YP.rotationDegrees(-180.0F);
   public static final Quaternionf X180 = Axis.XP.rotationDegrees(180.0F);
   public static final Quaternionf X90 = Axis.XP.rotationDegrees(90.0F);
   public static final Quaternionf X22 = Axis.XP.rotationDegrees(22.5F);
   public static final Quaternionf XN22 = Axis.XP.rotationDegrees(-22.5F);
   public static final Quaternionf XN90 = Axis.XP.rotationDegrees(-90.0F);
   public static final Quaternionf XN180 = Axis.XP.rotationDegrees(-180.0F);
   public static final Quaternionf Z180 = Axis.ZP.rotationDegrees(180.0F);
   public static final Quaternionf Z135 = Axis.ZP.rotationDegrees(135.0F);
   public static final Quaternionf Z90 = Axis.ZP.rotationDegrees(90.0F);
   public static final Quaternionf ZN45 = Axis.ZP.rotationDegrees(-45.0F);
   public static final Quaternionf ZN90 = Axis.ZP.rotationDegrees(-90.0F);
   public static final Quaternionf ZN180 = Axis.ZP.rotationDegrees(-180.0F);
   private static final Map<Direction, Quaternionf> DIR2ROT = Maps.newEnumMap(
      Arrays.stream(Direction.values()).collect(Collectors.toMap(Functions.identity(), d -> d.getOpposite().getRotation().mul(XN90)))
   );
   private static final Map<Integer, Quaternionf> YAW2ROT = Arrays.stream(Direction.values())
      .filter(d -> d.getAxis() != net.minecraft.core.Direction.Axis.Y)
      .map(d -> (int)(-d.toYRot()))
      .collect(Collectors.toMap(Functions.identity(), y -> Axis.YP.rotationDegrees(y.intValue())));
   private static final Quaternionf def = Axis.YP.rotationDegrees(0.0F);

   public static Quaternionf rot(Direction dir) {
      return DIR2ROT.get(dir);
   }

   public static Quaternionf rot(int rot) {
      return YAW2ROT.getOrDefault(rot, def);
   }
}
