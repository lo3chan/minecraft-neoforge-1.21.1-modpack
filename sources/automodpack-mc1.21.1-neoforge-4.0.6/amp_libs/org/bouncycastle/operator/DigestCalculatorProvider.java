package amp_libs.org.bouncycastle.operator;

import amp_libs.org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface DigestCalculatorProvider {
   DigestCalculator get(AlgorithmIdentifier var1) throws OperatorCreationException;
}
