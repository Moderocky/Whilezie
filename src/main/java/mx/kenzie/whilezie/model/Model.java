package mx.kenzie.whilezie.model;

import mx.kenzie.whilezie.compiler.VariableTable;
import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.lexer.Position;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.io.PrintStream;

public interface Model {

    void print(PrintStream stream);

    Position position();

    void compile(CodeBuilder code, VariableTable table) throws CompilingException;
}
