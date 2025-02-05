package mx.kenzie.whilezie.parser.statement;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenList;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.statement.ModelBlock;
import mx.kenzie.whilezie.parser.BracketedParser;
import mx.kenzie.whilezie.parser.Parser;
import mx.kenzie.whilezie.parser.Unit;

import java.util.ArrayList;
import java.util.List;

public record BlockParser() implements Parser, BracketedParser {

    @Override
    public ModelBlock parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
        Position position = input.here();
        TokenList list = this.findMatching(input, '{', '}');
        TokenStream stream = new TokenStream(list);
        List<Model> statements = new ArrayList<>();
        while (stream.hasNext()) {
            Model statement = outer.parse(outer, Unit.STATEMENT, stream);
            statements.add(statement);
        }
        return new ModelBlock(position, statements.toArray(new Model[0]));
    }

}
