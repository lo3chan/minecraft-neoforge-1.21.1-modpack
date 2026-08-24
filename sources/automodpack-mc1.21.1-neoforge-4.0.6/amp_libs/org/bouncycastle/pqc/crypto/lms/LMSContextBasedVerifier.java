package amp_libs.org.bouncycastle.pqc.crypto.lms;

public interface LMSContextBasedVerifier {
   LMSContext generateLMSContext(byte[] var1);

   boolean verify(LMSContext var1);
}
