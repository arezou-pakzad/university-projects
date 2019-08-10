
class FishingRod extends Tool {
    private float fishingProbability;

    public FishingRod(String name, String type, int power, float fishingProbability){
        super(name, type, false, power);
        this.fishingProbability = fishingProbability;
    }

    public void setStatus(){
        if (isBroken())
            status = "A " + type + " fishing rod." + " There is " + fishingProbability + " chance to successfully catch a fish in every try."
                + "\nIt will cost “num” Energy Points after every use.\n Broken";
        else
            status = "A " + type + " fishing rod." + " There is " + fishingProbability + " chance to successfully catch a fish in every try."
                    + "\nIt will cost “num” Energy Points after every use.\n Not broken";
    }
}
