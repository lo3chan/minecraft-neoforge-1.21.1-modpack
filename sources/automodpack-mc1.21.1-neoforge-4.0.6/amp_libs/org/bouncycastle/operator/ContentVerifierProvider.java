package amp_libs.org.bouncycastle.operator;

import amp_libs.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import amp_libs.org.bouncycastle.cert.X509CertificateHolder;

public interface ContentVerifierProvider {
   boolean hasAssociatedCertificate();

   X509CertificateHolder getAssociatedCertificate();

   ContentVerifier get(AlgorithmIdentifier var1) throws OperatorCreationException;
}
