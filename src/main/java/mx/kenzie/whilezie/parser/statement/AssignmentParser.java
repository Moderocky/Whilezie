package mx.kenzie.whilezie.parser.statement;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.WordLikeToken;
import mx.kenzie.whilezie.model.statement.ModelAssignment;
import mx.kenzie.whilezie.parser.Parser;
import mx.kenzie.whilezie.parser.Unit;

public record AssignmentParser() implements Parser, LeftAssignmentParser {

	@Override
	public ModelAssignment parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
		Position position = input.here();
		final WordLikeToken variable = this.find(WordLikeToken.class, input);
		this.keyword(":=", input);
		return new ModelAssignment(position, variable.value(), this.parse(outer, Unit.EXPRESSION, input, all));
	}

}
