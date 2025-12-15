import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        // 1) AnyBox: accepts anything
        AnyBox<String> bs = new AnyBox<>("hello");
        AnyBox<Integer> bi = new AnyBox<>(42);
        System.out.println(bs); // AnyBox[hello]
        System.out.println(bi); // AnyBox[42]

        // 2) SubtypeBox: only Animal or its children
        SubtypeBox<Animal> ba = new SubtypeBox<>();
        ba.put(new Animal("Generic"));
        System.out.println(ba.speak());

        SubtypeBox<Dog> bd = new SubtypeBox<>();
        bd.put(new Dog("Rex"));
        System.out.println(bd.speak());

        // The following would NOT compile:
        // SubtypeBox<String> nope = new SubtypeBox<>(); // String is not an Animal

        // 3) AncestorWriter: only accepts ANCESTORS of T on its API
        AncestorWriter<Dog> dogWriter = new AncestorWriter<>();

        Collection<Dog> dogBin = new ArrayList<>();
        Collection<Animal> animalBin = new ArrayList<>();
        Collection<Object> objectBin = new ArrayList<>();

        // OK: destination element type is Dog (same), Animal (ancestor), or Object (ancestor)
        dogWriter.writeAll(dogBin, new Dog("Spot"), new Dog("Rover"));
        dogWriter.writeAll(animalBin, new Dog("Fido"));
        dogWriter.writeAll(objectBin, new Dog("Buddy"));

        // Inspect results
        System.out.println("dogBin: " + dogBin);
        System.out.println("animalBin: " + animalBin);
        System.out.println("objectBin: " + objectBin);

        // More: moving a single item into an ancestor-typed collection
        Dog max = new Dog("Max");
        dogWriter.moveOne(max, animalBin); // OK: Animal is an ancestor of Dog
        System.out.println("animalBin after moveOne: " + animalBin);

        // But this would NOT compile (destination is NOT an ancestor of Dog):
        // Collection<Cat> catBin = new ArrayList<>();
        // dogWriter.writeAll(catBin, new Dog("Nope")); // Cat is not an ancestor of Dog

        // A common pattern: PECS — Producer Extends, Consumer Super.
        // - If you only read (producer), use ? extends T.
        // - If you only write (consumer), use ? super T.
        // Here, destination is a consumer of T, so we use ? super T.
    }
}

