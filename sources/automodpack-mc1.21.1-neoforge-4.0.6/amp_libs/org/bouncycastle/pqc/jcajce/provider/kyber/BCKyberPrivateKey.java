package amp_libs.org.bouncycastle.pqc.jcajce.provider.kyber;

import amp_libs.org.bouncycastle.asn1.ASN1Set;
import amp_libs.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import amp_libs.org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import amp_libs.org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import amp_libs.org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import amp_libs.org.bouncycastle.pqc.jcajce.interfaces.KyberPrivateKey;
import amp_libs.org.bouncycastle.pqc.jcajce.interfaces.KyberPublicKey;
import amp_libs.org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;
import amp_libs.org.bouncycastle.util.Arrays;
import amp_libs.org.bouncycastle.util.Strings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BCKyberPrivateKey implements KyberPrivateKey {
   private static final long serialVersionUID = 1L;
   private transient MLKEMPrivateKeyParameters params;
   private transient String algorithm;
   private transient ASN1Set attributes;

   public BCKyberPrivateKey(MLKEMPrivateKeyParameters var1) {
      this.params = var1;
      this.algorithm = Strings.toUpperCase(var1.getParameters().getName());
   }

   public BCKyberPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(PrivateKeyInfo var1) throws IOException {
      this.attributes = var1.getAttributes();
      this.params = (MLKEMPrivateKeyParameters)PrivateKeyFactory.createKey(var1);
      this.algorithm = Strings.toUpperCase(this.params.getParameters().getName());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCKyberPrivateKey) {
         BCKyberPrivateKey var2 = (BCKyberPrivateKey)var1;
         return Arrays.areEqual(this.getEncoded(), var2.getEncoded());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.getEncoded());
   }

   @Override
   public final String getAlgorithm() {
      return this.algorithm;
   }

   @Override
   public byte[] getEncoded() {
      try {
         PrivateKeyInfo var1 = PrivateKeyInfoFactory.createPrivateKeyInfo(this.params, this.attributes);
         return var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public KyberPublicKey getPublicKey() {
      return new BCKyberPublicKey(this.params.getPublicKeyParameters());
   }

   @Override
   public KyberParameterSpec getParameterSpec() {
      return KyberParameterSpec.fromName(this.params.getParameters().getName());
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   MLKEMPrivateKeyParameters getKeyParams() {
      return this.params;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte[] var2 = (byte[])var1.readObject();
      this.init(PrivateKeyInfo.getInstance(var2));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
