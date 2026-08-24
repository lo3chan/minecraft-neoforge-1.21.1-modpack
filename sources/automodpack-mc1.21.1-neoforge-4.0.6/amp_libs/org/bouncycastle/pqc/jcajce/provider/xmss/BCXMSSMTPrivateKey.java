package amp_libs.org.bouncycastle.pqc.jcajce.provider.xmss;

import amp_libs.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import amp_libs.org.bouncycastle.asn1.ASN1Set;
import amp_libs.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import amp_libs.org.bouncycastle.crypto.CipherParameters;
import amp_libs.org.bouncycastle.pqc.asn1.XMSSMTKeyParams;
import amp_libs.org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import amp_libs.org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import amp_libs.org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import amp_libs.org.bouncycastle.pqc.jcajce.interfaces.XMSSMTPrivateKey;
import amp_libs.org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;

public class BCXMSSMTPrivateKey implements PrivateKey, XMSSMTPrivateKey {
   private static final long serialVersionUID = 7682140473044521395L;
   private transient ASN1ObjectIdentifier treeDigest;
   private transient XMSSMTPrivateKeyParameters keyParams;
   private transient ASN1Set attributes;

   public BCXMSSMTPrivateKey(ASN1ObjectIdentifier var1, XMSSMTPrivateKeyParameters var2) {
      this.treeDigest = var1;
      this.keyParams = var2;
   }

   public BCXMSSMTPrivateKey(PrivateKeyInfo var1) throws IOException {
      this.init(var1);
   }

   private void init(PrivateKeyInfo var1) throws IOException {
      this.attributes = var1.getAttributes();
      XMSSMTKeyParams var2 = XMSSMTKeyParams.getInstance(var1.getPrivateKeyAlgorithm().getParameters());
      this.treeDigest = var2.getTreeDigest().getAlgorithm();
      this.keyParams = (XMSSMTPrivateKeyParameters)PrivateKeyFactory.createKey(var1);
   }

   @Override
   public long getIndex() {
      if (this.getUsagesRemaining() == 0L) {
         throw new IllegalStateException("key exhausted");
      } else {
         return this.keyParams.getIndex();
      }
   }

   @Override
   public long getUsagesRemaining() {
      return this.keyParams.getUsagesRemaining();
   }

   @Override
   public XMSSMTPrivateKey extractKeyShard(int var1) {
      return new BCXMSSMTPrivateKey(this.treeDigest, this.keyParams.extractKeyShard(var1));
   }

   @Override
   public String getAlgorithm() {
      return "XMSSMT";
   }

   @Override
   public String getFormat() {
      return "PKCS#8";
   }

   @Override
   public byte[] getEncoded() {
      try {
         PrivateKeyInfo var1 = PrivateKeyInfoFactory.createPrivateKeyInfo(this.keyParams, this.attributes);
         return var1.getEncoded();
      } catch (IOException var2) {
         return null;
      }
   }

   CipherParameters getKeyParams() {
      return this.keyParams;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BCXMSSMTPrivateKey)) {
         return false;
      } else {
         BCXMSSMTPrivateKey var2 = (BCXMSSMTPrivateKey)var1;
         return this.treeDigest.equals(var2.treeDigest) && Arrays.areEqual(this.keyParams.toByteArray(), var2.keyParams.toByteArray());
      }
   }

   @Override
   public int hashCode() {
      return this.treeDigest.hashCode() + 37 * Arrays.hashCode(this.keyParams.toByteArray());
   }

   ASN1ObjectIdentifier getTreeDigestOID() {
      return this.treeDigest;
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
      this.init(PrivateKeyInfo.getInstance(var2));
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.getEncoded());
   }
}
