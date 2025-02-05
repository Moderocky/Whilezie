package mx.kenzie.whilezie.model.expression;

import mx.kenzie.whilezie.Tree;
import mx.kenzie.whilezie.compiler.VariableTable;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.model.Literal;
import mx.kenzie.whilezie.model.Model;
import org.valross.foundation.assembler.code.OpCode;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.io.PrintStream;

public record ModelLiteralTree(Position position, Tree tree) implements Model, Literal {

    public ModelLiteralTree(Tree tree) {
        this(new Position(0, 0), tree);
    }

    @Override
    public void print(PrintStream stream) {
        stream.print(Tree.toString(tree));
    }

    @Override
    public void compile(CodeBuilder code, VariableTable table) {
        if (tree == null) code.write(OpCode.ACONST_NULL, OpCode.CHECKCAST.type(Tree.class));
        else code.write(OpCode.LDC.value(tree));
    }

    @Override
    public String toString() {
        return Tree.toString(tree);
    }

    @Override
    public Tree value() {
        return tree;
    }

}
