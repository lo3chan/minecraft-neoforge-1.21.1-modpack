package amp_libs.org.bouncycastle.pqc.jcajce.provider.hqc;

import amp_libs.org.bouncycastle.asn1.ASN1Set;
import amp_libs.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import amp_libs.org.bouncycastle.pqc.crypto.hqc.HQCPrivateKeyParameters;
import amp_libs.org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import amp_libs.org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import amp_libs.org.bouncycastle.pqc.jcajce.interfaces.HQCKey;
import amp_libs.org.bouncycastle.pqc.jcajce.spec.HQCParameterSpec;
import amp_libs.org.bouncycastle.util.Arrays;
import amp_libs.org.bouncycastle.util.Strings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;

public class BCHQCPrivateKey implements PrivateKey, HQCKey {
   private static final long serialVersionUID = 1L;
   private transient HQCPrivateKeyParameters params;
   private transient ASN1Set attributes;

   public BCHQCPrivateKey(HQCPrivateKeyParameters var1) {
      this.params = var1;
   }

   public BCHQCPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(PrivateKeyInfo var1) throws IOException {
      this.attributes = var1.getAttributes();
      this.params = (HQCPrivateKeyParameters)PrivateKeyFactory.createKey(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCHQCPrivateKey) {
         BCHQCPrivateKey var2 = (BCHQCPrivateKey)var1;
         return Arrays.areEqual(this.params.getEncoded(), var2.params.getEncoded());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.params.getEncoded());
   }

   @Override
   public final String getAlgorithm() {
      return Strings.toUpperCase(this.params.getParameters().getName());
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
   public HQCParameterSpec getParameterSpec() {
      return HQCParameterSpec.fromName(this.params.getParameters().getName());
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   HQCPrivateKeyParameters getKeyParams() {
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
