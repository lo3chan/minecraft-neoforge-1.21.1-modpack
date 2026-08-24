package amp_libs.org.bouncycastle.jcajce.provider.util;

import amp_libs.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import amp_libs.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface AsymmetricKeyInfoConverter {
   PrivateKey generatePrivate(PrivateKeyInfo var1) throws IOException;

   PublicKey generatePublic(SubjectPublicKeyInfo var1) throws IOException;
}
