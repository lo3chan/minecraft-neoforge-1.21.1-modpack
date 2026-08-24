package amp_libs.org.antlr.v4.runtime.tree;

import amp_libs.org.antlr.v4.runtime.misc.Interval;

public interface SyntaxTree extends Tree {
   Interval getSourceInterval();
}
