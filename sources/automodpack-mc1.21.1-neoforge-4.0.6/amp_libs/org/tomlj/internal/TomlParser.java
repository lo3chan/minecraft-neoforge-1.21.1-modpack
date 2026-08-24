package amp_libs.org.tomlj.internal;

import amp_libs.org.antlr.v4.runtime.NoViableAltException;
import amp_libs.org.antlr.v4.runtime.Parser;
import amp_libs.org.antlr.v4.runtime.ParserRuleContext;
import amp_libs.org.antlr.v4.runtime.RecognitionException;
import amp_libs.org.antlr.v4.runtime.RuntimeMetaData;
import amp_libs.org.antlr.v4.runtime.TokenStream;
import amp_libs.org.antlr.v4.runtime.Vocabulary;
import amp_libs.org.antlr.v4.runtime.VocabularyImpl;
import amp_libs.org.antlr.v4.runtime.atn.ATN;
import amp_libs.org.antlr.v4.runtime.atn.ATNDeserializer;
import amp_libs.org.antlr.v4.runtime.atn.ParserATNSimulator;
import amp_libs.org.antlr.v4.runtime.atn.PredictionContextCache;
import amp_libs.org.antlr.v4.runtime.dfa.DFA;
import amp_libs.org.antlr.v4.runtime.tree.ParseTreeListener;
import amp_libs.org.antlr.v4.runtime.tree.ParseTreeVisitor;
import amp_libs.org.antlr.v4.runtime.tree.TerminalNode;
import java.util.List;

public class TomlParser extends Parser {
   protected static final DFA[] _decisionToDFA;
   protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
   public static final int TripleQuotationMark = 1;
   public static final int TripleApostrophe = 2;
   public static final int StringChar = 3;
   public static final int Comma = 4;
   public static final int Dot = 5;
   public static final int Equals = 6;
   public static final int QuotationMark = 7;
   public static final int Apostrophe = 8;
   public static final int TableKeyStart = 9;
   public static final int TableKeyEnd = 10;
   public static final int ArrayTableKeyStart = 11;
   public static final int ArrayTableKeyEnd = 12;
   public static final int UnquotedKey = 13;
   public static final int WS = 14;
   public static final int Comment = 15;
   public static final int NewLine = 16;
   public static final int Error = 17;
   public static final int DecimalInteger = 18;
   public static final int HexInteger = 19;
   public static final int OctalInteger = 20;
   public static final int BinaryInteger = 21;
   public static final int FloatingPoint = 22;
   public static final int FloatingPointInf = 23;
   public static final int FloatingPointNaN = 24;
   public static final int TrueBoolean = 25;
   public static final int FalseBoolean = 26;
   public static final int ArrayStart = 27;
   public static final int ArrayEnd = 28;
   public static final int InlineTableStart = 29;
   public static final int EscapeSequence = 30;
   public static final int Dash = 31;
   public static final int Plus = 32;
   public static final int Colon = 33;
   public static final int Z = 34;
   public static final int TimeDelimiter = 35;
   public static final int DateDigits = 36;
   public static final int InlineTableEnd = 37;
   public static final int InlineTableComma = 38;
   public static final int RULE_toml = 0;
   public static final int RULE_expression = 1;
   public static final int RULE_tomlKey = 2;
   public static final int RULE_keyval = 3;
   public static final int RULE_key = 4;
   public static final int RULE_simpleKey = 5;
   public static final int RULE_unquotedKey = 6;
   public static final int RULE_quotedKey = 7;
   public static final int RULE_val = 8;
   public static final int RULE_string = 9;
   public static final int RULE_basicString = 10;
   public static final int RULE_basicChar = 11;
   public static final int RULE_basicUnescaped = 12;
   public static final int RULE_escaped = 13;
   public static final int RULE_mlBasicString = 14;
   public static final int RULE_mlBasicChar = 15;
   public static final int RULE_mlBasicUnescaped = 16;
   public static final int RULE_literalString = 17;
   public static final int RULE_literalBody = 18;
   public static final int RULE_mlLiteralString = 19;
   public static final int RULE_mlLiteralBody = 20;
   public static final int RULE_integer = 21;
   public static final int RULE_decInt = 22;
   public static final int RULE_hexInt = 23;
   public static final int RULE_octInt = 24;
   public static final int RULE_binInt = 25;
   public static final int RULE_floatValue = 26;
   public static final int RULE_regularFloat = 27;
   public static final int RULE_regularFloatInf = 28;
   public static final int RULE_regularFloatNaN = 29;
   public static final int RULE_booleanValue = 30;
   public static final int RULE_trueBool = 31;
   public static final int RULE_falseBool = 32;
   public static final int RULE_dateTime = 33;
   public static final int RULE_offsetDateTime = 34;
   public static final int RULE_localDateTime = 35;
   public static final int RULE_localDate = 36;
   public static final int RULE_localTime = 37;
   public static final int RULE_date = 38;
   public static final int RULE_time = 39;
   public static final int RULE_timeOffset = 40;
   public static final int RULE_hourOffset = 41;
   public static final int RULE_minuteOffset = 42;
   public static final int RULE_secondFraction = 43;
   public static final int RULE_year = 44;
   public static final int RULE_month = 45;
   public static final int RULE_day = 46;
   public static final int RULE_hour = 47;
   public static final int RULE_minute = 48;
   public static final int RULE_second = 49;
   public static final int RULE_array = 50;
   public static final int RULE_arrayValues = 51;
   public static final int RULE_arrayValue = 52;
   public static final int RULE_table = 53;
   public static final int RULE_standardTable = 54;
   public static final int RULE_inlineTable = 55;
   public static final int RULE_inlineTableValues = 56;
   public static final int RULE_arrayTable = 57;
   public static final String[] ruleNames = makeRuleNames();
   private static final String[] _LITERAL_NAMES = makeLiteralNames();
   private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
   public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
   @Deprecated
   public static final String[] tokenNames = new String[_SYMBOLIC_NAMES.length];
   public static final String _serializedATN = "\u0004\u0001&Ɨ\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002'\u0007'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u00022\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u00027\u00077\u00028\u00078\u00029\u00079\u0001\u0000\u0005\u0000v\b\u0000\n\u0000\f\u0000y\t\u0000\u0001\u0000\u0001\u0000\u0004\u0000}\b\u0000\u000b\u0000\f\u0000~\u0001\u0000\u0005\u0000\u0082\b\u0000\n\u0000\f\u0000\u0085\t\u0000\u0001\u0000\u0005\u0000\u0088\b\u0000\n\u0000\f\u0000\u008b\t\u0000\u0003\u0000\u008d\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003\u0001\u0093\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004\u009f\b\u0004\n\u0004\f\u0004¢\t\u0004\u0001\u0005\u0001\u0005\u0003\u0005¦\b\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0003\u0007¬\b\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\bµ\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t»\b\t\u0001\n\u0001\n\u0005\n¿\b\n\n\n\f\nÂ\t\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0003\u000bÈ\b\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0005\u000eÐ\b\u000e\n\u000e\f\u000eÓ\t\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0003\u000fÙ\b\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0005\u0012â\b\u0012\n\u0012\f\u0012å\t\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0005\u0014ì\b\u0014\n\u0014\f\u0014ï\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015õ\b\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0003\u001aĂ\b\u001a\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0003\u001eČ\b\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0003!Ė\b!\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0001$\u0001$\u0001%\u0001%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001'\u0001'\u0001'\u0001'\u0001'\u0001'\u0001'\u0003'Ĳ\b'\u0001(\u0001(\u0001(\u0001(\u0001(\u0003(Ĺ\b(\u0001)\u0001)\u0001)\u0001*\u0001*\u0001+\u0001+\u0001,\u0001,\u0001-\u0001-\u0001.\u0001.\u0001/\u0001/\u00010\u00010\u00011\u00011\u00012\u00012\u00012\u00052ő\b2\n2\f2Ŕ\t2\u00012\u00032ŗ\b2\u00032ř\b2\u00012\u00052Ŝ\b2\n2\f2ş\t2\u00012\u00012\u00013\u00013\u00053ť\b3\n3\f3Ũ\t3\u00013\u00013\u00053Ŭ\b3\n3\f3ů\t3\u00014\u00054Ų\b4\n4\f4ŵ\t4\u00014\u00014\u00015\u00015\u00035Ż\b5\u00016\u00016\u00036ſ\b6\u00016\u00016\u00017\u00017\u00037ƅ\b7\u00017\u00017\u00018\u00018\u00018\u00058ƌ\b8\n8\f8Ə\t8\u00019\u00019\u00039Ɠ\b9\u00019\u00019\u00019\u0000\u0000:\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnpr\u0000\u0001\u0001\u0000\u001f Ƌ\u0000w\u0001\u0000\u0000\u0000\u0002\u0092\u0001\u0000\u0000\u0000\u0004\u0094\u0001\u0000\u0000\u0000\u0006\u0097\u0001\u0000\u0000\u0000\b\u009b\u0001\u0000\u0000\u0000\n¥\u0001\u0000\u0000\u0000\f§\u0001\u0000\u0000\u0000\u000e«\u0001\u0000\u0000\u0000\u0010´\u0001\u0000\u0000\u0000\u0012º\u0001\u0000\u0000\u0000\u0014¼\u0001\u0000\u0000\u0000\u0016Ç\u0001\u0000\u0000\u0000\u0018É\u0001\u0000\u0000\u0000\u001aË\u0001\u0000\u0000\u0000\u001cÍ\u0001\u0000\u0000\u0000\u001eØ\u0001\u0000\u0000\u0000 Ú\u0001\u0000\u0000\u0000\"Ü\u0001\u0000\u0000\u0000$ã\u0001\u0000\u0000\u0000&æ\u0001\u0000\u0000\u0000(í\u0001\u0000\u0000\u0000*ô\u0001\u0000\u0000\u0000,ö\u0001\u0000\u0000\u0000.ø\u0001\u0000\u0000\u00000ú\u0001\u0000\u0000\u00002ü\u0001\u0000\u0000\u00004ā\u0001\u0000\u0000\u00006ă\u0001\u0000\u0000\u00008ą\u0001\u0000\u0000\u0000:ć\u0001\u0000\u0000\u0000<ċ\u0001\u0000\u0000\u0000>č\u0001\u0000\u0000\u0000@ď\u0001\u0000\u0000\u0000Bĕ\u0001\u0000\u0000\u0000Dė\u0001\u0000\u0000\u0000FĜ\u0001\u0000\u0000\u0000HĠ\u0001\u0000\u0000\u0000JĢ\u0001\u0000\u0000\u0000LĤ\u0001\u0000\u0000\u0000NĪ\u0001\u0000\u0000\u0000Pĸ\u0001\u0000\u0000\u0000Rĺ\u0001\u0000\u0000\u0000TĽ\u0001\u0000\u0000\u0000VĿ\u0001\u0000\u0000\u0000XŁ\u0001\u0000\u0000\u0000ZŃ\u0001\u0000\u0000\u0000\\Ņ\u0001\u0000\u0000\u0000^Ň\u0001\u0000\u0000\u0000`ŉ\u0001\u0000\u0000\u0000bŋ\u0001\u0000\u0000\u0000dō\u0001\u0000\u0000\u0000fŢ\u0001\u0000\u0000\u0000hų\u0001\u0000\u0000\u0000jź\u0001\u0000\u0000\u0000lż\u0001\u0000\u0000\u0000nƂ\u0001\u0000\u0000\u0000pƈ\u0001\u0000\u0000\u0000rƐ\u0001\u0000\u0000\u0000tv\u0005\u0010\u0000\u0000ut\u0001\u0000\u0000\u0000vy\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000x\u008c\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000z\u0083\u0003\u0002\u0001\u0000{}\u0005\u0010\u0000\u0000|{\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~|\u0001\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080\u0082\u0003\u0002\u0001\u0000\u0081|\u0001\u0000\u0000\u0000\u0082\u0085\u0001\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0089\u0001\u0000\u0000\u0000\u0085\u0083\u0001\u0000\u0000\u0000\u0086\u0088\u0005\u0010\u0000\u0000\u0087\u0086\u0001\u0000\u0000\u0000\u0088\u008b\u0001\u0000\u0000\u0000\u0089\u0087\u0001\u0000\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000\u008a\u008d\u0001\u0000\u0000\u0000\u008b\u0089\u0001\u0000\u0000\u0000\u008cz\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u008f\u0005\u0000\u0000\u0001\u008f\u0001\u0001\u0000\u0000\u0000\u0090\u0093\u0003\u0006\u0003\u0000\u0091\u0093\u0003j5\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0092\u0091\u0001\u0000\u0000\u0000\u0093\u0003\u0001\u0000\u0000\u0000\u0094\u0095\u0003\b\u0004\u0000\u0095\u0096\u0005\u0000\u0000\u0001\u0096\u0005\u0001\u0000\u0000\u0000\u0097\u0098\u0003\b\u0004\u0000\u0098\u0099\u0005\u0006\u0000\u0000\u0099\u009a\u0003\u0010\b\u0000\u009a\u0007\u0001\u0000\u0000\u0000\u009b \u0003\n\u0005\u0000\u009c\u009d\u0005\u0005\u0000\u0000\u009d\u009f\u0003\n\u0005\u0000\u009e\u009c\u0001\u0000\u0000\u0000\u009f¢\u0001\u0000\u0000\u0000 \u009e\u0001\u0000\u0000\u0000 ¡\u0001\u0000\u0000\u0000¡\t\u0001\u0000\u0000\u0000¢ \u0001\u0000\u0000\u0000£¦\u0003\u000e\u0007\u0000¤¦\u0003\f\u0006\u0000¥£\u0001\u0000\u0000\u0000¥¤\u0001\u0000\u0000\u0000¦\u000b\u0001\u0000\u0000\u0000§¨\u0005\r\u0000\u0000¨\r\u0001\u0000\u0000\u0000©¬\u0003\u0014\n\u0000ª¬\u0003\"\u0011\u0000«©\u0001\u0000\u0000\u0000«ª\u0001\u0000\u0000\u0000¬\u000f\u0001\u0000\u0000\u0000\u00adµ\u0003\u0012\t\u0000®µ\u0003*\u0015\u0000¯µ\u00034\u001a\u0000°µ\u0003<\u001e\u0000±µ\u0003B!\u0000²µ\u0003d2\u0000³µ\u0003n7\u0000´\u00ad\u0001\u0000\u0000\u0000´®\u0001\u0000\u0000\u0000´¯\u0001\u0000\u0000\u0000´°\u0001\u0000\u0000\u0000´±\u0001\u0000\u0000\u0000´²\u0001\u0000\u0000\u0000´³\u0001\u0000\u0000\u0000µ\u0011\u0001\u0000\u0000\u0000¶»\u0003\u001c\u000e\u0000·»\u0003\u0014\n\u0000¸»\u0003&\u0013\u0000¹»\u0003\"\u0011\u0000º¶\u0001\u0000\u0000\u0000º·\u0001\u0000\u0000\u0000º¸\u0001\u0000\u0000\u0000º¹\u0001\u0000\u0000\u0000»\u0013\u0001\u0000\u0000\u0000¼À\u0005\u0007\u0000\u0000½¿\u0003\u0016\u000b\u0000¾½\u0001\u0000\u0000\u0000¿Â\u0001\u0000\u0000\u0000À¾\u0001\u0000\u0000\u0000ÀÁ\u0001\u0000\u0000\u0000ÁÃ\u0001\u0000\u0000\u0000ÂÀ\u0001\u0000\u0000\u0000ÃÄ\u0005\u0007\u0000\u0000Ä\u0015\u0001\u0000\u0000\u0000ÅÈ\u0003\u0018\f\u0000ÆÈ\u0003\u001a\r\u0000ÇÅ\u0001\u0000\u0000\u0000ÇÆ\u0001\u0000\u0000\u0000È\u0017\u0001\u0000\u0000\u0000ÉÊ\u0005\u0003\u0000\u0000Ê\u0019\u0001\u0000\u0000\u0000ËÌ\u0005\u001e\u0000\u0000Ì\u001b\u0001\u0000\u0000\u0000ÍÑ\u0005\u0001\u0000\u0000ÎÐ\u0003\u001e\u000f\u0000ÏÎ\u0001\u0000\u0000\u0000ÐÓ\u0001\u0000\u0000\u0000ÑÏ\u0001\u0000\u0000\u0000ÑÒ\u0001\u0000\u0000\u0000ÒÔ\u0001\u0000\u0000\u0000ÓÑ\u0001\u0000\u0000\u0000ÔÕ\u0005\u0001\u0000\u0000Õ\u001d\u0001\u0000\u0000\u0000ÖÙ\u0003 \u0010\u0000×Ù\u0003\u001a\r\u0000ØÖ\u0001\u0000\u0000\u0000Ø×\u0001\u0000\u0000\u0000Ù\u001f\u0001\u0000\u0000\u0000ÚÛ\u0005\u0003\u0000\u0000Û!\u0001\u0000\u0000\u0000ÜÝ\u0005\b\u0000\u0000ÝÞ\u0003$\u0012\u0000Þß\u0005\b\u0000\u0000ß#\u0001\u0000\u0000\u0000àâ\u0005\u0003\u0000\u0000áà\u0001\u0000\u0000\u0000âå\u0001\u0000\u0000\u0000ãá\u0001\u0000\u0000\u0000ãä\u0001\u0000\u0000\u0000ä%\u0001\u0000\u0000\u0000åã\u0001\u0000\u0000\u0000æç\u0005\u0002\u0000\u0000çè\u0003(\u0014\u0000èé\u0005\u0002\u0000\u0000é'\u0001\u0000\u0000\u0000êì\u0005\u0003\u0000\u0000ëê\u0001\u0000\u0000\u0000ìï\u0001\u0000\u0000\u0000íë\u0001\u0000\u0000\u0000íî\u0001\u0000\u0000\u0000î)\u0001\u0000\u0000\u0000ïí\u0001\u0000\u0000\u0000ðõ\u0003,\u0016\u0000ñõ\u0003.\u0017\u0000òõ\u00030\u0018\u0000óõ\u00032\u0019\u0000ôð\u0001\u0000\u0000\u0000ôñ\u0001\u0000\u0000\u0000ôò\u0001\u0000\u0000\u0000ôó\u0001\u0000\u0000\u0000õ+\u0001\u0000\u0000\u0000ö÷\u0005\u0012\u0000\u0000÷-\u0001\u0000\u0000\u0000øù\u0005\u0013\u0000\u0000ù/\u0001\u0000\u0000\u0000úû\u0005\u0014\u0000\u0000û1\u0001\u0000\u0000\u0000üý\u0005\u0015\u0000\u0000ý3\u0001\u0000\u0000\u0000þĂ\u00036\u001b\u0000ÿĂ\u00038\u001c\u0000ĀĂ\u0003:\u001d\u0000āþ\u0001\u0000\u0000\u0000āÿ\u0001\u0000\u0000\u0000āĀ\u0001\u0000\u0000\u0000Ă5\u0001\u0000\u0000\u0000ăĄ\u0005\u0016\u0000\u0000Ą7\u0001\u0000\u0000\u0000ąĆ\u0005\u0017\u0000\u0000Ć9\u0001\u0000\u0000\u0000ćĈ\u0005\u0018\u0000\u0000Ĉ;\u0001\u0000\u0000\u0000ĉČ\u0003>\u001f\u0000ĊČ\u0003@ \u0000ċĉ\u0001\u0000\u0000\u0000ċĊ\u0001\u0000\u0000\u0000Č=\u0001\u0000\u0000\u0000čĎ\u0005\u0019\u0000\u0000Ď?\u0001\u0000\u0000\u0000ďĐ\u0005\u001a\u0000\u0000ĐA\u0001\u0000\u0000\u0000đĖ\u0003D\"\u0000ĒĖ\u0003F#\u0000ēĖ\u0003H$\u0000ĔĖ\u0003J%\u0000ĕđ\u0001\u0000\u0000\u0000ĕĒ\u0001\u0000\u0000\u0000ĕē\u0001\u0000\u0000\u0000ĕĔ\u0001\u0000\u0000\u0000ĖC\u0001\u0000\u0000\u0000ėĘ\u0003L&\u0000Ęę\u0005#\u0000\u0000ęĚ\u0003N'\u0000Ěě\u0003P(\u0000ěE\u0001\u0000\u0000\u0000Ĝĝ\u0003L&\u0000ĝĞ\u0005#\u0000\u0000Ğğ\u0003N'\u0000ğG\u0001\u0000\u0000\u0000Ġġ\u0003L&\u0000ġI\u0001\u0000\u0000\u0000Ģģ\u0003N'\u0000ģK\u0001\u0000\u0000\u0000Ĥĥ\u0003X,\u0000ĥĦ\u0005\u001f\u0000\u0000Ħħ\u0003Z-\u0000ħĨ\u0005\u001f\u0000\u0000Ĩĩ\u0003\\.\u0000ĩM\u0001\u0000\u0000\u0000Īī\u0003^/\u0000īĬ\u0005!\u0000\u0000Ĭĭ\u0003`0\u0000ĭĮ\u0005!\u0000\u0000Įı\u0003b1\u0000įİ\u0005\u0005\u0000\u0000İĲ\u0003V+\u0000ıį\u0001\u0000\u0000\u0000ıĲ\u0001\u0000\u0000\u0000ĲO\u0001\u0000\u0000\u0000ĳĹ\u0005\"\u0000\u0000Ĵĵ\u0003R)\u0000ĵĶ\u0005!\u0000\u0000Ķķ\u0003T*\u0000ķĹ\u0001\u0000\u0000\u0000ĸĳ\u0001\u0000\u0000\u0000ĸĴ\u0001\u0000\u0000\u0000ĹQ\u0001\u0000\u0000\u0000ĺĻ\u0007\u0000\u0000\u0000Ļļ\u0003^/\u0000ļS\u0001\u0000\u0000\u0000Ľľ\u0005$\u0000\u0000ľU\u0001\u0000\u0000\u0000Ŀŀ\u0005$\u0000\u0000ŀW\u0001\u0000\u0000\u0000Łł\u0005$\u0000\u0000łY\u0001\u0000\u0000\u0000Ńń\u0005$\u0000\u0000ń[\u0001\u0000\u0000\u0000Ņņ\u0005$\u0000\u0000ņ]\u0001\u0000\u0000\u0000Ňň\u0005$\u0000\u0000ň_\u0001\u0000\u0000\u0000ŉŊ\u0005$\u0000\u0000Ŋa\u0001\u0000\u0000\u0000ŋŌ\u0005$\u0000\u0000Ōc\u0001\u0000\u0000\u0000ōŘ\u0005\u001b\u0000\u0000ŎŒ\u0003f3\u0000ŏő\u0005\u0010\u0000\u0000Őŏ\u0001\u0000\u0000\u0000őŔ\u0001\u0000\u0000\u0000ŒŐ\u0001\u0000\u0000\u0000Œœ\u0001\u0000\u0000\u0000œŖ\u0001\u0000\u0000\u0000ŔŒ\u0001\u0000\u0000\u0000ŕŗ\u0005\u0004\u0000\u0000Ŗŕ\u0001\u0000\u0000\u0000Ŗŗ\u0001\u0000\u0000\u0000ŗř\u0001\u0000\u0000\u0000ŘŎ\u0001\u0000\u0000\u0000Řř\u0001\u0000\u0000\u0000řŝ\u0001\u0000\u0000\u0000ŚŜ\u0005\u0010\u0000\u0000śŚ\u0001\u0000\u0000\u0000Ŝş\u0001\u0000\u0000\u0000ŝś\u0001\u0000\u0000\u0000ŝŞ\u0001\u0000\u0000\u0000ŞŠ\u0001\u0000\u0000\u0000şŝ\u0001\u0000\u0000\u0000Šš\u0005\u001c\u0000\u0000še\u0001\u0000\u0000\u0000Ţŭ\u0003h4\u0000ţť\u0005\u0010\u0000\u0000Ťţ\u0001\u0000\u0000\u0000ťŨ\u0001\u0000\u0000\u0000ŦŤ\u0001\u0000\u0000\u0000Ŧŧ\u0001\u0000\u0000\u0000ŧũ\u0001\u0000\u0000\u0000ŨŦ\u0001\u0000\u0000\u0000ũŪ\u0005\u0004\u0000\u0000ŪŬ\u0003h4\u0000ūŦ\u0001\u0000\u0000\u0000Ŭů\u0001\u0000\u0000\u0000ŭū\u0001\u0000\u0000\u0000ŭŮ\u0001\u0000\u0000\u0000Ůg\u0001\u0000\u0000\u0000ůŭ\u0001\u0000\u0000\u0000ŰŲ\u0005\u0010\u0000\u0000űŰ\u0001\u0000\u0000\u0000Ųŵ\u0001\u0000\u0000\u0000ųű\u0001\u0000\u0000\u0000ųŴ\u0001\u0000\u0000\u0000ŴŶ\u0001\u0000\u0000\u0000ŵų\u0001\u0000\u0000\u0000Ŷŷ\u0003\u0010\b\u0000ŷi\u0001\u0000\u0000\u0000ŸŻ\u0003l6\u0000ŹŻ\u0003r9\u0000źŸ\u0001\u0000\u0000\u0000źŹ\u0001\u0000\u0000\u0000Żk\u0001\u0000\u0000\u0000żž\u0005\t\u0000\u0000Žſ\u0003\b\u0004\u0000žŽ\u0001\u0000\u0000\u0000žſ\u0001\u0000\u0000\u0000ſƀ\u0001\u0000\u0000\u0000ƀƁ\u0005\n\u0000\u0000Ɓm\u0001\u0000\u0000\u0000ƂƄ\u0005\u001d\u0000\u0000ƃƅ\u0003p8\u0000Ƅƃ\u0001\u0000\u0000\u0000Ƅƅ\u0001\u0000\u0000\u0000ƅƆ\u0001\u0000\u0000\u0000ƆƇ\u0005%\u0000\u0000Ƈo\u0001\u0000\u0000\u0000ƈƍ\u0003\u0006\u0003\u0000ƉƊ\u0005\u0004\u0000\u0000Ɗƌ\u0003\u0006\u0003\u0000ƋƉ\u0001\u0000\u0000\u0000ƌƏ\u0001\u0000\u0000\u0000ƍƋ\u0001\u0000\u0000\u0000ƍƎ\u0001\u0000\u0000\u0000Ǝq\u0001\u0000\u0000\u0000Əƍ\u0001\u0000\u0000\u0000Ɛƒ\u0005\u000b\u0000\u0000ƑƓ\u0003\b\u0004\u0000ƒƑ\u0001\u0000\u0000\u0000ƒƓ\u0001\u0000\u0000\u0000ƓƔ\u0001\u0000\u0000\u0000Ɣƕ\u0005\f\u0000\u0000ƕs\u0001\u0000\u0000\u0000#w~\u0083\u0089\u008c\u0092 ¥«´ºÀÇÑØãíôāċĕıĸŒŖŘŝŦŭųźžƄƍƒ";
   public static final ATN _ATN;

