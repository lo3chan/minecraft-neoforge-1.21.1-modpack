package amp_libs.org.bouncycastle.operator;

import amp_libs.org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import amp_libs.org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.io.OutputStream;

public interface DigestCalculator {
   AlgorithmIdentifier SHA_256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
   AlgorithmIdentifier SHA_512 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512);

   AlgorithmIdentifier getAlgorithmIdentifier();

   OutputStream getOutputStream();

   byte[] getDigest();
}
