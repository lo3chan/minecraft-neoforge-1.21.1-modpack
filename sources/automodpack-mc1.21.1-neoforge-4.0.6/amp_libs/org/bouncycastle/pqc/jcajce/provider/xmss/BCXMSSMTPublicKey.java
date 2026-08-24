package amp_libs.org.bouncycastle.pqc.jcajce.provider.xmss;

import amp_libs.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import amp_libs.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import amp_libs.org.bouncycastle.crypto.CipherParameters;
import amp_libs.org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import amp_libs.org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import amp_libs.org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import amp_libs.org.bouncycastle.pqc.jcajce.interfaces.XMSSMTKey;
import amp_libs.org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;

public class BCXMSSMTPublicKey implements PublicKey, XMSSMTKey {
   private static final long serialVersionUID = 3230324130542413475L;
   private transient ASN1ObjectIdentifier treeDigest;
   private transient XMSSMTPublicKeyParameters keyParams;

   public BCXMSSMTPublicKey(ASN1ObjectIdentifier var1, XMSSMTPublicKeyParameters var2) {
      this.treeDigest = var1;
      this.keyParams = var2;
   }

   public BCXMSSMTPublicKey(SubjectPublicKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(SubjectPublicKeyInfo var1) throws IOException {
      this.keyParams = (XMSSMTPublicKeyParameters)PublicKeyFactory.createKey(var1);
      this.treeDigest = DigestUtil.getDigestOID(this.keyParams.getTreeDigest());
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BCXMSSMTPublicKey)) {
         return false;
      } else {
         BCXMSSMTPublicKey var2 = (BCXMSSMTPublicKey)var1;
         return this.treeDigest.equals(var2.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), var2.keyParams.toByteArray());
      }
   }

   @Override
   public int hashCode() {
      return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.keyParams.toByteArray());
   }

   @Override
   public final String getAlgorithm() {
      return "XMSSMT";
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
   public int getHeight() {
      return this.keyParams.getParameters().getHeight();
   }

   @Override
   public int getLayers() {
      return this.keyParams.getParameters().getLayers();
   }

   @Override
   public String getTreeDigest() {
      return DigestUtil.getXMSSDigestName(this.treeDigest);
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
