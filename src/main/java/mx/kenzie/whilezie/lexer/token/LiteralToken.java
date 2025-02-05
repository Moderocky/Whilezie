package mx.kenzie.whilezie.lexer.token;

public interface LiteralToken<Type> extends Token {

    Type value();

}
