package mx.kenzie.whilezie.parser.extensions;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.statement.ModelIfElse;
import mx.kenzie.whilezie.parser.Keywords;
import mx.kenzie.whilezie.parser.Parser;
import mx.kenzie.whilezie.parser.Unit;

public record IfParser() implements Parser {

	@Override
	public Model parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
		Position position = input.here();
		this.keyword(Keywords.IF, input);
		Model condition = this.parse(outer, Unit.EXPRESSION, input, false);
		Model then = this.parse(outer, Unit.STATEMENT, input, all);
		return new ModelIfElse(position, condition, then, null);
	}

}