   private static String[] makeRuleNames() {
      return new String[]{
         "toml",
         "expression",
         "tomlKey",
         "keyval",
         "key",
         "simpleKey",
         "unquotedKey",
         "quotedKey",
         "val",
         "string",
         "basicString",
         "basicChar",
         "basicUnescaped",
         "escaped",
         "mlBasicString",
         "mlBasicChar",
         "mlBasicUnescaped",
         "literalString",
         "literalBody",
         "mlLiteralString",
         "mlLiteralBody",
         "integer",
         "decInt",
         "hexInt",
         "octInt",
         "binInt",
         "floatValue",
         "regularFloat",
         "regularFloatInf",
         "regularFloatNaN",
         "booleanValue",
         "trueBool",
         "falseBool",
         "dateTime",
         "offsetDateTime",
         "localDateTime",
         "localDate",
         "localTime",
         "date",
         "time",
         "timeOffset",
         "hourOffset",
         "minuteOffset",
         "secondFraction",
         "year",
         "month",
         "day",
         "hour",
         "minute",
         "second",
         "array",
         "arrayValues",
         "arrayValue",
         "table",
         "standardTable",
         "inlineTable",
         "inlineTableValues",
         "arrayTable"
      };
   }

   private static String[] makeLiteralNames() {
      return new String[]{
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         "'['",
         "']'",
         "'[['",
         "']]'",
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         "'true'",
         "'false'",
         null,
         null,
         null,
         null,
         "'-'",
         "'+'",
         "':'",
         null,
         null,
         null,
         null,
         "','"
      };
   }

   private static String[] makeSymbolicNames() {
      return new String[]{
         null,
         "TripleQuotationMark",
         "TripleApostrophe",
         "StringChar",
         "Comma",
         "Dot",
         "Equals",
         "QuotationMark",
         "Apostrophe",
         "TableKeyStart",
         "TableKeyEnd",
         "ArrayTableKeyStart",
         "ArrayTableKeyEnd",
         "UnquotedKey",
         "WS",
         "Comment",
         "NewLine",
         "Error",
         "DecimalInteger",
         "HexInteger",
         "OctalInteger",
         "BinaryInteger",
         "FloatingPoint",
         "FloatingPointInf",
         "FloatingPointNaN",
         "TrueBoolean",
         "FalseBoolean",
         "ArrayStart",
         "ArrayEnd",
         "InlineTableStart",
         "EscapeSequence",
         "Dash",
         "Plus",
         "Colon",
         "Z",
         "TimeDelimiter",
         "DateDigits",
         "InlineTableEnd",
         "InlineTableComma"
      };
   }

   @Deprecated
   @Override
   public String[] getTokenNames() {
      return tokenNames;
   }

   @Override
   public Vocabulary getVocabulary() {
      return VOCABULARY;
   }

   @Override
   public String getGrammarFileName() {
      return "java-escape";
   }

   @Override
   public String[] getRuleNames() {
      return ruleNames;
   }

