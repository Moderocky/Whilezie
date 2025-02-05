package mx.kenzie.whilezie.parser.root;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.WordLikeToken;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.root.ModelProgram;
import mx.kenzie.whilezie.parser.Keywords;
import mx.kenzie.whilezie.parser.Parser;
import mx.kenzie.whilezie.parser.Unit;

public record ProgramParser() implements Parser {

	@Override
	public ModelProgram parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
		Position position = input.here();
		final WordLikeToken name = this.find(WordLikeToken.class, input);
		this.keyword(Keywords.READ, input);
		final WordLikeToken in = this.find(WordLikeToken.class, input);
		Model body = outer.parse(outer, Unit.STATEMENT, input, false);
		this.keyword(Keywords.WRITE, input);
		final WordLikeToken out = this.find(WordLikeToken.class, input);
		return new ModelProgram(position, name.value(), in.value(), out.value(), body);
	}

}
