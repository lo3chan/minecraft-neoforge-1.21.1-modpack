/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.optifine.util.LineBuffer
 *  org.apache.commons.lang3.StringUtils
 */
package net.diebuddies.util;

import net.diebuddies.util.ShaderFixes;
import net.optifine.util.LineBuffer;
import org.apache.commons.lang3.StringUtils;

public class ShaderFixesOptifine {
    public static LineBuffer applyOptifineFixes(LineBuffer lines, boolean onlyOptifineFix) {
        if (lines == null) {
            return null;
        }
        LineBuffer buffer = new LineBuffer();
        boolean hasColorModulator = false;
        for (String line : lines) {
            if (!line.contains("colorModulator")) continue;
            hasColorModulator = true;
            break;
        }
        for (String line : lines) {
            if (!onlyOptifineFix) {
                line = ShaderFixes.applyFixes(line);
            }
            if (!hasColorModulator) {
                if (line.contains("in vec4 vaColor;")) {
                    buffer.add(line);
                    buffer.add("uniform vec4 colorModulator;");
                    buffer.add("#define PHYSICS_COLOR (vaColor * colorModulator)");
                    continue;
                }
                line = StringUtils.replace((String)line, (String)"vaColor", (String)"PHYSICS_COLOR");
                buffer.add(line);
                continue;
            }
            buffer.add(line);
        }
        return buffer;
    }
}

