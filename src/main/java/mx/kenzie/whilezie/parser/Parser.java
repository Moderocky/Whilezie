package mx.kenzie.whilezie.parser;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Mark;
import mx.kenzie.whilezie.lexer.TokenList;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.Token;
import mx.kenzie.whilezie.lexer.token.WordLikeToken;
import mx.kenzie.whilezie.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface Parser {

    default Model parse(Parser outer, Unit unit, TokenStream input) throws ParsingException {
        return this.parse(outer, unit, input, false);
    }

    default Model parse(Parser outer, Unit unit, TokenStream input, boolean all) throws ParsingException {
        final List<ParsingException> errors = new ArrayList<>();
        for (Parser parser : this.parsers(outer, unit)) {
            try (Mark mark = input.markForReset()) {
                try {
                    final Model stmt = parser.parse(outer, input, all);
                    if (stmt == null)
                        throw new ParsingException("Nothing returned by " + parser.getClass().getSimpleName());
                    if (all && input.hasNext())
                        throw new ParsingException("Too many tokens remaining for " + parser.getClass()
                            .getSimpleName() + ": " + input.remaining());
                    mark.discard();
                    return stmt;
                } catch (ParsingException fault) {
                    errors.add(fault);
                }
            }
        }

        ParsingException exception;
        if (all) exception = new ParsingException("No " + unit + " match found for all " + input.remaining());
        else exception = new ParsingException("No " + unit + " match found for " + input.next());
        for (ParsingException error : errors) exception.addSuppressed(error);
        throw exception;
    }

    Model parse(Parser outer, TokenStream input, boolean all) throws ParsingException;

    default TokenList take(TokenStream input, int amount) throws ParsingException {
        TokenList list = new TokenList();
        for (int i = 0; i < amount; i++) {
            if (!input.hasNext()) throw new ParsingException("Reached end of tokens taking " + amount + " items.");
            list.add(input.next());
        }
        return list;
    }

    default <TokenType extends Token> TokenList getEverythingUntil(Class<? super TokenType> type, TokenStream input,
                                                                   Predicate<TokenType> predicate) {
        TokenList tokens = new TokenList();
        try (Mark _ = input.markForDiscard()) {
            while (input.hasNext()) {
                Token next = input.next();
                if (type.isInstance(next) && predicate.test((TokenType) type.cast(next))) break;
                tokens.add(next);
            }
        }
        return tokens;
    }

    default <TokenType extends Token> boolean hasUpcoming(Class<? super TokenType> type, TokenStream input,
                                                          Predicate<TokenType> predicate) {
        try (Mark _ = input.markForReset()) {
            while (input.hasNext()) {
                Token next = input.next();
                if (type.isInstance(next) && predicate.test((TokenType) type.cast(next))) return true;
            }
        }
        return false;
    }

    default <TokenType extends Token> TokenType find(Class<? super TokenType> type, TokenStream input)
    throws ParsingException {
        if (!input.hasNext()) throw new ParsingException("No " + type.getName() + " remaining.");
        final Token token = input.next();
        if (type.isInstance(token)) return (TokenType) token;
        throw new ParsingException("Expected " + type.getName() + " but got " + token);
    }

    default void keyword(String keyword, TokenStream input) throws ParsingException {
        if (input.hasNext()) {
            final Token token = input.next();
            if (token instanceof WordLikeToken word) {
                String value = word.value();
                if (value.equals(keyword)) return;
                throw new ParsingException("Expected " + keyword + " keyword, got " + value);
            }
        }
        throw new ParsingException("Expected " + keyword + " keyword, got nothing.");
    }

    default <TokenType extends Token> boolean expect(Class<? super TokenType> type, TokenStream input,
                                                     Predicate<TokenType> predicate)
    throws ParsingException {
        if (!input.hasNext()) return false;
        try (Mark mark = input.markForReset()) {
            final Token token = input.next();
            if (type.isInstance(token) && predicate.test((TokenType) type.cast(token))) {
                mark.discard();
                return true;
            }
        }
        return false;
    }

    default Iterable<Parser> parsers(Parser outer, Unit unit) {
        if (outer == this) throw new IllegalArgumentException("outer parser already set");
        return outer.parsers(outer, unit);
    }

}
