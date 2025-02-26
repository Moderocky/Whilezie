package mx.kenzie.whilezie;

import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

public class TreeTest {

    @Test
    public void testHashCode() {

        assert Tree.hashCode(null) == 0 : Tree.hashCode(null);
        assert Tree.hashCode(new Tree()) == 1 : Tree.hashCode(new Tree());
        assert Tree.hashCode(new Tree(new Tree(), new Tree())) == 18 : Tree.hashCode(new Tree(new Tree(), new Tree()));
        assert Tree.hashCode(Tree.encode(10)) == 10 : Tree.hashCode(new Tree());
    }

    @Test
    public void binary() {
    }

    @Test
    public void toBytes() {
        this.test(null, "0");
        this.test(new Tree(), "100");
        this.test(new Tree(new Tree(), new Tree()), "1100100");
        this.test(new Tree(new Tree(), null), "11000");
        this.test(new Tree(null, new Tree()), "10100");
        this.test(Tree.encode(5), "10101010100");
        this.test(Tree.encode(17), "10".repeat(17) + "0");
    }

    private void test(Tree tree, String expected) {
        String binaryString = Tree.toBinaryString(Tree.toBytes(tree));
        assert Objects.equals(binaryString, expected) : binaryString;
    }

    private void test(Tree tree) {
        byte[] bytes = Tree.toBytes(tree);
        Tree reconstituted = Tree.fromBytes(bytes);
        assert Objects.equals(reconstituted, tree) : reconstituted;
    }

    @Test
    public void fromBytes() {
        this.test(null);
        this.test(new Tree());
        this.test(new Tree(new Tree(), new Tree()));
        this.test(new Tree(new Tree(), null));
        this.test(new Tree(null, new Tree()));
        this.test(Tree.encode(5));
        this.test(Tree.encode(17));
    }

    @Test
    public void set() {
        assert Tree.set((byte) 0, 1, false) == 0;
        assert Tree.set((byte) 0, 0, true) == 1;
        assert Tree.set((byte) 0, 1, true) == 0b10;
        assert Tree.set((byte) 0, 7, true) == -128;
    }

    @Test
    public void toggle() {
    }

    @Test
    public void get() {
        assert !Tree.get((byte) 0, 0);
        assert Tree.get((byte) 1, 0);
        assert !Tree.get((byte) 0b10, 0);
        assert Tree.get((byte) 0b10, 1);
    }

    @Test
    public void toArrayForm() {
        assert Arrays.deepToString(Tree.toArrayForm(new Tree(new Tree(new Tree(), null), new Tree(new Tree(), null))))
            .equals("[[[[], []], []], [[[], []], []]]");
    }

}