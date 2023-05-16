import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Parses the output produced by the lexer.
 *
 * @author Mike Murphy
 */
class Parser {
    private List<Token> source;
    private Token token;
    private int position;
    private static final int MAX_PRECEDENCE = 13;
    public static final String FILE_TO_PROCESS = "hello";

    static class Node {
        public NodeType nt;
        public Node left, right;
        public String value;

        Node() {
            this.nt = null;
            this.left = null;
            this.right = null;
            this.value = null;
        }
        Node(NodeType node_type, Node left, Node right, String value) {
            this.nt = node_type;
            this.left = left;
            this.right = right;
            this.value = value;
        }
        public static Node make_node(NodeType nodetype, Node left, Node right) {
            return new Node(nodetype, left, right, "");
        }
        public static Node make_node(NodeType nodetype, Node left) {
            return new Node(nodetype, left, null, "");
        }
        public static Node make_leaf(NodeType nodetype, String value) {
            return new Node(nodetype, null, null, value);
        }
        public String toString() {
            return nt + " L: " + left + " R: " + right;
        }
    }

    static class Token {
        public TokenType tokentype;
        public String value;
        public int line;
        public int pos;

        Token(TokenType token, String value, int line, int pos) {
            this.tokentype = token; this.value = value; this.line = line; this.pos = pos;
        }
        @Override
        public String toString() {
            return String.format("%5d  %5d %-15s %s", this.line, this.pos, this.tokentype, this.value);
        }
    }

    static enum TokenType {
        End_of_input(false, false, false, -1, NodeType.nd_None),
        Op_multiply(false, true, false, 13, NodeType.nd_Mul),
        Op_divide(false, true, false, 13, NodeType.nd_Div),
        Op_mod(false, true, false, 13, NodeType.nd_Mod),
        Op_add(false, true, false, 12, NodeType.nd_Add),
        Op_subtract(false, true, false, 12, NodeType.nd_Sub),
        Op_negate(false, false, true, 14, NodeType.nd_Negate),
        Op_not(false, false, true, 14, NodeType.nd_Not),
        Op_less(false, true, false, 10, NodeType.nd_Lss),
        Op_lessequal(false, true, false, 10, NodeType.nd_Leq),
        Op_greater(false, true, false, 10, NodeType.nd_Gtr),
        Op_greaterequal(false, true, false, 10, NodeType.nd_Geq),
        Op_equal(false, true, true, 9, NodeType.nd_Eql),
        Op_notequal(false, true, false, 9, NodeType.nd_Neq),
        Op_assign(false, false, false, -1, NodeType.nd_Assign),
        Op_and(false, true, false, 5, NodeType.nd_And),
        Op_or(false, true, false, 4, NodeType.nd_Or),
        Keyword_if(false, false, false, -1, NodeType.nd_If),
        Keyword_else(false, false, false, -1, NodeType.nd_None),
        Keyword_while(false, false, false, -1, NodeType.nd_While),
        Keyword_print(false, false, false, -1, NodeType.nd_None),
        Keyword_putc(false, false, false, -1, NodeType.nd_None),
        LeftParen(false, false, false, -1, NodeType.nd_None),
        RightParen(false, false, false, -1, NodeType.nd_None),
        LeftBrace(false, false, false, -1, NodeType.nd_None),
        RightBrace(false, false, false, -1, NodeType.nd_None),
        Semicolon(false, false, false, -1, NodeType.nd_None),
        Comma(false, false, false, -1, NodeType.nd_None),
        Identifier(false, false, false, -1, NodeType.nd_Ident),
        Integer(false, false, false, -1, NodeType.nd_Integer),
        String(false, false, false, -1, NodeType.nd_String);

        private final int precedence;
        private final boolean right_assoc;
        private final boolean is_binary;
        private final boolean is_unary;
        private final NodeType node_type;

