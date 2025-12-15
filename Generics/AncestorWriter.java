import java.util.Collection;
import java.util.Objects;

/**
 * Demonstrates lower-bounded wildcards: methods accept Collections whose
 * element type is an ANCESTOR of T (e.g., for T=Dog, we can pass
 * Collection<Animal> or Collection<Object>).
 *
 * Note: you can't declare a class like class Box<? super T>.
 * Lower bounds belong on wildcards in method signatures, not on type params.
 */
public class AncestorWriter<T> {

    /**
     * Add items of type T into a destination that is typed to any ancestor of T.
     */
    public void writeAll(Collection<? super T> dst, T... items) {
        for (T t : items) {
            dst.add(Objects.requireNonNull(t));
        }
    }

    /**
     * Copy from a source of exactly T into a destination of any ancestor of T.
     */
    public void moveOne(T src, Collection<? super T> dst) {
        dst.add(Objects.requireNonNull(src));
    }
}

