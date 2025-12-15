public class Animal {
    private final String name;
    public Animal(String name) { this.name = name; }
    public String name() { return name; }
    @Override public String toString() { return getClass().getSimpleName() + "(" + name + ")"; }
}

