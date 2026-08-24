package amp_libs.org.bouncycastle.jce;

import amp_libs.org.bouncycastle.asn1.ASN1InputStream;
import amp_libs.org.bouncycastle.asn1.ASN1Sequence;
import amp_libs.org.bouncycastle.asn1.x500.X500Name;
import amp_libs.org.bouncycastle.asn1.x509.X509Name;
import java.io.IOException;
import java.security.Principal;
import java.util.Hashtable;
import java.util.Vector;

/** @deprecated */
public class X509Principal extends X509Name implements Principal {
   private static ASN1Sequence readSequence(ASN1InputStream var0) throws IOException {
      try {
         return ASN1Sequence.getInstance(var0.readObject());
      } catch (IllegalArgumentException var2) {
         throw new IOException("not an ASN.1 Sequence: " + var2);
      }
   }

   public X509Principal(byte[] var1) throws IOException {
      super(readSequence(new ASN1InputStream(var1)));
   }

   public X509Principal(X509Name var1) {
      super((ASN1Sequence)var1.toASN1Primitive());
   }

   public X509Principal(X500Name var1) {
      super((ASN1Sequence)var1.toASN1Primitive());
   }

   public X509Principal(Hashtable var1) {
      super(var1);
   }

   public X509Principal(Vector var1, Hashtable var2) {
      super(var1, var2);
   }

   public X509Principal(Vector var1, Vector var2) {
      super(var1, var2);
   }

   public X509Principal(String var1) {
      super(var1);
   }

   public X509Principal(boolean var1, String var2) {
      super(var1, var2);
   }

   public X509Principal(boolean var1, Hashtable var2, String var3) {
      super(var1, var2, var3);
   }

   @Override
   public String getName() {
      return this.toString();
   }

   @Override
   public byte[] getEncoded() {
      try {
         return this.getEncoded("DER");
      } catch (IOException var2) {
         throw new RuntimeException(var2.toString());
      }
   }
}
