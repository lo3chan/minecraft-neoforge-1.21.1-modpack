package amp_libs.org.bouncycastle.jce.interfaces;

import amp_libs.org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public interface GOST3410Params {
   String getPublicKeyParamSetOID();

   String getDigestParamSetOID();

   String getEncryptionParamSetOID();

   GOST3410PublicKeyParameterSetSpec getPublicKeyParameters();
}