   @Override
   public String getSerializedATN() {
      return "\u0004\u0001&Ɨ\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002'\u0007'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u00022\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u00027\u00077\u00028\u00078\u00029\u00079\u0001\u0000\u0005\u0000v\b\u0000\n\u0000\f\u0000y\t\u0000\u0001\u0000\u0001\u0000\u0004\u0000}\b\u0000\u000b\u0000\f\u0000~\u0001\u0000\u0005\u0000\u0082\b\u0000\n\u0000\f\u0000\u0085\t\u0000\u0001\u0000\u0005\u0000\u0088\b\u0000\n\u0000\f\u0000\u008b\t\u0000\u0003\u0000\u008d\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003\u0001\u0093\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004\u009f\b\u0004\n\u0004\f\u0004¢\t\u0004\u0001\u0005\u0001\u0005\u0003\u0005¦\b\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0003\u0007¬\b\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\bµ\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t»\b\t\u0001\n\u0001\n\u0005\n¿\b\n\n\n\f\nÂ\t\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0003\u000bÈ\b\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0005\u000eÐ\b\u000e\n\u000e\f\u000eÓ\t\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0003\u000fÙ\b\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0005\u0012â\b\u0012\n\u0012\f\u0012å\t\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0005\u0014ì\b\u0014\n\u0014\f\u0014ï\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015õ\b\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0003\u001aĂ\b\u001a\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0003\u001eČ\b\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0003!Ė\b!\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0001$\u0001$\u0001%\u0001%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001'\u0001'\u0001'\u0001'\u0001'\u0001'\u0001'\u0003'Ĳ\b'\u0001(\u0001(\u0001(\u0001(\u0001(\u0003(Ĺ\b(\u0001)\u0001)\u0001)\u0001*\u0001*\u0001+\u0001+\u0001,\u0001,\u0001-\u0001-\u0001.\u0001.\u0001/\u0001/\u00010\u00010\u00011\u00011\u00012\u00012\u00012\u00052ő\b2\n2\f2Ŕ\t2\u00012\u00032ŗ\b2\u00032ř\b2\u00012\u00052Ŝ\b2\n2\f2ş\t2\u00012\u00012\u00013\u00013\u00053ť\b3\n3\f3Ũ\t3\u00013\u00013\u00053Ŭ\b3\n3\f3ů\t3\u00014\u00054Ų\b4\n4\f4ŵ\t4\u00014\u00014\u00015\u00015\u00035Ż\b5\u00016\u00016\u00036ſ\b6\u00016\u00016\u00017\u00017\u00037ƅ\b7\u00017\u00017\u00018\u00018\u00018\u00058ƌ\b8\n8\f8Ə\t8\u00019\u00019\u00039Ɠ\b9\u00019\u00019\u00019\u0000\u0000:\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnpr\u0000\u0001\u0001\u0000\u001f Ƌ\u0000w\u0001\u0000\u0000\u0000\u0002\u0092\u0001\u0000\u0000\u0000\u0004\u0094\u0001\u0000\u0000\u0000\u0006\u0097\u0001\u0000\u0000\u0000\b\u009b\u0001\u0000\u0000\u0000\n¥\u0001\u0000\u0000\u0000\f§\u0001\u0000\u0000\u0000\u000e«\u0001\u0000\u0000\u0000\u0010´\u0001\u0000\u0000\u0000\u0012º\u0001\u0000\u0000\u0000\u0014¼\u0001\u0000\u0000\u0000\u0016Ç\u0001\u0000\u0000\u0000\u0018É\u0001\u0000\u0000\u0000\u001aË\u0001\u0000\u0000\u0000\u001cÍ\u0001\u0000\u0000\u0000\u001eØ\u0001\u0000\u0000\u0000 Ú\u0001\u0000\u0000\u0000\"Ü\u0001\u0000\u0000\u0000$ã\u0001\u0000\u0000\u0000&æ\u0001\u0000\u0000\u0000(í\u0001\u0000\u0000\u0000*ô\u0001\u0000\u0000\u0000,ö\u0001\u0000\u0000\u0000.ø\u0001\u0000\u0000\u00000ú\u0001\u0000\u0000\u00002ü\u0001\u0000\u0000\u00004ā\u0001\u0000\u0000\u00006ă\u0001\u0000\u0000\u00008ą\u0001\u0000\u0000\u0000:ć\u0001\u0000\u0000\u0000<ċ\u0001\u0000\u0000\u0000>č\u0001\u0000\u0000\u0000@ď\u0001\u0000\u0000\u0000Bĕ\u0001\u0000\u0000\u0000Dė\u0001\u0000\u0000\u0000FĜ\u0001\u0000\u0000\u0000HĠ\u0001\u0000\u0000\u0000JĢ\u0001\u0000\u0000\u0000LĤ\u0001\u0000\u0000\u0000NĪ\u0001\u0000\u0000\u0000Pĸ\u0001\u0000\u0000\u0000Rĺ\u0001\u0000\u0000\u0000TĽ\u0001\u0000\u0000\u0000VĿ\u0001\u0000\u0000\u0000XŁ\u0001\u0000\u0000\u0000ZŃ\u0001\u0000\u0000\u0000\\Ņ\u0001\u0000\u0000\u0000^Ň\u0001\u0000\u0000\u0000`ŉ\u0001\u0000\u0000\u0000bŋ\u0001\u0000\u0000\u0000dō\u0001\u0000\u0000\u0000fŢ\u0001\u0000\u0000\u0000hų\u0001\u0000\u0000\u0000jź\u0001\u0000\u0000\u0000lż\u0001\u0000\u0000\u0000nƂ\u0001\u0000\u0000\u0000pƈ\u0001\u0000\u0000\u0000rƐ\u0001\u0000\u0000\u0000tv\u0005\u0010\u0000\u0000ut\u0001\u0000\u0000\u0000vy\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000x\u008c\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000z\u0083\u0003\u0002\u0001\u0000{}\u0005\u0010\u0000\u0000|{\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~|\u0001\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080\u0082\u0003\u0002\u0001\u0000\u0081|\u0001\u0000\u0000\u0000\u0082\u0085\u0001\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0089\u0001\u0000\u0000\u0000\u0085\u0083\u0001\u0000\u0000\u0000\u0086\u0088\u0005\u0010\u0000\u0000\u0087\u0086\u0001\u0000\u0000\u0000\u0088\u008b\u0001\u0000\u0000\u0000\u0089\u0087\u0001\u0000\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000\u008a\u008d\u0001\u0000\u0000\u0000\u008b\u0089\u0001\u0000\u0000\u0000\u008cz\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u008f\u0005\u0000\u0000\u0001\u008f\u0001\u0001\u0000\u0000\u0000\u0090\u0093\u0003\u0006\u0003\u0000\u0091\u0093\u0003j5\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0092\u0091\u0001\u0000\u0000\u0000\u0093\u0003\u0001\u0000\u0000\u0000\u0094\u0095\u0003\b\u0004\u0000\u0095\u0096\u0005\u0000\u0000\u0001\u0096\u0005\u0001\u0000\u0000\u0000\u0097\u0098\u0003\b\u0004\u0000\u0098\u0099\u0005\u0006\u0000\u0000\u0099\u009a\u0003\u0010\b\u0000\u009a\u0007\u0001\u0000\u0000\u0000\u009b \u0003\n\u0005\u0000\u009c\u009d\u0005\u0005\u0000\u0000\u009d\u009f\u0003\n\u0005\u0000\u009e\u009c\u0001\u0000\u0000\u0000\u009f¢\u0001\u0000\u0000\u0000 \u009e\u0001\u0000\u0000\u0000 ¡\u0001\u0000\u0000\u0000¡\t\u0001\u0000\u0000\u0000¢ \u0001\u0000\u0000\u0000£¦\u0003\u000e\u0007\u0000¤¦\u0003\f\u0006\u0000¥£\u0001\u0000\u0000\u0000¥¤\u0001\u0000\u0000\u0000¦\u000b\u0001\u0000\u0000\u0000§¨\u0005\r\u0000\u0000¨\r\u0001\u0000\u0000\u0000©¬\u0003\u0014\n\u0000ª¬\u0003\"\u0011\u0000«©\u0001\u0000\u0000\u0000«ª\u0001\u0000\u0000\u0000¬\u000f\u0001\u0000\u0000\u0000\u00adµ\u0003\u0012\t\u0000®µ\u0003*\u0015\u0000¯µ\u00034\u001a\u0000°µ\u0003<\u001e\u0000±µ\u0003B!\u0000²µ\u0003d2\u0000³µ\u0003n7\u0000´\u00ad\u0001\u0000\u0000\u0000´®\u0001\u0000\u0000\u0000´¯\u0001\u0000\u0000\u0000´°\u0001\u0000\u0000\u0000´±\u0001\u0000\u0000\u0000´²\u0001\u0000\u0000\u0000´³\u0001\u0000\u0000\u0000µ\u0011\u0001\u0000\u0000\u0000¶»\u0003\u001c\u000e\u0000·»\u0003\u0014\n\u0000¸»\u0003&\u0013\u0000¹»\u0003\"\u0011\u0000º¶\u0001\u0000\u0000\u0000º·\u0001\u0000\u0000\u0000º¸\u0001\u0000\u0000\u0000º¹\u0001\u0000\u0000\u0000»\u0013\u0001\u0000\u0000\u0000¼À\u0005\u0007\u0000\u0000½¿\u0003\u0016\u000b\u0000¾½\u0001\u0000\u0000\u0000¿Â\u0001\u0000\u0000\u0000À¾\u0001\u0000\u0000\u0000ÀÁ\u0001\u0000\u0000\u0000ÁÃ\u0001\u0000\u0000\u0000ÂÀ\u0001\u0000\u0000\u0000ÃÄ\u0005\u0007\u0000\u0000Ä\u0015\u0001\u0000\u0000\u0000ÅÈ\u0003\u0018\f\u0000ÆÈ\u0003\u001a\r\u0000ÇÅ\u0001\u0000\u0000\u0000ÇÆ\u0001\u0000\u0000\u0000È\u0017\u0001\u0000\u0000\u0000ÉÊ\u0005\u0003\u0000\u0000Ê\u0019\u0001\u0000\u0000\u0000ËÌ\u0005\u001e\u0000\u0000Ì\u001b\u0001\u0000\u0000\u0000ÍÑ\u0005\u0001\u0000\u0000ÎÐ\u0003\u001e\u000f\u0000ÏÎ\u0001\u0000\u0000\u0000ÐÓ\u0001\u0000\u0000\u0000ÑÏ\u0001\u0000\u0000\u0000ÑÒ\u0001\u0000\u0000\u0000ÒÔ\u0001\u0000\u0000\u0000ÓÑ\u0001\u0000\u0000\u0000ÔÕ\u0005\u0001\u0000\u0000Õ\u001d\u0001\u0000\u0000\u0000ÖÙ\u0003 \u0010\u0000×Ù\u0003\u001a\r\u0000ØÖ\u0001\u0000\u0000\u0000Ø×\u0001\u0000\u0000\u0000Ù\u001f\u0001\u0000\u0000\u0000ÚÛ\u0005\u0003\u0000\u0000Û!\u0001\u0000\u0000\u0000ÜÝ\u0005\b\u0000\u0000ÝÞ\u0003$\u0012\u0000Þß\u0005\b\u0000\u0000ß#\u0001\u0000\u0000\u0000àâ\u0005\u0003\u0000\u0000áà\u0001\u0000\u0000\u0000âå\u0001\u0000\u0000\u0000ãá\u0001\u0000\u0000\u0000ãä\u0001\u0000\u0000\u0000ä%\u0001\u0000\u0000\u0000åã\u0001\u0000\u0000\u0000æç\u0005\u0002\u0000\u0000çè\u0003(\u0014\u0000èé\u0005\u0002\u0000\u0000é'\u0001\u0000\u0000\u0000êì\u0005\u0003\u0000\u0000ëê\u0001\u0000\u0000\u0000ìï\u0001\u0000\u0000\u0000íë\u0001\u0000\u0000\u0000íî\u0001\u0000\u0000\u0000î)\u0001\u0000\u0000\u0000ïí\u0001\u0000\u0000\u0000ðõ\u0003,\u0016\u0000ñõ\u0003.\u0017\u0000òõ\u00030\u0018\u0000óõ\u00032\u0019\u0000ôð\u0001\u0000\u0000\u0000ôñ\u0001\u0000\u0000\u0000ôò\u0001\u0000\u0000\u0000ôó\u0001\u0000\u0000\u0000õ+\u0001\u0000\u0000\u0000ö÷\u0005\u0012\u0000\u0000÷-\u0001\u0000\u0000\u0000øù\u0005\u0013\u0000\u0000ù/\u0001\u0000\u0000\u0000úû\u0005\u0014\u0000\u0000û1\u0001\u0000\u0000\u0000üý\u0005\u0015\u0000\u0000ý3\u0001\u0000\u0000\u0000þĂ\u00036\u001b\u0000ÿĂ\u00038\u001c\u0000ĀĂ\u0003:\u001d\u0000āþ\u0001\u0000\u0000\u0000āÿ\u0001\u0000\u0000\u0000āĀ\u0001\u0000\u0000\u0000Ă5\u0001\u0000\u0000\u0000ăĄ\u0005\u0016\u0000\u0000Ą7\u0001\u0000\u0000\u0000ąĆ\u0005\u0017\u0000\u0000Ć9\u0001\u0000\u0000\u0000ćĈ\u0005\u0018\u0000\u0000Ĉ;\u0001\u0000\u0000\u0000ĉČ\u0003>\u001f\u0000ĊČ\u0003@ \u0000ċĉ\u0001\u0000\u0000\u0000ċĊ\u0001\u0000\u0000\u0000Č=\u0001\u0000\u0000\u0000čĎ\u0005\u0019\u0000\u0000Ď?\u0001\u0000\u0000\u0000ďĐ\u0005\u001a\u0000\u0000ĐA\u0001\u0000\u0000\u0000đĖ\u0003D\"\u0000ĒĖ\u0003F#\u0000ēĖ\u0003H$\u0000ĔĖ\u0003J%\u0000ĕđ\u0001\u0000\u0000\u0000ĕĒ\u0001\u0000\u0000\u0000ĕē\u0001\u0000\u0000\u0000ĕĔ\u0001\u0000\u0000\u0000ĖC\u0001\u0000\u0000\u0000ėĘ\u0003L&\u0000Ęę\u0005#\u0000\u0000ęĚ\u0003N'\u0000Ěě\u0003P(\u0000ěE\u0001\u0000\u0000\u0000Ĝĝ\u0003L&\u0000ĝĞ\u0005#\u0000\u0000Ğğ\u0003N'\u0000ğG\u0001\u0000\u0000\u0000Ġġ\u0003L&\u0000ġI\u0001\u0000\u0000\u0000Ģģ\u0003N'\u0000ģK\u0001\u0000\u0000\u0000Ĥĥ\u0003X,\u0000ĥĦ\u0005\u001f\u0000\u0000Ħħ\u0003Z-\u0000ħĨ\u0005\u001f\u0000\u0000Ĩĩ\u0003\\.\u0000ĩM\u0001\u0000\u0000\u0000Īī\u0003^/\u0000īĬ\u0005!\u0000\u0000Ĭĭ\u0003`0\u0000ĭĮ\u0005!\u0000\u0000Įı\u0003b1\u0000įİ\u0005\u0005\u0000\u0000İĲ\u0003V+\u0000ıį\u0001\u0000\u0000\u0000ıĲ\u0001\u0000\u0000\u0000ĲO\u0001\u0000\u0000\u0000ĳĹ\u0005\"\u0000\u0000Ĵĵ\u0003R)\u0000ĵĶ\u0005!\u0000\u0000Ķķ\u0003T*\u0000ķĹ\u0001\u0000\u0000\u0000ĸĳ\u0001\u0000\u0000\u0000ĸĴ\u0001\u0000\u0000\u0000ĹQ\u0001\u0000\u0000\u0000ĺĻ\u0007\u0000\u0000\u0000Ļļ\u0003^/\u0000ļS\u0001\u0000\u0000\u0000Ľľ\u0005$\u0000\u0000ľU\u0001\u0000\u0000\u0000Ŀŀ\u0005$\u0000\u0000ŀW\u0001\u0000\u0000\u0000Łł\u0005$\u0000\u0000łY\u0001\u0000\u0000\u0000Ńń\u0005$\u0000\u0000ń[\u0001\u0000\u0000\u0000Ņņ\u0005$\u0000\u0000ņ]\u0001\u0000\u0000\u0000Ňň\u0005$\u0000\u0000ň_\u0001\u0000\u0000\u0000ŉŊ\u0005$\u0000\u0000Ŋa\u0001\u0000\u0000\u0000ŋŌ\u0005$\u0000\u0000Ōc\u0001\u0000\u0000\u0000ōŘ\u0005\u001b\u0000\u0000ŎŒ\u0003f3\u0000ŏő\u0005\u0010\u0000\u0000Őŏ\u0001\u0000\u0000\u0000őŔ\u0001\u0000\u0000\u0000ŒŐ\u0001\u0000\u0000\u0000Œœ\u0001\u0000\u0000\u0000œŖ\u0001\u0000\u0000\u0000ŔŒ\u0001\u0000\u0000\u0000ŕŗ\u0005\u0004\u0000\u0000Ŗŕ\u0001\u0000\u0000\u0000Ŗŗ\u0001\u0000\u0000\u0000ŗř\u0001\u0000\u0000\u0000ŘŎ\u0001\u0000\u0000\u0000Řř\u0001\u0000\u0000\u0000řŝ\u0001\u0000\u0000\u0000ŚŜ\u0005\u0010\u0000\u0000śŚ\u0001\u0000\u0000\u0000Ŝş\u0001\u0000\u0000\u0000ŝś\u0001\u0000\u0000\u0000ŝŞ\u0001\u0000\u0000\u0000ŞŠ\u0001\u0000\u0000\u0000şŝ\u0001\u0000\u0000\u0000Šš\u0005\u001c\u0000\u0000še\u0001\u0000\u0000\u0000Ţŭ\u0003h4\u0000ţť\u0005\u0010\u0000\u0000Ťţ\u0001\u0000\u0000\u0000ťŨ\u0001\u0000\u0000\u0000ŦŤ\u0001\u0000\u0000\u0000Ŧŧ\u0001\u0000\u0000\u0000ŧũ\u0001\u0000\u0000\u0000ŨŦ\u0001\u0000\u0000\u0000ũŪ\u0005\u0004\u0000\u0000ŪŬ\u0003h4\u0000ūŦ\u0001\u0000\u0000\u0000Ŭů\u0001\u0000\u0000\u0000ŭū\u0001\u0000\u0000\u0000ŭŮ\u0001\u0000\u0000\u0000Ůg\u0001\u0000\u0000\u0000ůŭ\u0001\u0000\u0000\u0000ŰŲ\u0005\u0010\u0000\u0000űŰ\u0001\u0000\u0000\u0000Ųŵ\u0001\u0000\u0000\u0000ųű\u0001\u0000\u0000\u0000ųŴ\u0001\u0000\u0000\u0000ŴŶ\u0001\u0000\u0000\u0000ŵų\u0001\u0000\u0000\u0000Ŷŷ\u0003\u0010\b\u0000ŷi\u0001\u0000\u0000\u0000ŸŻ\u0003l6\u0000ŹŻ\u0003r9\u0000źŸ\u0001\u0000\u0000\u0000źŹ\u0001\u0000\u0000\u0000Żk\u0001\u0000\u0000\u0000żž\u0005\t\u0000\u0000Žſ\u0003\b\u0004\u0000žŽ\u0001\u0000\u0000\u0000žſ\u0001\u0000\u0000\u0000ſƀ\u0001\u0000\u0000\u0000ƀƁ\u0005\n\u0000\u0000Ɓm\u0001\u0000\u0000\u0000ƂƄ\u0005\u001d\u0000\u0000ƃƅ\u0003p8\u0000Ƅƃ\u0001\u0000\u0000\u0000Ƅƅ\u0001\u0000\u0000\u0000ƅƆ\u0001\u0000\u0000\u0000ƆƇ\u0005%\u0000\u0000Ƈo\u0001\u0000\u0000\u0000ƈƍ\u0003\u0006\u0003\u0000ƉƊ\u0005\u0004\u0000\u0000Ɗƌ\u0003\u0006\u0003\u0000ƋƉ\u0001\u0000\u0000\u0000ƌƏ\u0001\u0000\u0000\u0000ƍƋ\u0001\u0000\u0000\u0000ƍƎ\u0001\u0000\u0000\u0000Ǝq\u0001\u0000\u0000\u0000Əƍ\u0001\u0000\u0000\u0000Ɛƒ\u0005\u000b\u0000\u0000ƑƓ\u0003\b\u0004\u0000ƒƑ\u0001\u0000\u0000\u0000ƒƓ\u0001\u0000\u0000\u0000ƓƔ\u0001\u0000\u0000\u0000Ɣƕ\u0005\f\u0000\u0000ƕs\u0001\u0000\u0000\u0000#w~\u0083\u0089\u008c\u0092 ¥«´ºÀÇÑØãíôāċĕıĸŒŖŘŝŦŭųźžƄƍƒ";
   }

