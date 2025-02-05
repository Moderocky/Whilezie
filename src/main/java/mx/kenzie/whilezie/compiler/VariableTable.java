package mx.kenzie.whilezie.compiler;

import mx.kenzie.whilezie.error.CompilingException;

import java.util.Iterator;
import java.util.Objects;

public class VariableTable implements Iterable<String> {

	protected final String[] names = new String[1 << 8];

	protected int size;

	public int size() {
		return size;
	}

	public boolean contains(String name) {
		return this.indexOf(name) >= 0;
	}

	@SuppressWarnings("UnusedReturnValue")
	public boolean register(String string) throws CompilingException {
		if (size >= names.length)
			throw new CompilingException("Not enough variable slots available in the register.");
		if (this.contains(string))
			return false;
		this.names[size++] = string;
		return true;
	}

	public int indexOf(Object o) {
		final int min = Math.min(size, names.length);
		for (int i = 0; i < min; i++)
			if (Objects.equals(names[i], o)) return i;
		return -1;
	}

	@Override
	public Iterator<String> iterator() {
		return new Iterator<>() {

			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < size;
			}

			@Override
			public String next() {
				return names[index++];
			}

		};
	}
}
