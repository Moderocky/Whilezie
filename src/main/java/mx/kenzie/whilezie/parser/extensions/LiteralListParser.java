package mx.kenzie.whilezie.parser.extensions;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.lexer.TokenList;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.expression.ModelConstruct;
import mx.kenzie.whilezie.model.expression.ModelNil;
import mx.kenzie.whilezie.parser.BracketedParser;
import mx.kenzie.whilezie.parser.CSVParser;
import mx.kenzie.whilezie.parser.Parser;
import mx.kenzie.whilezie.parser.Unit;

public record LiteralListParser() implements Parser, BracketedParser, CSVParser {

	@Override
	public Model parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
		Position position = input.here();
		TokenList list = this.findMatching(input, '[', ']');
		if (list.isEmpty()) throw new ParsingException("Nothing inside the brackets.");
		if (all && input.hasNext()) throw new ParsingException("More remaining after brackets.");

		Model[] models = this.parseCSVs(outer, list, Unit.EXPRESSION);
		Model tail = new ModelNil(position);
		for (Model model : models)
			tail = new ModelConstruct(position, model, tail);

		return tail;
	}

}