   @Override
   public ATN getATN() {
      return _ATN;
   }

   public TomlParser(TokenStream input) {
      super(input);
      this._interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
   }

   public final TomlParser.TomlContext toml() throws RecognitionException {
      TomlParser.TomlContext _localctx = new TomlParser.TomlContext(this._ctx, this.getState());
      this.enterRule(_localctx, 0, 0);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(119);
         this._errHandler.sync(this);

         for (int _la = this._input.LA(1); _la == 16; _la = this._input.LA(1)) {
            this.setState(116);
            this.match(16);
            this.setState(121);
            this._errHandler.sync(this);
         }

         this.setState(140);
         this._errHandler.sync(this);
         int var9 = this._input.LA(1);
         if ((var9 & -64) == 0 && (1L << var9 & 11136L) != 0L) {
            this.setState(122);
            this.expression();
            this.setState(131);
            this._errHandler.sync(this);

            for (int _alt = this.getInterpreter().adaptivePredict(this._input, 2, this._ctx);
               _alt != 2 && _alt != 0;
               _alt = this.getInterpreter().adaptivePredict(this._input, 2, this._ctx)
            ) {
               if (_alt == 1) {
                  this.setState(124);
                  this._errHandler.sync(this);
                  var9 = this._input.LA(1);

                  do {
                     this.setState(123);
                     this.match(16);
                     this.setState(126);
                     this._errHandler.sync(this);
                     var9 = this._input.LA(1);
                  } while (var9 == 16);

                  this.setState(128);
                  this.expression();
               }

               this.setState(133);
               this._errHandler.sync(this);
            }

            this.setState(137);
            this._errHandler.sync(this);

            for (int var12 = this._input.LA(1); var12 == 16; var12 = this._input.LA(1)) {
               this.setState(134);
               this.match(16);
               this.setState(139);
               this._errHandler.sync(this);
            }
         }

         this.setState(142);
         this.match(-1);
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.ExpressionContext expression() throws RecognitionException {
      TomlParser.ExpressionContext _localctx = new TomlParser.ExpressionContext(this._ctx, this.getState());
      this.enterRule(_localctx, 2, 1);

      try {
         this.setState(146);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 7:
            case 8:
            case 13:
               this.enterOuterAlt(_localctx, 1);
               this.setState(144);
               this.keyval();
               break;
            case 9:
            case 11:
               this.enterOuterAlt(_localctx, 2);
               this.setState(145);
               this.table();
               break;
            case 10:
            case 12:
            default:
               throw new NoViableAltException(this);
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.TomlKeyContext tomlKey() throws RecognitionException {
      TomlParser.TomlKeyContext _localctx = new TomlParser.TomlKeyContext(this._ctx, this.getState());
      this.enterRule(_localctx, 4, 2);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(148);
         this.key();
         this.setState(149);
         this.match(-1);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.KeyvalContext keyval() throws RecognitionException {
      TomlParser.KeyvalContext _localctx = new TomlParser.KeyvalContext(this._ctx, this.getState());
      this.enterRule(_localctx, 6, 3);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(151);
         this.key();
         this.setState(152);
         this.match(6);
         this.setState(153);
         this.val();
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.KeyContext key() throws RecognitionException {
      TomlParser.KeyContext _localctx = new TomlParser.KeyContext(this._ctx, this.getState());
      this.enterRule(_localctx, 8, 4);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(155);
         this.simpleKey();
         this.setState(160);
         this._errHandler.sync(this);

         for (int _la = this._input.LA(1); _la == 5; _la = this._input.LA(1)) {
            this.setState(156);
            this.match(5);
            this.setState(157);
            this.simpleKey();
            this.setState(162);
            this._errHandler.sync(this);
         }
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.SimpleKeyContext simpleKey() throws RecognitionException {
      TomlParser.SimpleKeyContext _localctx = new TomlParser.SimpleKeyContext(this._ctx, this.getState());
      this.enterRule(_localctx, 10, 5);

      try {
         this.setState(165);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 7:
            case 8:
               this.enterOuterAlt(_localctx, 1);
               this.setState(163);
               this.quotedKey();
               break;
            case 13:
               this.enterOuterAlt(_localctx, 2);
               this.setState(164);
               this.unquotedKey();
               break;
            default:
               throw new NoViableAltException(this);
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.UnquotedKeyContext unquotedKey() throws RecognitionException {
      TomlParser.UnquotedKeyContext _localctx = new TomlParser.UnquotedKeyContext(this._ctx, this.getState());
      this.enterRule(_localctx, 12, 6);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(167);
         this.match(13);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.QuotedKeyContext quotedKey() throws RecognitionException {
      TomlParser.QuotedKeyContext _localctx = new TomlParser.QuotedKeyContext(this._ctx, this.getState());
      this.enterRule(_localctx, 14, 7);

      try {
         this.setState(171);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 7:
               this.enterOuterAlt(_localctx, 1);
               this.setState(169);
               this.basicString();
               break;
            case 8:
               this.enterOuterAlt(_localctx, 2);
               this.setState(170);
               this.literalString();
               break;
            default:
               throw new NoViableAltException(this);
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.ValContext val() throws RecognitionException {
      TomlParser.ValContext _localctx = new TomlParser.ValContext(this._ctx, this.getState());
      this.enterRule(_localctx, 16, 8);

      try {
         this.setState(180);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 1:
            case 2:
            case 7:
            case 8:
               this.enterOuterAlt(_localctx, 1);
               this.setState(173);
               this.string();
               break;
            case 3:
            case 4:
            case 5:
            case 6:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 28:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            default:
               throw new NoViableAltException(this);
            case 18:
            case 19:
            case 20:
            case 21:
               this.enterOuterAlt(_localctx, 2);
               this.setState(174);
               this.integer();
               break;
            case 22:
            case 23:
            case 24:
               this.enterOuterAlt(_localctx, 3);
               this.setState(175);
               this.floatValue();
               break;
            case 25:
            case 26:
               this.enterOuterAlt(_localctx, 4);
               this.setState(176);
               this.booleanValue();
               break;
            case 27:
               this.enterOuterAlt(_localctx, 6);
               this.setState(178);
               this.array();
               break;
            case 29:
               this.enterOuterAlt(_localctx, 7);
               this.setState(179);
               this.inlineTable();
               break;
            case 36:
               this.enterOuterAlt(_localctx, 5);
               this.setState(177);
               this.dateTime();
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.StringContext string() throws RecognitionException {
      TomlParser.StringContext _localctx = new TomlParser.StringContext(this._ctx, this.getState());
      this.enterRule(_localctx, 18, 9);

      try {
         this.setState(186);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 1:
               this.enterOuterAlt(_localctx, 1);
               this.setState(182);
               this.mlBasicString();
               break;
            case 2:
               this.enterOuterAlt(_localctx, 3);
               this.setState(184);
               this.mlLiteralString();
               break;
            case 3:
            case 4:
            case 5:
            case 6:
            default:
               throw new NoViableAltException(this);
            case 7:
               this.enterOuterAlt(_localctx, 2);
               this.setState(183);
               this.basicString();
               break;
            case 8:
               this.enterOuterAlt(_localctx, 4);
               this.setState(185);
               this.literalString();
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.BasicStringContext basicString() throws RecognitionException {
      TomlParser.BasicStringContext _localctx = new TomlParser.BasicStringContext(this._ctx, this.getState());
      this.enterRule(_localctx, 20, 10);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(188);
         this.match(7);
         this.setState(192);
         this._errHandler.sync(this);

         for (int _la = this._input.LA(1); _la == 3 || _la == 30; _la = this._input.LA(1)) {
            this.setState(189);
            this.basicChar();
            this.setState(194);
            this._errHandler.sync(this);
         }

         this.setState(195);
         this.match(7);
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.BasicCharContext basicChar() throws RecognitionException {
      TomlParser.BasicCharContext _localctx = new TomlParser.BasicCharContext(this._ctx, this.getState());
      this.enterRule(_localctx, 22, 11);

      try {
         this.setState(199);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 3:
               this.enterOuterAlt(_localctx, 1);
               this.setState(197);
               this.basicUnescaped();
               break;
            case 30:
               this.enterOuterAlt(_localctx, 2);
               this.setState(198);
               this.escaped();
               break;
            default:
               throw new NoViableAltException(this);
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.BasicUnescapedContext basicUnescaped() throws RecognitionException {
      TomlParser.BasicUnescapedContext _localctx = new TomlParser.BasicUnescapedContext(this._ctx, this.getState());
      this.enterRule(_localctx, 24, 12);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(201);
         this.match(3);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.EscapedContext escaped() throws RecognitionException {
      TomlParser.EscapedContext _localctx = new TomlParser.EscapedContext(this._ctx, this.getState());
      this.enterRule(_localctx, 26, 13);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(203);
         this.match(30);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.MlBasicStringContext mlBasicString() throws RecognitionException {
      TomlParser.MlBasicStringContext _localctx = new TomlParser.MlBasicStringContext(this._ctx, this.getState());
      this.enterRule(_localctx, 28, 14);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(205);
         this.match(1);
         this.setState(209);
         this._errHandler.sync(this);

         for (int _la = this._input.LA(1); _la == 3 || _la == 30; _la = this._input.LA(1)) {
            this.setState(206);
            this.mlBasicChar();
            this.setState(211);
            this._errHandler.sync(this);
         }

         this.setState(212);
         this.match(1);
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.MlBasicCharContext mlBasicChar() throws RecognitionException {
      TomlParser.MlBasicCharContext _localctx = new TomlParser.MlBasicCharContext(this._ctx, this.getState());
      this.enterRule(_localctx, 30, 15);

      try {
         this.setState(216);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 3:
               this.enterOuterAlt(_localctx, 1);
               this.setState(214);
               this.mlBasicUnescaped();
               break;
            case 30:
               this.enterOuterAlt(_localctx, 2);
               this.setState(215);
               this.escaped();
               break;
            default:
               throw new NoViableAltException(this);
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.MlBasicUnescapedContext mlBasicUnescaped() throws RecognitionException {
      TomlParser.MlBasicUnescapedContext _localctx = new TomlParser.MlBasicUnescapedContext(this._ctx, this.getState());
      this.enterRule(_localctx, 32, 16);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(218);
         this.match(3);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.LiteralStringContext literalString() throws RecognitionException {
      TomlParser.LiteralStringContext _localctx = new TomlParser.LiteralStringContext(this._ctx, this.getState());
      this.enterRule(_localctx, 34, 17);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(220);
         this.match(8);
         this.setState(221);
         this.literalBody();
         this.setState(222);
         this.match(8);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.LiteralBodyContext literalBody() throws RecognitionException {
      TomlParser.LiteralBodyContext _localctx = new TomlParser.LiteralBodyContext(this._ctx, this.getState());
      this.enterRule(_localctx, 36, 18);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(227);
         this._errHandler.sync(this);

         for (int _la = this._input.LA(1); _la == 3; _la = this._input.LA(1)) {
            this.setState(224);
            this.match(3);
            this.setState(229);
            this._errHandler.sync(this);
         }
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.MlLiteralStringContext mlLiteralString() throws RecognitionException {
      TomlParser.MlLiteralStringContext _localctx = new TomlParser.MlLiteralStringContext(this._ctx, this.getState());
      this.enterRule(_localctx, 38, 19);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(230);
         this.match(2);
         this.setState(231);
         this.mlLiteralBody();
         this.setState(232);
         this.match(2);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.MlLiteralBodyContext mlLiteralBody() throws RecognitionException {
      TomlParser.MlLiteralBodyContext _localctx = new TomlParser.MlLiteralBodyContext(this._ctx, this.getState());
      this.enterRule(_localctx, 40, 20);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(237);
         this._errHandler.sync(this);

         for (int _la = this._input.LA(1); _la == 3; _la = this._input.LA(1)) {
            this.setState(234);
            this.match(3);
            this.setState(239);
            this._errHandler.sync(this);
         }
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.IntegerContext integer() throws RecognitionException {
      TomlParser.IntegerContext _localctx = new TomlParser.IntegerContext(this._ctx, this.getState());
      this.enterRule(_localctx, 42, 21);

      try {
         this.setState(244);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 18:
               this.enterOuterAlt(_localctx, 1);
               this.setState(240);
               this.decInt();
               break;
            case 19:
               this.enterOuterAlt(_localctx, 2);
               this.setState(241);
               this.hexInt();
               break;
            case 20:
               this.enterOuterAlt(_localctx, 3);
               this.setState(242);
               this.octInt();
               break;
            case 21:
               this.enterOuterAlt(_localctx, 4);
               this.setState(243);
               this.binInt();
               break;
            default:
               throw new NoViableAltException(this);
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.DecIntContext decInt() throws RecognitionException {
      TomlParser.DecIntContext _localctx = new TomlParser.DecIntContext(this._ctx, this.getState());
      this.enterRule(_localctx, 44, 22);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(246);
         this.match(18);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.HexIntContext hexInt() throws RecognitionException {
      TomlParser.HexIntContext _localctx = new TomlParser.HexIntContext(this._ctx, this.getState());
      this.enterRule(_localctx, 46, 23);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(248);
         this.match(19);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.OctIntContext octInt() throws RecognitionException {
      TomlParser.OctIntContext _localctx = new TomlParser.OctIntContext(this._ctx, this.getState());
      this.enterRule(_localctx, 48, 24);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(250);
         this.match(20);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.BinIntContext binInt() throws RecognitionException {
      TomlParser.BinIntContext _localctx = new TomlParser.BinIntContext(this._ctx, this.getState());
      this.enterRule(_localctx, 50, 25);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(252);
         this.match(21);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.FloatValueContext floatValue() throws RecognitionException {
      TomlParser.FloatValueContext _localctx = new TomlParser.FloatValueContext(this._ctx, this.getState());
      this.enterRule(_localctx, 52, 26);

      try {
         this.setState(257);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 22:
               this.enterOuterAlt(_localctx, 1);
               this.setState(254);
               this.regularFloat();
               break;
            case 23:
               this.enterOuterAlt(_localctx, 2);
               this.setState(255);
               this.regularFloatInf();
               break;
            case 24:
               this.enterOuterAlt(_localctx, 3);
               this.setState(256);
               this.regularFloatNaN();
               break;
            default:
               throw new NoViableAltException(this);
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.RegularFloatContext regularFloat() throws RecognitionException {
      TomlParser.RegularFloatContext _localctx = new TomlParser.RegularFloatContext(this._ctx, this.getState());
      this.enterRule(_localctx, 54, 27);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(259);
         this.match(22);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.RegularFloatInfContext regularFloatInf() throws RecognitionException {
      TomlParser.RegularFloatInfContext _localctx = new TomlParser.RegularFloatInfContext(this._ctx, this.getState());
      this.enterRule(_localctx, 56, 28);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(261);
         this.match(23);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.RegularFloatNaNContext regularFloatNaN() throws RecognitionException {
      TomlParser.RegularFloatNaNContext _localctx = new TomlParser.RegularFloatNaNContext(this._ctx, this.getState());
      this.enterRule(_localctx, 58, 29);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(263);
         this.match(24);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.BooleanValueContext booleanValue() throws RecognitionException {
      TomlParser.BooleanValueContext _localctx = new TomlParser.BooleanValueContext(this._ctx, this.getState());
      this.enterRule(_localctx, 60, 30);

      try {
         this.setState(267);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 25:
               this.enterOuterAlt(_localctx, 1);
               this.setState(265);
               this.trueBool();
               break;
            case 26:
               this.enterOuterAlt(_localctx, 2);
               this.setState(266);
               this.falseBool();
               break;
            default:
               throw new NoViableAltException(this);
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.TrueBoolContext trueBool() throws RecognitionException {
      TomlParser.TrueBoolContext _localctx = new TomlParser.TrueBoolContext(this._ctx, this.getState());
      this.enterRule(_localctx, 62, 31);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(269);
         this.match(25);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.FalseBoolContext falseBool() throws RecognitionException {
      TomlParser.FalseBoolContext _localctx = new TomlParser.FalseBoolContext(this._ctx, this.getState());
      this.enterRule(_localctx, 64, 32);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(271);
         this.match(26);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.DateTimeContext dateTime() throws RecognitionException {
      TomlParser.DateTimeContext _localctx = new TomlParser.DateTimeContext(this._ctx, this.getState());
      this.enterRule(_localctx, 66, 33);

      try {
         this.setState(277);
         this._errHandler.sync(this);
         switch (this.getInterpreter().adaptivePredict(this._input, 20, this._ctx)) {
            case 1:
               this.enterOuterAlt(_localctx, 1);
               this.setState(273);
               this.offsetDateTime();
               break;
            case 2:
               this.enterOuterAlt(_localctx, 2);
               this.setState(274);
               this.localDateTime();
               break;
            case 3:
               this.enterOuterAlt(_localctx, 3);
               this.setState(275);
               this.localDate();
               break;
            case 4:
               this.enterOuterAlt(_localctx, 4);
               this.setState(276);
               this.localTime();
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.OffsetDateTimeContext offsetDateTime() throws RecognitionException {
      TomlParser.OffsetDateTimeContext _localctx = new TomlParser.OffsetDateTimeContext(this._ctx, this.getState());
      this.enterRule(_localctx, 68, 34);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(279);
         this.date();
         this.setState(280);
         this.match(35);
         this.setState(281);
         this.time();
         this.setState(282);
         this.timeOffset();
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.LocalDateTimeContext localDateTime() throws RecognitionException {
      TomlParser.LocalDateTimeContext _localctx = new TomlParser.LocalDateTimeContext(this._ctx, this.getState());
      this.enterRule(_localctx, 70, 35);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(284);
         this.date();
         this.setState(285);
         this.match(35);
         this.setState(286);
         this.time();
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.LocalDateContext localDate() throws RecognitionException {
      TomlParser.LocalDateContext _localctx = new TomlParser.LocalDateContext(this._ctx, this.getState());
      this.enterRule(_localctx, 72, 36);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(288);
         this.date();
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.LocalTimeContext localTime() throws RecognitionException {
      TomlParser.LocalTimeContext _localctx = new TomlParser.LocalTimeContext(this._ctx, this.getState());
      this.enterRule(_localctx, 74, 37);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(290);
         this.time();
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.DateContext date() throws RecognitionException {
      TomlParser.DateContext _localctx = new TomlParser.DateContext(this._ctx, this.getState());
      this.enterRule(_localctx, 76, 38);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(292);
         this.year();
         this.setState(293);
         this.match(31);
         this.setState(294);
         this.month();
         this.setState(295);
         this.match(31);
         this.setState(296);
         this.day();
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.TimeContext time() throws RecognitionException {
      TomlParser.TimeContext _localctx = new TomlParser.TimeContext(this._ctx, this.getState());
      this.enterRule(_localctx, 78, 39);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(298);
         this.hour();
         this.setState(299);
         this.match(33);
         this.setState(300);
         this.minute();
         this.setState(301);
         this.match(33);
         this.setState(302);
         this.second();
         this.setState(305);
         this._errHandler.sync(this);
         int _la = this._input.LA(1);
         if (_la == 5) {
            this.setState(303);
            this.match(5);
            this.setState(304);
            this.secondFraction();
         }
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.TimeOffsetContext timeOffset() throws RecognitionException {
      TomlParser.TimeOffsetContext _localctx = new TomlParser.TimeOffsetContext(this._ctx, this.getState());
      this.enterRule(_localctx, 80, 40);

      try {
         this.setState(312);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 31:
            case 32:
               this.enterOuterAlt(_localctx, 2);
               this.setState(308);
               this.hourOffset();
               this.setState(309);
               this.match(33);
               this.setState(310);
               this.minuteOffset();
               break;
            case 33:
            default:
               throw new NoViableAltException(this);
            case 34:
               this.enterOuterAlt(_localctx, 1);
               this.setState(307);
               this.match(34);
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.HourOffsetContext hourOffset() throws RecognitionException {
      TomlParser.HourOffsetContext _localctx = new TomlParser.HourOffsetContext(this._ctx, this.getState());
      this.enterRule(_localctx, 82, 41);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(314);
         int _la = this._input.LA(1);
         if (_la != 31 && _la != 32) {
            this._errHandler.recoverInline(this);
         } else {
            if (this._input.LA(1) == -1) {
               this.matchedEOF = true;
            }

            this._errHandler.reportMatch(this);
            this.consume();
         }

         this.setState(315);
         this.hour();
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.MinuteOffsetContext minuteOffset() throws RecognitionException {
      TomlParser.MinuteOffsetContext _localctx = new TomlParser.MinuteOffsetContext(this._ctx, this.getState());
      this.enterRule(_localctx, 84, 42);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(317);
         this.match(36);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.SecondFractionContext secondFraction() throws RecognitionException {
      TomlParser.SecondFractionContext _localctx = new TomlParser.SecondFractionContext(this._ctx, this.getState());
      this.enterRule(_localctx, 86, 43);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(319);
         this.match(36);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.YearContext year() throws RecognitionException {
      TomlParser.YearContext _localctx = new TomlParser.YearContext(this._ctx, this.getState());
      this.enterRule(_localctx, 88, 44);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(321);
         this.match(36);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.MonthContext month() throws RecognitionException {
      TomlParser.MonthContext _localctx = new TomlParser.MonthContext(this._ctx, this.getState());
      this.enterRule(_localctx, 90, 45);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(323);
         this.match(36);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.DayContext day() throws RecognitionException {
      TomlParser.DayContext _localctx = new TomlParser.DayContext(this._ctx, this.getState());
      this.enterRule(_localctx, 92, 46);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(325);
         this.match(36);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.HourContext hour() throws RecognitionException {
      TomlParser.HourContext _localctx = new TomlParser.HourContext(this._ctx, this.getState());
      this.enterRule(_localctx, 94, 47);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(327);
         this.match(36);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.MinuteContext minute() throws RecognitionException {
      TomlParser.MinuteContext _localctx = new TomlParser.MinuteContext(this._ctx, this.getState());
      this.enterRule(_localctx, 96, 48);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(329);
         this.match(36);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.SecondContext second() throws RecognitionException {
      TomlParser.SecondContext _localctx = new TomlParser.SecondContext(this._ctx, this.getState());
      this.enterRule(_localctx, 98, 49);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(331);
         this.match(36);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.ArrayContext array() throws RecognitionException {
      TomlParser.ArrayContext _localctx = new TomlParser.ArrayContext(this._ctx, this.getState());
      this.enterRule(_localctx, 100, 50);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(333);
         this.match(27);
         this.setState(344);
         this._errHandler.sync(this);
         switch (this.getInterpreter().adaptivePredict(this._input, 25, this._ctx)) {
            case 1:
               this.setState(334);
               this.arrayValues();
               this.setState(338);
               this._errHandler.sync(this);

               for (int _alt = this.getInterpreter().adaptivePredict(this._input, 23, this._ctx);
                  _alt != 2 && _alt != 0;
                  _alt = this.getInterpreter().adaptivePredict(this._input, 23, this._ctx)
               ) {
                  if (_alt == 1) {
                     this.setState(335);
                     this.match(16);
                  }

                  this.setState(340);
                  this._errHandler.sync(this);
               }

               this.setState(342);
               this._errHandler.sync(this);
               int _la = this._input.LA(1);
               if (_la == 4) {
                  this.setState(341);
                  this.match(4);
               }
         }

         this.setState(349);
         this._errHandler.sync(this);

         for (int _la = this._input.LA(1); _la == 16; _la = this._input.LA(1)) {
            this.setState(346);
            this.match(16);
            this.setState(351);
            this._errHandler.sync(this);
         }

         this.setState(352);
         this.match(28);
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.ArrayValuesContext arrayValues() throws RecognitionException {
      TomlParser.ArrayValuesContext _localctx = new TomlParser.ArrayValuesContext(this._ctx, this.getState());
      this.enterRule(_localctx, 102, 51);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(354);
         this.arrayValue();
         this.setState(365);
         this._errHandler.sync(this);

         for (int _alt = this.getInterpreter().adaptivePredict(this._input, 28, this._ctx);
            _alt != 2 && _alt != 0;
            _alt = this.getInterpreter().adaptivePredict(this._input, 28, this._ctx)
         ) {
            if (_alt == 1) {
               this.setState(358);
               this._errHandler.sync(this);

               for (int _la = this._input.LA(1); _la == 16; _la = this._input.LA(1)) {
                  this.setState(355);
                  this.match(16);
                  this.setState(360);
                  this._errHandler.sync(this);
               }

               this.setState(361);
               this.match(4);
               this.setState(362);
               this.arrayValue();
            }

            this.setState(367);
            this._errHandler.sync(this);
         }
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.ArrayValueContext arrayValue() throws RecognitionException {
      TomlParser.ArrayValueContext _localctx = new TomlParser.ArrayValueContext(this._ctx, this.getState());
      this.enterRule(_localctx, 104, 52);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(371);
         this._errHandler.sync(this);

         for (int _la = this._input.LA(1); _la == 16; _la = this._input.LA(1)) {
            this.setState(368);
            this.match(16);
            this.setState(373);
            this._errHandler.sync(this);
         }

         this.setState(374);
         this.val();
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.TableContext table() throws RecognitionException {
      TomlParser.TableContext _localctx = new TomlParser.TableContext(this._ctx, this.getState());
      this.enterRule(_localctx, 106, 53);

      try {
         this.setState(378);
         this._errHandler.sync(this);
         switch (this._input.LA(1)) {
            case 9:
               this.enterOuterAlt(_localctx, 1);
               this.setState(376);
               this.standardTable();
               break;
            case 11:
               this.enterOuterAlt(_localctx, 2);
               this.setState(377);
               this.arrayTable();
               break;
            default:
               throw new NoViableAltException(this);
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.StandardTableContext standardTable() throws RecognitionException {
      TomlParser.StandardTableContext _localctx = new TomlParser.StandardTableContext(this._ctx, this.getState());
      this.enterRule(_localctx, 108, 54);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(380);
         this.match(9);
         this.setState(382);
         this._errHandler.sync(this);
         int _la = this._input.LA(1);
         if ((_la & -64) == 0 && (1L << _la & 8576L) != 0L) {
            this.setState(381);
            this.key();
         }

         this.setState(384);
         this.match(10);
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.InlineTableContext inlineTable() throws RecognitionException {
      TomlParser.InlineTableContext _localctx = new TomlParser.InlineTableContext(this._ctx, this.getState());
      this.enterRule(_localctx, 110, 55);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(386);
         this.match(29);
         this.setState(388);
         this._errHandler.sync(this);
         int _la = this._input.LA(1);
         if ((_la & -64) == 0 && (1L << _la & 8576L) != 0L) {
            this.setState(387);
            this.inlineTableValues();
         }

         this.setState(390);
         this.match(37);
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.InlineTableValuesContext inlineTableValues() throws RecognitionException {
      TomlParser.InlineTableValuesContext _localctx = new TomlParser.InlineTableValuesContext(this._ctx, this.getState());
      this.enterRule(_localctx, 112, 56);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(392);
         this.keyval();
         this.setState(397);
         this._errHandler.sync(this);

         for (int _la = this._input.LA(1); _la == 4; _la = this._input.LA(1)) {
            this.setState(393);
            this.match(4);
            this.setState(394);
            this.keyval();
            this.setState(399);
            this._errHandler.sync(this);
         }
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final TomlParser.ArrayTableContext arrayTable() throws RecognitionException {
      TomlParser.ArrayTableContext _localctx = new TomlParser.ArrayTableContext(this._ctx, this.getState());
      this.enterRule(_localctx, 114, 57);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(400);
         this.match(11);
         this.setState(402);
         this._errHandler.sync(this);
         int _la = this._input.LA(1);
         if ((_la & -64) == 0 && (1L << _la & 8576L) != 0L) {
            this.setState(401);
            this.key();
         }

         this.setState(404);
         this.match(12);
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   static {
      RuntimeMetaData.checkVersion("4.11.1", "4.11.1");

      for (int i = 0; i < tokenNames.length; i++) {
         tokenNames[i] = VOCABULARY.getLiteralName(i);
         if (tokenNames[i] == null) {
            tokenNames[i] = VOCABULARY.getSymbolicName(i);
         }

         if (tokenNames[i] == null) {
            tokenNames[i] = "<INVALID>";
         }
      }

      _ATN = new ATNDeserializer()
         .deserialize(
            "\u0004\u0001&Ɨ\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002'\u0007'\u0002(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u00022\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u00027\u00077\u00028\u00078\u00029\u00079\u0001\u0000\u0005\u0000v\b\u0000\n\u0000\f\u0000y\t\u0000\u0001\u0000\u0001\u0000\u0004\u0000}\b\u0000\u000b\u0000\f\u0000~\u0001\u0000\u0005\u0000\u0082\b\u0000\n\u0000\f\u0000\u0085\t\u0000\u0001\u0000\u0005\u0000\u0088\b\u0000\n\u0000\f\u0000\u008b\t\u0000\u0003\u0000\u008d\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003\u0001\u0093\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004\u009f\b\u0004\n\u0004\f\u0004¢\t\u0004\u0001\u0005\u0001\u0005\u0003\u0005¦\b\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0003\u0007¬\b\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\bµ\b\b\u0001\t\u0001\t\u0001\t\u0001\t\u0003\t»\b\t\u0001\n\u0001\n\u0005\n¿\b\n\n\n\f\nÂ\t\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0003\u000bÈ\b\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0005\u000eÐ\b\u000e\n\u000e\f\u000eÓ\t\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0003\u000fÙ\b\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0005\u0012â\b\u0012\n\u0012\f\u0012å\t\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0005\u0014ì\b\u0014\n\u0014\f\u0014ï\t\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0003\u0015õ\b\u0015\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0003\u001aĂ\b\u001a\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0003\u001eČ\b\u001e\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001!\u0001!\u0001!\u0001!\u0003!Ė\b!\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001#\u0001#\u0001#\u0001#\u0001$\u0001$\u0001%\u0001%\u0001&\u0001&\u0001&\u0001&\u0001&\u0001&\u0001'\u0001'\u0001'\u0001'\u0001'\u0001'\u0001'\u0003'Ĳ\b'\u0001(\u0001(\u0001(\u0001(\u0001(\u0003(Ĺ\b(\u0001)\u0001)\u0001)\u0001*\u0001*\u0001+\u0001+\u0001,\u0001,\u0001-\u0001-\u0001.\u0001.\u0001/\u0001/\u00010\u00010\u00011\u00011\u00012\u00012\u00012\u00052ő\b2\n2\f2Ŕ\t2\u00012\u00032ŗ\b2\u00032ř\b2\u00012\u00052Ŝ\b2\n2\f2ş\t2\u00012\u00012\u00013\u00013\u00053ť\b3\n3\f3Ũ\t3\u00013\u00013\u00053Ŭ\b3\n3\f3ů\t3\u00014\u00054Ų\b4\n4\f4ŵ\t4\u00014\u00014\u00015\u00015\u00035Ż\b5\u00016\u00016\u00036ſ\b6\u00016\u00016\u00017\u00017\u00037ƅ\b7\u00017\u00017\u00018\u00018\u00018\u00058ƌ\b8\n8\f8Ə\t8\u00019\u00019\u00039Ɠ\b9\u00019\u00019\u00019\u0000\u0000:\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPRTVXZ\\^`bdfhjlnpr\u0000\u0001\u0001\u0000\u001f Ƌ\u0000w\u0001\u0000\u0000\u0000\u0002\u0092\u0001\u0000\u0000\u0000\u0004\u0094\u0001\u0000\u0000\u0000\u0006\u0097\u0001\u0000\u0000\u0000\b\u009b\u0001\u0000\u0000\u0000\n¥\u0001\u0000\u0000\u0000\f§\u0001\u0000\u0000\u0000\u000e«\u0001\u0000\u0000\u0000\u0010´\u0001\u0000\u0000\u0000\u0012º\u0001\u0000\u0000\u0000\u0014¼\u0001\u0000\u0000\u0000\u0016Ç\u0001\u0000\u0000\u0000\u0018É\u0001\u0000\u0000\u0000\u001aË\u0001\u0000\u0000\u0000\u001cÍ\u0001\u0000\u0000\u0000\u001eØ\u0001\u0000\u0000\u0000 Ú\u0001\u0000\u0000\u0000\"Ü\u0001\u0000\u0000\u0000$ã\u0001\u0000\u0000\u0000&æ\u0001\u0000\u0000\u0000(í\u0001\u0000\u0000\u0000*ô\u0001\u0000\u0000\u0000,ö\u0001\u0000\u0000\u0000.ø\u0001\u0000\u0000\u00000ú\u0001\u0000\u0000\u00002ü\u0001\u0000\u0000\u00004ā\u0001\u0000\u0000\u00006ă\u0001\u0000\u0000\u00008ą\u0001\u0000\u0000\u0000:ć\u0001\u0000\u0000\u0000<ċ\u0001\u0000\u0000\u0000>č\u0001\u0000\u0000\u0000@ď\u0001\u0000\u0000\u0000Bĕ\u0001\u0000\u0000\u0000Dė\u0001\u0000\u0000\u0000FĜ\u0001\u0000\u0000\u0000HĠ\u0001\u0000\u0000\u0000JĢ\u0001\u0000\u0000\u0000LĤ\u0001\u0000\u0000\u0000NĪ\u0001\u0000\u0000\u0000Pĸ\u0001\u0000\u0000\u0000Rĺ\u0001\u0000\u0000\u0000TĽ\u0001\u0000\u0000\u0000VĿ\u0001\u0000\u0000\u0000XŁ\u0001\u0000\u0000\u0000ZŃ\u0001\u0000\u0000\u0000\\Ņ\u0001\u0000\u0000\u0000^Ň\u0001\u0000\u0000\u0000`ŉ\u0001\u0000\u0000\u0000bŋ\u0001\u0000\u0000\u0000dō\u0001\u0000\u0000\u0000fŢ\u0001\u0000\u0000\u0000hų\u0001\u0000\u0000\u0000jź\u0001\u0000\u0000\u0000lż\u0001\u0000\u0000\u0000nƂ\u0001\u0000\u0000\u0000pƈ\u0001\u0000\u0000\u0000rƐ\u0001\u0000\u0000\u0000tv\u0005\u0010\u0000\u0000ut\u0001\u0000\u0000\u0000vy\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000x\u008c\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000z\u0083\u0003\u0002\u0001\u0000{}\u0005\u0010\u0000\u0000|{\u0001\u0000\u0000\u0000}~\u0001\u0000\u0000\u0000~|\u0001\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080\u0082\u0003\u0002\u0001\u0000\u0081|\u0001\u0000\u0000\u0000\u0082\u0085\u0001\u0000\u0000\u0000\u0083\u0081\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000\u0084\u0089\u0001\u0000\u0000\u0000\u0085\u0083\u0001\u0000\u0000\u0000\u0086\u0088\u0005\u0010\u0000\u0000\u0087\u0086\u0001\u0000\u0000\u0000\u0088\u008b\u0001\u0000\u0000\u0000\u0089\u0087\u0001\u0000\u0000\u0000\u0089\u008a\u0001\u0000\u0000\u0000\u008a\u008d\u0001\u0000\u0000\u0000\u008b\u0089\u0001\u0000\u0000\u0000\u008cz\u0001\u0000\u0000\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000\u0000\u008e\u008f\u0005\u0000\u0000\u0001\u008f\u0001\u0001\u0000\u0000\u0000\u0090\u0093\u0003\u0006\u0003\u0000\u0091\u0093\u0003j5\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0092\u0091\u0001\u0000\u0000\u0000\u0093\u0003\u0001\u0000\u0000\u0000\u0094\u0095\u0003\b\u0004\u0000\u0095\u0096\u0005\u0000\u0000\u0001\u0096\u0005\u0001\u0000\u0000\u0000\u0097\u0098\u0003\b\u0004\u0000\u0098\u0099\u0005\u0006\u0000\u0000\u0099\u009a\u0003\u0010\b\u0000\u009a\u0007\u0001\u0000\u0000\u0000\u009b \u0003\n\u0005\u0000\u009c\u009d\u0005\u0005\u0000\u0000\u009d\u009f\u0003\n\u0005\u0000\u009e\u009c\u0001\u0000\u0000\u0000\u009f¢\u0001\u0000\u0000\u0000 \u009e\u0001\u0000\u0000\u0000 ¡\u0001\u0000\u0000\u0000¡\t\u0001\u0000\u0000\u0000¢ \u0001\u0000\u0000\u0000£¦\u0003\u000e\u0007\u0000¤¦\u0003\f\u0006\u0000¥£\u0001\u0000\u0000\u0000¥¤\u0001\u0000\u0000\u0000¦\u000b\u0001\u0000\u0000\u0000§¨\u0005\r\u0000\u0000¨\r\u0001\u0000\u0000\u0000©¬\u0003\u0014\n\u0000ª¬\u0003\"\u0011\u0000«©\u0001\u0000\u0000\u0000«ª\u0001\u0000\u0000\u0000¬\u000f\u0001\u0000\u0000\u0000\u00adµ\u0003\u0012\t\u0000®µ\u0003*\u0015\u0000¯µ\u00034\u001a\u0000°µ\u0003<\u001e\u0000±µ\u0003B!\u0000²µ\u0003d2\u0000³µ\u0003n7\u0000´\u00ad\u0001\u0000\u0000\u0000´®\u0001\u0000\u0000\u0000´¯\u0001\u0000\u0000\u0000´°\u0001\u0000\u0000\u0000´±\u0001\u0000\u0000\u0000´²\u0001\u0000\u0000\u0000´³\u0001\u0000\u0000\u0000µ\u0011\u0001\u0000\u0000\u0000¶»\u0003\u001c\u000e\u0000·»\u0003\u0014\n\u0000¸»\u0003&\u0013\u0000¹»\u0003\"\u0011\u0000º¶\u0001\u0000\u0000\u0000º·\u0001\u0000\u0000\u0000º¸\u0001\u0000\u0000\u0000º¹\u0001\u0000\u0000\u0000»\u0013\u0001\u0000\u0000\u0000¼À\u0005\u0007\u0000\u0000½¿\u0003\u0016\u000b\u0000¾½\u0001\u0000\u0000\u0000¿Â\u0001\u0000\u0000\u0000À¾\u0001\u0000\u0000\u0000ÀÁ\u0001\u0000\u0000\u0000ÁÃ\u0001\u0000\u0000\u0000ÂÀ\u0001\u0000\u0000\u0000ÃÄ\u0005\u0007\u0000\u0000Ä\u0015\u0001\u0000\u0000\u0000ÅÈ\u0003\u0018\f\u0000ÆÈ\u0003\u001a\r\u0000ÇÅ\u0001\u0000\u0000\u0000ÇÆ\u0001\u0000\u0000\u0000È\u0017\u0001\u0000\u0000\u0000ÉÊ\u0005\u0003\u0000\u0000Ê\u0019\u0001\u0000\u0000\u0000ËÌ\u0005\u001e\u0000\u0000Ì\u001b\u0001\u0000\u0000\u0000ÍÑ\u0005\u0001\u0000\u0000ÎÐ\u0003\u001e\u000f\u0000ÏÎ\u0001\u0000\u0000\u0000ÐÓ\u0001\u0000\u0000\u0000ÑÏ\u0001\u0000\u0000\u0000ÑÒ\u0001\u0000\u0000\u0000ÒÔ\u0001\u0000\u0000\u0000ÓÑ\u0001\u0000\u0000\u0000ÔÕ\u0005\u0001\u0000\u0000Õ\u001d\u0001\u0000\u0000\u0000ÖÙ\u0003 \u0010\u0000×Ù\u0003\u001a\r\u0000ØÖ\u0001\u0000\u0000\u0000Ø×\u0001\u0000\u0000\u0000Ù\u001f\u0001\u0000\u0000\u0000ÚÛ\u0005\u0003\u0000\u0000Û!\u0001\u0000\u0000\u0000ÜÝ\u0005\b\u0000\u0000ÝÞ\u0003$\u0012\u0000Þß\u0005\b\u0000\u0000ß#\u0001\u0000\u0000\u0000àâ\u0005\u0003\u0000\u0000áà\u0001\u0000\u0000\u0000âå\u0001\u0000\u0000\u0000ãá\u0001\u0000\u0000\u0000ãä\u0001\u0000\u0000\u0000ä%\u0001\u0000\u0000\u0000åã\u0001\u0000\u0000\u0000æç\u0005\u0002\u0000\u0000çè\u0003(\u0014\u0000èé\u0005\u0002\u0000\u0000é'\u0001\u0000\u0000\u0000êì\u0005\u0003\u0000\u0000ëê\u0001\u0000\u0000\u0000ìï\u0001\u0000\u0000\u0000íë\u0001\u0000\u0000\u0000íî\u0001\u0000\u0000\u0000î)\u0001\u0000\u0000\u0000ïí\u0001\u0000\u0000\u0000ðõ\u0003,\u0016\u0000ñõ\u0003.\u0017\u0000òõ\u00030\u0018\u0000óõ\u00032\u0019\u0000ôð\u0001\u0000\u0000\u0000ôñ\u0001\u0000\u0000\u0000ôò\u0001\u0000\u0000\u0000ôó\u0001\u0000\u0000\u0000õ+\u0001\u0000\u0000\u0000ö÷\u0005\u0012\u0000\u0000÷-\u0001\u0000\u0000\u0000øù\u0005\u0013\u0000\u0000ù/\u0001\u0000\u0000\u0000úû\u0005\u0014\u0000\u0000û1\u0001\u0000\u0000\u0000üý\u0005\u0015\u0000\u0000ý3\u0001\u0000\u0000\u0000þĂ\u00036\u001b\u0000ÿĂ\u00038\u001c\u0000ĀĂ\u0003:\u001d\u0000āþ\u0001\u0000\u0000\u0000āÿ\u0001\u0000\u0000\u0000āĀ\u0001\u0000\u0000\u0000Ă5\u0001\u0000\u0000\u0000ăĄ\u0005\u0016\u0000\u0000Ą7\u0001\u0000\u0000\u0000ąĆ\u0005\u0017\u0000\u0000Ć9\u0001\u0000\u0000\u0000ćĈ\u0005\u0018\u0000\u0000Ĉ;\u0001\u0000\u0000\u0000ĉČ\u0003>\u001f\u0000ĊČ\u0003@ \u0000ċĉ\u0001\u0000\u0000\u0000ċĊ\u0001\u0000\u0000\u0000Č=\u0001\u0000\u0000\u0000čĎ\u0005\u0019\u0000\u0000Ď?\u0001\u0000\u0000\u0000ďĐ\u0005\u001a\u0000\u0000ĐA\u0001\u0000\u0000\u0000đĖ\u0003D\"\u0000ĒĖ\u0003F#\u0000ēĖ\u0003H$\u0000ĔĖ\u0003J%\u0000ĕđ\u0001\u0000\u0000\u0000ĕĒ\u0001\u0000\u0000\u0000ĕē\u0001\u0000\u0000\u0000ĕĔ\u0001\u0000\u0000\u0000ĖC\u0001\u0000\u0000\u0000ėĘ\u0003L&\u0000Ęę\u0005#\u0000\u0000ęĚ\u0003N'\u0000Ěě\u0003P(\u0000ěE\u0001\u0000\u0000\u0000Ĝĝ\u0003L&\u0000ĝĞ\u0005#\u0000\u0000Ğğ\u0003N'\u0000ğG\u0001\u0000\u0000\u0000Ġġ\u0003L&\u0000ġI\u0001\u0000\u0000\u0000Ģģ\u0003N'\u0000ģK\u0001\u0000\u0000\u0000Ĥĥ\u0003X,\u0000ĥĦ\u0005\u001f\u0000\u0000Ħħ\u0003Z-\u0000ħĨ\u0005\u001f\u0000\u0000Ĩĩ\u0003\\.\u0000ĩM\u0001\u0000\u0000\u0000Īī\u0003^/\u0000īĬ\u0005!\u0000\u0000Ĭĭ\u0003`0\u0000ĭĮ\u0005!\u0000\u0000Įı\u0003b1\u0000įİ\u0005\u0005\u0000\u0000İĲ\u0003V+\u0000ıį\u0001\u0000\u0000\u0000ıĲ\u0001\u0000\u0000\u0000ĲO\u0001\u0000\u0000\u0000ĳĹ\u0005\"\u0000\u0000Ĵĵ\u0003R)\u0000ĵĶ\u0005!\u0000\u0000Ķķ\u0003T*\u0000ķĹ\u0001\u0000\u0000\u0000ĸĳ\u0001\u0000\u0000\u0000ĸĴ\u0001\u0000\u0000\u0000ĹQ\u0001\u0000\u0000\u0000ĺĻ\u0007\u0000\u0000\u0000Ļļ\u0003^/\u0000ļS\u0001\u0000\u0000\u0000Ľľ\u0005$\u0000\u0000ľU\u0001\u0000\u0000\u0000Ŀŀ\u0005$\u0000\u0000ŀW\u0001\u0000\u0000\u0000Łł\u0005$\u0000\u0000łY\u0001\u0000\u0000\u0000Ńń\u0005$\u0000\u0000ń[\u0001\u0000\u0000\u0000Ņņ\u0005$\u0000\u0000ņ]\u0001\u0000\u0000\u0000Ňň\u0005$\u0000\u0000ň_\u0001\u0000\u0000\u0000ŉŊ\u0005$\u0000\u0000Ŋa\u0001\u0000\u0000\u0000ŋŌ\u0005$\u0000\u0000Ōc\u0001\u0000\u0000\u0000ōŘ\u0005\u001b\u0000\u0000ŎŒ\u0003f3\u0000ŏő\u0005\u0010\u0000\u0000Őŏ\u0001\u0000\u0000\u0000őŔ\u0001\u0000\u0000\u0000ŒŐ\u0001\u0000\u0000\u0000Œœ\u0001\u0000\u0000\u0000œŖ\u0001\u0000\u0000\u0000ŔŒ\u0001\u0000\u0000\u0000ŕŗ\u0005\u0004\u0000\u0000Ŗŕ\u0001\u0000\u0000\u0000Ŗŗ\u0001\u0000\u0000\u0000ŗř\u0001\u0000\u0000\u0000ŘŎ\u0001\u0000\u0000\u0000Řř\u0001\u0000\u0000\u0000řŝ\u0001\u0000\u0000\u0000ŚŜ\u0005\u0010\u0000\u0000śŚ\u0001\u0000\u0000\u0000Ŝş\u0001\u0000\u0000\u0000ŝś\u0001\u0000\u0000\u0000ŝŞ\u0001\u0000\u0000\u0000ŞŠ\u0001\u0000\u0000\u0000şŝ\u0001\u0000\u0000\u0000Šš\u0005\u001c\u0000\u0000še\u0001\u0000\u0000\u0000Ţŭ\u0003h4\u0000ţť\u0005\u0010\u0000\u0000Ťţ\u0001\u0000\u0000\u0000ťŨ\u0001\u0000\u0000\u0000ŦŤ\u0001\u0000\u0000\u0000Ŧŧ\u0001\u0000\u0000\u0000ŧũ\u0001\u0000\u0000\u0000ŨŦ\u0001\u0000\u0000\u0000ũŪ\u0005\u0004\u0000\u0000ŪŬ\u0003h4\u0000ūŦ\u0001\u0000\u0000\u0000Ŭů\u0001\u0000\u0000\u0000ŭū\u0001\u0000\u0000\u0000ŭŮ\u0001\u0000\u0000\u0000Ůg\u0001\u0000\u0000\u0000ůŭ\u0001\u0000\u0000\u0000ŰŲ\u0005\u0010\u0000\u0000űŰ\u0001\u0000\u0000\u0000Ųŵ\u0001\u0000\u0000\u0000ųű\u0001\u0000\u0000\u0000ųŴ\u0001\u0000\u0000\u0000ŴŶ\u0001\u0000\u0000\u0000ŵų\u0001\u0000\u0000\u0000Ŷŷ\u0003\u0010\b\u0000ŷi\u0001\u0000\u0000\u0000ŸŻ\u0003l6\u0000ŹŻ\u0003r9\u0000źŸ\u0001\u0000\u0000\u0000źŹ\u0001\u0000\u0000\u0000Żk\u0001\u0000\u0000\u0000żž\u0005\t\u0000\u0000Žſ\u0003\b\u0004\u0000žŽ\u0001\u0000\u0000\u0000žſ\u0001\u0000\u0000\u0000ſƀ\u0001\u0000\u0000\u0000ƀƁ\u0005\n\u0000\u0000Ɓm\u0001\u0000\u0000\u0000ƂƄ\u0005\u001d\u0000\u0000ƃƅ\u0003p8\u0000Ƅƃ\u0001\u0000\u0000\u0000Ƅƅ\u0001\u0000\u0000\u0000ƅƆ\u0001\u0000\u0000\u0000ƆƇ\u0005%\u0000\u0000Ƈo\u0001\u0000\u0000\u0000ƈƍ\u0003\u0006\u0003\u0000ƉƊ\u0005\u0004\u0000\u0000Ɗƌ\u0003\u0006\u0003\u0000ƋƉ\u0001\u0000\u0000\u0000ƌƏ\u0001\u0000\u0000\u0000ƍƋ\u0001\u0000\u0000\u0000ƍƎ\u0001\u0000\u0000\u0000Ǝq\u0001\u0000\u0000\u0000Əƍ\u0001\u0000\u0000\u0000Ɛƒ\u0005\u000b\u0000\u0000ƑƓ\u0003\b\u0004\u0000ƒƑ\u0001\u0000\u0000\u0000ƒƓ\u0001\u0000\u0000\u0000ƓƔ\u0001\u0000\u0000\u0000Ɣƕ\u0005\f\u0000\u0000ƕs\u0001\u0000\u0000\u0000#w~\u0083\u0089\u008c\u0092 ¥«´ºÀÇÑØãíôāċĕıĸŒŖŘŝŦŭųźžƄƍƒ"
               .toCharArray()
         );
      _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];

      for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
         _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
      }
   }

   public static class ArrayContext extends ParserRuleContext {
      public TerminalNode ArrayStart() {
         return this.getToken(27, 0);
      }

      public TerminalNode ArrayEnd() {
         return this.getToken(28, 0);
      }

      public TomlParser.ArrayValuesContext arrayValues() {
         return this.getRuleContext(TomlParser.ArrayValuesContext.class, 0);
      }

      public List<TerminalNode> NewLine() {
         return this.getTokens(16);
      }

      public TerminalNode NewLine(int i) {
         return this.getToken(16, i);
      }

      public TerminalNode Comma() {
         return this.getToken(4, 0);
      }

      public ArrayContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 50;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterArray(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitArray(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitArray(this) : visitor.visitChildren(this));
      }
   }

   public static class ArrayTableContext extends ParserRuleContext {
      public TerminalNode ArrayTableKeyStart() {
         return this.getToken(11, 0);
      }

      public TerminalNode ArrayTableKeyEnd() {
         return this.getToken(12, 0);
      }

      public TomlParser.KeyContext key() {
         return this.getRuleContext(TomlParser.KeyContext.class, 0);
      }

      public ArrayTableContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 57;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterArrayTable(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitArrayTable(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitArrayTable(this) : visitor.visitChildren(this));
      }
   }

   public static class ArrayValueContext extends ParserRuleContext {
      public TomlParser.ValContext val() {
         return this.getRuleContext(TomlParser.ValContext.class, 0);
      }

      public List<TerminalNode> NewLine() {
         return this.getTokens(16);
      }

      public TerminalNode NewLine(int i) {
         return this.getToken(16, i);
      }

      public ArrayValueContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 52;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterArrayValue(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitArrayValue(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitArrayValue(this) : visitor.visitChildren(this));
      }
   }

   public static class ArrayValuesContext extends ParserRuleContext {
      public List<TomlParser.ArrayValueContext> arrayValue() {
         return this.getRuleContexts(TomlParser.ArrayValueContext.class);
      }

      public TomlParser.ArrayValueContext arrayValue(int i) {
         return this.getRuleContext(TomlParser.ArrayValueContext.class, i);
      }

      public List<TerminalNode> Comma() {
         return this.getTokens(4);
      }

      public TerminalNode Comma(int i) {
         return this.getToken(4, i);
      }

      public List<TerminalNode> NewLine() {
         return this.getTokens(16);
      }

      public TerminalNode NewLine(int i) {
         return this.getToken(16, i);
      }

      public ArrayValuesContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 51;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterArrayValues(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitArrayValues(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitArrayValues(this) : visitor.visitChildren(this));
      }
   }

   public static class BasicCharContext extends ParserRuleContext {
      public TomlParser.BasicUnescapedContext basicUnescaped() {
         return this.getRuleContext(TomlParser.BasicUnescapedContext.class, 0);
      }

      public TomlParser.EscapedContext escaped() {
         return this.getRuleContext(TomlParser.EscapedContext.class, 0);
      }

      public BasicCharContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 11;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterBasicChar(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitBasicChar(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitBasicChar(this) : visitor.visitChildren(this));
      }
   }

   public static class BasicStringContext extends ParserRuleContext {
      public List<TerminalNode> QuotationMark() {
         return this.getTokens(7);
      }

      public TerminalNode QuotationMark(int i) {
         return this.getToken(7, i);
      }

      public List<TomlParser.BasicCharContext> basicChar() {
         return this.getRuleContexts(TomlParser.BasicCharContext.class);
      }

      public TomlParser.BasicCharContext basicChar(int i) {
         return this.getRuleContext(TomlParser.BasicCharContext.class, i);
      }

      public BasicStringContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 10;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterBasicString(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitBasicString(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitBasicString(this) : visitor.visitChildren(this));
      }
   }

   public static class BasicUnescapedContext extends ParserRuleContext {
      public TerminalNode StringChar() {
         return this.getToken(3, 0);
      }

      public BasicUnescapedContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 12;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterBasicUnescaped(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitBasicUnescaped(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitBasicUnescaped(this) : visitor.visitChildren(this));
      }
   }

   public static class BinIntContext extends ParserRuleContext {
      public TerminalNode BinaryInteger() {
         return this.getToken(21, 0);
      }

      public BinIntContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 25;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterBinInt(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitBinInt(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitBinInt(this) : visitor.visitChildren(this));
      }
   }

   public static class BooleanValueContext extends ParserRuleContext {
      public TomlParser.TrueBoolContext trueBool() {
         return this.getRuleContext(TomlParser.TrueBoolContext.class, 0);
      }

      public TomlParser.FalseBoolContext falseBool() {
         return this.getRuleContext(TomlParser.FalseBoolContext.class, 0);
      }

      public BooleanValueContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 30;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterBooleanValue(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitBooleanValue(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitBooleanValue(this) : visitor.visitChildren(this));
      }
   }

   public static class DateContext extends ParserRuleContext {
      public TomlParser.YearContext year() {
         return this.getRuleContext(TomlParser.YearContext.class, 0);
      }

      public List<TerminalNode> Dash() {
         return this.getTokens(31);
      }

      public TerminalNode Dash(int i) {
         return this.getToken(31, i);
      }

      public TomlParser.MonthContext month() {
         return this.getRuleContext(TomlParser.MonthContext.class, 0);
      }

      public TomlParser.DayContext day() {
         return this.getRuleContext(TomlParser.DayContext.class, 0);
      }

      public DateContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 38;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterDate(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitDate(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitDate(this) : visitor.visitChildren(this));
      }
   }

   public static class DateTimeContext extends ParserRuleContext {
      public TomlParser.OffsetDateTimeContext offsetDateTime() {
         return this.getRuleContext(TomlParser.OffsetDateTimeContext.class, 0);
      }

      public TomlParser.LocalDateTimeContext localDateTime() {
         return this.getRuleContext(TomlParser.LocalDateTimeContext.class, 0);
      }

      public TomlParser.LocalDateContext localDate() {
         return this.getRuleContext(TomlParser.LocalDateContext.class, 0);
      }

      public TomlParser.LocalTimeContext localTime() {
         return this.getRuleContext(TomlParser.LocalTimeContext.class, 0);
      }

      public DateTimeContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 33;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterDateTime(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitDateTime(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitDateTime(this) : visitor.visitChildren(this));
      }
   }

   public static class DayContext extends ParserRuleContext {
      public TerminalNode DateDigits() {
         return this.getToken(36, 0);
      }

      public DayContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 46;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterDay(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitDay(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitDay(this) : visitor.visitChildren(this));
      }
   }

   public static class DecIntContext extends ParserRuleContext {
      public TerminalNode DecimalInteger() {
         return this.getToken(18, 0);
      }

      public DecIntContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 22;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterDecInt(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitDecInt(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitDecInt(this) : visitor.visitChildren(this));
      }
   }

   public static class EscapedContext extends ParserRuleContext {
      public TerminalNode EscapeSequence() {
         return this.getToken(30, 0);
      }

      public EscapedContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 13;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterEscaped(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitEscaped(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitEscaped(this) : visitor.visitChildren(this));
      }
   }

   public static class ExpressionContext extends ParserRuleContext {
      public TomlParser.KeyvalContext keyval() {
         return this.getRuleContext(TomlParser.KeyvalContext.class, 0);
      }

      public TomlParser.TableContext table() {
         return this.getRuleContext(TomlParser.TableContext.class, 0);
      }

      public ExpressionContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 1;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterExpression(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitExpression(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitExpression(this) : visitor.visitChildren(this));
      }
   }

   public static class FalseBoolContext extends ParserRuleContext {
      public TerminalNode FalseBoolean() {
         return this.getToken(26, 0);
      }

      public FalseBoolContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 32;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterFalseBool(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitFalseBool(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitFalseBool(this) : visitor.visitChildren(this));
      }
   }

   public static class FloatValueContext extends ParserRuleContext {
      public TomlParser.RegularFloatContext regularFloat() {
         return this.getRuleContext(TomlParser.RegularFloatContext.class, 0);
      }

      public TomlParser.RegularFloatInfContext regularFloatInf() {
         return this.getRuleContext(TomlParser.RegularFloatInfContext.class, 0);
      }

      public TomlParser.RegularFloatNaNContext regularFloatNaN() {
         return this.getRuleContext(TomlParser.RegularFloatNaNContext.class, 0);
      }

      public FloatValueContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 26;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterFloatValue(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitFloatValue(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitFloatValue(this) : visitor.visitChildren(this));
      }
   }

   public static class HexIntContext extends ParserRuleContext {
      public TerminalNode HexInteger() {
         return this.getToken(19, 0);
      }

      public HexIntContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 23;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterHexInt(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitHexInt(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitHexInt(this) : visitor.visitChildren(this));
      }
   }

   public static class HourContext extends ParserRuleContext {
      public TerminalNode DateDigits() {
         return this.getToken(36, 0);
      }

      public HourContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 47;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterHour(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitHour(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitHour(this) : visitor.visitChildren(this));
      }
   }

   public static class HourOffsetContext extends ParserRuleContext {
      public TomlParser.HourContext hour() {
         return this.getRuleContext(TomlParser.HourContext.class, 0);
      }

      public TerminalNode Dash() {
         return this.getToken(31, 0);
      }

      public TerminalNode Plus() {
         return this.getToken(32, 0);
      }

      public HourOffsetContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 41;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterHourOffset(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitHourOffset(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitHourOffset(this) : visitor.visitChildren(this));
      }
   }

   public static class InlineTableContext extends ParserRuleContext {
      public TerminalNode InlineTableStart() {
         return this.getToken(29, 0);
      }

      public TerminalNode InlineTableEnd() {
         return this.getToken(37, 0);
      }

      public TomlParser.InlineTableValuesContext inlineTableValues() {
         return this.getRuleContext(TomlParser.InlineTableValuesContext.class, 0);
      }

      public InlineTableContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 55;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterInlineTable(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitInlineTable(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitInlineTable(this) : visitor.visitChildren(this));
      }
   }

   public static class InlineTableValuesContext extends ParserRuleContext {
      public List<TomlParser.KeyvalContext> keyval() {
         return this.getRuleContexts(TomlParser.KeyvalContext.class);
      }

      public TomlParser.KeyvalContext keyval(int i) {
         return this.getRuleContext(TomlParser.KeyvalContext.class, i);
      }

      public List<TerminalNode> Comma() {
         return this.getTokens(4);
      }

      public TerminalNode Comma(int i) {
         return this.getToken(4, i);
      }

      public InlineTableValuesContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 56;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterInlineTableValues(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitInlineTableValues(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitInlineTableValues(this) : visitor.visitChildren(this));
      }
   }

   public static class IntegerContext extends ParserRuleContext {
      public TomlParser.DecIntContext decInt() {
         return this.getRuleContext(TomlParser.DecIntContext.class, 0);
      }

      public TomlParser.HexIntContext hexInt() {
         return this.getRuleContext(TomlParser.HexIntContext.class, 0);
      }

      public TomlParser.OctIntContext octInt() {
         return this.getRuleContext(TomlParser.OctIntContext.class, 0);
      }

      public TomlParser.BinIntContext binInt() {
         return this.getRuleContext(TomlParser.BinIntContext.class, 0);
      }

      public IntegerContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 21;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterInteger(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitInteger(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitInteger(this) : visitor.visitChildren(this));
      }
   }

   public static class KeyContext extends ParserRuleContext {
      public List<TomlParser.SimpleKeyContext> simpleKey() {
         return this.getRuleContexts(TomlParser.SimpleKeyContext.class);
      }

      public TomlParser.SimpleKeyContext simpleKey(int i) {
         return this.getRuleContext(TomlParser.SimpleKeyContext.class, i);
      }

      public List<TerminalNode> Dot() {
         return this.getTokens(5);
      }

      public TerminalNode Dot(int i) {
         return this.getToken(5, i);
      }

      public KeyContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 4;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterKey(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitKey(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitKey(this) : visitor.visitChildren(this));
      }
   }

   public static class KeyvalContext extends ParserRuleContext {
      public TomlParser.KeyContext key() {
         return this.getRuleContext(TomlParser.KeyContext.class, 0);
      }

      public TerminalNode Equals() {
         return this.getToken(6, 0);
      }

      public TomlParser.ValContext val() {
         return this.getRuleContext(TomlParser.ValContext.class, 0);
      }

      public KeyvalContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 3;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterKeyval(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitKeyval(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitKeyval(this) : visitor.visitChildren(this));
      }
   }

   public static class LiteralBodyContext extends ParserRuleContext {
      public List<TerminalNode> StringChar() {
         return this.getTokens(3);
      }

      public TerminalNode StringChar(int i) {
         return this.getToken(3, i);
      }

      public LiteralBodyContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 18;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterLiteralBody(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitLiteralBody(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitLiteralBody(this) : visitor.visitChildren(this));
      }
   }

   public static class LiteralStringContext extends ParserRuleContext {
      public List<TerminalNode> Apostrophe() {
         return this.getTokens(8);
      }

      public TerminalNode Apostrophe(int i) {
         return this.getToken(8, i);
      }

      public TomlParser.LiteralBodyContext literalBody() {
         return this.getRuleContext(TomlParser.LiteralBodyContext.class, 0);
      }

      public LiteralStringContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 17;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterLiteralString(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitLiteralString(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitLiteralString(this) : visitor.visitChildren(this));
      }
   }

   public static class LocalDateContext extends ParserRuleContext {
      public TomlParser.DateContext date() {
         return this.getRuleContext(TomlParser.DateContext.class, 0);
      }

      public LocalDateContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 36;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterLocalDate(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitLocalDate(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitLocalDate(this) : visitor.visitChildren(this));
      }
   }

   public static class LocalDateTimeContext extends ParserRuleContext {
      public TomlParser.DateContext date() {
         return this.getRuleContext(TomlParser.DateContext.class, 0);
      }

      public TerminalNode TimeDelimiter() {
         return this.getToken(35, 0);
      }

      public TomlParser.TimeContext time() {
         return this.getRuleContext(TomlParser.TimeContext.class, 0);
      }

      public LocalDateTimeContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 35;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterLocalDateTime(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitLocalDateTime(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitLocalDateTime(this) : visitor.visitChildren(this));
      }
   }

   public static class LocalTimeContext extends ParserRuleContext {
      public TomlParser.TimeContext time() {
         return this.getRuleContext(TomlParser.TimeContext.class, 0);
      }

      public LocalTimeContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 37;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterLocalTime(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitLocalTime(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitLocalTime(this) : visitor.visitChildren(this));
      }
   }

   public static class MinuteContext extends ParserRuleContext {
      public TerminalNode DateDigits() {
         return this.getToken(36, 0);
      }

      public MinuteContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 48;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterMinute(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitMinute(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitMinute(this) : visitor.visitChildren(this));
      }
   }

   public static class MinuteOffsetContext extends ParserRuleContext {
      public TerminalNode DateDigits() {
         return this.getToken(36, 0);
      }

      public MinuteOffsetContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 42;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterMinuteOffset(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitMinuteOffset(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitMinuteOffset(this) : visitor.visitChildren(this));
      }
   }

   public static class MlBasicCharContext extends ParserRuleContext {
      public TomlParser.MlBasicUnescapedContext mlBasicUnescaped() {
         return this.getRuleContext(TomlParser.MlBasicUnescapedContext.class, 0);
      }

      public TomlParser.EscapedContext escaped() {
         return this.getRuleContext(TomlParser.EscapedContext.class, 0);
      }

      public MlBasicCharContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 15;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterMlBasicChar(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitMlBasicChar(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitMlBasicChar(this) : visitor.visitChildren(this));
      }
   }

   public static class MlBasicStringContext extends ParserRuleContext {
      public List<TerminalNode> TripleQuotationMark() {
         return this.getTokens(1);
      }

      public TerminalNode TripleQuotationMark(int i) {
         return this.getToken(1, i);
      }

      public List<TomlParser.MlBasicCharContext> mlBasicChar() {
         return this.getRuleContexts(TomlParser.MlBasicCharContext.class);
      }

      public TomlParser.MlBasicCharContext mlBasicChar(int i) {
         return this.getRuleContext(TomlParser.MlBasicCharContext.class, i);
      }

      public MlBasicStringContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 14;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterMlBasicString(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitMlBasicString(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitMlBasicString(this) : visitor.visitChildren(this));
      }
   }

   public static class MlBasicUnescapedContext extends ParserRuleContext {
      public TerminalNode StringChar() {
         return this.getToken(3, 0);
      }

      public MlBasicUnescapedContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 16;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterMlBasicUnescaped(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitMlBasicUnescaped(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitMlBasicUnescaped(this) : visitor.visitChildren(this));
      }
   }

   public static class MlLiteralBodyContext extends ParserRuleContext {
      public List<TerminalNode> StringChar() {
         return this.getTokens(3);
      }

      public TerminalNode StringChar(int i) {
         return this.getToken(3, i);
      }

      public MlLiteralBodyContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 20;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterMlLiteralBody(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitMlLiteralBody(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitMlLiteralBody(this) : visitor.visitChildren(this));
      }
   }

   public static class MlLiteralStringContext extends ParserRuleContext {
      public List<TerminalNode> TripleApostrophe() {
         return this.getTokens(2);
      }

      public TerminalNode TripleApostrophe(int i) {
         return this.getToken(2, i);
      }

      public TomlParser.MlLiteralBodyContext mlLiteralBody() {
         return this.getRuleContext(TomlParser.MlLiteralBodyContext.class, 0);
      }

      public MlLiteralStringContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 19;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterMlLiteralString(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitMlLiteralString(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitMlLiteralString(this) : visitor.visitChildren(this));
      }
   }

   public static class MonthContext extends ParserRuleContext {
      public TerminalNode DateDigits() {
         return this.getToken(36, 0);
      }

      public MonthContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 45;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterMonth(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitMonth(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitMonth(this) : visitor.visitChildren(this));
      }
   }

   public static class OctIntContext extends ParserRuleContext {
      public TerminalNode OctalInteger() {
         return this.getToken(20, 0);
      }

      public OctIntContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 24;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterOctInt(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitOctInt(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitOctInt(this) : visitor.visitChildren(this));
      }
   }

   public static class OffsetDateTimeContext extends ParserRuleContext {
      public TomlParser.DateContext date() {
         return this.getRuleContext(TomlParser.DateContext.class, 0);
      }

      public TerminalNode TimeDelimiter() {
         return this.getToken(35, 0);
      }

      public TomlParser.TimeContext time() {
         return this.getRuleContext(TomlParser.TimeContext.class, 0);
      }

      public TomlParser.TimeOffsetContext timeOffset() {
         return this.getRuleContext(TomlParser.TimeOffsetContext.class, 0);
      }

      public OffsetDateTimeContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 34;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterOffsetDateTime(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitOffsetDateTime(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitOffsetDateTime(this) : visitor.visitChildren(this));
      }
   }

   public static class QuotedKeyContext extends ParserRuleContext {
      public TomlParser.BasicStringContext basicString() {
         return this.getRuleContext(TomlParser.BasicStringContext.class, 0);
      }

      public TomlParser.LiteralStringContext literalString() {
         return this.getRuleContext(TomlParser.LiteralStringContext.class, 0);
      }

      public QuotedKeyContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 7;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterQuotedKey(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitQuotedKey(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitQuotedKey(this) : visitor.visitChildren(this));
      }
   }

   public static class RegularFloatContext extends ParserRuleContext {
      public TerminalNode FloatingPoint() {
         return this.getToken(22, 0);
      }

      public RegularFloatContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 27;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterRegularFloat(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitRegularFloat(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitRegularFloat(this) : visitor.visitChildren(this));
      }
   }

   public static class RegularFloatInfContext extends ParserRuleContext {
      public TerminalNode FloatingPointInf() {
         return this.getToken(23, 0);
      }

      public RegularFloatInfContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 28;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterRegularFloatInf(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitRegularFloatInf(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitRegularFloatInf(this) : visitor.visitChildren(this));
      }
   }

   public static class RegularFloatNaNContext extends ParserRuleContext {
      public TerminalNode FloatingPointNaN() {
         return this.getToken(24, 0);
      }

      public RegularFloatNaNContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 29;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterRegularFloatNaN(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitRegularFloatNaN(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitRegularFloatNaN(this) : visitor.visitChildren(this));
      }
   }

   public static class SecondContext extends ParserRuleContext {
      public TerminalNode DateDigits() {
         return this.getToken(36, 0);
      }

      public SecondContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 49;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterSecond(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitSecond(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitSecond(this) : visitor.visitChildren(this));
      }
   }

   public static class SecondFractionContext extends ParserRuleContext {
      public TerminalNode DateDigits() {
         return this.getToken(36, 0);
      }

      public SecondFractionContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 43;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterSecondFraction(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitSecondFraction(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitSecondFraction(this) : visitor.visitChildren(this));
      }
   }

   public static class SimpleKeyContext extends ParserRuleContext {
      public TomlParser.QuotedKeyContext quotedKey() {
         return this.getRuleContext(TomlParser.QuotedKeyContext.class, 0);
      }

      public TomlParser.UnquotedKeyContext unquotedKey() {
         return this.getRuleContext(TomlParser.UnquotedKeyContext.class, 0);
      }

      public SimpleKeyContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 5;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterSimpleKey(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitSimpleKey(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitSimpleKey(this) : visitor.visitChildren(this));
      }
   }

   public static class StandardTableContext extends ParserRuleContext {
      public TerminalNode TableKeyStart() {
         return this.getToken(9, 0);
      }

      public TerminalNode TableKeyEnd() {
         return this.getToken(10, 0);
      }

      public TomlParser.KeyContext key() {
         return this.getRuleContext(TomlParser.KeyContext.class, 0);
      }

      public StandardTableContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 54;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterStandardTable(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitStandardTable(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitStandardTable(this) : visitor.visitChildren(this));
      }
   }

   public static class StringContext extends ParserRuleContext {
      public TomlParser.MlBasicStringContext mlBasicString() {
         return this.getRuleContext(TomlParser.MlBasicStringContext.class, 0);
      }

      public TomlParser.BasicStringContext basicString() {
         return this.getRuleContext(TomlParser.BasicStringContext.class, 0);
      }

      public TomlParser.MlLiteralStringContext mlLiteralString() {
         return this.getRuleContext(TomlParser.MlLiteralStringContext.class, 0);
      }

      public TomlParser.LiteralStringContext literalString() {
         return this.getRuleContext(TomlParser.LiteralStringContext.class, 0);
      }

      public StringContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 9;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterString(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitString(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitString(this) : visitor.visitChildren(this));
      }
   }

   public static class TableContext extends ParserRuleContext {
      public TomlParser.StandardTableContext standardTable() {
         return this.getRuleContext(TomlParser.StandardTableContext.class, 0);
      }

      public TomlParser.ArrayTableContext arrayTable() {
         return this.getRuleContext(TomlParser.ArrayTableContext.class, 0);
      }

      public TableContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 53;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterTable(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitTable(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitTable(this) : visitor.visitChildren(this));
      }
   }

   public static class TimeContext extends ParserRuleContext {
      public TomlParser.HourContext hour() {
         return this.getRuleContext(TomlParser.HourContext.class, 0);
      }

      public List<TerminalNode> Colon() {
         return this.getTokens(33);
      }

      public TerminalNode Colon(int i) {
         return this.getToken(33, i);
      }

      public TomlParser.MinuteContext minute() {
         return this.getRuleContext(TomlParser.MinuteContext.class, 0);
      }

      public TomlParser.SecondContext second() {
         return this.getRuleContext(TomlParser.SecondContext.class, 0);
      }

      public TerminalNode Dot() {
         return this.getToken(5, 0);
      }

      public TomlParser.SecondFractionContext secondFraction() {
         return this.getRuleContext(TomlParser.SecondFractionContext.class, 0);
      }

      public TimeContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 39;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterTime(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitTime(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitTime(this) : visitor.visitChildren(this));
      }
   }

   public static class TimeOffsetContext extends ParserRuleContext {
      public TerminalNode Z() {
         return this.getToken(34, 0);
      }

      public TomlParser.HourOffsetContext hourOffset() {
         return this.getRuleContext(TomlParser.HourOffsetContext.class, 0);
      }

      public TerminalNode Colon() {
         return this.getToken(33, 0);
      }

      public TomlParser.MinuteOffsetContext minuteOffset() {
         return this.getRuleContext(TomlParser.MinuteOffsetContext.class, 0);
      }

      public TimeOffsetContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 40;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterTimeOffset(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitTimeOffset(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitTimeOffset(this) : visitor.visitChildren(this));
      }
   }

   public static class TomlContext extends ParserRuleContext {
      public TerminalNode EOF() {
         return this.getToken(-1, 0);
      }

      public List<TerminalNode> NewLine() {
         return this.getTokens(16);
      }

      public TerminalNode NewLine(int i) {
         return this.getToken(16, i);
      }

      public List<TomlParser.ExpressionContext> expression() {
         return this.getRuleContexts(TomlParser.ExpressionContext.class);
      }

      public TomlParser.ExpressionContext expression(int i) {
         return this.getRuleContext(TomlParser.ExpressionContext.class, i);
      }

      public TomlContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 0;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterToml(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitToml(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitToml(this) : visitor.visitChildren(this));
      }
   }

   public static class TomlKeyContext extends ParserRuleContext {
      public TomlParser.KeyContext key() {
         return this.getRuleContext(TomlParser.KeyContext.class, 0);
      }

      public TerminalNode EOF() {
         return this.getToken(-1, 0);
      }

      public TomlKeyContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 2;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterTomlKey(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitTomlKey(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitTomlKey(this) : visitor.visitChildren(this));
      }
   }

   public static class TrueBoolContext extends ParserRuleContext {
      public TerminalNode TrueBoolean() {
         return this.getToken(25, 0);
      }

      public TrueBoolContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 31;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterTrueBool(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitTrueBool(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitTrueBool(this) : visitor.visitChildren(this));
      }
   }

   public static class UnquotedKeyContext extends ParserRuleContext {
      public TerminalNode UnquotedKey() {
         return this.getToken(13, 0);
      }

      public UnquotedKeyContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 6;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterUnquotedKey(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitUnquotedKey(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitUnquotedKey(this) : visitor.visitChildren(this));
      }
   }

   public static class ValContext extends ParserRuleContext {
      public TomlParser.StringContext string() {
         return this.getRuleContext(TomlParser.StringContext.class, 0);
      }

      public TomlParser.IntegerContext integer() {
         return this.getRuleContext(TomlParser.IntegerContext.class, 0);
      }

      public TomlParser.FloatValueContext floatValue() {
         return this.getRuleContext(TomlParser.FloatValueContext.class, 0);
      }

      public TomlParser.BooleanValueContext booleanValue() {
         return this.getRuleContext(TomlParser.BooleanValueContext.class, 0);
      }

      public TomlParser.DateTimeContext dateTime() {
         return this.getRuleContext(TomlParser.DateTimeContext.class, 0);
      }

      public TomlParser.ArrayContext array() {
         return this.getRuleContext(TomlParser.ArrayContext.class, 0);
      }

      public TomlParser.InlineTableContext inlineTable() {
         return this.getRuleContext(TomlParser.InlineTableContext.class, 0);
      }

      public ValContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 8;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterVal(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitVal(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitVal(this) : visitor.visitChildren(this));
      }
   }

   public static class YearContext extends ParserRuleContext {
      public TerminalNode DateDigits() {
         return this.getToken(36, 0);
      }

      public YearContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      @Override
      public int getRuleIndex() {
         return 44;
      }

      @Override
      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).enterYear(this);
         }
      }

      @Override
      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof TomlParserListener) {
            ((TomlParserListener)listener).exitYear(this);
         }
      }

      @Override
      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return (T)(visitor instanceof TomlParserVisitor ? ((TomlParserVisitor)visitor).visitYear(this) : visitor.visitChildren(this));
      }
   }
}
