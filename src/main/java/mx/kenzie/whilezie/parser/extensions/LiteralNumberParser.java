package mx.kenzie.whilezie.parser.extensions;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.NumberToken;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.expression.ModelConstruct;
import mx.kenzie.whilezie.model.expression.ModelNil;
import mx.kenzie.whilezie.parser.Parser;

public record LiteralNumberParser() implements Parser {

	@Override
	public Model parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
		Position position = input.here();
		NumberToken token = this.find(NumberToken.class, input);
		Model tail = new ModelNil(position);
		for (int i = 0; i < token.value().intValue(); i++)
			tail = new ModelConstruct(position, new ModelNil(), tail);
		return tail;
	}

}
