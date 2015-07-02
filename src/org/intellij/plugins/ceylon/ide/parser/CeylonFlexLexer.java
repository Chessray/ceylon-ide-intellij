/* The following code was generated by JFlex 1.4.3 on 02/07/15 09:16 */

package org.intellij.plugins.ceylon.ide.parser;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.intellij.plugins.ceylon.ide.psi.CeylonTokens;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 02/07/15 09:16 from the specification file
 * <tt>/Users/bastien/Dev/ceylon/ceylon-ide-intellij/src/org/intellij/plugins/ceylon/ide/parser/Ceylon.flex</tt>
 */
class CeylonFlexLexer implements FlexLexer {
  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int MCOMMENT = 2;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1, 1
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\26\1\30\1\0\1\26\1\27\22\0\1\26\1\32\1\21"+
    "\1\16\1\17\1\72\1\67\1\20\1\55\1\56\1\71\1\4\1\62"+
    "\1\5\1\15\1\31\2\102\10\100\1\54\1\61\1\73\1\63\1\64"+
    "\1\65\1\75\4\101\1\3\1\101\1\7\1\77\1\76\3\77\1\7"+
    "\2\77\1\7\3\77\1\7\6\77\1\57\1\23\1\60\1\74\1\1"+
    "\1\22\1\33\1\35\1\42\1\47\1\2\1\14\1\44\1\45\1\43"+
    "\1\53\1\6\1\36\1\10\1\12\1\46\1\13\1\77\1\40\1\34"+
    "\1\41\1\11\1\51\1\52\1\50\1\37\1\77\1\24\1\70\1\25"+
    "\1\66\101\0\uff3f\77\1\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\2\0\1\1\2\2\1\3\1\4\4\2\1\5\4\1"+
    "\1\6\1\1\1\7\1\10\1\11\1\12\1\13\15\2"+
    "\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23"+
    "\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33"+
    "\1\34\1\35\1\36\3\37\2\2\1\40\1\41\1\42"+
    "\1\43\1\44\7\2\1\45\1\36\1\46\1\36\1\0"+
    "\1\47\2\0\1\50\4\0\1\51\1\52\1\53\17\2"+
    "\1\54\1\55\1\56\2\2\1\57\5\2\1\60\1\61"+
    "\1\62\1\63\1\64\1\65\1\66\1\67\1\70\1\71"+
    "\1\72\1\73\1\74\1\75\1\36\1\0\1\76\1\77"+
    "\4\2\1\100\4\2\1\101\1\102\2\46\1\0\1\50"+
    "\1\103\1\104\1\0\1\105\2\0\7\2\1\106\1\2"+
    "\1\107\12\2\1\110\5\2\1\111\1\112\1\113\1\114"+
    "\1\36\1\115\7\2\1\47\1\103\1\50\1\0\1\116"+
    "\1\0\11\2\1\117\1\2\1\120\1\121\12\2\1\122"+
    "\1\2\1\36\7\2\1\103\1\104\1\105\1\0\4\2"+
    "\1\123\1\124\2\2\1\125\1\2\1\126\1\127\1\130"+
    "\3\2\1\131\1\132\2\2\1\133\1\134\1\36\1\2"+
    "\1\135\1\136\4\2\1\103\1\116\1\2\1\137\1\140"+
    "\2\2\1\141\1\142\1\2\1\143\1\2\1\144\1\2"+
    "\1\145\1\2\1\146\1\2\1\147\1\103\5\2\1\150"+
    "\1\151\1\152\1\103\1\153\2\2\1\154\1\2\1\155"+
    "\1\156\1\157";

