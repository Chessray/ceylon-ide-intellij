// $ANTLR 3.4 grammar/IdeaAstTypesGen.g 2017-01-24 18:07:50
 
    package com.redhat.ceylon.compiler.typechecker.treegen; 


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class IdeaAstTypesGenLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int CARAT=4;
    public static final int DESCRIPTION=5;
    public static final int EXTENDS=6;
    public static final int FIELD_NAME=7;
    public static final int LPAREN=8;
    public static final int MANY=9;
    public static final int NODE_NAME=10;
    public static final int OPTIONAL=11;
    public static final int RPAREN=12;
    public static final int SEMI=13;
    public static final int TYPE_NAME=14;
    public static final int WS=15;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public IdeaAstTypesGenLexer() {} 
    public IdeaAstTypesGenLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public IdeaAstTypesGenLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "grammar/IdeaAstTypesGen.g"; }

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:6:7: ( '*' )
            // grammar/IdeaAstTypesGen.g:6:9: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:7:7: ( 'abstract' )
            // grammar/IdeaAstTypesGen.g:7:9: 'abstract'
            {
            match("abstract"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:8:7: ( 'boolean' )
            // grammar/IdeaAstTypesGen.g:8:9: 'boolean'
            {
            match("boolean"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:9:7: ( 'string' )
            // grammar/IdeaAstTypesGen.g:9:9: 'string'
            {
            match("string"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "NODE_NAME"
    public final void mNODE_NAME() throws RecognitionException {
        try {
            int _type = NODE_NAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:40:11: ( ( 'A' .. 'Z' | '_' )+ )
            // grammar/IdeaAstTypesGen.g:40:13: ( 'A' .. 'Z' | '_' )+
            {
            // grammar/IdeaAstTypesGen.g:40:13: ( 'A' .. 'Z' | '_' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0 >= 'A' && LA1_0 <= 'Z')||LA1_0=='_') ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // grammar/IdeaAstTypesGen.g:
            	    {
            	    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NODE_NAME"

    // $ANTLR start "FIELD_NAME"
    public final void mFIELD_NAME() throws RecognitionException {
        try {
            int _type = FIELD_NAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:42:12: ( ( 'a' .. 'z' ) ( 'a' .. 'z' | 'A' .. 'Z' )* )
            // grammar/IdeaAstTypesGen.g:42:14: ( 'a' .. 'z' ) ( 'a' .. 'z' | 'A' .. 'Z' )*
            {
            if ( (input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // grammar/IdeaAstTypesGen.g:42:25: ( 'a' .. 'z' | 'A' .. 'Z' )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= 'A' && LA2_0 <= 'Z')||(LA2_0 >= 'a' && LA2_0 <= 'z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // grammar/IdeaAstTypesGen.g:
            	    {
            	    if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FIELD_NAME"

    // $ANTLR start "TYPE_NAME"
    public final void mTYPE_NAME() throws RecognitionException {
        try {
            int _type = TYPE_NAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:43:11: ( ( 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '<' | '>' )* )
            // grammar/IdeaAstTypesGen.g:43:13: ( 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '<' | '>' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // grammar/IdeaAstTypesGen.g:43:24: ( 'a' .. 'z' | 'A' .. 'Z' | '<' | '>' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0=='<'||LA3_0=='>'||(LA3_0 >= 'A' && LA3_0 <= 'Z')||(LA3_0 >= 'a' && LA3_0 <= 'z')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // grammar/IdeaAstTypesGen.g:
            	    {
            	    if ( input.LA(1)=='<'||input.LA(1)=='>'||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TYPE_NAME"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:45:4: ( ( ' ' | '\\n' | '\\t' | '\\r' | '\\u000C' ) )
            // grammar/IdeaAstTypesGen.g:45:6: ( ' ' | '\\n' | '\\t' | '\\r' | '\\u000C' )
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


             skip(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "CARAT"
    public final void mCARAT() throws RecognitionException {
        try {
            int _type = CARAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:47:7: ( '^' )
            // grammar/IdeaAstTypesGen.g:47:9: '^'
            {
            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CARAT"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:49:8: ( '(' )
            // grammar/IdeaAstTypesGen.g:49:10: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:50:8: ( ')' )
            // grammar/IdeaAstTypesGen.g:50:10: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "MANY"
    public final void mMANY() throws RecognitionException {
        try {
            int _type = MANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:52:6: ( '*' | '+' )
            // grammar/IdeaAstTypesGen.g:
            {
            if ( (input.LA(1) >= '*' && input.LA(1) <= '+') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MANY"

    // $ANTLR start "OPTIONAL"
    public final void mOPTIONAL() throws RecognitionException {
        try {
            int _type = OPTIONAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:53:10: ( '?' )
            // grammar/IdeaAstTypesGen.g:53:12: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OPTIONAL"

    // $ANTLR start "EXTENDS"
    public final void mEXTENDS() throws RecognitionException {
        try {
            int _type = EXTENDS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:55:9: ( ':' )
            // grammar/IdeaAstTypesGen.g:55:11: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EXTENDS"

    // $ANTLR start "SEMI"
    public final void mSEMI() throws RecognitionException {
        try {
            int _type = SEMI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:57:6: ( ';' )
            // grammar/IdeaAstTypesGen.g:57:8: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SEMI"

    // $ANTLR start "DESCRIPTION"
    public final void mDESCRIPTION() throws RecognitionException {
        try {
            int _type = DESCRIPTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // grammar/IdeaAstTypesGen.g:59:13: ( '\\\"' (~ '\\\"' )* '\\\"' )
            // grammar/IdeaAstTypesGen.g:59:15: '\\\"' (~ '\\\"' )* '\\\"'
            {
            match('\"'); 

            // grammar/IdeaAstTypesGen.g:59:20: (~ '\\\"' )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0 >= '\u0000' && LA4_0 <= '!')||(LA4_0 >= '#' && LA4_0 <= '\uFFFF')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // grammar/IdeaAstTypesGen.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DESCRIPTION"

    public void mTokens() throws RecognitionException {
        // grammar/IdeaAstTypesGen.g:1:8: ( T__16 | T__17 | T__18 | T__19 | NODE_NAME | FIELD_NAME | TYPE_NAME | WS | CARAT | LPAREN | RPAREN | MANY | OPTIONAL | EXTENDS | SEMI | DESCRIPTION )
        int alt5=16;
        alt5 = dfa5.predict(input);
        switch (alt5) {
            case 1 :
                // grammar/IdeaAstTypesGen.g:1:10: T__16
                {
                mT__16(); 


                }
                break;
            case 2 :
                // grammar/IdeaAstTypesGen.g:1:16: T__17
                {
                mT__17(); 


                }
                break;
            case 3 :
                // grammar/IdeaAstTypesGen.g:1:22: T__18
                {
                mT__18(); 


                }
                break;
            case 4 :
                // grammar/IdeaAstTypesGen.g:1:28: T__19
                {
                mT__19(); 


                }
                break;
            case 5 :
                // grammar/IdeaAstTypesGen.g:1:34: NODE_NAME
                {
                mNODE_NAME(); 


                }
                break;
            case 6 :
                // grammar/IdeaAstTypesGen.g:1:44: FIELD_NAME
                {
                mFIELD_NAME(); 


                }
                break;
            case 7 :
                // grammar/IdeaAstTypesGen.g:1:55: TYPE_NAME
                {
                mTYPE_NAME(); 


                }
                break;
            case 8 :
                // grammar/IdeaAstTypesGen.g:1:65: WS
                {
                mWS(); 


                }
                break;
            case 9 :
                // grammar/IdeaAstTypesGen.g:1:68: CARAT
                {
                mCARAT(); 


                }
                break;
            case 10 :
                // grammar/IdeaAstTypesGen.g:1:74: LPAREN
                {
                mLPAREN(); 


                }
                break;
            case 11 :
                // grammar/IdeaAstTypesGen.g:1:81: RPAREN
                {
                mRPAREN(); 


                }
                break;
            case 12 :
                // grammar/IdeaAstTypesGen.g:1:88: MANY
                {
                mMANY(); 


                }
                break;
            case 13 :
                // grammar/IdeaAstTypesGen.g:1:93: OPTIONAL
                {
                mOPTIONAL(); 


                }
                break;
            case 14 :
                // grammar/IdeaAstTypesGen.g:1:102: EXTENDS
                {
                mEXTENDS(); 


                }
                break;
            case 15 :
                // grammar/IdeaAstTypesGen.g:1:110: SEMI
                {
                mSEMI(); 


                }
                break;
            case 16 :
                // grammar/IdeaAstTypesGen.g:1:115: DESCRIPTION
                {
                mDESCRIPTION(); 


                }
                break;

        }

    }


    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA5_eotS =
        "\2\uffff\3\6\1\7\14\uffff\3\6\1\7\1\uffff\13\6\1\45\1\6\1\47\1\uffff"+
        "\1\50\2\uffff";
    static final String DFA5_eofS =
        "\51\uffff";
    static final String DFA5_minS =
        "\1\11\1\uffff\1\142\1\157\1\164\1\74\14\uffff\1\163\1\157\1\162"+
        "\1\74\1\uffff\1\164\1\154\1\151\1\162\1\145\1\156\2\141\1\147\1"+
        "\143\1\156\1\101\1\164\1\101\1\uffff\1\101\2\uffff";
    static final String DFA5_maxS =
        "\1\172\1\uffff\1\142\1\157\1\164\1\172\14\uffff\1\163\1\157\1\162"+
        "\1\172\1\uffff\1\164\1\154\1\151\1\162\1\145\1\156\2\141\1\147\1"+
        "\143\1\156\1\172\1\164\1\172\1\uffff\1\172\2\uffff";
    static final String DFA5_acceptS =
        "\1\uffff\1\1\4\uffff\1\6\1\5\1\10\1\11\1\12\1\13\1\14\1\15\1\16"+
        "\1\17\1\20\1\1\4\uffff\1\7\16\uffff\1\4\1\uffff\1\3\1\2";
    static final String DFA5_specialS =
        "\51\uffff}>";
    static final String[] DFA5_transitionS = {
            "\2\10\1\uffff\2\10\22\uffff\1\10\1\uffff\1\20\5\uffff\1\12\1"+
            "\13\1\1\1\14\16\uffff\1\16\1\17\3\uffff\1\15\1\uffff\32\5\3"+
            "\uffff\1\11\1\7\1\uffff\1\2\1\3\20\6\1\4\7\6",
            "",
            "\1\22",
            "\1\23",
            "\1\24",
            "\1\26\1\uffff\1\26\2\uffff\32\25\6\uffff\32\26",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\27",
            "\1\30",
            "\1\31",
            "\1\26\1\uffff\1\26\2\uffff\32\25\6\uffff\32\26",
            "",
            "\1\32",
            "\1\33",
            "\1\34",
            "\1\35",
            "\1\36",
            "\1\37",
            "\1\40",
            "\1\41",
            "\1\42",
            "\1\43",
            "\1\44",
            "\32\6\6\uffff\32\6",
            "\1\46",
            "\32\6\6\uffff\32\6",
            "",
            "\32\6\6\uffff\32\6",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__16 | T__17 | T__18 | T__19 | NODE_NAME | FIELD_NAME | TYPE_NAME | WS | CARAT | LPAREN | RPAREN | MANY | OPTIONAL | EXTENDS | SEMI | DESCRIPTION );";
        }
    }
 

}