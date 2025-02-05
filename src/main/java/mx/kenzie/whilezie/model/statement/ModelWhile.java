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

public record ModelWhile(Position position, Model condition, Model then) implements Model {

    public ModelWhile(Model condition, Model then) {
        this(new Position(0, 0), condition, then);
    }

    @Override
    public void print(PrintStream stream) {
        stream.print(Keywords.WHILE);
        stream.print(' ');
        condition.print(stream);
        stream.print(' ');
        then.print(stream);
    }

    @Override
    public void compile(CodeBuilder code, VariableTable table) throws CompilingException {
        final Branch top = new Branch(), end;
        code.write(top);
        end = new Branch();
        condition.compile(code, table);
        code.write(IFNULL.jump(end));
        then.compile(code, table);
        code.write(GOTO.jump(top));
        code.write(end);
    }

    @Override
    public String toString() {
        return "while(" + condition + ": \n" + then + ")";
    }

}