  private static int [] zzUnpackAction() {
    int [] result = new int[302];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\103\0\206\0\311\0\u010c\0\u014f\0\u0192\0\u01d5"+
    "\0\u0218\0\u025b\0\u029e\0\u02e1\0\u0324\0\u0367\0\u03aa\0\u03ed"+
    "\0\u0430\0\u0473\0\206\0\206\0\u04b6\0\u04f9\0\u053c\0\u057f"+
    "\0\u05c2\0\u0605\0\u0648\0\u068b\0\u06ce\0\u0711\0\u0754\0\u0797"+
    "\0\u07da\0\u081d\0\u0860\0\u08a3\0\206\0\206\0\206\0\206"+
    "\0\206\0\206\0\206\0\u08e6\0\u0929\0\u096c\0\u09af\0\u09f2"+
    "\0\u0a35\0\u0a78\0\u0abb\0\u0afe\0\206\0\206\0\u0b41\0\u0b84"+
    "\0\u0bc7\0\u0c0a\0\u0c4d\0\u0c90\0\206\0\206\0\206\0\206"+
    "\0\206\0\u0cd3\0\u0d16\0\u0d59\0\u0d9c\0\u0ddf\0\u0e22\0\u0e65"+
    "\0\u0ea8\0\u0eeb\0\u0f2e\0\u0f71\0\u03aa\0\206\0\u0fb4\0\u0ff7"+
    "\0\u103a\0\u107d\0\u10c0\0\u1103\0\311\0\206\0\206\0\206"+
    "\0\u1146\0\u1189\0\u11cc\0\u120f\0\u1252\0\u1295\0\u12d8\0\u131b"+
    "\0\u135e\0\u13a1\0\u13e4\0\u1427\0\u146a\0\u14ad\0\u14f0\0\u1533"+
    "\0\311\0\311\0\u1576\0\u15b9\0\311\0\u15fc\0\u163f\0\u1682"+
    "\0\u16c5\0\u1708\0\u174b\0\206\0\206\0\206\0\206\0\206"+
    "\0\u178e\0\206\0\u17d1\0\206\0\206\0\206\0\206\0\u1814"+
    "\0\206\0\u1857\0\206\0\206\0\u189a\0\u18dd\0\u1920\0\u1963"+
    "\0\311\0\u19a6\0\u19e9\0\u1a2c\0\u1a6f\0\311\0\206\0\u1ab2"+
    "\0\206\0\u1af5\0\206\0\u1b38\0\206\0\u1b7b\0\206\0\u1bbe"+
    "\0\u1c01\0\u1c44\0\u1c87\0\u1cca\0\u1d0d\0\u1d50\0\u1d93\0\u1dd6"+
    "\0\311\0\u1e19\0\311\0\u1e5c\0\u1e9f\0\u1ee2\0\u1f25\0\u1f68"+
    "\0\u1fab\0\u1fee\0\u2031\0\u2074\0\u20b7\0\u20fa\0\u213d\0\u2180"+
    "\0\u21c3\0\u2206\0\u2249\0\206\0\206\0\206\0\206\0\u228c"+
    "\0\311\0\u22cf\0\u2312\0\u2355\0\u2398\0\u23db\0\u241e\0\u2461"+
    "\0\u1af5\0\u24a4\0\u1b7b\0\u24e7\0\206\0\u252a\0\u256d\0\u25b0"+
    "\0\u25f3\0\u2636\0\u2679\0\u26bc\0\u26ff\0\u2742\0\u2785\0\311"+
    "\0\u27c8\0\311\0\311\0\u280b\0\u284e\0\u2891\0\u28d4\0\u2917"+
    "\0\u295a\0\u299d\0\u29e0\0\u2a23\0\u2a66\0\311\0\u2aa9\0\u2aec"+
    "\0\u2b2f\0\u2b72\0\u2bb5\0\u2bf8\0\u2c3b\0\u2c7e\0\u2cc1\0\u2d04"+
    "\0\u24e7\0\u252a\0\u2d47\0\u2d8a\0\u2dcd\0\u2e10\0\u2e53\0\311"+
    "\0\311\0\u2e96\0\u2ed9\0\311\0\u2f1c\0\311\0\311\0\311"+
    "\0\u2f5f\0\u2fa2\0\u2fe5\0\311\0\311\0\u3028\0\u306b\0\311"+
    "\0\311\0\u30ae\0\u30f1\0\311\0\311\0\u3134\0\u3177\0\u31ba"+
    "\0\u31fd\0\u3240\0\u2d47\0\u3283\0\311\0\311\0\u32c6\0\u3309"+
    "\0\311\0\311\0\u334c\0\311\0\u338f\0\311\0\u33d2\0\311"+
    "\0\u3415\0\311\0\u3458\0\311\0\u349b\0\u34de\0\u3521\0\u3564"+
    "\0\u35a7\0\u35ea\0\311\0\311\0\311\0\206\0\311\0\u362d"+
    "\0\u3670\0\311\0\u36b3\0\311\0\311\0\311";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[302];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\3\1\4\1\5\1\4\1\6\1\7\2\4\1\10"+
    "\1\4\1\11\1\12\1\13\1\14\1\15\1\16\1\17"+
    "\1\20\1\21\1\22\1\23\1\24\3\25\1\26\1\27"+
    "\1\30\1\31\1\32\1\33\1\4\1\34\1\35\1\36"+
    "\1\37\1\40\1\4\1\41\1\42\1\4\1\43\1\44"+
    "\1\4\1\45\1\46\1\47\1\50\1\51\1\52\1\53"+
    "\1\54\1\55\1\56\1\57\1\60\1\61\1\62\1\63"+
    "\1\64\1\65\1\66\2\4\1\67\1\4\1\67\31\70"+
    "\1\71\37\70\1\72\11\70\104\0\3\4\2\0\7\4"+
    "\16\0\21\4\22\0\5\4\1\0\3\4\2\0\7\4"+
    "\16\0\3\4\1\73\11\4\1\74\3\4\22\0\5\4"+
    "\4\0\1\75\56\0\1\76\24\0\1\77\55\0\1\100"+
    "\1\101\17\0\3\4\2\0\7\4\16\0\13\4\1\102"+
    "\5\4\22\0\5\4\1\0\1\4\1\103\1\4\2\0"+
    "\7\4\16\0\13\4\1\104\5\4\22\0\5\4\1\0"+
    "\3\4\2\0\7\4\16\0\1\105\20\4\22\0\5\4"+
    "\1\0\3\4\2\0\3\4\1\106\3\4\16\0\10\4"+
    "\1\107\2\4\1\110\5\4\22\0\5\4\15\0\1\111"+
    "\67\0\2\112\10\0\1\112\15\0\1\113\1\112\1\0"+
    "\1\112\4\0\1\112\4\0\1\112\30\0\3\112\102\0"+
    "\1\114\20\115\1\116\2\115\1\117\57\115\21\120\1\121"+
    "\1\122\1\123\57\120\22\0\1\124\123\0\1\125\32\0"+
    "\1\125\32\0\3\25\103\0\1\113\31\0\1\126\5\0"+
    "\1\127\74\0\1\130\20\0\3\4\2\0\7\4\16\0"+
    "\1\4\1\131\1\132\1\133\15\4\22\0\5\4\1\0"+
    "\3\4\2\0\3\4\1\134\3\4\16\0\1\135\16\4"+
    "\1\136\1\4\22\0\5\4\1\0\3\4\2\0\7\4"+
    "\16\0\5\4\1\137\13\4\22\0\5\4\1\0\1\4"+
    "\1\140\1\4\2\0\7\4\16\0\21\4\22\0\5\4"+
    "\1\0\1\4\1\141\1\4\2\0\7\4\16\0\21\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\5\4"+
    "\1\142\4\4\1\143\6\4\22\0\5\4\1\0\3\4"+
    "\2\0\7\4\16\0\1\144\2\4\1\145\7\4\1\146"+
    "\5\4\22\0\5\4\1\0\3\4\2\0\2\4\1\147"+
    "\1\4\1\150\1\4\1\151\16\0\1\4\1\152\17\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\10\4"+
    "\1\153\10\4\22\0\5\4\1\0\3\4\2\0\3\4"+
    "\1\154\2\4\1\155\16\0\2\4\1\156\16\4\22\0"+
    "\5\4\1\0\3\4\2\0\7\4\16\0\4\4\1\157"+
    "\14\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\1\160\12\4\1\161\5\4\22\0\5\4\1\0\3\4"+
    "\2\0\7\4\16\0\12\4\1\162\6\4\22\0\5\4"+
    "\63\0\1\163\1\164\101\0\1\165\34\0\1\166\150\0"+
    "\1\167\102\0\1\170\3\0\1\171\76\0\1\172\4\0"+
    "\1\173\27\0\1\174\45\0\1\175\5\0\1\176\74\0"+
    "\1\177\102\0\1\200\20\0\1\67\4\0\7\201\1\202"+
    "\62\0\1\67\1\0\1\67\31\70\1\0\37\70\1\0"+
    "\42\70\1\71\37\70\1\203\42\70\1\204\37\70\1\72"+
    "\11\70\1\0\3\4\2\0\7\4\16\0\1\4\1\205"+
    "\17\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\6\4\1\206\1\4\1\207\10\4\22\0\5\4\1\0"+
    "\3\4\2\0\7\4\16\0\14\4\1\210\4\4\22\0"+
    "\5\4\1\0\3\4\2\0\7\4\16\0\17\4\1\211"+
    "\1\4\22\0\5\4\1\0\3\4\2\0\4\4\1\212"+
    "\2\4\16\0\21\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\7\4\1\213\11\4\22\0\5\4\1\0"+
    "\3\4\2\0\4\4\1\214\2\4\16\0\21\4\22\0"+
    "\5\4\1\0\3\4\2\0\4\4\1\215\2\4\16\0"+
    "\21\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\5\4\1\216\13\4\22\0\5\4\15\0\1\217\66\0"+
    "\3\112\10\0\1\112\16\0\1\112\1\0\1\112\4\0"+
    "\1\112\4\0\1\112\30\0\3\112\27\113\1\220\1\221"+
    "\52\113\1\0\1\114\100\0\1\114\24\115\1\222\56\115"+
    "\21\120\1\223\1\122\1\123\57\120\21\0\1\224\61\0"+
    "\21\120\1\223\1\225\104\120\1\226\56\120\21\124\1\227"+
    "\1\230\1\231\57\124\1\0\3\4\2\0\7\4\16\0"+
    "\1\4\1\232\17\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\1\4\1\233\17\4\22\0\5\4\1\0"+
    "\3\4\2\0\7\4\16\0\10\4\1\234\10\4\22\0"+
    "\5\4\1\0\3\4\2\0\5\4\1\235\1\4\16\0"+
    "\21\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\6\4\1\236\12\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\10\4\1\237\10\4\22\0\5\4\1\0"+
    "\1\4\1\240\1\4\2\0\7\4\16\0\21\4\22\0"+
    "\5\4\1\0\3\4\2\0\7\4\16\0\6\4\1\241"+
    "\12\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\6\4\1\242\12\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\4\4\1\243\14\4\22\0\5\4\1\0"+
    "\1\4\1\244\1\4\2\0\7\4\16\0\5\4\1\245"+
    "\2\4\1\246\10\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\1\4\1\247\4\4\1\250\12\4\22\0"+
    "\5\4\1\0\3\4\2\0\7\4\16\0\1\251\20\4"+
    "\22\0\5\4\1\0\3\4\2\0\4\4\1\252\2\4"+
    "\16\0\21\4\22\0\5\4\1\0\3\4\2\0\5\4"+
    "\1\253\1\4\16\0\21\4\22\0\5\4\1\0\3\4"+
    "\2\0\7\4\16\0\6\4\1\254\12\4\22\0\5\4"+
    "\1\0\3\4\2\0\7\4\16\0\16\4\1\255\2\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\6\4"+
    "\1\256\12\4\22\0\5\4\1\0\3\4\2\0\7\4"+
    "\16\0\20\4\1\257\22\0\5\4\1\0\3\4\2\0"+
    "\4\4\1\260\2\4\16\0\21\4\22\0\5\4\1\0"+
    "\3\4\2\0\7\4\16\0\3\4\1\261\15\4\22\0"+
    "\5\4\1\0\3\4\2\0\7\4\16\0\10\4\1\262"+
    "\10\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\10\4\1\263\10\4\22\0\5\4\63\0\1\264\102\0"+
    "\1\265\102\0\1\266\103\0\1\267\116\0\1\270\1\0"+
    "\1\270\1\0\1\4\1\271\1\4\2\0\7\4\16\0"+
    "\21\4\22\0\5\4\1\0\1\4\1\272\1\4\2\0"+
    "\7\4\16\0\21\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\1\4\1\273\17\4\22\0\5\4\1\0"+
    "\3\4\2\0\3\4\1\274\3\4\16\0\21\4\22\0"+
    "\5\4\1\0\1\4\1\275\1\4\2\0\7\4\16\0"+
    "\21\4\22\0\5\4\1\0\3\4\2\0\1\276\6\4"+
    "\16\0\21\4\22\0\5\4\1\0\3\4\2\0\7\4"+
    "\16\0\7\4\1\277\11\4\22\0\5\4\1\0\3\4"+
    "\2\0\7\4\16\0\1\300\20\4\22\0\5\4\30\0"+
    "\1\221\52\0\20\222\1\301\4\222\1\115\55\222\21\224"+
    "\1\302\61\224\21\226\1\303\1\304\2\226\1\120\55\226"+
    "\21\124\1\0\1\305\104\124\1\306\56\124\1\0\1\4"+
    "\1\307\1\4\2\0\7\4\16\0\10\4\1\310\10\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\6\4"+
    "\1\311\12\4\22\0\5\4\1\0\3\4\2\0\7\4"+
    "\16\0\1\312\20\4\22\0\5\4\1\0\1\4\1\313"+
    "\1\4\2\0\7\4\16\0\21\4\22\0\5\4\1\0"+
    "\3\4\2\0\7\4\16\0\10\4\1\314\10\4\22\0"+
    "\5\4\1\0\3\4\2\0\7\4\16\0\6\4\1\315"+
    "\12\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\1\316\20\4\22\0\5\4\1\0\3\4\2\0\3\4"+
    "\1\317\3\4\16\0\21\4\22\0\5\4\1\0\3\4"+
    "\2\0\4\4\1\320\2\4\16\0\21\4\22\0\5\4"+
    "\1\0\3\4\2\0\7\4\16\0\13\4\1\321\5\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\1\4"+
    "\1\322\17\4\22\0\5\4\1\0\1\4\1\323\1\4"+
    "\2\0\7\4\16\0\21\4\22\0\5\4\1\0\3\4"+
    "\2\0\7\4\16\0\7\4\1\324\11\4\22\0\5\4"+
    "\1\0\3\4\2\0\7\4\16\0\1\4\1\325\17\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\6\4"+
    "\1\326\12\4\22\0\5\4\1\0\3\4\2\0\7\4"+
    "\16\0\13\4\1\327\5\4\22\0\5\4\1\0\1\4"+
    "\1\330\1\4\2\0\7\4\16\0\21\4\22\0\5\4"+
    "\1\0\1\4\1\331\1\4\2\0\7\4\16\0\21\4"+
    "\22\0\5\4\1\0\1\4\1\332\1\4\2\0\7\4"+
    "\16\0\21\4\22\0\5\4\1\0\1\4\1\333\1\4"+
    "\2\0\7\4\16\0\21\4\22\0\5\4\1\0\3\4"+
    "\2\0\7\4\16\0\1\334\20\4\22\0\5\4\1\0"+
    "\3\4\2\0\3\4\1\335\3\4\16\0\21\4\22\0"+
    "\5\4\1\0\3\4\2\0\7\4\16\0\14\4\1\336"+
    "\4\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\3\4\1\337\15\4\22\0\5\4\1\0\1\270\2\340"+
    "\2\0\7\201\63\0\1\270\1\0\1\270\1\0\3\4"+
    "\2\0\4\4\1\341\2\4\16\0\21\4\22\0\5\4"+
    "\1\0\3\4\2\0\7\4\16\0\6\4\1\342\12\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\3\4"+
    "\1\343\15\4\22\0\5\4\1\0\3\4\2\0\2\4"+
    "\1\344\4\4\16\0\21\4\22\0\5\4\1\0\3\4"+
    "\2\0\7\4\16\0\1\345\20\4\22\0\5\4\1\0"+
    "\3\4\2\0\7\4\16\0\6\4\1\346\12\4\22\0"+
    "\5\4\1\0\3\4\2\0\7\4\16\0\3\4\1\347"+
    "\15\4\22\0\5\4\21\224\1\350\61\224\21\226\1\303"+
    "\1\351\2\226\1\120\55\226\21\306\1\352\1\353\2\306"+
    "\1\124\55\306\1\0\3\4\2\0\2\4\1\354\4\4"+
    "\16\0\5\4\1\355\13\4\22\0\5\4\1\0\3\4"+
    "\2\0\7\4\16\0\11\4\1\356\7\4\22\0\5\4"+
    "\1\0\3\4\2\0\7\4\16\0\5\4\1\357\13\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\1\4"+
    "\1\360\17\4\22\0\5\4\1\0\3\4\2\0\7\4"+
    "\16\0\5\4\1\361\13\4\22\0\5\4\1\0\3\4"+
    "\2\0\7\4\16\0\1\4\1\362\17\4\22\0\5\4"+
    "\1\0\3\4\2\0\7\4\16\0\7\4\1\363\11\4"+
    "\22\0\5\4\1\0\3\4\2\0\1\364\6\4\16\0"+
    "\21\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\5\4\1\365\13\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\17\4\1\366\1\4\22\0\5\4\1\0"+
    "\3\4\2\0\7\4\16\0\12\4\1\367\6\4\22\0"+
    "\5\4\1\0\3\4\2\0\7\4\16\0\1\4\1\370"+
    "\17\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\10\4\1\371\10\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\5\4\1\372\13\4\22\0\5\4\1\0"+
    "\3\4\2\0\7\4\16\0\5\4\1\373\13\4\22\0"+
    "\5\4\1\0\3\4\2\0\4\4\1\374\2\4\16\0"+
    "\21\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\5\4\1\375\13\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\7\4\1\376\11\4\22\0\5\4\1\0"+
    "\3\4\2\0\2\4\1\377\4\4\16\0\21\4\22\0"+
    "\5\4\1\0\1\4\1\u0100\1\4\2\0\7\4\16\0"+
    "\21\4\22\0\5\4\1\0\1\4\1\u0101\1\4\2\0"+
    "\7\4\16\0\21\4\22\0\5\4\4\0\2\u0102\72\0"+
    "\1\u0102\1\0\1\u0102\1\0\3\4\2\0\7\4\16\0"+
    "\14\4\1\u0103\4\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\1\4\1\u0104\17\4\22\0\5\4\1\0"+
    "\1\4\1\u0105\1\4\2\0\7\4\16\0\21\4\22\0"+
    "\5\4\1\0\3\4\2\0\5\4\1\u0106\1\4\16\0"+
    "\21\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\11\4\1\u0107\7\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\10\4\1\u0108\10\4\22\0\5\4\1\0"+
    "\3\4\2\0\7\4\16\0\3\4\1\u0109\15\4\22\0"+
    "\5\4\21\0\1\u010a\61\0\21\306\1\352\1\u010b\2\306"+
    "\1\124\55\306\1\0\3\4\2\0\7\4\16\0\2\4"+
    "\1\u010c\16\4\22\0\5\4\1\0\3\4\2\0\7\4"+
    "\16\0\6\4\1\u010d\12\4\22\0\5\4\1\0\3\4"+
    "\2\0\4\4\1\u010e\2\4\16\0\21\4\22\0\5\4"+
    "\1\0\3\4\2\0\7\4\16\0\1\u010f\20\4\22\0"+
    "\5\4\1\0\3\4\2\0\6\4\1\u0110\16\0\21\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\12\4"+
    "\1\u0111\6\4\22\0\5\4\1\0\3\4\2\0\4\4"+
    "\1\u0112\2\4\16\0\21\4\22\0\5\4\1\0\3\4"+
    "\2\0\4\4\1\u0113\2\4\16\0\21\4\22\0\5\4"+
    "\1\0\3\4\2\0\7\4\16\0\6\4\1\u0114\12\4"+
    "\22\0\5\4\1\0\3\4\2\0\6\4\1\u0115\16\0"+
    "\21\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\6\4\1\u0116\12\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\10\4\1\u0117\10\4\22\0\5\4\100\0"+
    "\1\u0102\1\0\1\u0102\1\0\3\4\2\0\7\4\16\0"+
    "\1\4\1\u0118\17\4\22\0\5\4\1\0\3\4\2\0"+
    "\7\4\16\0\6\4\1\u0119\12\4\22\0\5\4\1\0"+
    "\1\4\1\u011a\1\4\2\0\7\4\16\0\21\4\22\0"+
    "\5\4\1\0\3\4\2\0\7\4\16\0\13\4\1\u011b"+
    "\5\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\4\4\1\u011c\14\4\22\0\5\4\21\0\1\u011d\62\0"+
    "\3\4\2\0\7\4\16\0\3\4\1\u011e\15\4\22\0"+
    "\5\4\1\0\3\4\2\0\7\4\16\0\7\4\1\u011f"+
    "\11\4\22\0\5\4\1\0\3\4\2\0\7\4\16\0"+
    "\10\4\1\u0120\10\4\22\0\5\4\1\0\3\4\2\0"+
    "\3\4\1\u0121\3\4\16\0\21\4\22\0\5\4\1\0"+
    "\3\4\2\0\7\4\16\0\1\u0122\20\4\22\0\5\4"+
    "\1\0\3\4\2\0\7\4\16\0\7\4\1\u0123\11\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\4\4"+
    "\1\u0124\14\4\22\0\5\4\1\0\3\4\2\0\4\4"+
    "\1\u0125\2\4\16\0\21\4\22\0\5\4\21\0\1\u0126"+
    "\62\0\3\4\2\0\7\4\16\0\4\4\1\u0127\14\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\6\4"+
    "\1\u0128\12\4\22\0\5\4\1\0\1\4\1\u0129\1\4"+
    "\2\0\7\4\16\0\21\4\22\0\5\4\1\0\1\4"+
    "\1\u012a\1\4\2\0\7\4\16\0\21\4\22\0\5\4"+
    "\1\0\3\4\2\0\7\4\16\0\7\4\1\u012b\11\4"+
    "\22\0\5\4\1\0\3\4\2\0\7\4\16\0\1\4"+
    "\1\u012c\17\4\22\0\5\4\1\0\3\4\2\0\7\4"+
    "\16\0\1\4\1\u012d\17\4\22\0\5\4\1\0\1\4"+
    "\1\u012e\1\4\2\0\7\4\16\0\21\4\22\0\5\4";

  private static int [] zzUnpackTrans() {
    int [] result = new int[14070];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;
  private static final char[] EMPTY_BUFFER = new char[0];
  private static final int YYEOF = -1;
  private static java.io.Reader zzReader = null; // Fake

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\2\0\1\11\17\1\2\11\20\1\7\11\11\1\2\11"+
    "\6\1\5\11\13\1\1\0\1\11\2\0\1\1\4\0"+
    "\3\11\33\1\5\11\1\1\1\11\1\1\4\11\1\1"+
    "\1\11\1\0\2\11\12\1\1\11\1\1\1\11\1\0"+
    "\1\11\1\1\1\11\1\0\1\11\2\0\32\1\4\11"+
    "\14\1\1\0\1\11\1\0\44\1\1\0\72\1\1\11"+
    "\10\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[302];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** this buffer may contains the current text array to be matched when it is cheap to acquire it */
  private char[] zzBufferArray;

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /**
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
    int multiCommentLevel = 0;


  CeylonFlexLexer(java.io.Reader in) {
    this.zzReader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  CeylonFlexLexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 168) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  public final int getTokenStart(){
    return zzStartRead;
  }

  public final int getTokenEnd(){
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end,int initialState){
    zzBuffer = buffer;
    zzBufferArray = com.intellij.util.text.CharArrayUtil.fromSequenceWithoutCopying(buffer);
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzPushbackPos = 0;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position <tt>pos</tt> from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBufferArray != null ? zzBufferArray[zzStartRead+pos]:zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() {
    if (!zzEOFDone) {
      zzEOFDone = true;
    
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;
    char[] zzBufferArrayL = zzBufferArray;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL)
            zzInput = (zzBufferArrayL != null ? zzBufferArrayL[zzCurrentPosL++] : zzBufferL.charAt(zzCurrentPosL++));
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = (zzBufferArrayL != null ? zzBufferArrayL[zzCurrentPosL++] : zzBufferL.charAt(zzCurrentPosL++));
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 32: 
          { return CeylonTokens.INCREMENT_OP;
          }
        case 112: break;
        case 27: 
          { return CeylonTokens.SMALLER_OP;
          }
        case 113: break;
        case 35: 
          { return CeylonTokens.SUBTRACT_SPECIFY;
          }
        case 114: break;
        case 69: 
          { return CeylonTokens.STRING_END;
          }
        case 115: break;
        case 50: 
          { return CeylonTokens.LARGE_AS_OP;
          }
        case 116: break;
        case 40: 
          { return CeylonTokens.STRING_LITERAL;
          }
        case 117: break;
        case 49: 
          { return CeylonTokens.COMPUTE;
          }
        case 118: break;
        case 38: 
          { return CeylonTokens.LINE_COMMENT;
          }
        case 119: break;
        case 91: 
          { return CeylonTokens.VALUE_MODIFIER;
          }
        case 120: break;
        case 81: 
          { return CeylonTokens.CASE_CLAUSE;
          }
        case 121: break;
        case 11: 
          { return CeylonTokens.NOT_OP;
          }
        case 122: break;
        case 65: 
          { return CeylonTokens.FOR_CLAUSE;
          }
        case 123: break;
        case 83: 
          { return CeylonTokens.ALIAS;
          }
        case 124: break;
        case 57: 
          { return CeylonTokens.SPREAD_OP;
          }
        case 125: break;
        case 4: 
          { return CeylonTokens.DIFFERENCE_OP;
          }
        case 126: break;
        case 45: 
          { return CeylonTokens.IF_CLAUSE;
          }
        case 127: break;
        case 8: 
          { return CeylonTokens.RBRACE;
          }
        case 128: break;
        case 104: 
          { return CeylonTokens.DYNAMIC;
          }
        case 129: break;
        case 58: 
          { return CeylonTokens.MULTIPLY_SPECIFY;
          }
        case 130: break;
        case 7: 
          { return CeylonTokens.LBRACE;
          }
        case 131: break;
        case 96: 
          { return CeylonTokens.ASSIGN;
          }
        case 132: break;
        case 22: 
          { return CeylonTokens.COMPLEMENT_OP;
          }
        case 133: break;
        case 73: 
          { return CeylonTokens.IDENTICAL_OP;
          }
        case 134: break;
        case 23: 
          { return CeylonTokens.INTERSECTION_OP;
          }
        case 135: break;
        case 89: 
          { return CeylonTokens.TYPE_CONSTRAINT;
          }
        case 136: break;
        case 54: 
          { return CeylonTokens.AND_OP;
          }
        case 137: break;
        case 2: 
          { return CeylonTokens.UIDENTIFIER;
          }
        case 138: break;
        case 63: 
          { --multiCommentLevel;
                    if (multiCommentLevel <= 0) {
                        yybegin(YYINITIAL);
                    }
                    return CeylonTokens.MULTI_COMMENT;
          }
        case 139: break;
        case 28: 
          { return CeylonTokens.POWER_OP;
          }
        case 140: break;
        case 99: 
          { return CeylonTokens.IMPORT;
          }
        case 141: break;
        case 93: 
          { return CeylonTokens.EXISTS;
          }
        case 142: break;
        case 12: 
          { return CeylonTokens.SEGMENT_OP;
          }
        case 143: break;
        case 16: 
          { return CeylonTokens.RBRACKET;
          }
        case 144: break;
        case 85: 
          { return CeylonTokens.BREAK;
          }
        case 145: break;
        case 82: 
          { return CeylonTokens.VOID_MODIFIER;
          }
        case 146: break;
        case 31: 
          { return CeylonTokens.MULTI_COMMENT;
          }
        case 147: break;
        case 106: 
          { return CeylonTokens.FUNCTION_MODIFIER;
          }
        case 148: break;
        case 43: 
          { return CeylonTokens.NOT_EQUAL_OP;
          }
        case 149: break;
        case 14: 
          { return CeylonTokens.RPAREN;
          }
        case 150: break;
        case 78: 
          { return CeylonTokens.STRING_MID;
          }
        case 151: break;
        case 109: 
          { return CeylonTokens.ABSTRACTED_TYPE;
          }
        case 152: break;
        case 9: 
          { return CeylonTokens.WS;
          }
        case 153: break;
        case 97: 
          { return CeylonTokens.SWITCH_CLAUSE;
          }
        case 154: break;
        case 74: 
          { return CeylonTokens.AND_SPECIFY;
          }
        case 155: break;
        case 84: 
          { return CeylonTokens.SUPER;
          }
        case 156: break;
        case 51: 
          { return CeylonTokens.SAFE_MEMBER_OP;
          }
        case 157: break;
        case 55: 
          { return CeylonTokens.UNION_SPECIFY;
          }
        case 158: break;
        case 19: 
          { return CeylonTokens.SPECIFY;
          }
        case 159: break;
        case 3: 
          { return CeylonTokens.SUM_OP;
          }
        case 160: break;
        case 59: 
          { return CeylonTokens.SCALE_OP;
          }
        case 161: break;
        case 18: 
          { return CeylonTokens.COMMA;
          }
        case 162: break;
        case 71: 
          { return CeylonTokens.TRY_CLAUSE;
          }
        case 163: break;
        case 15: 
          { return CeylonTokens.LBRACKET;
          }
        case 164: break;
        case 13: 
          { return CeylonTokens.LPAREN;
          }
        case 165: break;
        case 101: 
          { return CeylonTokens.EXTENDS;
          }
        case 166: break;
        case 33: 
          { return CeylonTokens.ADD_SPECIFY;
          }
        case 167: break;
        case 47: 
          { return CeylonTokens.CASE_TYPES;
          }
        case 168: break;
        case 21: 
          { return CeylonTokens.OPTIONAL;
          }
        case 169: break;
        case 64: 
          { return CeylonTokens.NEW;
          }
        case 170: break;
        case 72: 
          { return CeylonTokens.OUT;
          }
        case 171: break;
        case 30: 
          { return CeylonTokens.NATURAL_LITERAL;
          }
        case 172: break;
        case 80: 
          { return CeylonTokens.THIS;
          }
        case 173: break;
        case 1: 
          { return TokenType.BAD_CHARACTER;
          }
        case 174: break;
        case 41: 
          { return CeylonTokens.DIVIDE_SPECIFY;
          }
        case 175: break;
        case 62: 
          { ++multiCommentLevel; return CeylonTokens.MULTI_COMMENT;
          }
        case 176: break;
        case 44: 
          { return CeylonTokens.IN_OP;
          }
        case 177: break;
        case 88: 
          { return CeylonTokens.CLASS_DEFINITION;
          }
        case 178: break;
        case 107: 
          { return CeylonTokens.ASSEMBLY;
          }
        case 179: break;
        case 46: 
          { return CeylonTokens.IS_OP;
          }
        case 180: break;
        case 76: 
          { return CeylonTokens.COMPARE_OP;
          }
        case 181: break;
        case 34: 
          { return CeylonTokens.DECREMENT_OP;
          }
        case 182: break;
        case 52: 
          { return CeylonTokens.COMPLEMENT_SPECIFY;
          }
        case 183: break;
        case 79: 
          { return CeylonTokens.THEN_CLAUSE;
          }
        case 184: break;
        case 39: 
          { return CeylonTokens.CHAR_LITERAL;
          }
        case 185: break;
        case 68: 
          { return CeylonTokens.STRING_START;
          }
        case 186: break;
        case 98: 
          { return CeylonTokens.RETURN;
          }
        case 187: break;
        case 29: 
          { return CeylonTokens.COMPILER_ANNOTATION;
          }
        case 188: break;
        case 60: 
          { return CeylonTokens.REMAINDER_SPECIFY;
          }
        case 189: break;
        case 95: 
          { return CeylonTokens.ASSERT;
          }
        case 190: break;
        case 75: 
          { return CeylonTokens.OR_SPECIFY;
          }
        case 191: break;
        case 42: 
          { yybegin(MCOMMENT); multiCommentLevel = 1; return CeylonTokens.MULTI_COMMENT;
          }
        case 192: break;
        case 56: 
          { return CeylonTokens.OR_OP;
          }
        case 193: break;
        case 36: 
          { return CeylonTokens.ENTRY_OP;
          }
        case 194: break;
        case 53: 
          { return CeylonTokens.INTERSECT_SPECIFY;
          }
        case 195: break;
        case 105: 
          { return CeylonTokens.NONEMPTY;
          }
        case 196: break;
        case 102: 
          { return CeylonTokens.PACKAGE;
          }
        case 197: break;
        case 24: 
          { return CeylonTokens.UNION_OP;
          }
        case 198: break;
        case 94: 
          { return CeylonTokens.MODULE;
          }
        case 199: break;
        case 100: 
          { return CeylonTokens.OBJECT_DEFINITION;
          }
        case 200: break;
        case 110: 
          { return CeylonTokens.SATISFIES;
          }
        case 201: break;
        case 108: 
          { return CeylonTokens.CONTINUE;
          }
        case 202: break;
        case 77: 
          { return CeylonTokens.ELSE_CLAUSE;
          }
        case 203: break;
        case 48: 
          { return CeylonTokens.EQUAL_OP;
          }
        case 204: break;
        case 20: 
          { return CeylonTokens.LARGER_OP;
          }
        case 205: break;
        case 5: 
          { return CeylonTokens.MEMBER_OP;
          }
        case 206: break;
        case 6: 
          { return CeylonTokens.BACKTICK;
          }
        case 207: break;
        case 10: 
          { return CeylonTokens.QUOTIENT_OP;
          }
        case 208: break;
        case 90: 
          { return CeylonTokens.OUTER;
          }
        case 209: break;
        case 37: 
          { return CeylonTokens.RANGE_OP;
          }
        case 210: break;
        case 111: 
          { return CeylonTokens.INTERFACE_DEFINITION;
          }
        case 211: break;
        case 25: 
          { return CeylonTokens.PRODUCT_OP;
          }
        case 212: break;
        case 17: 
          { return CeylonTokens.SEMICOLON;
          }
        case 213: break;
        case 26: 
          { return CeylonTokens.REMAINDER_OP;
          }
        case 214: break;
        case 87: 
          { return CeylonTokens.CATCH_CLAUSE;
          }
        case 215: break;
        case 66: 
          { return CeylonTokens.ELLIPSIS;
          }
        case 216: break;
        case 86: 
          { return CeylonTokens.THROW;
          }
        case 217: break;
        case 70: 
          { return CeylonTokens.LET;
          }
        case 218: break;
        case 92: 
          { return CeylonTokens.WHILE_CLAUSE;
          }
        case 219: break;
        case 67: 
          { return CeylonTokens.VERBATIM_STRING;
          }
        case 220: break;
        case 103: 
          { return CeylonTokens.FINALLY_CLAUSE;
          }
        case 221: break;
        case 61: 
          { return CeylonTokens.SMALL_AS_OP;
          }
        case 222: break;
        default:
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
            return null;
          }
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
