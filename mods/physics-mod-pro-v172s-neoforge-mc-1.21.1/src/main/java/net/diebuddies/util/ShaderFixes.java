/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package net.diebuddies.util;

import java.io.IOException;
import net.diebuddies.util.cpp.DefaultPreprocessorListener;
import net.diebuddies.util.cpp.LexerException;
import net.diebuddies.util.cpp.Preprocessor;
import net.diebuddies.util.cpp.Source;
import net.diebuddies.util.cpp.StringLexerSource;
import net.diebuddies.util.cpp.Token;
import org.apache.commons.lang3.StringUtils;

public class ShaderFixes {
    public static String applyFixes(String shaderSource) {
        if (!shaderSource.contains("gl_TextureMatrix[0]")) {
            shaderSource = StringUtils.replace((String)shaderSource, (String)"gl_MultiTexCoord0", (String)"(gl_TextureMatrix[0] * gl_MultiTexCoord0)");
        }
        shaderSource = StringUtils.replace((String)shaderSource, (String)"flat varying mat3 tbn;", (String)"varying mat3 tbn;");
        shaderSource = StringUtils.replace((String)shaderSource, (String)"vec3 normalM = VdotN > 0.0 ? -normal : normal;", (String)"vec3 normalM = normal;");
        shaderSource = StringUtils.replace((String)shaderSource, (String)"flat out vec3 normal;", (String)"out vec3 normal;");
        shaderSource = StringUtils.replace((String)shaderSource, (String)"flat in vec3 normal;", (String)"in vec3 normal;");
        shaderSource = StringUtils.replace((String)shaderSource, (String)"flat out mat3 tbn;", (String)"out mat3 tbn;");
        shaderSource = StringUtils.replace((String)shaderSource, (String)"flat in mat3 tbn;", (String)"in mat3 tbn;");
        return shaderSource;
    }

    public static String preprocessOptifineSource(String shaderSource) {
        String string;
        String content = shaderSource;
        content = StringUtils.replace((String)content, (String)"#version", (String)"#warning TMP_VERSION");
        content = StringUtils.replace((String)content, (String)"#extension", (String)"#warning TMP_EXTENSION");
        Preprocessor processor = new Preprocessor(new StringLexerSource(content, true));
        try {
            IgnorePreprocessorListener listener = new IgnorePreprocessorListener();
            processor.setListener(listener);
            StringBuilder builder = new StringBuilder();
            Token token = null;
            try {
                while ((token = processor.token()) != null && token.getType() != 265) {
                    builder.append(token.getText());
                }
            }
            catch (LexerException e) {
                e.printStackTrace();
            }
            processor.close();
            string = listener.getIgnoredTokens() + builder.toString();
        }
        catch (Throwable throwable) {
            try {
                try {
                    processor.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                e.printStackTrace();
                return shaderSource;
            }
        }
        processor.close();
        return string;
    }

    private static class IgnorePreprocessorListener
    extends DefaultPreprocessorListener {
        private StringBuilder sb = new StringBuilder();

        private IgnorePreprocessorListener() {
        }

        @Override
        public void handleWarning(Source source, int line, int column, String msg) throws LexerException {
            if (msg.contains("TMP_VERSION")) {
                this.sb.append(StringUtils.replace((String)msg, (String)"#warning TMP_VERSION", (String)"#version")).append("\n");
            } else if (msg.contains("TMP_EXTENSION")) {
                this.sb.append(StringUtils.replace((String)msg, (String)"#warning TMP_EXTENSION", (String)"#extension")).append("\n");
            } else {
                super.handleWarning(source, line, column, msg);
            }
        }

        public String getIgnoredTokens() {
            return this.sb.toString();
        }
    }
}

