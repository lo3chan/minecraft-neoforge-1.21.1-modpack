package amp_libs.org.antlr.v4.runtime.tree;

import amp_libs.org.antlr.v4.runtime.Token;

public interface TerminalNode extends ParseTree {
   Token getSymbol();
}
