import java.io.*;
import java.util.*;

public class MainSim {
    static class SimpleJSON {
        String all="";
        SimpleJSON(String file){
            try{
                BufferedReader br=new BufferedReader(new FileReader(file));
                String line; StringBuilder sb=new StringBuilder();
                while((line=br.readLine())!=null) sb.append(line.trim());
                br.close();
                all=sb.toString();
            }catch(Exception e){}
        }
        int getInt(String key,int def){
            try{
                int i=all.indexOf("\""+key+"\"");
                if(i<0) return def;
                int colon=all.indexOf(":",i);
                int j=colon+1; String num="";
                while(j<all.length() && (Character.isDigit(all.charAt(j))||all.charAt(j)=='-')){ num+=all.charAt(j); j++; }
                if(num.length()==0) return def;
                return Integer.parseInt(num);
            }catch(Exception e){ return def; }
        }
        ArrayList<String> getArray(String key){
            ArrayList<String> out=new ArrayList<String>();
            int i=all.indexOf("\""+key+"\"");
            if(i<0) return out;
            int lb=all.indexOf("[",i), rb=all.indexOf("]",lb);
            if(lb<0||rb<0) return out;
            String inside=all.substring(lb+1,rb);
            String[] parts=inside.split(",");
            for(String p:parts){
                p=p.trim();
                if(p.startsWith("\"")&&p.endsWith("\"")&&p.length()>=2){
                    out.add(p.substring(1,p.length()-1));
                }
            }
            return out;
        }
    }

    public static void main(String[] args) {
        String cfg = (args.length>0? args[0] : "world.json");

        try{
            if(!Plant.hasSpecies("oak")) Plant.defineSpecies("oak","perennial woody seed");
            if(!Plant.hasSpecies("ivy")) Plant.defineSpecies("ivy","perennial soft clone");
            if(!Plant.hasSpecies("moss")) Plant.defineSpecies("moss","perennial soft spore");
        }catch(Exception e){}

        SimpleJSON j = new SimpleJSON(cfg);
        int W = j.getInt("width",3);
        int H = j.getInt("height",3);
        int water = j.getInt("water",5);
        int temp = j.getInt("temperature",20);
        int nutr = j.getInt("nutrients",5);

        World world = new World(W,H,water,temp,nutr);

        ArrayList<String> plants = j.getArray("plants");
        if(plants.isEmpty()){ plants.add("oak"); plants.add("ivy"); plants.add("moss"); }
        ArrayList<String> animals = j.getArray("animals");
        if(animals.isEmpty()){ animals.add("bird:sparrow"); animals.add("mammal:mouse"); animals.add("fish:carp"); }

        Random r = new Random();
        for(String sp: plants){
            int row=r.nextInt(H), col=r.nextInt(W);
            Plant p = new Plant(sp);
            if (sp.toLowerCase().contains("moss")) p.setReproduction("spore");
            if (sp.toLowerCase().contains("ivy")) p.setReproduction("clone");
            world.get(row,col).addCreature(p);
        }
        for(String a: animals){
            int row=r.nextInt(H), col=r.nextInt(W);
            Creature c=null;
            if(a.startsWith("bird:")) c=new Bird(a.substring(5));
            else if(a.startsWith("mammal:")) c=new Mammal(a.substring(7));
            else if(a.startsWith("fish:")) c=new Fish(a.substring(5));
            if(c!=null) world.get(row,col).addCreature(c);
        }

        for(int step=0; step<100; step++){
            world.takeTurn();
        }

        System.out.print(world.summary());
    }
}
