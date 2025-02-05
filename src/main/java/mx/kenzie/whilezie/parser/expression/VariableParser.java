package mx.kenzie.whilezie.parser.expression;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.WordLikeToken;
import mx.kenzie.whilezie.model.expression.ModelVariable;
import mx.kenzie.whilezie.parser.Parser;

public record VariableParser() implements Parser {

    @Override
    public ModelVariable parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
        return new ModelVariable(input.here(), this.find(WordLikeToken.class, input).value());
    }

}
