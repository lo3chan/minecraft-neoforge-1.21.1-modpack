package vazkii.psi.api.internal;

import java.util.Formatter;
import java.util.Locale;

public class Quat {
   public final double x;
   public final double y;
   public final double z;
   public final double s;

   public Quat(double d, double d1, double d2, double d3) {
      this.x = d1;
      this.y = d2;
      this.z = d3;
      this.s = d;
   }

   public static Quat aroundAxis(double ax, double ay, double az, double angle) {
      angle *= 0.5;
      double d4 = Math.sin(angle);
      return new Quat(Math.cos(angle), ax * d4, ay * d4, az * d4);
   }

   public static Quat aroundAxis(Vector3 axis, double angle) {
      return aroundAxis(axis.x, axis.y, axis.z, angle);
   }

   public void rotate(Vector3 vec) {
      double d = -this.x * vec.x - this.y * vec.y - this.z * vec.z;
      double d1 = this.s * vec.x + this.y * vec.z - this.z * vec.y;
      double d2 = this.s * vec.y - this.x * vec.z + this.z * vec.x;
      double d3 = this.s * vec.z + this.x * vec.y - this.y * vec.x;
      vec.x = d1 * this.s - d * this.x - d2 * this.z + d3 * this.y;
      vec.y = d2 * this.s - d * this.y + d1 * this.z - d3 * this.x;
      vec.z = d3 * this.s - d * this.z - d1 * this.y + d2 * this.x;
   }

   @Override
   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();
      Formatter formatter = new Formatter(stringbuilder, Locale.US);
      formatter.format("Quaternion:\n");
      formatter.format("  < %f %f %f %f >\n", this.s, this.x, this.y, this.z);
      formatter.close();
      return stringbuilder.toString();
   }
}
