package amp_libs.org.bouncycastle.math.ec.endo;

import amp_libs.org.bouncycastle.math.ec.ECCurve;
import amp_libs.org.bouncycastle.math.ec.ECPointMap;
import amp_libs.org.bouncycastle.math.ec.ScaleXPointMap;
import java.math.BigInteger;

public class GLVTypeBEndomorphism implements GLVEndomorphism {
   protected final GLVTypeBParameters parameters;
   protected final ECPointMap pointMap;

   public GLVTypeBEndomorphism(ECCurve var1, GLVTypeBParameters var2) {
      this.parameters = var2;
      this.pointMap = new ScaleXPointMap(var1.fromBigInteger(var2.getBeta()));
   }

   @Override
   public BigInteger[] decomposeScalar(BigInteger var1) {
      return EndoUtil.decomposeScalar(this.parameters.getSplitParams(), var1);
   }

   @Override
   public ECPointMap getPointMap() {
      return this.pointMap;
   }

   @Override
   public boolean hasEfficientPointMap() {
      return true;
   }
}
