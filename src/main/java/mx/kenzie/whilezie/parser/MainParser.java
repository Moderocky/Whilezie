package mx.kenzie.whilezie.parser;

import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.model.Document;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.parser.expression.*;
import mx.kenzie.whilezie.parser.root.ProgramParser;
import mx.kenzie.whilezie.parser.statement.*;

import java.util.*;

public class MainParser implements Parser {

	protected final Map<Unit, Collection<Parser>> parsers = new EnumMap<>(Unit.class);

	public static void includeDefaultLanguageSet(MainParser parser) {
		parser.include(Unit.ROOT, new ProgramParser());

		parser.include(Unit.EXPRESSION, new NilParser());
		parser.include(Unit.EXPRESSION, new HeadParser());
		parser.include(Unit.EXPRESSION, new TailParser());
		parser.include(Unit.EXPRESSION, new MacroParser());
		parser.include(Unit.EXPRESSION, new ConstructParser());
		parser.include(Unit.EXPRESSION, new VariableParser());

		parser.include(Unit.STATEMENT, new AssignmentParser());
		parser.include(Unit.STATEMENT, new BlockParser());
		parser.include(Unit.STATEMENT, new WhileParser());
	}
	public static void includeMacro(MainParser parser) {
		parser.include(Unit.EXPRESSION, new MacroParser());
	}

	public static void includeIfElse(MainParser parser) {
		parser.include(Unit.STATEMENT, new IfElseParser());
		parser.include(Unit.STATEMENT, new IfParser());
	}

	@Override
	public Model parse(Parser outer, TokenStream input, boolean all) throws ParsingException {
		return this.parse(this, Unit.ROOT, input, all);
	}

	@Override
	public Iterable<Parser> parsers(Parser outer, Unit unit) {
		return parsers.getOrDefault(unit, Collections.emptyList());
	}

	public Document parseDocument(TokenStream input) throws ParsingException {
		List<Model> list = new ArrayList<>();
		while (input.hasNext()) {
			list.add(this.parse(this, Unit.ROOT, input, false));
		}
		return new Document(list.toArray(new Model[0]));
	}

	public void include(Unit unit, Parser parser) {
		this.parsers.computeIfAbsent(unit, _ -> new ArrayList<>()).add(parser);
	}

}
