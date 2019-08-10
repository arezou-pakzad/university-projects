import java.util.Random;

public class Constants {
    private static int handTheCollector = 5;
    public static float branchFallProbability;
    //public static float fishCatchProbability;
    public static float breakingProbability;
    public static int generateProbability(int min, int max){ //used when fishing
        Random r = new Random();
        return r.nextInt(max - min) + min;
    }
    public static int collectStoneProbability(){
        Random r = new Random();
        int WhichRand = r.nextInt(10);
        return (WhichRand * handTheCollector) / 10 ;
    }
    public static int specialCollector(int collectorPower, int collectablePower, float Cr){ //really dunno what  the hell is CR :|
        int a = (int)(Cr *(int) Math.pow(2, collectorPower) /(int) Math.pow(2, collectablePower));
        Random r = new Random();
        System.out.println(a);
        return a;
    }

}
