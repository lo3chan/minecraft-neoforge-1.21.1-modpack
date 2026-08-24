package amp_libs.org.antlr.v4.runtime.tree;

import amp_libs.org.antlr.v4.runtime.RuleContext;

public interface RuleNode extends ParseTree {
   RuleContext getRuleContext();
}
