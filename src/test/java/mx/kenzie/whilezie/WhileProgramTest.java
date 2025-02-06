package mx.kenzie.whilezie;

import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.error.ParsingException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public class WhileProgramTest {

    protected static Macro load(String name) {
        try (InputStream stream = WhileProgramTest.class.getClassLoader().getResourceAsStream(name)) {

            WhileProgramBuilder builder = new WhileProgramBuilder().includeDefaultSyntax().includeMacros();
            builder.loadMacros(stream);

            File file = new File("target/" + name + ".class");
            if (!file.exists()) file.createNewFile();
            builder.compileTo(file);

            return builder.build().macros().values().iterator().next();
        } catch (IOException | CompilingException | ParsingException ex) {
            throw new AssertionError(ex);
        }
    }

    @Test
    public void test() throws CompilingException, ParsingException, IOException {
        WhileProgramBuilder builder = new WhileProgramBuilder()
            .includeDefaultSyntax()
            .loadMacros("""
                test read x
                {
                	y := x
                }
                write y
                """);
        WhileProgram program = builder.build();

        Macro test = program.macro("test");

        assert test.run(null) == null;
        assert test.run(new Tree()).equals(new Tree());

        Tree tree = new Tree(new Tree(), new Tree());
        assert test.run(tree) == tree;

    }

    @Test
    public void testComment() {
        Macro macro = load("comment.while");

        assert macro.run(1, Tree::encode, Tree::decode) == 1;
    }

    @Test
    public void testSimpleMacro() {
        Macro macro = WhileProgram.load("""
            my_program read X
            Y := X
            write Y
            """);

        assert macro.run(null) == null;

        Tree tree = new Tree(new Tree(), new Tree());
        assert macro.run(tree) == tree;
    }

    @Test
    public void testIncrement() {
        Macro macro = WhileProgram.load("""
            increment read X
            Y := cons nil X
            write Y
            """);

        assert macro.run(0, Tree::encode, Tree::decode) == 1;
        assert macro.run(5, Tree::encode, Tree::decode) == 6;
    }

    @Test
    public void testSum() {
        Macro macro = WhileProgram.load("""
            sum read X
            {
            	Y := tl X
            	X := hd X
            	while X {
            		Y := cons nil Y
            		X := tl X
            	}
            }
            write Y
            """);

        assert macro.run(new Tree(1, 1, Tree::encode), Tree::decode) == 2;
        assert macro.run(new Tree(0, 5, Tree::encode), Tree::decode) == 5;
        assert macro.run(new Tree(5, 5, Tree::encode), Tree::decode) == 10;
    }

    @Test
    public void testCallMacro() throws CompilingException, ParsingException, IOException {
        WhileProgramBuilder builder = new WhileProgramBuilder()
            .includeDefaultSyntax()
            .includeMacros()
            .loadMacros("""
                sum read X
                {
                	Y := tl X
                	X := hd X
                	while X {
                		Y := cons nil Y
                		X := tl X
                	}
                }
                write Y
                """)
            .loadMacros("""
                test read x
                y := <sum> x
                write y
                """);
        WhileProgram program = builder.build();

        Macro macro = program.macro("test");

        assert macro.run(new Tree(1, 1, Tree::encode), Tree::decode) == 2;
        assert macro.run(new Tree(0, 5, Tree::encode), Tree::decode) == 5;
        assert macro.run(new Tree(5, 5, Tree::encode), Tree::decode) == 10;
    }

    @Test
    public void testAnd() {
        Macro macro = load("and.while");

        assert macro.run(new Tree(1, 1, Tree::encode), Tree::decode) == 1;
        assert macro.run(new Tree(1, 0, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(0, 1, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(0, 0, Tree::encode), Tree::decode) == 0;
    }

    @Test
    public void testOr() {
        Macro macro = load("or.while");

        assert macro.run(new Tree(1, 1, Tree::encode), Tree::decode) == 1;
        assert macro.run(new Tree(1, 0, Tree::encode), Tree::decode) == 1;
        assert macro.run(new Tree(0, 1, Tree::encode), Tree::decode) == 1;
        assert macro.run(new Tree(0, 0, Tree::encode), Tree::decode) == 0;
    }

    @Test
    public void testXor() {
        Macro macro = load("xor.while");

        assert macro.run(new Tree(1, 1, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(1, 0, Tree::encode), Tree::decode) == 1;
        assert macro.run(new Tree(0, 1, Tree::encode), Tree::decode) == 1;
        assert macro.run(new Tree(0, 0, Tree::encode), Tree::decode) == 0;
    }

    @Test
    public void testIf() {
        Macro macro = load("if.while");

        assert macro.run(new Tree(1, 1, Tree::encode), Tree::decode) == 1;
        assert macro.run(new Tree(1, 0, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(0, 1, Tree::encode), Tree::decode) == 1;
        assert macro.run(new Tree(0, 0, Tree::encode), Tree::decode) == 1;
    }

    @Test
    public void testNot() {
        Macro macro = load("not.while");

        assert macro.run(1, Tree::encode, Tree::decode) == 0;
        assert macro.run(0, Tree::encode, Tree::decode) == 1;
    }

    @Test
    public void testReverse() {
        Macro macro = load("reverse.while");

        assert Tree.delist(Tree::decode, macro.run(Tree.list(Tree::encode, 1, 2, 3))).equals(List.of(3, 2, 1));
    }

    @Test
    public void testAdd() {
        Macro macro = load("add.while");

        assert macro.run(new Tree(1, 1, Tree::encode), Tree::decode) == 2;
        assert macro.run(new Tree(0, 5, Tree::encode), Tree::decode) == 5;
        assert macro.run(new Tree(5, 5, Tree::encode), Tree::decode) == 10;
    }

    @Test
    public void testSubtract() {
        Macro macro = load("subtract.while");

        assert macro.run(new Tree(1, 1, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(2, 0, Tree::encode), Tree::decode) == 2;
        assert macro.run(new Tree(0, 0, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(5, 5, Tree::encode), Tree::decode) == 0;
    }

    @Test
    public void testMultiply() {
        Macro macro = load("multiply.while");

        assert macro.run(new Tree(0, 0, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(0, 1, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(1, 1, Tree::encode), Tree::decode) == 1 : macro.run(new Tree(1, 1, Tree::encode), Tree::decode);
        assert macro.run(new Tree(2, 0, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(2, 1, Tree::encode), Tree::decode) == 2;
        assert macro.run(new Tree(2, 2, Tree::encode), Tree::decode) == 4;
        assert macro.run(new Tree(5, 5, Tree::encode), Tree::decode) == 25;
        assert macro.run(new Tree(10, 6, Tree::encode), Tree::decode) == 60;
        assert macro.run(new Tree(6, 10, Tree::encode), Tree::decode) == 60;
    }

    @Test
    public void testDivide() {
        Macro macro = load("divide.while");

        assert macro.run(new Tree(8, 2, Tree::encode), Tree::decode) == 4;
        assert macro.run(new Tree(9, 3, Tree::encode), Tree::decode) == 3;
        assert macro.run(new Tree(100, 10, Tree::encode), Tree::decode) == 10;

        assert macro.run(new Tree(1, 0, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(0, 1, Tree::encode), Tree::decode) == 0;

        assert macro.run(new Tree(10, 3, Tree::encode), Tree::decode) == 3;
        assert macro.run(new Tree(101, 10, Tree::encode), Tree::decode) == 10;
    }

    @Test
    public void testModulo() {
        Macro macro = load("modulo.while");

        assert macro.run(new Tree(8, 2, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(9, 3, Tree::encode), Tree::decode) == 0;
        assert macro.run(new Tree(100, 10, Tree::encode), Tree::decode) == 0;

        assert macro.run(new Tree(10, 3, Tree::encode), Tree::decode) == 10 % 3;
        assert macro.run(new Tree(102, 10, Tree::encode), Tree::decode) == 102 % 10;
    }

    @Test
    public void testEquals() {
        Macro macro = load("equals.while");

        assert macro.run(new Tree(3, 3, Tree::encode)) != null;
        assert macro.run(new Tree(new Tree(), new Tree())) != null;
        assert macro.run(new Tree()) != null;
        assert macro.run(new Tree(new Tree(new Tree(), new Tree()), new Tree(new Tree(), new Tree()))) != null;

        assert macro.run(new Tree(8, 2, Tree::encode)) == null;
        assert macro.run(new Tree(1, 12, Tree::encode)) == null;
        assert macro.run(new Tree(new Tree(), null)) == null;
        assert macro.run(new Tree(null, new Tree())) == null;
    }

    @Test
    public void testIsNumeric() {
        Macro macro = load("is_numeric.while");

        assert macro.run(0, Tree::encode) != null;
        assert macro.run(1, Tree::encode) != null;
        assert macro.run(5, Tree::encode) != null;
        assert macro.run(11, Tree::encode) != null;

        assert macro.run(null) != null;
        assert macro.run(new Tree()) != null;
        assert macro.run(new Tree(null, new Tree())) != null;
        assert macro.run(new Tree(null, new Tree(null, new Tree()))) != null;

        assert macro.run(new Tree(new Tree(), new Tree())) == null;
        assert macro.run(new Tree(new Tree(), new Tree(null, new Tree()))) == null;
        assert macro.run(new Tree(new Tree(new Tree(), null), new Tree())) == null;
    }

    @Test
    public void testDoNothing() {
        Macro macro = WhileProgram.load("""
            test read X
            {}
            write X
            """);
        Collection<Integer> list = Tree.delist(Tree::decode, macro.run(Tree.list(Tree::encode, 1, 2, 3)));
        assert list.equals(List.of(1, 2, 3)) : list;
    }

    @Test
    public void testLiteralBoolean() throws CompilingException, ParsingException, IOException {
        WhileProgramBuilder builder = new WhileProgramBuilder()
            .includeExtendedLiterals()
            .includeDefaultSyntax()
            .loadMacros("""
                test read X
                X := true
                write X
                """);
        WhileProgram program = builder.build();

        Macro test = program.macro("test");

        assert test.run(null).equals(new Tree());
        assert test.run(new Tree()).equals(new Tree());
    }

    @Test
    public void testLiterals() throws CompilingException, ParsingException, IOException {
        WhileProgramBuilder builder = new WhileProgramBuilder()
            .includeExtendedLiterals()
            .includeDefaultSyntax()
            .loadMacros(WhileProgramTest.class.getClassLoader().getResourceAsStream("literals.while"));

        File file = new File("target/literals.class");
        if (!file.exists()) file.createNewFile();
        builder.compileTo(file);

        Macro macro = builder.build().macros().values().iterator().next();

        assert macro.run(null) != null;
    }

    @Test
    public void testMultipleMacros() throws CompilingException, ParsingException, IOException {
        WhileProgramBuilder builder = new WhileProgramBuilder()
            .includeDefaultSyntax()
            .includeMacros()
            .loadMacros("""
                one read X
                X := <two> X
                write X
                
                two read X
                X := cons nil X
                write X
                """);
        WhileProgram program = builder.build();

        Macro test = program.macro("one");

        assert test.run(null).equals(new Tree());

        assert test.run(new Tree()).equals(new Tree(null, new Tree()));
    }

}