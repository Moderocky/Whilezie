package mx.kenzie.whilezie.parser.expression;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.expression.ModelTail;
import mx.kenzie.whilezie.parser.Keywords;
import mx.kenzie.whilezie.parser.Parser;
import mx.kenzie.whilezie.parser.Unit;

public record TailParser() implements Parser {

	@Override
	public Model parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
		Position position = input.here();
		this.keyword(Keywords.TAIL, input);
		Model of = this.parse(outer, Unit.EXPRESSION, input, false);
		return new ModelTail(position, of);
	}

}
