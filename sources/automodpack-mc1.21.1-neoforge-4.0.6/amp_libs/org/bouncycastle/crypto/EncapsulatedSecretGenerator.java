package amp_libs.org.bouncycastle.crypto;

import amp_libs.org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface EncapsulatedSecretGenerator {
   SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter var1);
}
