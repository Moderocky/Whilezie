package mx.kenzie.whilezie.model.root;

import mx.kenzie.whilezie.compiler.VariableTable;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.NamedModelElement;
import mx.kenzie.whilezie.parser.Keywords;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.io.PrintStream;

public record ModelProgram(Position position, String name, String input, String output,
                           Model body) implements Model, NamedModelElement {

    public ModelProgram(String name, String input, String output, Model body) {
        this(new Position(0, 0), name, input, output, body);
    }

    @Override
    public void print(PrintStream stream) {
        stream.print(name);
        stream.print(' ');
        stream.print(Keywords.READ);
        stream.print(' ');
        stream.print(input);
        stream.println();
        body.print(stream);
        stream.println();
        stream.print(Keywords.WRITE);
        stream.print(' ');
        stream.print(output);
        stream.println();
    }

    @Override
    public void compile(CodeBuilder code, VariableTable table) {
        throw new IllegalStateException();
    }

    @Override
    public String toString() {
        return "program(" + name + ": " + input + " -> " + output + "\n" + body + "\n" + ")";
    }
}
