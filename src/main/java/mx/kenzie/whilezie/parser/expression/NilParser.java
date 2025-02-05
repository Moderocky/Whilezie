package mx.kenzie.whilezie.parser.expression;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.model.expression.ModelNil;
import mx.kenzie.whilezie.parser.Keywords;
import mx.kenzie.whilezie.parser.Parser;

public record NilParser() implements Parser {

    @Override
    public ModelNil parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
        Position here = input.here();
        this.keyword(Keywords.NIL, input);
        return new ModelNil(here);
    }

}
