package mx.kenzie.whilezie.parser.extensions;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Mark;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.WordLikeToken;
import mx.kenzie.whilezie.model.Literal;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.statement.ModelSwitch;
import mx.kenzie.whilezie.parser.Keywords;
import mx.kenzie.whilezie.parser.Parser;
import mx.kenzie.whilezie.parser.Unit;

import java.util.ArrayList;
import java.util.List;

public record SwitchParser() implements Parser {

    @Override
    public Model parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
        Position position = input.here();
        this.keyword(Keywords.SWITCH, input);
        Model check = this.parse(outer, Unit.EXPRESSION, input, false);

        List<ModelSwitch.Case> cases = new ArrayList<>();

        boolean shouldTryAgain = true;

        do {
            try (Mark mark = input.markForReset()) {
                Position here = input.here();
                WordLikeToken word = this.find(WordLikeToken.class, input);
                switch (word.value()) {
                    case Keywords.DEFAULT -> {
                        mark.discard();
                        shouldTryAgain = false;
                        Model then = this.parse(outer, Unit.STATEMENT, input, false);
                        cases.add(new ModelSwitch.Case(here, null, then));
                    }
                    case Keywords.CASE -> {
                        mark.discard();
                        Model condition = this.parse(outer, Unit.EXPRESSION, input, false);
                        if (!(condition instanceof Literal))
                            throw new ParsingException("Expected a literal for case check but found " + condition);
                        Model statement = this.parse(outer, Unit.STATEMENT, input, false);
                        cases.add(new ModelSwitch.Case(here, condition, statement));
                    }
                    default -> throw new ParsingException("Unsupported switch case keyword: " + word.value());
                }
            }

        } while (shouldTryAgain);
        return new ModelSwitch(position, check, cases.toArray(new ModelSwitch.Case[0]));
    }

}
