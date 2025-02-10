package mx.kenzie.whilezie;

import org.junit.Test;

import java.io.File;

public class WhileInWhileTest {

    protected WhileProgram program;

    public WhileInWhileTest() throws Throwable {
        WhileProgramBuilder builder = new WhileProgramBuilder()
            .includeDefaultSyntax()
            .includeMacros()
            .loadMacros(WhileProgramTest.class.getClassLoader().getResourceAsStream("while_in_while.while"));

        File file = new File("target/while_in_while.class");
        if (!file.exists()) file.createNewFile();
        builder.compileTo(file);

        this.program = builder.build();
    }

    @Test
    public void testListGet() {

        Tree list = Tree.list(Tree::encode, 4, 5, 6);
        Macro get = program.macro("get");

        assert get.run(new Tree(list, Tree.encode(0)), Tree::decode) == 6;
        assert get.run(new Tree(list, Tree.encode(1)), Tree::decode) == 5;
        assert get.run(new Tree(list, Tree.encode(2)), Tree::decode) == 4;
    }

    @Test
    public void testListStore() {

        Tree list = Tree.list(Tree::encode, 4, 5, 6);
        Macro get = program.macro("get");
        Macro store = program.macro("store");

        assert get.run(new Tree(list, Tree.encode(0)), Tree::decode) == 6;
        Tree stored = store.run(new Tree(list, new Tree(Tree.encode(0), Tree.encode(8))));
        assert get.run(new Tree(stored, Tree.encode(0)), Tree::decode) == 8 : get.run(new Tree(stored, Tree.encode(0)), Tree::decode);
    }

    @Test
    public void testListStoreEmpty() {
        Macro get = program.macro("get");
        Macro store = program.macro("store");

        Tree stored = store.run(new Tree(null, new Tree(Tree.encode(0), Tree.encode(8))));
        assert get.run(new Tree(stored, Tree.encode(0)), Tree::decode) == 8 : get.run(new Tree(stored, Tree.encode(0)), Tree::decode);

        stored = store.run(new Tree(stored, new Tree(Tree.encode(3), Tree.encode(6))));
        assert get.run(new Tree(stored, Tree.encode(3)), Tree::decode) == 6 : get.run(new Tree(stored, Tree.encode(3)), Tree::decode);
    }

    @Test
    public void testWhileInWhile() {

        Tree data = WhileInWhile.program(
            WhileInWhile.pair_(
                WhileInWhile.write_(0, WhileInWhile.cons_(WhileInWhile.read_(0), WhileInWhile.read_(0))),
                WhileInWhile.write_(0, WhileInWhile.cons_(WhileInWhile.read_(0), WhileInWhile.read_(0)))
            )
        );

        Macro macro = program.macros().values().iterator().next();

        Tree result = macro.run(data);
        assert result.equals(new Tree(new Tree(), new Tree())) : result;
    }

}