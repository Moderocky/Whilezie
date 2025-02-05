package mx.kenzie.whilezie;

import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.error.ParsingException;
import org.junit.Test;

import java.io.IOException;

public class WhileProgramTest {

	@Test
	public void test() throws CompilingException, ParsingException, IOException {
		WhileProgramBuilder builder = new WhileProgramBuilder()
			.includeDefaultSyntax()
			.loadMacro("""
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
			.loadMacro("""
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
			.loadMacro("""
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

}