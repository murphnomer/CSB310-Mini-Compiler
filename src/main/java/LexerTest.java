import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
    @Test
    void LexerTest1() {
        Lexer myLexer = new Lexer("count");
        assertDoesNotThrow(()-> myLexer.char_lit(1, 1));
    }

    @Test
    void LexerTest2() {
        Lexer myLexer = new Lexer("print");
        assertDoesNotThrow(myLexer::printTokens);
    }

    @Test
    void LexerTest3() {
        Lexer myLexer = new Lexer("Buzz");
        assertDoesNotThrow(myLexer::getToken);
    }

}