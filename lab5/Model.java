import java.util.*;

interface TurnTaker { void takeTurn(); }

abstract class Creature implements TurnTaker {
    protected String species;
    protected int energy = 10;
    protected Tile tile;

    public Creature(String species) { this.species = species; }
    public void setTile(Tile t) { this.tile = t; }
    public String getSpecies() { return species; }
    public String toString() { return getClass().getSimpleName()+"("+species+", energy="+energy+")"; }
    public void takeTurn() { energy--; if (energy < 0) energy = 0; }
}

class Plant extends Creature {
    boolean perennial, woody, flowers, evergreen;

    interface Repro { void repro(Plant p, Tile t); }
    static class Seed implements Repro { public void repro(Plant p, Tile t){ if(t!=null && t.getNutrients()>3 && Math.random()<0.2) t.addCreature(new Plant(p.species)); } }
    static class Spore implements Repro { public void repro(Plant p, Tile t){ if(t!=null && t.getWater()>3 && Math.random()<0.25) t.addCreature(new Plant(p.species)); } }
    static class Clone implements Repro { public void repro(Plant p, Tile t){ if(t!=null && Math.random()<0.15) t.addCreature(new Plant(p.species)); } }

    private Repro repro = new Seed();
    private static Map<String,String> defs = new HashMap<String,String>();

    public Plant(String species) {
        super(species);
        this.perennial = true; this.woody = false; this.flowers = false; this.evergreen = false;
    }
    public Plant(String species, boolean perennial, boolean woody, boolean flowers, boolean evergreen) {
        super(species);
        this.perennial = perennial; this.woody = woody; this.flowers = flowers; this.evergreen = evergreen;
    }

    public void setReproduction(String type){
        String t = (type==null?"":type.toLowerCase());
        if (t.equals("seed")) repro = new Seed();
        else if (t.equals("spore")) repro = new Spore();
        else if (t.equals("clone")) repro = new Clone();
    }
    public void setReproduction(Repro r){ if(r!=null) repro = r; }

    public static void defineSpecies(String name, String def) throws Exception {
        if (defs.containsKey(name)) throw new Exception("Species exists: "+name);
        defs.put(name, def);
    }
    public static boolean hasSpecies(String name){ return defs.containsKey(name); }
    public static String getDef(String name){ return defs.get(name); }

    @Override
    public void takeTurn() {
        if (tile!=null && tile.getNutrients()>0){
            tile.setNutrients(tile.getNutrients()-1);
            energy++; if (energy>20) energy=20;
        }
        if (repro!=null) repro.repro(this, tile);
        if (energy>0) energy--;
    }
}

class Bird extends Creature {
    public Bird(String species){ super(species); }
    @Override public void takeTurn(){
        if (tile!=null){
            if (tile.getTemperature()>=15 && tile.getTemperature()<=30) energy+=2;
            else energy--;
        }
        if (energy>25) energy=25; if (energy<0) energy=0;
    }
}

class Mammal extends Creature {
    public Mammal(String species){ super(species); }
    @Override public void takeTurn(){
        if (tile!=null){
            if (tile.getNutrients()>2){ tile.setNutrients(tile.getNutrients()-2); energy+=2; }
            else energy--;
        }
        if (energy>30) energy=30; if (energy<0) energy=0;
    }
}

class Fish extends Creature {
    public Fish(String species){ super(species); }
    @Override public void takeTurn(){
        if (tile!=null){
            if (tile.getWater()>=5) energy+=2; else energy-=2;
        }
        if (energy>20) energy=20; if (energy<0) energy=0;
    }
}

class Tile implements TurnTaker {
    private ArrayList<Creature> list = new ArrayList<Creature>();
    private int water, temperature, nutrients;

    public Tile(int water, int temperature, int nutrients){
        this.water=water; this.temperature=temperature; this.nutrients=nutrients;
    }

    public void addCreature(Creature c){ if(c!=null) list.add(c); }
    public ArrayList<Creature> getCreatures(){ return list; }

    public int getWater(){ return water; }
    public void setWater(int w){ water=w; }
    public int getTemperature(){ return temperature; }
    public void setTemperature(int t){ temperature=t; }
    public int getNutrients(){ return nutrients; }
    public void setNutrients(int n){ nutrients=n; }

    public void takeTurn(){
        ArrayList<Creature> snap = new ArrayList<Creature>(list);
        for (int i=0;i<snap.size();i++){
            Creature c = snap.get(i);
            c.setTile(this);
            c.takeTurn();
        }
        if (nutrients<10 && Math.random()<0.2) nutrients++;
    }

    public String toString(){
        return "Tile(w="+water+", t="+temperature+", n="+nutrients+", creatures="+list.size()+")";
    }
}

class World implements TurnTaker {
    private Tile[][] grid;
    private int h,w;

    public World(int w, int h, int water, int temp, int nutr){
        this.w=w; this.h=h;
        grid = new Tile[h][w];
        for(int r=0;r<h;r++){
            for(int c=0;c<w;c++){
                grid[r][c] = new Tile(water,temp,nutr);
            }
        }
    }

    public Tile get(int r,int c){
        if(r<0||r>=h||c<0||c>=w) return null;
        return grid[r][c];
    }

    public void takeTurn(){
        for(int r=0;r<h;r++){
            for(int c=0;c<w;c++){
                grid[r][c].takeTurn();
            }
        }
    }

    public String summary(){
        String s="";
        for(int r=0;r<h;r++){
            for(int c=0;c<w;c++){
                s += grid[r][c].toString()+"\n";
            }
        }
        return s;
    }
}
