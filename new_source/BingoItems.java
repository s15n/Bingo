public class BingoItems {
    private Random random;
    private ItemStack[] items;
    
    public BingoItems(int items, long seed) {
        random = new Random(seed);
        this.items = generate(items);
    }
    
    public BingoItems(int items) {
        random = new Random();
        this.items = generate(items);
    }
    
    public ItemStack[] generate(int count) {
        
    }
}