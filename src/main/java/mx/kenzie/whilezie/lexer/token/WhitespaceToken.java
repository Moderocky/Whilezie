package mx.kenzie.whilezie.lexer.token;

public record WhitespaceToken(int line, int position) implements Token {

    @Override
    public String print() {
        return " ";
    }

}
