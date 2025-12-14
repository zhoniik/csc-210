import java.io.File;

public interface WorldAdapter {
    void takeTurn();
    String getTurnLog();
    int getTurnNumber();

    String getCellShort(int r, int c);
    String getCellSummary(int r, int c);
    String getCreaturesInfo(int r, int c);

    void loadFromFile(File f);
    void saveToFile(File f);
    void reset();
}
