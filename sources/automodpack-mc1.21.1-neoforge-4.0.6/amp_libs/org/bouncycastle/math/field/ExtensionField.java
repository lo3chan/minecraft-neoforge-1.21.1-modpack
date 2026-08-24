package amp_libs.org.bouncycastle.math.field;

public interface ExtensionField extends FiniteField {
   FiniteField getSubfield();

   int getDegree();
}
