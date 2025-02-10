package mx.kenzie.whilezie;

import org.junit.Test;

public class TreeTest {

    @Test
    public void testHashCode() {

        assert Tree.hashCode(null) == 0 : Tree.hashCode(null);
        assert Tree.hashCode(new Tree()) == 1 : Tree.hashCode(new Tree());
        assert Tree.hashCode(new Tree(new Tree(), new Tree())) == 18 : Tree.hashCode(new Tree(new Tree(), new Tree()));
        assert Tree.hashCode(Tree.encode(10)) == 10 : Tree.hashCode(new Tree());
    }

}