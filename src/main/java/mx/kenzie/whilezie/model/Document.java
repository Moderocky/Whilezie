package mx.kenzie.whilezie.model;

import java.util.Iterator;
import java.util.List;

public record Document(Model... rootElements) implements Iterable<Model> {

    @Override
    public Iterator<Model> iterator() {
        return List.of(rootElements).iterator();
    }

}
