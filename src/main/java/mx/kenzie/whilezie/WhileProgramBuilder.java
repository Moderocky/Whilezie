package mx.kenzie.whilezie;

import mx.kenzie.whilezie.compiler.Compiler;
import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.error.ParsingException;
import mx.kenzie.whilezie.lexer.Lexer;
import mx.kenzie.whilezie.lexer.TokenList;
import mx.kenzie.whilezie.lexer.TokenStream;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.model.root.ModelProgram;
import mx.kenzie.whilezie.parser.MainParser;
import mx.kenzie.whilezie.parser.Unit;
import org.valross.foundation.Loader;
import org.valross.foundation.assembler.ClassFile;
import org.valross.foundation.assembler.tool.Access;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.MethodBuilder;
import org.valross.foundation.detail.Type;
import org.valross.foundation.detail.TypeHint;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.valross.foundation.assembler.code.OpCode.*;
import static org.valross.foundation.detail.Version.JAVA_22;
import static org.valross.foundation.detail.Version.RELEASE;

public class WhileProgramBuilder {

    protected final TypeHint type;
    protected Compiler compiler;
    protected MainParser parser;
    protected Set<String> macros;
    protected Loader loader;

    protected WhileProgramBuilder(Compiler compiler, MainParser parser) {
        this.compiler = compiler;
        this.parser = parser;
        this.macros = new LinkedHashSet<>();
        this.type = compiler.asType();
    }

    public WhileProgramBuilder(String programName) {
        this(new Compiler(Type.of("while", programName)), new MainParser());
    }

    public WhileProgramBuilder() {
        this("Program");
    }

    public WhileProgramBuilder includeDefaultSyntax() {
        MainParser.includeDefaultLanguageSet(parser);
        return this;
    }

    public WhileProgramBuilder includeMacros() {
        MainParser.includeMacro(parser);
        return this;
    }

    public WhileProgramBuilder includeIfElse() {
        MainParser.includeIfElse(parser);
        return this;
    }

    public WhileProgramBuilder includeExtendedLiterals() {
        MainParser.includeExtendedLiteralSet(parser);
        return this;
    }

    public WhileProgramBuilder loadMacro(String source) throws CompilingException, ParsingException, IOException {
        return this.loadMacro(new StringReader(source));
    }

    public WhileProgramBuilder loadMacro(InputStream source) throws CompilingException, ParsingException, IOException {
        return this.loadMacro(new InputStreamReader(source));
    }

    public WhileProgramBuilder loadMacro(Reader source) throws IOException, ParsingException, CompilingException {
        for (ModelProgram program : this.readPrograms(source)) {
            if (macros.contains(program.name()))
                throw new IllegalArgumentException("Macro already defined: " + program.name());
            this.compiler.insertMacro(program);
            this.macros.add(program.name());
        }
        return this;
    }

    public WhileProgram build() {
        Class<?> loaded = this.asLoadedClass(this.classLoader());
        Map<String, Macro> macros = new LinkedHashMap<>();
        for (String macro : this.macros) {
            try {
                macros.put(macro, this.compileBridgeStub(loaded.getDeclaredMethod(macro, Tree.class)));
            } catch (NoSuchMethodException e) {
                throw new AssertionError("Macro '" + macro + "' was not present in finished program.");
            }
        }
        return new WhileProgram(loaded, macros);
    }

    public ClassFile asClassFile() {
        return compiler.compile();
    }

    public Class<?> asLoadedClass() {
        return this.asLoadedClass(Loader.createDefault());
    }

    public Class<?> asLoadedClass(Loader loader) {
        return loader.loadClass(this.asClassFile());
    }

    public void compileTo(OutputStream stream) throws IOException {
        this.asClassFile().write(stream);
    }

    public void compileTo(File file) throws CompilingException, IOException {
        try (OutputStream stream = new FileOutputStream(file)) {
            this.compileTo(stream);
        }
    }

    protected Loader classLoader() {
        if (loader == null) loader = Loader.createDefault();
        return loader;
    }

    protected Iterable<ModelProgram> readPrograms(Reader reader) throws IOException, ParsingException {
        final Lexer lexer = new Lexer(new BufferedReader(reader));
        final TokenList tokens = lexer.run();
        tokens.removeWhitespace();
        assert tokens.hasMatchingBrackets();

        List<ModelProgram> programs = new ArrayList<>();
        TokenStream input = new TokenStream(tokens);
        while (input.hasNext()) {
            Model model = parser.parse(parser, Unit.ROOT, input, false);
            if (model instanceof ModelProgram program)
                programs.add(program);
        }
        return programs;
    }

    protected Macro compileBridgeStub(Method method) {
        ClassFileBuilder builder = new ClassFileBuilder(JAVA_22, RELEASE).addModifiers(Access.PUBLIC, Access.SYNTHETIC);
        builder.setType(Type.of("while", "Macro_" + method.getName()));
        builder.setInterfaces(Macro.class);

        // Easier to give it a constructor than MALLOC it
        builder.constructor().setModifiers(Access.PUBLIC)
            .code().write(ALOAD_0, INVOKESPECIAL.constructor(Object.class), RETURN);

        // Bridge stub for the run method
        MethodBuilder run = builder.method().named("run").returns(Tree.class).parameters(Tree.class);
        run.setModifiers(Access.PUBLIC, Access.SYNTHETIC, Access.BRIDGE);
        CodeBuilder code = run.code();
        code.write(ALOAD_1, INVOKESTATIC.method(type, Tree.class, method.getName(), Tree.class), ARETURN);
        Class<?> aClass = this.classLoader().loadClass(builder.build());
        try {
            return (Macro) aClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
