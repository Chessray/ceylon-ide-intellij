// $ANTLR 3.4 grammar/PsiFactoryGen.g 2017-01-23 17:38:51
 
    package com.redhat.ceylon.compiler.typechecker.treegen; 
    import static com.redhat.ceylon.compiler.typechecker.treegen.Util.*; 


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class PsiFactoryGenParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "CARAT", "DESCRIPTION", "EXTENDS", "FIELD_NAME", "LPAREN", "MANY", "NODE_NAME", "OPTIONAL", "RPAREN", "SEMI", "TYPE_NAME", "WS", "'*'", "'abstract'", "'boolean'", "'string'"
    };

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
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public PsiFactoryGenParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public PsiFactoryGenParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return PsiFactoryGenParser.tokenNames; }
    public String getGrammarFileName() { return "grammar/PsiFactoryGen.g"; }



    // $ANTLR start "nodeList"
    // grammar/PsiFactoryGen.g:11:1: nodeList : ( ( DESCRIPTION )? node )+ EOF ;
    public final void nodeList() throws RecognitionException {
        try {
            // grammar/PsiFactoryGen.g:11:10: ( ( ( DESCRIPTION )? node )+ EOF )
            // grammar/PsiFactoryGen.g:11:12: ( ( DESCRIPTION )? node )+ EOF
            {
             
                        println("package org.intellij.plugins.ceylon.ide.psi;\n");
                        println("import com.intellij.psi.tree.IElementType;");
                        println("import com.intellij.psi.PsiElement;");
                        println("import com.intellij.lang.ASTNode;");
                        println("import org.intellij.plugins.ceylon.ide.psi.impl.*;\n");
                        println("import org.intellij.plugins.ceylon.ide.psi.CeylonPsiImpl.*;\n");
                        println("import static org.intellij.plugins.ceylon.ide.psi.CeylonTypes.*;\n");
                        println("/* Generated using Antlr by PsiFactoryGen.g */");
                        println("public class CeylonPsiFactory {");
                        println("");
                        println("    public static PsiElement createElement(ASTNode node) {");
                        println("        IElementType type = node.getElementType();");
                        println("        if (false) {");
                       

            // grammar/PsiFactoryGen.g:26:12: ( ( DESCRIPTION )? node )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= CARAT && LA2_0 <= DESCRIPTION)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // grammar/PsiFactoryGen.g:26:13: ( DESCRIPTION )? node
            	    {
            	    // grammar/PsiFactoryGen.g:26:13: ( DESCRIPTION )?
            	    int alt1=2;
            	    int LA1_0 = input.LA(1);

            	    if ( (LA1_0==DESCRIPTION) ) {
            	        alt1=1;
            	    }
            	    switch (alt1) {
            	        case 1 :
            	            // grammar/PsiFactoryGen.g:26:13: DESCRIPTION
            	            {
            	            match(input,DESCRIPTION,FOLLOW_DESCRIPTION_in_nodeList41); 

            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_node_in_nodeList44);
            	    node();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            match(input,EOF,FOLLOW_EOF_in_nodeList60); 


                        println("        }");
                        println("");
                        println("        return new CeylonCompositeElementImpl(node);");
                        println("    }");
                        println("}");
                       

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "nodeList"



    // $ANTLR start "node"
    // grammar/PsiFactoryGen.g:37:1: node : '^' '(' ( 'abstract' NODE_NAME |n= NODE_NAME ) ( ':' en= NODE_NAME )? ( ( DESCRIPTION )? subnode )* ( ( DESCRIPTION )? field )* ')' ;
    public final void node() throws RecognitionException {
        Token n=null;
        Token en=null;

        try {
            // grammar/PsiFactoryGen.g:37:6: ( '^' '(' ( 'abstract' NODE_NAME |n= NODE_NAME ) ( ':' en= NODE_NAME )? ( ( DESCRIPTION )? subnode )* ( ( DESCRIPTION )? field )* ')' )
            // grammar/PsiFactoryGen.g:37:8: '^' '(' ( 'abstract' NODE_NAME |n= NODE_NAME ) ( ':' en= NODE_NAME )? ( ( DESCRIPTION )? subnode )* ( ( DESCRIPTION )? field )* ')'
            {
            match(input,CARAT,FOLLOW_CARAT_in_node93); 

            match(input,LPAREN,FOLLOW_LPAREN_in_node95); 

            // grammar/PsiFactoryGen.g:38:8: ( 'abstract' NODE_NAME |n= NODE_NAME )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==17) ) {
                alt3=1;
            }
            else if ( (LA3_0==NODE_NAME) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }
            switch (alt3) {
                case 1 :
                    // grammar/PsiFactoryGen.g:39:10: 'abstract' NODE_NAME
                    {
                    match(input,17,FOLLOW_17_in_node115); 

                    match(input,NODE_NAME,FOLLOW_NODE_NAME_in_node117); 

                    }
                    break;
                case 2 :
                    // grammar/PsiFactoryGen.g:40:12: n= NODE_NAME
                    {
                    n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_node132); 


                              println("        } else if (type == " + (n!=null?n.getText():null) + ") {");
                              println("            return new " + className((n!=null?n.getText():null)) + "PsiImpl(node);");
                             

                    }
                    break;

            }


            // grammar/PsiFactoryGen.g:46:8: ( ':' en= NODE_NAME )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==EXTENDS) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // grammar/PsiFactoryGen.g:46:9: ':' en= NODE_NAME
                    {
                    match(input,EXTENDS,FOLLOW_EXTENDS_in_node162); 

                    en=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_node166); 

                    }
                    break;

            }


            // grammar/PsiFactoryGen.g:47:8: ( ( DESCRIPTION )? subnode )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==DESCRIPTION) ) {
                    int LA6_1 = input.LA(2);

                    if ( (LA6_1==NODE_NAME) ) {
                        alt6=1;
                    }


                }
                else if ( (LA6_0==NODE_NAME) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // grammar/PsiFactoryGen.g:47:9: ( DESCRIPTION )? subnode
            	    {
            	    // grammar/PsiFactoryGen.g:47:9: ( DESCRIPTION )?
            	    int alt5=2;
            	    int LA5_0 = input.LA(1);

            	    if ( (LA5_0==DESCRIPTION) ) {
            	        alt5=1;
            	    }
            	    switch (alt5) {
            	        case 1 :
            	            // grammar/PsiFactoryGen.g:47:9: DESCRIPTION
            	            {
            	            match(input,DESCRIPTION,FOLLOW_DESCRIPTION_in_node178); 

            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_subnode_in_node181);
            	    subnode();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            // grammar/PsiFactoryGen.g:48:8: ( ( DESCRIPTION )? field )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==DESCRIPTION||LA8_0==TYPE_NAME||(LA8_0 >= 17 && LA8_0 <= 19)) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // grammar/PsiFactoryGen.g:48:9: ( DESCRIPTION )? field
            	    {
            	    // grammar/PsiFactoryGen.g:48:9: ( DESCRIPTION )?
            	    int alt7=2;
            	    int LA7_0 = input.LA(1);

            	    if ( (LA7_0==DESCRIPTION) ) {
            	        alt7=1;
            	    }
            	    switch (alt7) {
            	        case 1 :
            	            // grammar/PsiFactoryGen.g:48:9: DESCRIPTION
            	            {
            	            match(input,DESCRIPTION,FOLLOW_DESCRIPTION_in_node193); 

            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_field_in_node196);
            	    field();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            match(input,RPAREN,FOLLOW_RPAREN_in_node207); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "node"



    // $ANTLR start "subnode"
    // grammar/PsiFactoryGen.g:52:1: subnode : (n= NODE_NAME ( '?' )? (f= FIELD_NAME )? |mn= NODE_NAME '*' (f= FIELD_NAME )? );
    public final void subnode() throws RecognitionException {
        Token n=null;
        Token f=null;
        Token mn=null;

        try {
            // grammar/PsiFactoryGen.g:52:9: (n= NODE_NAME ( '?' )? (f= FIELD_NAME )? |mn= NODE_NAME '*' (f= FIELD_NAME )? )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==NODE_NAME) ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==16) ) {
                    alt12=2;
                }
                else if ( (LA12_1==DESCRIPTION||LA12_1==FIELD_NAME||(LA12_1 >= NODE_NAME && LA12_1 <= RPAREN)||LA12_1==TYPE_NAME||(LA12_1 >= 17 && LA12_1 <= 19)) ) {
                    alt12=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }
            switch (alt12) {
                case 1 :
                    // grammar/PsiFactoryGen.g:52:11: n= NODE_NAME ( '?' )? (f= FIELD_NAME )?
                    {
                    n=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode223); 

                    // grammar/PsiFactoryGen.g:52:23: ( '?' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==OPTIONAL) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // grammar/PsiFactoryGen.g:52:23: '?'
                            {
                            match(input,OPTIONAL,FOLLOW_OPTIONAL_in_subnode225); 

                            }
                            break;

                    }


                    // grammar/PsiFactoryGen.g:52:29: (f= FIELD_NAME )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==FIELD_NAME) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // grammar/PsiFactoryGen.g:52:29: f= FIELD_NAME
                            {
                            f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_subnode230); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // grammar/PsiFactoryGen.g:53:11: mn= NODE_NAME '*' (f= FIELD_NAME )?
                    {
                    mn=(Token)match(input,NODE_NAME,FOLLOW_NODE_NAME_in_subnode246); 

                    match(input,16,FOLLOW_16_in_subnode248); 

                    // grammar/PsiFactoryGen.g:53:29: (f= FIELD_NAME )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==FIELD_NAME) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // grammar/PsiFactoryGen.g:53:29: f= FIELD_NAME
                            {
                            f=(Token)match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_subnode252); 

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "subnode"



    // $ANTLR start "field"
    // grammar/PsiFactoryGen.g:56:1: field : ( 'abstract' )? ( TYPE_NAME | 'boolean' | 'string' ) FIELD_NAME ';' ;
    public final void field() throws RecognitionException {
        try {
            // grammar/PsiFactoryGen.g:56:7: ( ( 'abstract' )? ( TYPE_NAME | 'boolean' | 'string' ) FIELD_NAME ';' )
            // grammar/PsiFactoryGen.g:56:9: ( 'abstract' )? ( TYPE_NAME | 'boolean' | 'string' ) FIELD_NAME ';'
            {
            // grammar/PsiFactoryGen.g:56:9: ( 'abstract' )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==17) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // grammar/PsiFactoryGen.g:56:9: 'abstract'
                    {
                    match(input,17,FOLLOW_17_in_field270); 

                    }
                    break;

            }


            if ( input.LA(1)==TYPE_NAME||(input.LA(1) >= 18 && input.LA(1) <= 19) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            match(input,FIELD_NAME,FOLLOW_FIELD_NAME_in_field281); 

            match(input,SEMI,FOLLOW_SEMI_in_field283); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "field"

    // Delegated rules


 

    public static final BitSet FOLLOW_DESCRIPTION_in_nodeList41 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_node_in_nodeList44 = new BitSet(new long[]{0x0000000000000030L});
    public static final BitSet FOLLOW_EOF_in_nodeList60 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARAT_in_node93 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_LPAREN_in_node95 = new BitSet(new long[]{0x0000000000020400L});
    public static final BitSet FOLLOW_17_in_node115 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_node117 = new BitSet(new long[]{0x00000000000E5460L});
    public static final BitSet FOLLOW_NODE_NAME_in_node132 = new BitSet(new long[]{0x00000000000E5460L});
    public static final BitSet FOLLOW_EXTENDS_in_node162 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NODE_NAME_in_node166 = new BitSet(new long[]{0x00000000000E5420L});
    public static final BitSet FOLLOW_DESCRIPTION_in_node178 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_subnode_in_node181 = new BitSet(new long[]{0x00000000000E5420L});
    public static final BitSet FOLLOW_DESCRIPTION_in_node193 = new BitSet(new long[]{0x00000000000E4000L});
    public static final BitSet FOLLOW_field_in_node196 = new BitSet(new long[]{0x00000000000E5020L});
    public static final BitSet FOLLOW_RPAREN_in_node207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode223 = new BitSet(new long[]{0x0000000000000882L});
    public static final BitSet FOLLOW_OPTIONAL_in_subnode225 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_FIELD_NAME_in_subnode230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NODE_NAME_in_subnode246 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_subnode248 = new BitSet(new long[]{0x0000000000000082L});
    public static final BitSet FOLLOW_FIELD_NAME_in_subnode252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_field270 = new BitSet(new long[]{0x00000000000C4000L});
    public static final BitSet FOLLOW_set_in_field273 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FIELD_NAME_in_field281 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_SEMI_in_field283 = new BitSet(new long[]{0x0000000000000002L});

}