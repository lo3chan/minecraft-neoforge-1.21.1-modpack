package amp_libs.org.antlr.v4.runtime.tree.xpath;

import amp_libs.org.antlr.v4.runtime.tree.ParseTree;
import amp_libs.org.antlr.v4.runtime.tree.Tree;
import amp_libs.org.antlr.v4.runtime.tree.Trees;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XPathWildcardElement extends XPathElement {
   public XPathWildcardElement() {
      super("*");
   }

   @Override
   public Collection<ParseTree> evaluate(ParseTree t) {
      if (this.invert) {
         return new ArrayList<>();
      } else {
         List<ParseTree> kids = new ArrayList<>();

         for (Tree c : Trees.getChildren(t)) {
            kids.add((ParseTree)c);
         }

         return kids;
      }
   }
}
