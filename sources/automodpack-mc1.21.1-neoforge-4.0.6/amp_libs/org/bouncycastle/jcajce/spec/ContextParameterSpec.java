package amp_libs.org.bouncycastle.jcajce.spec;

import amp_libs.org.bouncycastle.util.Arrays;
import java.security.spec.AlgorithmParameterSpec;

public class ContextParameterSpec implements AlgorithmParameterSpec {
   public static ContextParameterSpec EMPTY_CONTEXT_SPEC = new ContextParameterSpec(new byte[0]);
   private final byte[] context;

   public ContextParameterSpec(byte[] var1) {
      this.context = Arrays.clone(var1);
   }

   public byte[] getContext() {
      return Arrays.clone(this.context);
   }
}
