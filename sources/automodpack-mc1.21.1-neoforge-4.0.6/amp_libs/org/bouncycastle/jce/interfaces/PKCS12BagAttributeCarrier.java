package amp_libs.org.bouncycastle.jce.interfaces;

import amp_libs.org.bouncycastle.asn1.ASN1Encodable;
import amp_libs.org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Enumeration;

public interface PKCS12BagAttributeCarrier {
   void setBagAttribute(ASN1ObjectIdentifier var1, ASN1Encodable var2);

   ASN1Encodable getBagAttribute(ASN1ObjectIdentifier var1);

   Enumeration getBagAttributeKeys();

   boolean hasFriendlyName();

   void setFriendlyName(String var1);
}
