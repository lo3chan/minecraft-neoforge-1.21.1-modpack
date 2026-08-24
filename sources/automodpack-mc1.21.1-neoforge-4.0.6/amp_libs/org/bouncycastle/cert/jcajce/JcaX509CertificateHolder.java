package amp_libs.org.bouncycastle.cert.jcajce;

import amp_libs.org.bouncycastle.asn1.x509.Certificate;
import amp_libs.org.bouncycastle.cert.X509CertificateHolder;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public class JcaX509CertificateHolder extends X509CertificateHolder {
   public JcaX509CertificateHolder(X509Certificate var1) throws CertificateEncodingException {
      super(Certificate.getInstance(var1.getEncoded()));
   }
}
