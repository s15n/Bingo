public class Bingo extends JavaPlugin {
    private static Bingo instance;
    
    @Override
    public void onEnable() {
        instance = this;
        commandRegistration();
        listenerRegistration();
        
    }
    
    @Override
    public void onDisable() {
        instance = null;
    }
    
    @NotNull
    public Bingo getInstance() {
        return instance;
    }
    
    private void listenerRegistration() {
        PluginManager manager = Bukkit.getPluginManager();
        
    }
    
    private void commandRegistration() {
        
    }
} 