package mx.kenzie.whilezie.model.expression;

import mx.kenzie.whilezie.Tree;
import mx.kenzie.whilezie.compiler.VariableTable;
import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.model.Model;
import org.valross.foundation.assembler.code.OpCode;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.io.PrintStream;

public record ModelMacro(Position position, String name, Model value)
    implements Model {

    @Override
    public void print(PrintStream stream) {
        stream.print('<');
        stream.print(name);
        stream.print('>');
        stream.print(' ');
        value.print(stream);
    }

    @Override
    public void compile(CodeBuilder code, VariableTable table) throws CompilingException {
        value.compile(code, table);
        code.write(OpCode.INVOKESTATIC.method(code.exit().exit(), Tree.class, name, Tree.class));
    }

    @Override
    public String toString() {
        return "macro(" + name + ": " + value + ")";
    }

}
