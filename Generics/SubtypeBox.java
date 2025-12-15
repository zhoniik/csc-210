/**
 * Holds only Animals (or subclasses). The <T extends Animal> bound enforces
 * that T is Animal or a subtype (e.g., Dog, Cat).
 */
public class SubtypeBox<T extends Animal> {
    private T animal;

    public void put(T animal) {
        this.animal = animal;
    }

    public T get() {
        return animal;
    }

    public String speak() {
        return "I hold an " + animal;
    }
}

