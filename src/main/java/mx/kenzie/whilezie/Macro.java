package mx.kenzie.whilezie;

import java.util.function.Function;

@FunctionalInterface
public interface Macro extends Function<Tree, Tree> {

	Tree run(Tree tree);

	default <Type> Type run(Type input, Tree.Encoder<Type> encoder, Tree.Decoder<Type> decoder) {
		return decoder.apply(this.run(encoder.apply(input)));
	}

	default <Type> Tree run(Type input, Tree.Encoder<Type> encoder) {
		return this.run(encoder.apply(input));
	}

	default <Type> Type run(Tree input, Tree.Decoder<Type> decoder) {
		return decoder.apply(this.run(input));
	}

	@Override
	default Tree apply(Tree tree) {
		return this.run(tree);
	}

}
