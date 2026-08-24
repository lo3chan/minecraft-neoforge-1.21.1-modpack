package amp_libs.org.bouncycastle.asn1.x9;

import amp_libs.org.bouncycastle.asn1.ASN1BitString;
import amp_libs.org.bouncycastle.asn1.ASN1Integer;
import amp_libs.org.bouncycastle.asn1.ASN1Object;
import amp_libs.org.bouncycastle.asn1.ASN1Primitive;
import amp_libs.org.bouncycastle.asn1.ASN1Sequence;
import amp_libs.org.bouncycastle.asn1.ASN1TaggedObject;
import amp_libs.org.bouncycastle.asn1.DERBitString;
import amp_libs.org.bouncycastle.asn1.DERSequence;
import java.math.BigInteger;

public class ValidationParams extends ASN1Object {
   private ASN1BitString seed;
   private ASN1Integer pgenCounter;

   public static ValidationParams getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static ValidationParams getInstance(Object var0) {
      if (var0 instanceof ValidationParams) {
         return (ValidationParams)var0;
      } else {
         return var0 != null ? new ValidationParams(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ValidationParams(byte[] var1, int var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("'seed' cannot be null");
      } else {
         this.seed = new DERBitString(var1);
         this.pgenCounter = new ASN1Integer(var2);
      }
   }

   public ValidationParams(DERBitString var1, ASN1Integer var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("'seed' cannot be null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("'pgenCounter' cannot be null");
      } else {
         this.seed = var1;
         this.pgenCounter = var2;
      }
   }

   private ValidationParams(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.seed = ASN1BitString.getInstance(var1.getObjectAt(0));
         this.pgenCounter = ASN1Integer.getInstance(var1.getObjectAt(1));
      }
   }

   public byte[] getSeed() {
      return this.seed.getBytes();
   }

   public BigInteger getPgenCounter() {
      return this.pgenCounter.getPositiveValue();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.seed, this.pgenCounter);
   }
}
