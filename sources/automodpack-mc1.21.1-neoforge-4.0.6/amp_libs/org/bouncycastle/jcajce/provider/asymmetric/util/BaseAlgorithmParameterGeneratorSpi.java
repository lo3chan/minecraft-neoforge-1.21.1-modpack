package amp_libs.org.bouncycastle.jcajce.provider.asymmetric.util;

import amp_libs.org.bouncycastle.jcajce.util.BCJcaJceHelper;
import amp_libs.org.bouncycastle.jcajce.util.JcaJceHelper;
import java.security.AlgorithmParameterGeneratorSpi;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public abstract class BaseAlgorithmParameterGeneratorSpi extends AlgorithmParameterGeneratorSpi {
   private final JcaJceHelper helper = new BCJcaJceHelper();

   protected final AlgorithmParameters createParametersInstance(String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      return this.helper.createAlgorithmParameters(var1);
   }
}
