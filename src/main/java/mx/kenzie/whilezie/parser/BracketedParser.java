package mx.kenzie.whilezie.parser;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Mark;
import mx.kenzie.whilezie.lexer.TokenList;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.StructureToken;
import mx.kenzie.whilezie.lexer.token.Token;

public interface BracketedParser extends Parser {

    default TokenList findMatching(TokenStream input, char open, char close)
    throws ParsingException {
        try (Mark mark = input.markForReset()) {
            final StructureToken first = this.find(StructureToken.class, input);
            if (first.symbol() != open)
                throw new ParsingException("Expected an opening " + open + " bracket, got " + first);
            int count = 1;
            TokenList list = new TokenList();
            for (Token token : input) {
                if (token instanceof StructureToken structure) {
                    if (structure.symbol() == open) ++count;
                    if (structure.symbol() == close) --count;
                    if (count == 0) {
                        mark.discard();
                        return list;
                    }
                }
                list.add(token);
            }
            throw new ParsingException("Expected a closing " + close + " bracket matching " + open);
        }
    }

}
