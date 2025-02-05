package mx.kenzie.whilezie.parser;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Mark;
import mx.kenzie.whilezie.lexer.TokenList;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.lexer.token.StructureToken;
import mx.kenzie.whilezie.model.Model;

import java.util.ArrayList;
import java.util.List;

public interface CSVParser extends Parser {

	default Model[] parseCSVs(Parser outer, TokenList list, Unit unit) throws ParsingException {
		final TokenStream stream = new TokenStream(list);
		final List<Model> models = new ArrayList<>();
		outer:
		while (stream.hasNext()) {
			TokenList until = new TokenList();
			do {
				try (Mark mark = stream.markForReset()) {
					until.addAll(this.getEverythingUntil(StructureToken.class, stream, token -> token.symbol() == ','));
					try {
						final Model parsed = this.parse(outer, unit, new TokenStream(until), true);
						mark.discard();
						models.add(parsed);
						continue outer;
					} catch (ParsingException e) {
						until = this.take(stream, until.size() + 1);
					}
				}
			} while (stream.hasNext());
		}
		return models.toArray(new Model[0]);
	}

}
