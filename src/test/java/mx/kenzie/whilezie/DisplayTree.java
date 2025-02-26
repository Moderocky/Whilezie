package mx.kenzie.whilezie;

import mx.kenzie.whilezie.model.expression.ModelConstruct;

import java.io.PrintStream;

public class DisplayTree {

    public static void printProofTree(Tree tree, PrintStream out) {
        out.print("\\Tree");

        printTree(tree, out);


    }

    protected static void printTree(Tree tree, PrintStream out) {
        out.print(' ');
        if (tree == null)
            out.print("[.nil ].nil");
        else {
            out.print("[.");
            out.println();
            printTree(tree.head(), out);
            out.println();
            printTree(tree.tail(), out);
            out.println();
            out.print("].");
        }
    }

    public static void main(String[] args) throws Throwable {
        Tree tree = parse("""
            <<nil.nil>.<<nil.nil>.<<nil.nil>.<<nil.<nil.nil>>.<nil.nil>>>>>
            """);

        System.out.println(tree);
        System.out.println();
        printProofTree(tree, System.out);
        System.out.println();

    }

    private static Tree parse(String string) throws Throwable {

        WhileProgramBuilder builder = new WhileProgramBuilder()
            .includeExtendedLiterals()
            .includeDefaultSyntax()
            .loadMacros("_ read _ { _ := " + string + " } write _");

        Macro macro = builder.build().macros().values().iterator().next();

        return macro.run(null);
    }

}
