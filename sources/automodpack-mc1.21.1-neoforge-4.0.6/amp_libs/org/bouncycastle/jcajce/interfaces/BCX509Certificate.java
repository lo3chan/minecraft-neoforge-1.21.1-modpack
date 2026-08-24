package amp_libs.org.bouncycastle.jcajce.interfaces;

import amp_libs.org.bouncycastle.asn1.x500.X500Name;
import amp_libs.org.bouncycastle.asn1.x509.TBSCertificate;

public interface BCX509Certificate {
   X500Name getIssuerX500Name();

   TBSCertificate getTBSCertificateNative();

   X500Name getSubjectX500Name();
}
