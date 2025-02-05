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

public record ModelConstruct(Position position, Model head, Model tail)
	implements Model {

	@Override
	public void print(PrintStream stream) {
		stream.print(Keywords.CONSTRUCT);
		stream.print(' ');
		head.print(stream);
		stream.print(' ');
		tail.print(stream);
	}

	@Override
	public void compile(CodeBuilder code, VariableTable table) throws CompilingException {
		code.write(OpCode.NEW.type(Tree.class), OpCode.DUP);
		head.compile(code, table);
		tail.compile(code, table);
		code.write(OpCode.INVOKESPECIAL.constructor(Tree.class, Tree.class, Tree.class));
	}

	@Override
	public String toString() {
		return "construct(" + head + ", " + tail + ")";
	}

}
