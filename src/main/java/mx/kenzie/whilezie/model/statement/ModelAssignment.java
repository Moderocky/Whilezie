package mx.kenzie.whilezie.model.statement;

import mx.kenzie.whilezie.Tree;
import mx.kenzie.whilezie.compiler.VariableTable;
import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.NamedModelElement;
import org.valross.foundation.assembler.code.OpCode;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.io.PrintStream;


public record ModelAssignment(Position position, String name, Model value)
	implements Model, NamedModelElement {

	public ModelAssignment(String name, Model value) {
		this(new Position(0, 0), name, value);
	}

	@Override
	public void print(PrintStream stream) {
		stream.print(name);
		stream.print(" := ");
		value.print(stream);
	}

	@Override
	public void compile(CodeBuilder code, VariableTable table) throws CompilingException {
		value.compile(code, table);
		table.register(name);
		code.write(OpCode.ASTORE.var(Tree.class, table.indexOf(name)));
	}

	@Override
	public String toString() {
		return "assign(" + name + ": " + value + ")";
	}
}
