package mx.kenzie.whilezie;

import org.valross.constantine.RecordConstant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public record Tree(Tree head, Tree tail) implements RecordConstant {

    public Tree() {
        this(null, null);
    }

    public <Type> Tree(Type head, Type tail, Encoder<Type> encoder) {
        this(encoder.apply(head), encoder.apply(tail));
    }

    @SafeVarargs
    public static <Type> Tree list(Encoder<Type> encoder, Type... values) {
        if (values.length == 0) return null;
        Tree tree = null;
        for (Type value : values) {
            tree = new Tree(encoder.apply(value), tree);
        }
        return tree;
    }

    public static <Type> Collection<Type> delist(Decoder<Type> decoder, Tree tree) {
        List<Type> list = new ArrayList<>();
        while (tree != null) {
            list.add(decoder.apply(tree.head));
            tree = tree.tail;
        }
        return list.reversed();
    }

    public static Tree encode(int number) {
        if (number == 0) return null;
        Tree tree = null;
        for (int i = 0; i < number; i++) {
            tree = new Tree(null, tree);
        }
        return tree;
    }

    public static int decode(Tree tree) {
        int count = 0;
        while (tree != null && tree.numeric()) {
            ++count;
            tree = tree.tail;
        }
        return count;
    }

    public static String toString(Tree tree) {
        if (tree == null) return "nil";
        return "<" + toString(tree.head) + "." + toString(tree.tail) + ">";
    }

    public boolean unary() {
        return head == null && tail == null;
    }

    public boolean numeric() {
        return head == null;
    }

    @Override
    public String toString() {
        return toString(this);
    }

    @FunctionalInterface
    public interface Encoder<Type> extends Function<Type, Tree> {
    }

    @FunctionalInterface
    public interface Decoder<Type> extends Function<Tree, Type> {
    }

}
