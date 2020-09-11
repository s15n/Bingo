public class BingoGame {
    boolean running = false;
    String name = null;
    boolean autosave = false;
    List<OfflinePlayer> players = new ArrayList<>();
    List<BingoTeam> teams = new ArrayList<>();
    BingoScoreboard scoreboard = new BingoScoreboard();
    BingoItems items;
    
    public BingoGame(int items, long seed) {
        this.items = new BingoItems(items, seed);
    }
    
    public BingoGame(int items) {
        this.items = new BingoItems(items);
    }
    
    public void start() {
        
    }
    
    public void stop() {
        
    }
    
    public void setName(@NotNull String name) {
        
    }
}