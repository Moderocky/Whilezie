package mx.kenzie.whilezie.lexer;

import java.util.EmptyStackException;
import java.util.Stack;

public class IntStack extends Stack<Integer> {

    private int[] contents;
    private int size;

    public IntStack() {
        super();
        this.contents = new int[8];
    }

    protected void grow() {
        if (size + 1 < contents.length) return;
        final int[] array = new int[contents.length * 2];
        System.arraycopy(contents, 0, array, 0, contents.length);
        this.contents = array;
    }

    protected void shrink() {
        if (size < 64) return;
        if (size > contents.length / 4) return;
        final int[] array = new int[contents.length / 2];
        System.arraycopy(contents, 0, array, 0, contents.length);
        this.contents = array;
    }

    @Override
    public Integer push(Integer item) {
        this.grow();
        this.contents[size++] = item;
        return super.push(item);
    }

    @Override
    public Integer pop() {
        if (size == 0) throw new EmptyStackException();
        this.shrink();
        return contents[--size];
    }

    @Override
    public Integer peek() {
        if (size == 0) throw new EmptyStackException();
        return contents[size - 1];
    }

    @Override
    public boolean empty() {
        return size == 0;
    }

    @Override
    public int search(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            if (o.equals(contents[i])) return i;
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public synchronized boolean isEmpty() {
        return size == 0;
    }

}
