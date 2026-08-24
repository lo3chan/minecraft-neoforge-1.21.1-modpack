package amp_libs.org.bouncycastle.crypto;

public interface CryptoServiceProperties {
   int bitsOfSecurity();

   String getServiceName();

   CryptoServicePurpose getPurpose();

   Object getParams();
}
