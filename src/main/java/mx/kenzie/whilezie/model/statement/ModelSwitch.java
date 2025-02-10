package mx.kenzie.whilezie.model.statement;

import mx.kenzie.whilezie.Tree;
import mx.kenzie.whilezie.compiler.VariableTable;
import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.lexer.Position;
import mx.kenzie.whilezie.model.Literal;
import mx.kenzie.whilezie.model.Model;
import mx.kenzie.whilezie.parser.Keywords;
import org.valross.foundation.assembler.code.Branch;
import org.valross.foundation.assembler.code.LookupSwitchCode;
import org.valross.foundation.assembler.code.OpCode;
import org.valross.foundation.assembler.tool.CodeBuilder;

import java.io.PrintStream;
import java.util.Objects;

import static org.valross.foundation.assembler.code.OpCode.*;

public record ModelSwitch(Position position, Model expression, Case... cases)
    implements Model {

    public record Case(Position position, Model condition, Model statement) {

        public void print(PrintStream stream) {
            if (condition != null) {
                stream.print(Keywords.CASE);
                stream.print(' ');
                condition.print(stream);
            } else {
                stream.print(Keywords.DEFAULT);
            }
            stream.print(' ');
            statement.print(stream);
        }

        public void compile(CodeBuilder code, VariableTable table, LookupSwitchCode.Builder builder, Branch branch, Branch end) throws CompilingException {
            if (condition != null) {
                code.write(branch);
                Literal literal = (Literal) condition;
                code.write(LDC.value(literal.value()));
                code.write(INVOKESTATIC.method(Objects.class, boolean.class, "equals", Object.class, Object.class));
                code.write(IFEQ.jump(end));
            } else {
                code.write(branch);
                code.write(POP);
                code.write(NOP);
            }
            statement.compile(code, table);
            code.write(GOTO.jump(end));
        }

    }

    @Override
    public void print(PrintStream stream) {
        stream.print(Keywords.SWITCH);
        stream.print(' ');
        expression.print(stream);
        stream.println();
        for (Case aCase : cases) {
            aCase.print(stream);
            stream.println();
        }
    }

    @Override
    public void compile(CodeBuilder code, VariableTable table) throws CompilingException {
        Branch end = new Branch(), def = null;
        Branch[] branches = new Branch[cases.length];
        for (int i = 0; i < branches.length; i++) {
            branches[i] = new Branch();
        }
        boolean hasSeenDefault = false;
        LookupSwitchCode.Builder builder = OpCode.LOOKUPSWITCH.new Builder();
        for (int i = 0; i < cases.length; i++) {
            Case aCase = cases[i];
            Branch branch = branches[i];
            if (aCase.condition != null) {
                hasSeenDefault = true;
                Literal literal = (Literal) aCase.condition;
                Tree value = literal.value();
                final int hash = Tree.hashCode(value);
                builder.test(hash, branch);
            } else {
                def = branch;
                builder.defaultCase(branch);
            }
        }
        if (!hasSeenDefault) {
            def = new Branch();
            builder.defaultCase(def);
        }
        expression.compile(code, table);
        code.write(DUP);
        code.write(INVOKESTATIC.method(false, Tree.class, int.class, "hashCode", Tree.class));
        code.write(builder);
        for (int i = 0; i < cases.length; i++) {
            Branch branch = branches[i];
            Case aCase = cases[i];
            aCase.compile(code, table, builder, branch, end);
        }
        if (!hasSeenDefault)
            new Case(new Position(0, 0), null, new ModelBlock())
                .compile(code, table, builder, def, end);
        code.write(end, NOP);
    }

    @Override
    public String toString() {
        return "if("
//            + condition + ": \n" + then + "\nelse " + otherwise
            + ")";
    }

}
