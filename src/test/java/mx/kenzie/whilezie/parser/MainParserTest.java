package mx.kenzie.whilezie.parser;

import mx.kenzie.whilezie.lexer.Lexer;
import mx.kenzie.whilezie.lexer.TokenList;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.expression.ModelNil;
import mx.kenzie.whilezie.model.expression.ModelVariable;
import mx.kenzie.whilezie.model.root.ModelProgram;
import mx.kenzie.whilezie.model.statement.ModelAssignment;
import mx.kenzie.whilezie.model.statement.ModelBlock;
import mx.kenzie.whilezie.model.statement.ModelWhile;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;

public class MainParserTest {
    private void includeAll(MainParser parser) {
        MainParser.includeDefaultLanguageSet(parser);
    }

    @Test
    public void simpleTest() throws Throwable {
        final String input = """
            test read X
            {}
            write Y
            """;
        final Lexer lexer = new Lexer(new BufferedReader(new StringReader(input)));
        final TokenList tokens = lexer.run();
        tokens.removeWhitespace();
        assert tokens.hasMatchingBrackets();
        final MainParser parser = new MainParser();
        this.includeAll(parser);
        final Model parsed = parser.parse(parser, Unit.ROOT, new TokenStream(tokens), true);
        assert parsed != null;
        assert parsed.equals(new ModelProgram("test", "X", "Y", new ModelBlock())) : parsed.toString();
    }

    @Test
    public void simpleTest1() throws Throwable {
        final String input = """
            test read X {
                Y := nil
                while X {
                }
            }
            write Y
            """;
        final Lexer lexer = new Lexer(new BufferedReader(new StringReader(input)));
        final TokenList tokens = lexer.run();
        tokens.removeWhitespace();
        assert tokens.hasMatchingBrackets();
        final MainParser parser = new MainParser();
        this.includeAll(parser);
        final Model parsed = parser.parse(parser, Unit.ROOT, new TokenStream(tokens), true);
        assert parsed != null;
        assert parsed.equals(new ModelProgram("test", "X", "Y", new ModelBlock(
            new ModelAssignment("Y", new ModelNil()),
            new ModelWhile(new ModelVariable("X"), new ModelBlock())
        ))) : parsed.toString();
    }

}