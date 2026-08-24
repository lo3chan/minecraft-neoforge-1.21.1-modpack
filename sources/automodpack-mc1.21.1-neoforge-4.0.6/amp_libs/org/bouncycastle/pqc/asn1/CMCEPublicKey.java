package amp_libs.org.bouncycastle.pqc.asn1;

import amp_libs.org.bouncycastle.asn1.ASN1EncodableVector;
import amp_libs.org.bouncycastle.asn1.ASN1Object;
import amp_libs.org.bouncycastle.asn1.ASN1OctetString;
import amp_libs.org.bouncycastle.asn1.ASN1Primitive;
import amp_libs.org.bouncycastle.asn1.ASN1Sequence;
import amp_libs.org.bouncycastle.asn1.DEROctetString;
import amp_libs.org.bouncycastle.asn1.DERSequence;
import amp_libs.org.bouncycastle.util.Arrays;

public class CMCEPublicKey extends ASN1Object {
   private byte[] T;

   public CMCEPublicKey(byte[] var1) {
      this.T = var1;
   }

   @Deprecated
   public CMCEPublicKey(ASN1Sequence var1) {
      this.T = Arrays.clone(ASN1OctetString.getInstance(var1.getObjectAt(0)).getOctets());
   }

   public byte[] getT() {
      return Arrays.clone(this.T);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      var1.add(new DEROctetString(this.T));
      return new DERSequence(var1);
   }

   public static CMCEPublicKey getInstance(Object var0) {
      if (var0 instanceof CMCEPublicKey) {
         return (CMCEPublicKey)var0;
      } else {
         return var0 != null ? new CMCEPublicKey(ASN1Sequence.getInstance(var0)) : null;
      }
   }
}
