package amp_libs.org.bouncycastle.math.field;

public interface PolynomialExtensionField extends ExtensionField {
   Polynomial getMinimalPolynomial();
}
