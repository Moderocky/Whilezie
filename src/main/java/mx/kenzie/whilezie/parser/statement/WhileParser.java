package mx.kenzie.whilezie.parser.statement;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.statement.ModelWhile;
import mx.kenzie.whilezie.parser.Keywords;
import mx.kenzie.whilezie.parser.Parser;
import mx.kenzie.whilezie.parser.Unit;

public record WhileParser() implements Parser {

    @Override
    public Model parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
        Position position = input.here();
        this.keyword(Keywords.WHILE, input);
        Model condition = this.parse(outer, Unit.EXPRESSION, input, false);
//        this.keyword("do", input);
        Model then = this.parse(outer, Unit.STATEMENT, input, all);
        return new ModelWhile(position, condition, then);
    }

}
