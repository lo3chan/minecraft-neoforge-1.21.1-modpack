package amp_libs.org.bouncycastle.math.ec.endo;

import amp_libs.org.bouncycastle.math.ec.ECPointMap;

public interface ECEndomorphism {
   ECPointMap getPointMap();

   boolean hasEfficientPointMap();
}
