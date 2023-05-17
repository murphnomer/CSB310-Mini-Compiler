import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test
    void ParserTest1 () {
        if (1==1) {
            try {
                String value, token;
                String result = " ";
                StringBuilder sb = new StringBuilder();
                int line, pos;
                Parser.Token t;
                boolean found;
                List<Parser.Token> list = new ArrayList<>();
                Map<String, Parser.TokenType> str_to_tokens = new HashMap<>();


                str_to_tokens.put("End_of_input", Parser.TokenType.End_of_input);
                // finish creating your Hashmap. I left one as a model
                str_to_tokens.put("Op_multiply", Parser.TokenType.Op_multiply);
                str_to_tokens.put("Op_divide", Parser.TokenType.Op_divide);
                str_to_tokens.put("Op_mod", Parser.TokenType.Op_mod);
                str_to_tokens.put("Op_add", Parser.TokenType.Op_add);
                str_to_tokens.put("Op_subtract", Parser.TokenType.Op_subtract);
                str_to_tokens.put("Op_negate", Parser.TokenType.Op_negate);
                str_to_tokens.put("Op_not", Parser.TokenType.Op_not);
                str_to_tokens.put("Op_less", Parser.TokenType.Op_less);
                str_to_tokens.put("Op_lessequal", Parser.TokenType.Op_lessequal);
                str_to_tokens.put("Op_greater", Parser.TokenType.Op_greater);
                str_to_tokens.put("Op_greaterequal", Parser.TokenType.Op_greaterequal);
                str_to_tokens.put("Op_equal", Parser.TokenType.Op_equal);
                str_to_tokens.put("Op_notequal", Parser.TokenType.Op_notequal);
                str_to_tokens.put("Op_assign", Parser.TokenType.Op_assign);
                str_to_tokens.put("Op_and", Parser.TokenType.Op_and);
                str_to_tokens.put("Op_or", Parser.TokenType.Op_or);
                str_to_tokens.put("Keyword_if", Parser.TokenType.Keyword_if);
                str_to_tokens.put("Keyword_else", Parser.TokenType.Keyword_else);
                str_to_tokens.put("Keyword_while", Parser.TokenType.Keyword_while);
                str_to_tokens.put("Keyword_print", Parser.TokenType.Keyword_print);
                str_to_tokens.put("Keyword_putc", Parser.TokenType.Keyword_putc);
                str_to_tokens.put("LeftParen", Parser.TokenType.LeftParen);
                str_to_tokens.put("RightParen", Parser.TokenType.RightParen);
                str_to_tokens.put("LeftBrace", Parser.TokenType.LeftBrace);
                str_to_tokens.put("RightBrace", Parser.TokenType.RightBrace);
                str_to_tokens.put("Semicolon", Parser.TokenType.Semicolon);
                str_to_tokens.put("Comma", Parser.TokenType.Comma);
                str_to_tokens.put("Identifier", Parser.TokenType.Identifier);
                str_to_tokens.put("Integer", Parser.TokenType.Integer);
                str_to_tokens.put("String", Parser.TokenType.String);

                Scanner s = new Scanner(new File("src/main/resources/test.lex"));
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
                        list.add(new Parser.Token(str_to_tokens.get(token), value, line, pos));
                    }
                    if (found == false) {
                        throw new Exception("Token not found: '" + token + "'");
                    }
                }
                Parser p = new Parser(list);
                result = p.printAST(p.parse(), sb);
                assertEquals(result,"Sequence\n;\nAssign\nIdentifier count \nInteger 1 \n");
            } catch (FileNotFoundException e) {
                System.out.println("Exception: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        } else {
            System.out.println("No args");
        }
    }

    @Test
    void ParserTest2() {
        String msg = "";
        if (1==1) {
            try {
                String value, token;
                String result = " ";
                StringBuilder sb = new StringBuilder();
                int line, pos;
                Parser.Token t;
                boolean found;
                List<Parser.Token> list = new ArrayList<>();
                Map<String, Parser.TokenType> str_to_tokens = new HashMap<>();


                str_to_tokens.put("End_of_input", Parser.TokenType.End_of_input);
                // finish creating your Hashmap. I left one as a model
                str_to_tokens.put("Op_multiply", Parser.TokenType.Op_multiply);
                str_to_tokens.put("Op_divide", Parser.TokenType.Op_divide);
                str_to_tokens.put("Op_mod", Parser.TokenType.Op_mod);
                str_to_tokens.put("Op_add", Parser.TokenType.Op_add);
                str_to_tokens.put("Op_subtract", Parser.TokenType.Op_subtract);
                str_to_tokens.put("Op_negate", Parser.TokenType.Op_negate);
                str_to_tokens.put("Op_not", Parser.TokenType.Op_not);
                str_to_tokens.put("Op_less", Parser.TokenType.Op_less);
                str_to_tokens.put("Op_lessequal", Parser.TokenType.Op_lessequal);
                str_to_tokens.put("Op_greater", Parser.TokenType.Op_greater);
                str_to_tokens.put("Op_greaterequal", Parser.TokenType.Op_greaterequal);
                str_to_tokens.put("Op_equal", Parser.TokenType.Op_equal);
                str_to_tokens.put("Op_notequal", Parser.TokenType.Op_notequal);
                str_to_tokens.put("Op_assign", Parser.TokenType.Op_assign);
                str_to_tokens.put("Op_and", Parser.TokenType.Op_and);
                str_to_tokens.put("Op_or", Parser.TokenType.Op_or);
                str_to_tokens.put("Keyword_if", Parser.TokenType.Keyword_if);
                str_to_tokens.put("Keyword_else", Parser.TokenType.Keyword_else);
                str_to_tokens.put("Keyword_while", Parser.TokenType.Keyword_while);
                str_to_tokens.put("Keyword_print", Parser.TokenType.Keyword_print);
                str_to_tokens.put("Keyword_putc", Parser.TokenType.Keyword_putc);
                str_to_tokens.put("LeftParen", Parser.TokenType.LeftParen);
                str_to_tokens.put("RightParen", Parser.TokenType.RightParen);
                str_to_tokens.put("LeftBrace", Parser.TokenType.LeftBrace);
                str_to_tokens.put("RightBrace", Parser.TokenType.RightBrace);
                str_to_tokens.put("Semicolon", Parser.TokenType.Semicolon);
                str_to_tokens.put("Comma", Parser.TokenType.Comma);
                str_to_tokens.put("Identifier", Parser.TokenType.Identifier);
                str_to_tokens.put("Integer", Parser.TokenType.Integer);
                str_to_tokens.put("String", Parser.TokenType.String);

                Scanner s = new Scanner(new File("src/main/resources/this_file_does_not_exist.lex"));
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
                        list.add(new Parser.Token(str_to_tokens.get(token), value, line, pos));
                    }
                    if (found == false) {
                        throw new Exception("Token not found: '" + token + "'");
                    }
                }
                Parser p = new Parser(list);
                result = p.printAST(p.parse(), sb);
            } catch (FileNotFoundException e) {
                msg = "Exception: " + e.getMessage();
                System.out.println("Exception: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        } else {
            System.out.println("No args");
        }
        assertEquals(msg, "Exception: src\\main\\resources\\this_file_does_not_exist.lex (The system cannot find the file specified)");
    }

    @Test
    void ParserTest3() {
        if (1==1) {
            try {
                String value, token;
                String result = " ";
                StringBuilder sb = new StringBuilder();
                int line, pos;
                Parser.Token t;
                boolean found;
                List<Parser.Token> list = new ArrayList<>();
                Map<String, Parser.TokenType> str_to_tokens = new HashMap<>();


                str_to_tokens.put("End_of_input", Parser.TokenType.End_of_input);
                // finish creating your Hashmap. I left one as a model
                str_to_tokens.put("Op_multiply", Parser.TokenType.Op_multiply);
                str_to_tokens.put("Op_divide", Parser.TokenType.Op_divide);
                str_to_tokens.put("Op_mod", Parser.TokenType.Op_mod);
                str_to_tokens.put("Op_add", Parser.TokenType.Op_add);
                str_to_tokens.put("Op_subtract", Parser.TokenType.Op_subtract);
                str_to_tokens.put("Op_negate", Parser.TokenType.Op_negate);
                str_to_tokens.put("Op_not", Parser.TokenType.Op_not);
                str_to_tokens.put("Op_less", Parser.TokenType.Op_less);
                str_to_tokens.put("Op_lessequal", Parser.TokenType.Op_lessequal);
                str_to_tokens.put("Op_greater", Parser.TokenType.Op_greater);
                str_to_tokens.put("Op_greaterequal", Parser.TokenType.Op_greaterequal);
                str_to_tokens.put("Op_equal", Parser.TokenType.Op_equal);
                str_to_tokens.put("Op_notequal", Parser.TokenType.Op_notequal);
                str_to_tokens.put("Op_assign", Parser.TokenType.Op_assign);
                str_to_tokens.put("Op_and", Parser.TokenType.Op_and);
                str_to_tokens.put("Op_or", Parser.TokenType.Op_or);
                str_to_tokens.put("Keyword_if", Parser.TokenType.Keyword_if);
                str_to_tokens.put("Keyword_else", Parser.TokenType.Keyword_else);
                str_to_tokens.put("Keyword_while", Parser.TokenType.Keyword_while);
                str_to_tokens.put("Keyword_print", Parser.TokenType.Keyword_print);
                str_to_tokens.put("Keyword_putc", Parser.TokenType.Keyword_putc);
                str_to_tokens.put("LeftParen", Parser.TokenType.LeftParen);
                str_to_tokens.put("RightParen", Parser.TokenType.RightParen);
                str_to_tokens.put("LeftBrace", Parser.TokenType.LeftBrace);
                str_to_tokens.put("RightBrace", Parser.TokenType.RightBrace);
                str_to_tokens.put("Semicolon", Parser.TokenType.Semicolon);
                str_to_tokens.put("Comma", Parser.TokenType.Comma);
                str_to_tokens.put("Identifier", Parser.TokenType.Identifier);
                str_to_tokens.put("Integer", Parser.TokenType.Integer);
                str_to_tokens.put("String", Parser.TokenType.String);

                Scanner s = new Scanner(new File("src/main/resources/test.lex"));
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
                        list.add(new Parser.Token(str_to_tokens.get(token), value, line, pos));
                    }
                    if (found == false) {
                        throw new Exception("Token not found: '" + token + "'");
                    }
                }
                Parser p = new Parser(list);
                result = p.printAST(p.parse(), sb);
                assertEquals(result,"Sequence\n;\nAssign\nIdentifier count \nInteger 1 \n");
            } catch (FileNotFoundException e) {
                System.out.println("Exception: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        } else {
            System.out.println("No args");
        }
    }

}