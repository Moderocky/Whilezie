package mx.kenzie.whilezie;

import mx.kenzie.whilezie.error.CompilingException;
import mx.kenzie.whilezie.error.ParsingException;

import java.io.*;
import java.util.Collections;
import java.util.Map;

public class WhileProgram {

    private final Class<?> loaded;
    private final Map<String, Macro> macros;


    protected WhileProgram(Class<?> loaded, Map<String, Macro> macros) {
        this.loaded = loaded;
        this.macros = Collections.unmodifiableMap(macros);
    }

    public static Macro load(String source) {
        return load(new StringReader(source));
    }

    public static Macro load(InputStream source) {
        return load(new InputStreamReader(source));
    }

    public static Macro load(Reader source) {
        try {
            return new WhileProgramBuilder().includeDefaultSyntax().loadMacros(source)
                .build().macros().values().iterator().next();
        } catch (CompilingException | ParsingException | IOException ignored) {
            return null;
        }
    }

    public Class<?> getLoadedClass() {
        return loaded;
    }

    public Macro macro(String name) {
        return macros.get(name);
    }

    public Map<String, Macro> macros() {
        return macros;
    }

}
