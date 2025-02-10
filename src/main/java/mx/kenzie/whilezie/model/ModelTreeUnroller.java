package mx.kenzie.whilezie.model;

import mx.kenzie.whilezie.Tree;
import mx.kenzie.whilezie.model.expression.ModelConstruct;
import mx.kenzie.whilezie.model.expression.ModelHead;
import mx.kenzie.whilezie.model.expression.ModelTail;
import mx.kenzie.whilezie.model.expression.ModelVariable;
import mx.kenzie.whilezie.model.root.ModelProgram;
import mx.kenzie.whilezie.model.statement.ModelAssignment;
import mx.kenzie.whilezie.model.statement.ModelBlock;
import mx.kenzie.whilezie.model.statement.ModelWhile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModelTreeUnroller {

    private static final int WHILE = 1, READ = 2, WRITE = 3, CONS = 4, HD = 5, TL = 6, PAIR = 7;
    private static final String[] variables = "_ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");

    protected final Tree tree;

    public ModelTreeUnroller(Tree tree) {
        this.tree = tree;
    }

    public ModelProgram unroll() {
        Collection<Tree> instructions = Tree.delist(t -> t, tree);
        List<Model> statements = new ArrayList<>();
        for (Tree instruction : instructions) {
            Model model = this.unroll(instruction);
            statements.add(model);
        }
        if (statements.size() == 1)
            return new ModelProgram("_", "_", "_", statements.getFirst());
        return new ModelProgram("_", "_", "_", new ModelBlock(statements.toArray(new Model[0])));
    }

    protected Model unroll(Tree instruction) {
        final int opcode = Tree.decode(instruction.head());
        final Tree a = instruction.tail().head(), b = instruction.tail().tail();
        return switch (opcode) {
            case WHILE -> new ModelWhile(unroll(a), unroll(b));
            case PAIR -> new ModelBlock(unroll(a), unroll(b));
            case CONS -> new ModelConstruct(unroll(a), unroll(b));
            case HD -> new ModelHead(unroll(a));
            case TL -> new ModelTail(unroll(a));
            case READ -> new ModelVariable(variables[Tree.decode(a)]);
            case WRITE -> new ModelAssignment(variables[Tree.decode(a)], unroll(b));
            default -> throw new IllegalStateException("Unexpected value: " + opcode);
        };
    }

}
