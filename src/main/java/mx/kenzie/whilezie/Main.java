package mx.kenzie.whilezie;

public class Main {

    public static void main(String... args) throws Throwable {
        new WhileProgramBuilder().includeDefaultSyntax().loadMacros(System.in).compileTo(System.out);
    }

}