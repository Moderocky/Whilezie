package mx.kenzie.whilezie.parser.extensions;

import mx.kenzie.whilezie.Tree;
import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.WordLikeToken;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.expression.ModelLiteralTree;
import mx.kenzie.whilezie.model.expression.ModelNil;
import mx.kenzie.whilezie.parser.Parser;

public record LiteralBooleanParser() implements Parser {

    @Override
    public Model parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
        Position position = input.here();
        WordLikeToken token = this.find(WordLikeToken.class, input);
        return switch (token.value()) {
            case "true" -> new ModelLiteralTree(position, new Tree());
            case "false" -> new ModelNil();
            default -> throw new ParsingException("Unexpected boolean value: " + token.value());
        };
    }

}
