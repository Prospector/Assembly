package teamreborn.assembly.util;

@FunctionalInterface
public interface ObjectConsumer<T> {

	void accept(T value);
}