        TokenType(boolean right_assoc, boolean is_binary, boolean is_unary, int precedence, NodeType node) {
            this.right_assoc = right_assoc;
            this.is_binary = is_binary;
            this.is_unary = is_unary;
            this.precedence = precedence;
            this.node_type = node;
        }
        boolean isRightAssoc() { return this.right_assoc; }
        boolean isBinary() { return this.is_binary; }
        boolean isUnary() { return this.is_unary; }
        int getPrecedence() { return this.precedence; }
        NodeType getNodeType() { return this.node_type; }
    }
    static enum NodeType {
        nd_None(""), nd_Ident("Identifier"), nd_String("String"), nd_Integer("Integer"), nd_Sequence("Sequence"), nd_If("If"),
        nd_Prtc("Prtc"), nd_Prts("Prts"), nd_Prti("Prti"), nd_While("While"),
        nd_Assign("Assign"), nd_Negate("Negate"), nd_Not("Not"), nd_Mul("Multiply"), nd_Div("Divide"), nd_Mod("Mod"), nd_Add("Add"),
        nd_Sub("Subtract"), nd_Lss("Less"), nd_Leq("LessEqual"),
        nd_Gtr("Greater"), nd_Geq("GreaterEqual"), nd_Eql("Equal"), nd_Neq("NotEqual"), nd_And("And"), nd_Or("Or");

        private final String name;

        NodeType(String name) {
            this.name = name;
        }

        @Override
        public String toString() { return this.name; }
    }

    /**
     * Throws an error if the encountered syntax does not match the specified grammar.
     *
     * @param line is the line number of the source file where the error occurred
     * @param pos is the position on the line where the error occurred
     * @param msg is the message to pass on to the user about the incorrect item
     */
    static void error(int line, int pos, String msg) {
        if (line > 0 && pos > 0) {
            System.out.printf("%s in line %d, pos %d\n", msg, line, pos);
        } else {
            System.out.println(msg);
        }
        System.exit(1);
    }
    Parser(List<Token> source) {
        this.source = source;
        this.token = null;
        this.position = 0;
    }

    /**
     * Consume the next token in the stream
     *
     * @return is the next token
     */
    Token getNextToken() {
        this.token = this.source.get(this.position++);
        return this.token;
    }

    /**
     * Parse an expression item in the grammar. Uses precedence climbing to associate terms according to the precedence
     * order specified by the grammar of this language.
     *
     * @param p is the current maximum precedence level of tokens encountered thus far in the expression
     * @return is a Node binary tree object representing this expression.
     */
    Node expr(int p) {
        // create nodes for token types such as LeftParen, Op_add, Op_subtract, etc.
        // be very careful here and be aware of the precedence rules for the AST tree
        Node result = null, node, t, t1;
        Token b = null;
        int r = MAX_PRECEDENCE;

        t = primary();
        getNextToken();

        while ((token.tokentype.isBinary() || token.tokentype.isUnary()) && (token.tokentype.getPrecedence() > p && token.tokentype.getPrecedence() <= r )) {
            b = token;
            getNextToken();
            if (b.tokentype.is_binary) {
                t1 = expr(b.tokentype.getPrecedence());
                t = Node.make_node(b.tokentype.getNodeType(), t, t1);
            } else {
                t = primary();
            }
            r = b.tokentype.getPrecedence();
        }

        return t;
    }

    /**
     * Generates Node objects for a primary expression (identifier, literal, or either of these combined with a unary
     * operator.
     *
     * @return is the binary tree Node representing this primary.
     */
    Node primary() {
        Node node = null;
        switch (token.tokentype) {
            case Identifier:
                node = Node.make_leaf(NodeType.nd_Ident, token.value);
                break;
            case Integer:
                node = Node.make_leaf(NodeType.nd_Integer, token.value);
                break;
            case LeftParen:
                node = paren_expr();
                break;
            case Op_negate:
                getNextToken();
                node = Node.make_node(NodeType.nd_Negate, primary());
                break;
            case Op_not:
                getNextToken();
                node = Node.make_node(NodeType.nd_Not, primary());
                break;
        }
        if (node == null) {
            error(this.token.line, this.token.pos, "primary" + ": Expecting '" + "primary expression" + "', found: '" + this.token.tokentype + "'");
        }
        return node;
    }

    /**
     * Parses a parenthetical expression.
     *
     * @return is a Node binary tree object representing the expression inside the parentheses.
     */
    Node paren_expr() {
        expect("paren_expr", TokenType.LeftParen);
        Node node = expr(0);
        expect("paren_expr", TokenType.RightParen);
        return node;
    }

    /**
     * Checks to see whether the next token in the stream is of the expected type when certain tokens are required
     * by the grammar.
     *
     * @param msg is a user-friendly string descriptor of the expected token.
     * @param s is the token type from the token enum.
     */
    void expect(String msg, TokenType s) {
        if (this.token.tokentype == s) {
            getNextToken();
            return;
        }
        error(this.token.line, this.token.pos, msg + ": Expecting '" + s + "', found: '" + this.token.tokentype + "'");
    }

