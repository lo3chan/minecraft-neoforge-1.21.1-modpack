package amp_libs.org.bouncycastle.asn1.pkcs;

import amp_libs.org.bouncycastle.asn1.ASN1Encodable;
import amp_libs.org.bouncycastle.asn1.ASN1EncodableVector;
import amp_libs.org.bouncycastle.asn1.ASN1Object;
import amp_libs.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import amp_libs.org.bouncycastle.asn1.ASN1Primitive;
import amp_libs.org.bouncycastle.asn1.ASN1Sequence;
import amp_libs.org.bouncycastle.asn1.ASN1TaggedObject;
import amp_libs.org.bouncycastle.asn1.BERSequence;
import amp_libs.org.bouncycastle.asn1.BERTaggedObject;
import amp_libs.org.bouncycastle.asn1.DLSequence;
import java.util.Enumeration;

public class ContentInfo extends ASN1Object implements PKCSObjectIdentifiers {
   private ASN1ObjectIdentifier contentType;
   private ASN1Encodable content;
   private boolean isBer = true;

   public static ContentInfo getInstance(Object var0) {
      if (var0 instanceof ContentInfo) {
         return (ContentInfo)var0;
      } else {
         return var0 != null ? new ContentInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private ContentInfo(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();
      this.contentType = (ASN1ObjectIdentifier)var2.nextElement();
      if (var2.hasMoreElements()) {
         this.content = ((ASN1TaggedObject)var2.nextElement()).getExplicitBaseObject();
      }

      this.isBer = var1 instanceof BERSequence;
   }

   public ContentInfo(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.contentType = var1;
      this.content = var2;
   }

   public ASN1ObjectIdentifier getContentType() {
      return this.contentType;
   }

   public ASN1Encodable getContent() {
      return this.content;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.contentType);
      if (this.content != null) {
         var1.add(new BERTaggedObject(true, 0, this.content));
      }

      return (ASN1Primitive)(this.isBer ? new BERSequence(var1) : new DLSequence(var1));
   }
}
