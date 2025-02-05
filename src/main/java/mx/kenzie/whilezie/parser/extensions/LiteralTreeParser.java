package mx.kenzie.whilezie.parser.extensions;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.expression.ModelConstruct;
import mx.kenzie.whilezie.parser.Parser;
import mx.kenzie.whilezie.parser.Unit;

public record LiteralTreeParser() implements Parser {

	@Override
	public Model parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
		Position position = input.here();
		this.keyword("<", input);
		Model head = this.parse(outer, Unit.EXPRESSION, input, false);
		this.keyword(".", input);
		Model tail = this.parse(outer, Unit.EXPRESSION, input, false);
		this.keyword(">", input);
		return new ModelConstruct(position, head, tail);
	}

}
