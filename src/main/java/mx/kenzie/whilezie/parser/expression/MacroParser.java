package mx.kenzie.whilezie.parser.expression;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.WordLikeToken;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.expression.ModelMacro;
import mx.kenzie.whilezie.parser.Parser;
import mx.kenzie.whilezie.parser.Unit;

public record MacroParser() implements Parser {

	@Override
	public Model parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
		Position position = input.here();
		WordLikeToken name = this.find(WordLikeToken.class, input);
		if (!name.value().startsWith("<"))
			throw new ParsingException("Macro name must start with '<': " + name.value());
		String string = name.value().substring(1);
		if (string.isBlank())
			throw new ParsingException("Invalid macro name: " + name.value());
		this.keyword(">", input);
		Model argument = this.parse(outer, Unit.EXPRESSION, input, false);
		return new ModelMacro(position, string, argument);
	}

}
