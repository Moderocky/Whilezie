package mx.kenzie.whilezie;

public interface WhileInWhile {

    static Tree finish(Tree program, int registerSize) {
        return new Tree(program, Tree.encode(registerSize));
    }

    static Tree program(Tree... instructions) {
        Tree tail = null;
        for (Tree instruction : instructions) {
            tail = new Tree(instruction, tail);
        }
        return tail;
    }

    static Tree while_(Tree expression, Tree statement) {
        return new Tree(Tree.encode(1), new Tree(expression, statement));
    }

    static Tree read_(int variable) {
        return new Tree(Tree.encode(2), new Tree(Tree.encode(variable), null));
    }

    static Tree write_(int variable, Tree expression) {
        return new Tree(Tree.encode(3), new Tree(Tree.encode(variable), expression));
    }

    static Tree cons_(Tree head, Tree tail) {
        return new Tree(Tree.encode(4), new Tree(head, tail));
    }

    static Tree hd_(Tree expression) {
        return new Tree(Tree.encode(5), new Tree(expression, null));
    }

    static Tree tl_(Tree expression) {
        return new Tree(Tree.encode(6), new Tree(expression, null));
    }

    static Tree pair_(Tree statement1, Tree statement2) {
        return new Tree(Tree.encode(7), new Tree(statement1, statement2));
    }

}