    /**
     * Parses a complete statement.
     *
     * @return is a Node binary tree object that represents the statement.
     */
    Node stmt() {
        // this one handles TokenTypes such as Keyword_if, Keyword_else, nd_If, Keyword_print, etc.
        // also handles while, end of file, braces
        Node s, s2, t = null, e, v;

        switch(token.tokentype) {
            case Semicolon:
                getNextToken();
                break;
            case Identifier:
                v = Node.make_leaf(NodeType.nd_Ident, token.value);
                getNextToken();
                expect("=", TokenType.Op_assign);
                e = expr(0);
                expect(";", TokenType.Semicolon);
                t = Node.make_node(NodeType.nd_Assign, v, e);
                break;
            case Keyword_while:
                getNextToken();
                v = paren_expr();
                e = stmt();
                t = Node.make_node(NodeType.nd_While, v, e);
                //getNextToken();
                break;
            case Keyword_if:
                getNextToken();
                v = paren_expr();
                e = stmt();
                getNextToken();
                s2 = null;
                if (token.tokentype == TokenType.Keyword_else) {
                    s2 = Node.make_node(NodeType.nd_Sequence, stmt());
                }
                s = Node.make_node(NodeType.nd_Sequence, e, s2);
                t = Node.make_node(NodeType.nd_If, s, stmt());
                break;
            case Keyword_print:
                getNextToken();
                expect("(", TokenType.LeftParen);
                t = prt_list();
                expect(")", TokenType.RightParen);
                //t = Node.make_node(NodeType.nd_Sequence, v);
                break;
            case Keyword_putc:
                getNextToken();
                v = paren_expr();
                expect(";", TokenType.Semicolon);
                t = Node.make_node(NodeType.nd_Prtc,v);
                getNextToken();
                break;
            case LeftBrace:
                getNextToken();
                t = stmt_list();
                //v = stmt_list();
                //t = Node.make_node(NodeType.nd_Sequence, v);
                //getNextToken();
                break;


        }

        return t;
    }

    /**
     * Parses a comma-delimited list of items to be printed by a print command.
     *
     * @return is a Node binary tree object representing the sequence of items to be printed.
     */
    Node prt_list() {
        Node node = null, v, t = null;

        while (token.tokentype != TokenType.RightParen) {
            if (token.tokentype == TokenType.String) {
                v = Node.make_leaf(NodeType.nd_String, token.value);
                t = Node.make_node(NodeType.nd_Prts, v);
                getNextToken();
            } else {
                //getNextToken();
                t = Node.make_node(NodeType.nd_Prti, expr(0));
            }
            if (token.tokentype == TokenType.Comma) {
                getNextToken();
                node = Node.make_node(NodeType.nd_Sequence, node, t);
            }
        }

        return (node==null) ? t : node;
    }

    /**
     * Parses a list of statements inside of a code block.
     *
     * @return is a Node binary tree object representing the sequence of statements.
     */
    Node stmt_list() {
        Node node = null;
        Token b;

        b = token;
        while (b.tokentype != TokenType.RightBrace) {
            //getNextToken();
            node = Node.make_node(NodeType.nd_Sequence, node, stmt());
            b = token;
            getNextToken();
        }

        return node;
    }

    /**
     * Starts the parsing process at the top of the lexer document.
     *
     * @return is the root Node binary tree object representing the whole parsed document.
     */
    Node parse() {
        Node t = null;
        getNextToken();
        while (this.token.tokentype != TokenType.End_of_input) {
            t = Node.make_node(NodeType.nd_Sequence, t, stmt());
        }
        return t;
    }

    /**
     * Prints out a flattened tree representing the output of the parsing process.
     *
     * @param t is the root node of the binary tree to be output.
     * @param sb is a StringBuilder object that will be used to construct the flattened output string.
     * @return is the string representation of the binary tree.
     */
    String printAST(Node t, StringBuilder sb) {
        int i = 0;
        if (t == null) {
            sb.append(";");
            sb.append("\n");
            System.out.println(";");
        } else {
            sb.append(t.nt);
            System.out.printf("%-14s", t.nt);
            if (t.nt == NodeType.nd_Ident || t.nt == NodeType.nd_Integer || t.nt == NodeType.nd_String) {
                sb.append(" " + t.value);
                sb.append("\n");
                System.out.println(" " + t.value);
            } else {
                sb.append("\n");
                System.out.println();
                printAST(t.left, sb);
                printAST(t.right, sb);
            }

        }
        return sb.toString();
    }

