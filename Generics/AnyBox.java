public class AnyBox<T> {
    private T value;

    public AnyBox() {}

    public AnyBox(T initial) {
        this.value = initial;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    @Override
    public String toString() {
        return "AnyBox[" + value + "]";
    }
}

