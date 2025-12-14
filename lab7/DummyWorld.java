import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DummyWorld implements WorldAdapter {
    private int rows;
    private int cols;

    private int[][] water;
    private int[][] temp;
    private int[][] nutri;
    private List<String>[][] creatures;

    private String lastLog = "";
    private int turn = 0;

    private int[][] water0, temp0, nutri0;
    private List<String>[][] creatures0;

    @SuppressWarnings("unchecked")
    public DummyWorld(int r, int c) {
        rows = r; cols = c;
        water = new int[r][c];
        temp = new int[r][c];
        nutri = new int[r][c];
        creatures = new List[r][c];

        Random random = new Random(1);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                water[i][j] = random.nextInt(5);
                temp[i][j] = 10 + random.nextInt(21);
                nutri[i][j] = random.nextInt(5);
                creatures[i][j] = new ArrayList<>();
                if (random.nextDouble() < 0.25) creatures[i][j].add("Plant");
                if (random.nextDouble() < 0.10) creatures[i][j].add("Fish");
                if (random.nextDouble() < 0.08)  creatures[i][j].add("Bird");
            }
        }
        snapshot();
    }

    private void snapshot() {
        water0 = copy2D(water);
        temp0 = copy2D(temp);
        nutri0 = copy2D(nutri);
        creatures0 = copyListGrid(creatures);
    }

    private int[][] copy2D(int[][] a) {
        int[][] b = new int[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) b[i] = Arrays.copyOf(a[i], a[i].length);
        return b;
    }

    @SuppressWarnings("unchecked")
    private List<String>[][] copyListGrid(List<String>[][] src) {
        int r = src.length, c = src[0].length;
        List<String>[][] dst = new List[r][c];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                dst[i][j] = new ArrayList<>(src[i][j]);
            }
        }
        return dst;
    }

    @Override
    public void takeTurn() {
        turn++;
        lastLog = "turn " + turn + ": plants spread to random neighbors";
        Random rand = new Random(turn * 31L);
        for (int k = 0; k < rows * cols / 6; k++) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);
            int nr = Math.max(0, Math.min(rows - 1, r + (rand.nextInt(3) - 1)));
            int nc = Math.max(0, Math.min(cols - 1, c + (rand.nextInt(3) - 1)));
            if (!creatures[nr][nc].contains("Plant")) creatures[nr][nc].add("Plant");
        }
    }

    @Override
    public String getTurnLog() { return lastLog; }

    @Override
    public int getTurnNumber() { return turn; }

    @Override
    public String getCellShort(int r, int c) {
        if (creatures[r][c].isEmpty()) return ".";
        return String.valueOf(creatures[r][c].size());
    }

    @Override
    public String getCellSummary(int r, int c) {
        return "water=" + water[r][c] + ", temp=" + temp[r][c] + ", nutrients=" + nutri[r][c];
    }

    @Override
    public String getCreaturesInfo(int r, int c) {
        if (creatures[r][c].isEmpty()) return "no creatures";
        return String.join(", ", creatures[r][c]);
    }

    @Override
    public void loadFromFile(File f) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            for (List<String>[] row : creatures) for (List<String> cell : row) cell.clear();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\s+");
                int r = Integer.parseInt(parts[0]);
                int c = Integer.parseInt(parts[1]);
                if (parts.length > 2) {
                    String[] list = parts[2].split(",");
                    for (String s : list) creatures[r][c].add(s.trim());
                }
            }
            turn = 0;
            lastLog = "loaded from file";
            snapshot();
        } catch (Exception ex) {
            lastLog = "load error: " + ex.getMessage();
        }
    }

    @Override
    public void saveToFile(File f) {
        try (PrintWriter pw = new PrintWriter(f)) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (!creatures[r][c].isEmpty()) {
                        pw.println(r + " " + c + " " + String.join(",", creatures[r][c]));
                    }
                }
            }
            lastLog = "saved to file";
        } catch (Exception ex) {
            lastLog = "save error: " + ex.getMessage();
        }
    }

    @Override
    public void reset() {
        water = copy2D(water0);
        temp = copy2D(temp0);
        nutri = copy2D(nutri0);
        creatures = copyListGrid(creatures0);
        turn = 0;
        lastLog = "reset done";
    }
}
