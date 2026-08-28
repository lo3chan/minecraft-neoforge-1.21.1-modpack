/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.APIUtil
 *  org.lwjgl.system.CustomBuffer
 *  org.lwjgl.system.FunctionProvider
 *  org.lwjgl.system.JNI
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.SharedLibrary
 */
package net.caffeinemc.mods.sodium.client.platform.windows.api.version;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.caffeinemc.mods.sodium.client.platform.windows.api.version.QueryResult;
import net.caffeinemc.mods.sodium.client.platform.windows.api.version.VersionInfo;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.CustomBuffer;
import org.lwjgl.system.FunctionProvider;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.SharedLibrary;

public class Version {
    private static final SharedLibrary LIBRARY = APIUtil.apiCreateLibrary((String)"version");
    private static final long PFN_GetFileVersionInfoSizeW = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"GetFileVersionInfoSizeW");
    private static final long PFN_GetFileVersionInfoW = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"GetFileVersionInfoW");
    private static final long PFN_VerQueryValueW = APIUtil.apiGetFunctionAddress((FunctionProvider)LIBRARY, (String)"VerQueryValueW");

    @Nullable
    static QueryResult query(ByteBuffer pBlock, String subBlock) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            ByteBuffer pSubBlock = stack.malloc(16, MemoryUtil.memLengthUTF16((CharSequence)subBlock, (boolean)true));
            MemoryUtil.memUTF16((CharSequence)subBlock, (boolean)true, (ByteBuffer)pSubBlock);
            PointerBuffer pBuffer = stack.callocPointer(1);
            IntBuffer pLen = stack.callocInt(1);
            int result = JNI.callPPPPI((long)MemoryUtil.memAddress((ByteBuffer)pBlock), (long)MemoryUtil.memAddress((ByteBuffer)pSubBlock), (long)MemoryUtil.memAddress((CustomBuffer)pBuffer), (long)MemoryUtil.memAddress((IntBuffer)pLen), (long)PFN_VerQueryValueW);
            if (result == 0) {
                QueryResult queryResult = null;
                return queryResult;
            }
            QueryResult queryResult = new QueryResult(pBuffer.get(), pLen.get());
            return queryResult;
        }
    }

    /*
     * Exception decompiling
     */
    @Nullable
    public static VersionInfo getModuleFileVersion(String filename) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }
}

