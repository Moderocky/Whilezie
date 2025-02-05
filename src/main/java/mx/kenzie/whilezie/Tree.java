package mx.kenzie.whilezie;

import java.util.function.Function;

public record Tree(Tree head, Tree tail) {

	public Tree() {
		this(null, null);
	}

	public <Type> Tree(Type head, Type tail, Encoder<Type> encoder) {
		this(encoder.apply(head), encoder.apply(tail));
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

	public boolean unary() {
		return head == null && tail == null;
	}

	public boolean numeric() {
		return head == null;
	}

	@FunctionalInterface
	public interface Encoder<Type> extends Function<Type, Tree> {
	}

	@FunctionalInterface
	public interface Decoder<Type> extends Function<Tree, Type> {
	}

}
