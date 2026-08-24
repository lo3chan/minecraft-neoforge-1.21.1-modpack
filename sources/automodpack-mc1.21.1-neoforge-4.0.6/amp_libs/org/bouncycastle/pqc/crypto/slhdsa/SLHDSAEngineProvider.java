package amp_libs.org.bouncycastle.pqc.crypto.slhdsa;

interface SLHDSAEngineProvider {
   int getN();

   SLHDSAEngine get();
}
