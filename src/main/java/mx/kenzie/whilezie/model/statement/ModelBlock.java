package mx.kenzie.whilezie.model.statement;

import mx.kenzie.whilezie.compiler.VariableTable;
import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.model.Model;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;

public record ModelBlock(Position position, Model... statements) implements Model {

    public ModelBlock(Model... statements) {
        this(new Position(0, 0), statements);
    }

    @Override
    public void print(PrintStream stream) {
        stream.println('{');
        for (Model statement : statements) {
            statement.print(stream);
            stream.println();
        }
        stream.print('}');
    }

    @Override
    public void compile(CodeBuilder code, VariableTable table) throws CompilingException {
        for (Model statement : statements) {
            statement.compile(code, table);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelBlock block)) return false;
        return Objects.deepEquals(statements, block.statements);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(statements);
    }

    @Override
    public String toString() {
        return String.join("\n", Arrays.stream(statements).map(Model::toString).toList());
    }

}
