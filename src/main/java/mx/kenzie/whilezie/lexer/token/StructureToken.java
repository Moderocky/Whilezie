package mx.kenzie.whilezie.lexer.token;

public record StructureToken(char symbol, int line, int position) implements Token {

	@Override
	public String print() {
		return Character.toString(symbol);
	}

}
