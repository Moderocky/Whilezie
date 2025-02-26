package mx.kenzie.whilezie;

import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.expression.ModelConstruct;
import mx.kenzie.whilezie.model.expression.ModelNil;
import org.valross.constantine.RecordConstant;

import java.util.*;
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

    public static Model toModel(Tree tree) {
        if (tree == null) return new ModelNil();
        return new ModelConstruct(toModel(tree.head), toModel(tree.tail));
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Tree tree = (Tree) object;
        return Objects.equals(head, tree.head) && Objects.equals(tail, tree.tail);
    }

    @Override
    public int hashCode() {
        return hashCode(this);
    }

    public static int hashCode(Tree tree) {
        if (tree == null) return 0;
        int count = 0;
        while (tree != null) {
            ++count;
            count ^= hashCode(tree.head) << 4;
            tree = tree.tail;
        }
        return count;
    }

    public byte[] binary() {
        return toBytes(this);
    }

    public String toBinaryString() {
        return toBinaryString(this.binary());
    }

    public static byte[] toBytes(Tree tree) {
        BitAppender appender = new BitAppender();
        toBytes(tree, appender);
        return appender.getBytes();
    }

    private static void toBytes(Tree tree, BitAppender appender) {
        if (tree == null) {
            appender.append(false);
            return;
        }
        appender.append(true);
        toBytes(tree.head, appender);
        toBytes(tree.tail, appender);
    }

    private static final Object[] EMPTY_ARRAY = new Object[0];

    public static Object[] toArrayForm(Tree tree) {
        if (tree == null) return EMPTY_ARRAY;
        return new Object[] {toArrayForm(tree.head), toArrayForm(tree.tail)};
    }

    public static Tree fromArrayForm(Object[] array) {
        if (array == null || array.length == 0) return null;
        assert array.length == 2;
        if (array[0] instanceof Object[] a && array[1] instanceof Object[] b)
            return new Tree(fromArrayForm(a), fromArrayForm(b));
        throw new IllegalArgumentException("Invalid array form: " + array.length);
    }

    public static Tree fromBytes(byte[] bytes) {
        final Biterator iterator = new Biterator(bytes);
        return fromBytes(iterator);
    }

    private static Tree fromBytes(Biterator iterator) {
        return iterator.next()
            ? new Tree(fromBytes(iterator), fromBytes(iterator))
            : null;
    }

    public static String toBinaryString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "0";
        StringBuilder builder = new StringBuilder();
        int take = 1;
        loop:
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean set = get(b, i);
                builder.append(set ? 1 : 0);
                if (set) ++take;
                else if (--take < 1) break loop;
            }
        }
        return builder.toString();
    }

    static byte set(byte b, int bit, boolean value) {
        if (value)
            b |= (byte) (1 << bit);
        else
            b &= (byte) ~(1 << bit);
        return b;
    }

    static byte toggle(byte b, int bit) {
        return (byte) (b ^ (byte) (1 << bit));
    }

    static boolean get(byte b, int bit) {
        return (b & (byte) (1 << bit)) != 0;
    }

    protected static class Biterator implements Iterator<Boolean> {

        private final byte[] binary;
        private int pointer = 0;

        protected Biterator(byte[] binary) {
            this.binary = binary;
        }

        @Override
        public boolean hasNext() {
            return pointer / 8 < binary.length;
        }

        @Override
        public Boolean next() {
            final int index = pointer / 8, bit = pointer++ % 8;
            if (index >= binary.length) return false;
            return get(binary[index], bit);
        }

    }

    protected static class BitAppender {

        private byte[] bytes = new byte[8];
        private int pointer;

        public BitAppender append(boolean value) {
            final int index = pointer / 8, bit = pointer++ % 8;
            if (bytes.length <= index) this.grow();
            byte b = bytes[index];
            b = set(b, bit, value);
            this.bytes[index] = b;
            return this;
        }

        private void grow() {
            this.bytes = Arrays.copyOf(bytes, bytes.length * 2);
        }

        public byte[] getBytes() {
            return Arrays.copyOf(bytes, (pointer / 8) + 1);
        }

    }

}
