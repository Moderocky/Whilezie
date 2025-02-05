package mx.kenzie.whilezie.model.expression;

import mx.kenzie.whilezie.Tree;
import mx.kenzie.whilezie.compiler.VariableTable;
import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.NamedModelElement;
import org.valross.foundation.assembler.code.OpCode;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.io.PrintStream;

public record ModelVariable(Position position, String name) implements Model, NamedModelElement {

    public ModelVariable(String name) {
        this(new Position(0, 0), name);
    }

    @Override
    public void print(PrintStream stream) {
        stream.print(name);
    }

    @Override
    public void compile(CodeBuilder code, VariableTable table) throws CompilingException {
        if (!table.contains(name))
            throw new CompilingException("Variable '" + name + "' was never assigned.");
        code.write(OpCode.ALOAD.var(Tree.class, table.indexOf(name)));
    }

    @Override
    public String toString() {
        return "var(" + name + ")";
    }

}
