package amp_libs.org.bouncycastle.pqc.jcajce.provider.mayo;

import amp_libs.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import amp_libs.org.bouncycastle.pqc.crypto.mayo.MayoPublicKeyParameters;
import amp_libs.org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import amp_libs.org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import amp_libs.org.bouncycastle.pqc.jcajce.interfaces.MayoKey;
import amp_libs.org.bouncycastle.pqc.jcajce.spec.MayoParameterSpec;
import amp_libs.org.bouncycastle.util.Arrays;
import amp_libs.org.bouncycastle.util.Strings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;

public class BCMayoPublicKey implements PublicKey, MayoKey {
   private static final long serialVersionUID = 1L;
   private transient MayoPublicKeyParameters params;

   public BCMayoPublicKey(MayoPublicKeyParameters var1) {
      this.params = var1;
   }

   public BCMayoPublicKey(SubjectPublicKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(SubjectPublicKeyInfo var1) throws IOException {
      this.params = (MayoPublicKeyParameters)PublicKeyFactory.createKey(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCMayoPublicKey) {
         BCMayoPublicKey var2 = (BCMayoPublicKey)var1;
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
         SubjectPublicKeyInfo var1 = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(this.params);
         return var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   @Override
   public MayoParameterSpec getParameterSpec() {
      return MayoParameterSpec.fromName(this.params.getParameters().getName());
   }

   MayoPublicKeyParameters getKeyParams() {
      return this.params;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      byte[] var2 = (byte[])var1.readObject();
      this.init(SubjectPublicKeyInfo.getInstance(var2));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
