package mx.kenzie.whilezie.lexer;

import mx.kenzie.whilezie.lexer.token.StructureToken;
import mx.kenzie.whilezie.lexer.token.Token;
import mx.kenzie.whilezie.lexer.token.WhitespaceToken;

import java.util.LinkedList;

public class TokenList extends LinkedList<Token> {

	public void removeWhitespace() {
		this.removeIf(WhitespaceToken.class::isInstance);
	}

	public boolean hasMatchingBrackets() {
		int round = 0, square = 0, curly = 0;
		for (Token token : this) {
			if (!(token instanceof StructureToken structure)) continue;
			switch (structure.symbol()) {
				case '(' -> ++round;
				case '[' -> ++square;
				case '{' -> ++curly;
				case ')' -> --round;
				case ']' -> --square;
				case '}' -> --curly;
			}
		}
		return round == 0 && square == 0 && curly == 0;
	}

}
