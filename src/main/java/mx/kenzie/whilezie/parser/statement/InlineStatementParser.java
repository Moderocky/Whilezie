package mx.kenzie.whilezie.parser.statement;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.TokenList;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.StructureToken;
import mx.kenzie.whilezie.lexer.token.Token;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.parser.Parser;
import mx.kenzie.whilezie.parser.Unit;

import java.util.ArrayList;
import java.util.List;

public interface InlineStatementParser extends Parser {

    default Model parseExpression(Parser outer, TokenStream input, boolean all) throws ParsingException {
        TokenList list = new TokenList();
        Model statement = null;
        List<ParsingException> errors = new ArrayList<>();
        while (input.hasNext()) {
            Token token = input.next();
            attempt:
            if (token instanceof StructureToken structure && structure.symbol() == ';') {
                if (all && input.hasNext()) break attempt;
                try {
                    statement = this.parse(outer, Unit.EXPRESSION, new TokenStream(list), true);
                    if (statement != null) break;
                } catch (ParsingException ex) {
                    errors.add(ex);
                }
            }
            list.add(token);
        }
        if (statement == null) {
            ParsingException exception = new ParsingException("Not a statement.");
            for (ParsingException error : errors) {
                exception.addSuppressed(error);
            }
            throw exception;
        }
        return statement;
    }

}
