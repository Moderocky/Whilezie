package mx.kenzie.whilezie.compiler;

import mx.kenzie.whilezie.Tree;
import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.model.root.ModelProgram;
import org.valross.foundation.Loader;
import org.valross.foundation.assembler.ClassFile;
import org.valross.foundation.assembler.code.OpCode;
import org.valross.foundation.assembler.tool.Access;
import org.valross.foundation.assembler.tool.ClassFileBuilder;
import org.valross.foundation.assembler.tool.CodeBuilder;
import org.valross.foundation.assembler.tool.MethodBuilder;
import org.valross.foundation.detail.Type;
import org.valross.foundation.detail.TypeHint;

import static org.valross.foundation.detail.Version.JAVA_22;
import static org.valross.foundation.detail.Version.RELEASE;

public class Compiler {

    private final ClassFileBuilder builder;

    public Compiler(Type classPath) {
        this.builder = new ClassFileBuilder(JAVA_22, RELEASE);
        this.builder.addModifiers(Access.PUBLIC, Access.SYNTHETIC);
        this.builder.setType(classPath);
    }

    public void insertMacro(ModelProgram program) throws CompilingException {
        MethodBuilder method = this.builder.method()
            .setModifiers(Access.PUBLIC, Access.STATIC)
            .named(program.name());
        method.parameters(Tree.class).returns(Tree.class);
        CodeBuilder code = method.code();
        VariableTable table = new VariableTable();
        table.register(program.input());

        program.body().compile(code, table);

        if (!table.contains(program.output()))
            throw new CompilingException("Output variable '" + program.output() + "' was never assigned.");

        code.write(OpCode.ALOAD.var(Tree.class, table.indexOf(program.output())));
        code.write(OpCode.ARETURN);

    }

    public ClassFileBuilder getBuilder() {
        return builder;
    }

    public ClassFile compile() {
        return this.builder.build();
    }

    public TypeHint asType() {
        return this.builder;
    }

    public Class<?> compileAndLoad() throws CompilingException {
        ClassFile file = this.builder.build();

        Loader loader = Loader.createDefault();
        return loader.loadClass(file);
    }


}
