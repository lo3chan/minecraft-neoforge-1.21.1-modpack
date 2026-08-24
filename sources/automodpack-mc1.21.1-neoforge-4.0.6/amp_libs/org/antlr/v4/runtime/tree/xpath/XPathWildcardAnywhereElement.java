package amp_libs.org.antlr.v4.runtime.tree.xpath;

import amp_libs.org.antlr.v4.runtime.tree.ParseTree;
import amp_libs.org.antlr.v4.runtime.tree.Trees;
import java.util.ArrayList;
import java.util.Collection;

public class XPathWildcardAnywhereElement extends XPathElement {
   public XPathWildcardAnywhereElement() {
      super("*");
   }

   @Override
   public Collection<ParseTree> evaluate(ParseTree t) {
      return (Collection<ParseTree>)(this.invert ? new ArrayList<>() : Trees.getDescendants(t));
   }
}
