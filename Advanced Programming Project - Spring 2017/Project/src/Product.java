public class Product extends Item {
    public Product(String name, String type, boolean isSpecial) {
        super(name, type, isSpecial);
        if(name.equals("Fish")){
            setPrice(200);
        }
    }
}