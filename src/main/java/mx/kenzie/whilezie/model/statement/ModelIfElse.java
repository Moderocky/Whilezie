package mx.kenzie.whilezie.model.statement;

import mx.kenzie.whilezie.compiler.VariableTable;
import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.parser.Keywords;
import org.valross.foundation.assembler.code.Branch;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.io.PrintStream;

import static org.valross.foundation.assembler.code.OpCode.GOTO;
import static org.valross.foundation.assembler.code.OpCode.IFNULL;

public record ModelIfElse(Position position, Model condition, Model then, Model otherwise)
	implements Model {

	@Override
	public void print(PrintStream stream) {
		stream.print(Keywords.IF);
		stream.print(' ');
		condition.print(stream);
		stream.print(' ');
		then.print(stream);
		stream.println();
		stream.print(Keywords.ELSE);
		stream.print(' ');
		otherwise.print(stream);
	}

	@Override
	public void compile(CodeBuilder code, VariableTable table) throws CompilingException {
		condition.compile(code, table);
		if (otherwise != null) {
			final Branch goElse = new Branch(), goEnd = new Branch();
			code.write(IFNULL.jump(goElse));
			then.compile(code, table);
			code.write(GOTO.jump(goEnd));
			code.write(goElse);
			otherwise.compile(code, table);
			code.write(goEnd);
		} else {
			final Branch goEnd = new Branch();
			code.write(IFNULL.jump(goEnd));
			then.compile(code, table);
			code.write(goEnd);
		}
	}

	@Override
	public String toString() {
		return "if(" + condition + ": \n" + then + "\nelse " + otherwise + ")";
	}

}
