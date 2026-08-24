package amp_libs.org.antlr.v4.runtime.tree;

import amp_libs.org.antlr.v4.runtime.Parser;
import amp_libs.org.antlr.v4.runtime.RuleContext;

public interface ParseTree extends SyntaxTree {
   ParseTree getParent();

   ParseTree getChild(int var1);

   void setParent(RuleContext var1);

   <T> T accept(ParseTreeVisitor<? extends T> var1);

   String getText();

   String toStringTree(Parser var1);
}
