package amp_libs.org.bouncycastle.pqc.jcajce.provider.newhope;

import amp_libs.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import amp_libs.org.bouncycastle.crypto.CipherParameters;
import amp_libs.org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import amp_libs.org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import amp_libs.org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import amp_libs.org.bouncycastle.pqc.jcajce.interfaces.NHPublicKey;
import amp_libs.org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BCNHPublicKey implements NHPublicKey {
   private static final long serialVersionUID = 1L;
   private transient NHPublicKeyParameters params;

   public BCNHPublicKey(NHPublicKeyParameters var1) {
      this.params = var1;
   }

   public BCNHPublicKey(SubjectPublicKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(SubjectPublicKeyInfo var1) throws IOException {
      this.params = (NHPublicKeyParameters)PublicKeyFactory.createKey(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof BCNHPublicKey) {
         BCNHPublicKey var2 = (BCNHPublicKey)var1;
         return Arrays.areEqual(this.params.getPubData(), var2.params.getPubData());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.params.getPubData());
   }

   @Override
   public final String getAlgorithm() {
      return "NH";
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
   public byte[] getPublicData() {
      return this.params.getPubData();
   }

   CipherParameters getKeyParams() {
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