    /**
     * Writes the flattened output to a file.
     *
     * @param result is the string representation to output.
     */
    static void outputToFile(String result) {
        try {
            FileWriter myWriter = new FileWriter("src/main/resources/" + FILE_TO_PROCESS + ".par");
            myWriter.write(result);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        if (1==1) {
            try {
                String value, token;
                String result = " ";
                StringBuilder sb = new StringBuilder();
                int line, pos;
                Token t;
                boolean found;
                List<Token> list = new ArrayList<>();
                Map<String, TokenType> str_to_tokens = new HashMap<>();


                str_to_tokens.put("End_of_input", TokenType.End_of_input);
                // finish creating your Hashmap. I left one as a model
                str_to_tokens.put("Op_multiply", TokenType.Op_multiply);
                str_to_tokens.put("Op_divide", TokenType.Op_divide);
                str_to_tokens.put("Op_mod", TokenType.Op_mod);
                str_to_tokens.put("Op_add", TokenType.Op_add);
                str_to_tokens.put("Op_subtract", TokenType.Op_subtract);
                str_to_tokens.put("Op_negate", TokenType.Op_negate);
                str_to_tokens.put("Op_not", TokenType.Op_not);
                str_to_tokens.put("Op_less", TokenType.Op_less);
                str_to_tokens.put("Op_lessequal", TokenType.Op_lessequal);
                str_to_tokens.put("Op_greater", TokenType.Op_greater);
                str_to_tokens.put("Op_greaterequal", TokenType.Op_greaterequal);
                str_to_tokens.put("Op_equal", TokenType.Op_equal);
                str_to_tokens.put("Op_notequal", TokenType.Op_notequal);
                str_to_tokens.put("Op_assign", TokenType.Op_assign);
                str_to_tokens.put("Op_and", TokenType.Op_and);
                str_to_tokens.put("Op_or", TokenType.Op_or);
                str_to_tokens.put("Keyword_if", TokenType.Keyword_if);
                str_to_tokens.put("Keyword_else", TokenType.Keyword_else);
                str_to_tokens.put("Keyword_while", TokenType.Keyword_while);
                str_to_tokens.put("Keyword_print", TokenType.Keyword_print);
                str_to_tokens.put("Keyword_putc", TokenType.Keyword_putc);
                str_to_tokens.put("LeftParen", TokenType.LeftParen);
                str_to_tokens.put("RightParen", TokenType.RightParen);
                str_to_tokens.put("LeftBrace", TokenType.LeftBrace);
                str_to_tokens.put("RightBrace", TokenType.RightBrace);
                str_to_tokens.put("Semicolon", TokenType.Semicolon);
                str_to_tokens.put("Comma", TokenType.Comma);
                str_to_tokens.put("Identifier", TokenType.Identifier);
                str_to_tokens.put("Integer", TokenType.Integer);
                str_to_tokens.put("String", TokenType.String);

                Scanner s = new Scanner(new File("src/main/resources/" + FILE_TO_PROCESS + ".lex"));
                String source = " ";
                while (s.hasNext()) {
                    String str = s.nextLine();
                    StringTokenizer st = new StringTokenizer(str);
                    line = Integer.parseInt(st.nextToken());
                    pos = Integer.parseInt(st.nextToken());
                    token = st.nextToken();
                    value = "";
                    while (st.hasMoreTokens()) {
                        value += st.nextToken() + " ";
                    }
                    found = false;
                    if (str_to_tokens.containsKey(token)) {
                        found = true;
                        list.add(new Token(str_to_tokens.get(token), value, line, pos));
                    }
                    if (found == false) {
                        throw new Exception("Token not found: '" + token + "'");
                    }
                }
                Parser p = new Parser(list);
                result = p.printAST(p.parse(), sb);
                outputToFile(result);
            } catch (FileNotFoundException e) {
                error(-1, -1, "Exception: " + e.getMessage());
            } catch (Exception e) {
                error(-1, -1, "Exception: " + e.getMessage());
            }
        } else {
            error(-1, -1, "No args");
        }
    }
}