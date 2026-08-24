package amp_libs.org.bouncycastle.pqc.jcajce.provider.lms;

import amp_libs.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import amp_libs.org.bouncycastle.crypto.CipherParameters;
import amp_libs.org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import amp_libs.org.bouncycastle.pqc.crypto.lms.LMSKeyParameters;
import amp_libs.org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import amp_libs.org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import amp_libs.org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import amp_libs.org.bouncycastle.pqc.jcajce.interfaces.LMSKey;
import amp_libs.org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;

public class BCLMSPublicKey implements PublicKey, LMSKey {
   private static final long serialVersionUID = -5617456225328969766L;
   private transient LMSKeyParameters keyParams;

   public BCLMSPublicKey(LMSKeyParameters var1) {
      this.keyParams = (LMSKeyParameters)(var1 instanceof HSSPublicKeyParameters ? var1 : new HSSPublicKeyParameters(1, (LMSPublicKeyParameters)var1));
   }

   public BCLMSPublicKey(SubjectPublicKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(SubjectPublicKeyInfo var1) throws IOException {
      this.keyParams = (LMSKeyParameters)PublicKeyFactory.createKey(var1);
   }

   @Override
   public final String getAlgorithm() {
      return "LMS";
   }

   @Override
   public byte[] getEncoded() {
      try {
         SubjectPublicKeyInfo var1 = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(this.keyParams);
         return var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   @Override
   public String getFormat() {
      return "X.509";
   }

   CipherParameters getKeyParams() {
      return this.keyParams;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 instanceof BCLMSPublicKey) {
         BCLMSPublicKey var2 = (BCLMSPublicKey)var1;

         try {
            return Arrays.areEqual(this.keyParams.getEncoded(), var2.keyParams.getEncoded());
         } catch (IOException var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      try {
         return Arrays.hashCode(this.keyParams.getEncoded());
      } catch (IOException var2) {
         return -1;
      }
   }

   @Override
   public int getLevels() {
      return this.keyParams instanceof LMSPublicKeyParameters ? 1 : ((HSSPublicKeyParameters)this.keyParams).getL();
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
