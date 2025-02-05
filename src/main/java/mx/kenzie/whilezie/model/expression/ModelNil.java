package mx.kenzie.whilezie.model.expression;

import mx.kenzie.whilezie.Tree;
import mx.kenzie.whilezie.compiler.VariableTable;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.model.Model;
import org.valross.foundation.assembler.code.OpCode;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.io.PrintStream;

public record ModelNil(Position position) implements Model {

	public ModelNil() {
		this(new Position(0, 0));
	}

	@Override
	public void print(PrintStream stream) {
		stream.print("nil");
	}

	@Override
	public void compile(CodeBuilder code, VariableTable table) {
		code.write(OpCode.ACONST_NULL, OpCode.CHECKCAST.type(Tree.class));
	}

	@Override
	public String toString() {
		return "nil";
	}
}
