package mx.kenzie.whilezie.parser.statement;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.WordLikeToken;
import mx.kenzie.whilezie.lexer.token.WordToken;
import mx.kenzie.whilezie.parser.Parser;

public interface LeftAssignmentParser extends Parser {

	default Tokens take(TokenStream input) throws ParsingException {
		WordLikeToken type = new WordToken(null, 0, 0);
		WordLikeToken name = this.find(WordLikeToken.class, input);
		WordLikeToken next = this.find(WordLikeToken.class, input);
		if (!next.value().equals("=")) {
			type = name;
			name = next;
			next = this.find(WordLikeToken.class, input);
			if (!next.value().equals("="))
				throw new ParsingException("Expected = following variable name identifier.");
		}
		if (name.value().startsWith("."))
			throw new ParsingException("Disallowed dot variable name.");
		return new Tokens(type.value(), name.value());
	}

	record Tokens(String type, String name) {
	}

}
