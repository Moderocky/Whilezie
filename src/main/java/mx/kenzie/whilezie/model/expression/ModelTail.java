package mx.kenzie.whilezie.model.expression;

import mx.kenzie.whilezie.Tree;
import mx.kenzie.whilezie.compiler.VariableTable;
import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.parser.Keywords;
import org.valross.foundation.assembler.code.OpCode;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.io.PrintStream;

public record ModelTail(Position position, Model value)
	implements Model {

	@Override
	public void print(PrintStream stream) {
		stream.print(Keywords.TAIL);
		stream.print(' ');
		value.print(stream);
	}

	@Override
	public void compile(CodeBuilder code, VariableTable table) throws CompilingException {
		value.compile(code, table);
		code.write(OpCode.INVOKEVIRTUAL.method(false, Tree.class, Tree.class, "tail"));
	}

	@Override
	public String toString() {
		return "tail(" + value + ")";
	}

}
