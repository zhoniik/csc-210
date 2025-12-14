import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Agent‐based simulation where rabbits behave autonomously.
 * Their behavior emerges solely from interactions with a limited food resource (carrots),
 * without explicitly coding logistic map parameters.
 * The simulation output is saved to a CSV file.
 */
public class RabbitAgentSimulationEmergent {

    public static void main(String[] args) {
        // Simulation parameters
        int initialPopulation = 50;           // Starting number of rabbits
        int carrotSupplyPerGeneration = 100;  // Total carrots available each generation
        int generations = 50;                 // Number of generations to simulate

        Ecosystem ecosystem = new Ecosystem(initialPopulation, carrotSupplyPerGeneration);

        // Define the output file name.
        String outputFilename = "simulation_output.csv";

        // Write simulation data to a CSV file.
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilename))) {
            // Write CSV header.
            writer.println("Generation,Population");

            // Run simulation for the given number of generations.
            for (int gen = 0; gen < generations; gen++) {
                writer.println(gen + "," + ecosystem.getPopulation());
                ecosystem.simulateGeneration();
            }
            System.out.println("Simulation complete! Output written to " + outputFilename);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}

/**
 * The Rabbit class encapsulates all behaviors of the rabbit agents.
 */
class Rabbit {
    private static int nextId = 0;
    private int id;
    private double energy; // Energy represents the rabbit's health and ability to reproduce

    // Each rabbit is born with an initial energy of 1.0.
    public Rabbit() {
        this.id = nextId++;
        this.energy = 1.0;
    }

    public double getEnergy() {
        return energy;
    }

    /**
     * The rabbit forages for food. If a carrot is available in the supply, it eats one and gains energy.
     *
     * @param supply The shared carrot supply.
     */
    public void forage(CarrotSupply supply) {
        if (supply.takeCarrot()) {
            // Gain energy from eating a carrot
            energy += 1.0;
        }
    }

    /**
     * The rabbit metabolizes, which reduces its energy.
     * This represents the cost of living.
     */
    public void metabolize() {
        energy -= 0.5; // Metabolic cost per generation
    }

    /**
     * Determines if the rabbit is still alive (energy > 0).
     *
     * @return true if alive.
     */
    public boolean isAlive() {
        return energy > 0;
    }

    /**
     * The rabbit is willing to mate if it has enough energy.
     *
     * @return true if energy is high enough for reproduction.
     */
    public boolean isWillingToMate() {
        return energy >= 2.0;
    }

    /**
     * When two rabbits mate, they each invest energy into reproduction,
     * and together they produce one offspring.
     *
     * @param partner The mate.
     * @return A new Rabbit (offspring) if mating occurs; otherwise, null.
     */
    public Rabbit mateWith(Rabbit partner) {
        if (this.isWillingToMate() && partner.isWillingToMate()) {
            // Both rabbits invest energy into reproduction.
            this.energy -= 1.0;
            partner.energy -= 1.0;
            // Offspring starts with the baseline energy.
            return new Rabbit();
        }
        return null;
    }
}

/**
 * The CarrotSupply class represents the limiting food resource.
 * Each generation, a fixed number of carrots is available.
 */
class CarrotSupply {
    private int availableCarrots;

    public CarrotSupply(int count) {
        availableCarrots = count;
    }

    /**
     * Attempt to take a carrot from the supply.
     *
     * @return true if a carrot was taken, false if none remain.
     */
    public boolean takeCarrot() {
        if (availableCarrots > 0) {
            availableCarrots--;
            return true;
        }
        return false;
    }
}

/**
 * The Ecosystem class manages the rabbit population and food resource.
 * Over each generation, rabbits forage, metabolize, and mate.
 * Emergent population dynamics arise from competition for a limited supply of carrots.
 */
class Ecosystem {
    private ArrayList<Rabbit> rabbits;
    private int carrotSupplyPerGen;

    public Ecosystem(int initialPopulation, int carrotSupplyPerGen) {
        this.carrotSupplyPerGen = carrotSupplyPerGen;
        rabbits = new ArrayList<>();
        for (int i = 0; i < initialPopulation; i++) {
            rabbits.add(new Rabbit());
        }
    }

    public int getPopulation() {
        return rabbits.size();
    }

    /**
     * Simulate one generation:
     * 1. Reset the carrot supply.
     * 2. Each rabbit forages for food.
     * 3. Rabbits lose energy due to metabolism.
     * 4. Rabbits with insufficient energy die.
     * 5. Rabbits that are sufficiently energized mate to produce offspring.
     */
    public void simulateGeneration() {
        // Reset carrot supply for this generation.
        CarrotSupply supply = new CarrotSupply(carrotSupplyPerGen);

        // Rabbits forage in random order.
        Collections.shuffle(rabbits);
        for (Rabbit r : rabbits) {
            r.forage(supply);
        }

        // All rabbits metabolize.
        for (Rabbit r : rabbits) {
            r.metabolize();
        }

        // Remove rabbits that have died (energy <= 0).
        Iterator<Rabbit> iter = rabbits.iterator();
        while (iter.hasNext()) {
            Rabbit r = iter.next();
            if (!r.isAlive()) {
                iter.remove();
            }
        }

        // Mating phase: rabbits that are willing to mate pair up randomly.
        ArrayList<Rabbit> offspring = new ArrayList<>();
        Collections.shuffle(rabbits);
        for (int i = 0; i < rabbits.size() - 1; i += 2) {
            Rabbit r1 = rabbits.get(i);
            Rabbit r2 = rabbits.get(i + 1);
            if (r1.isWillingToMate() && r2.isWillingToMate()) {
                Rabbit child = r1.mateWith(r2);
                if (child != null) {
                    offspring.add(child);
                }
            }
        }

        // Add the offspring to the population.
        rabbits.addAll(offspring);
    }
}

