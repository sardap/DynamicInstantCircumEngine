package club.psarda.dynamicinstantcircumengine.database.DataHolders;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import club.psarda.dynamicinstantcircumengine.Utils.UtilsClass;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StorageClasses.StatsDataStorage;

/**
 * Created by pfsar on 8/09/2017.
 */

public class StatsData implements Comparable<StatsData>, Parcelable, Serializable {
    
    private static Random seedRanomder = new Random();
    
    private String _name;
    private int _sides;
    private long _numberOfDice;
    private ArrayList<RollData> _rollData = new ArrayList<>();
    private long _creationDate = Calendar.getInstance().getTimeInMillis();
    private Double _averageRoll;
    private Long _seed;
    
    public StatsData(String name, int sides, long numberOfDice, ArrayList<RollData> rollData){
        _name = name;
        _sides = sides;
        _numberOfDice = numberOfDice;
        _rollData = rollData;
        _averageRoll = null;
        _seed = null;
    }
    
    public StatsData(StatsDataStorage statsDataStorage){
        _name = statsDataStorage.getName();
        _sides = statsDataStorage.getSides();
        _numberOfDice = statsDataStorage.getNumberOfRolls();
        _creationDate = statsDataStorage.getCreationDate();
        _averageRoll = statsDataStorage.getAverageRoll();
        _seed = statsDataStorage.getSeed();
    
        List<TargetData> targetDataList = new ArrayList<>();
    
        for(String target : statsDataStorage.getTargets()){
            targetDataList.add(new TargetData(target));
        }
    
        roll(targetDataList);
    }
    
    public StatsData(){}
    
    public void set_name(String name){
        _name = name;
    }
    
    public void set_sides(int sides){
        _sides = sides;
    }
    
    public void set_numberOfDice(long numberOfDice){
        _numberOfDice = numberOfDice;
    }
    
    public void addRollData(RollData newData){
        _rollData.add(newData);
    }
    
    public String get_name(){
        return _name;
    }
    
    public int get_sides(){
        return _sides;
    }

    public Long get_seed(){
        return _seed;
    }
    
    public long get_numberOfDice(){
        return _numberOfDice;
    }
    
    public ArrayList<RollData> get_rollData(){
        return _rollData;
    }
    
    public long get_timeCreation(){
        return _creationDate;
    }
    
    public void set_creationDate(long value){
        _creationDate = value;
    }

    public Double get_averageRoll(){
        return _averageRoll;
    }
    
    public void set_averageRoll(double newValue){
        _averageRoll = newValue;
    }

    public void set_seed(long newSeed){
        _seed = newSeed;
    }
    
    public double getAverageForSides(){
        
        Double sum = 0.0;
        
        // Can't remeber the math word for this pls
        for(int i = 1; i <= _sides; i++){
            sum += i;
        }
        
        return sum / _sides;
    }
    
    public void calcauteAndStore(){
        _averageRoll = calculateAverageRoll();
    }
    
    public void roll(List<TargetData> targets){
        
        long sucessfullRolls;
        long numberOfDice = get_numberOfDice();
        Random random = new Random();
        
        if(get_seed() == null) {
            long seed = seedRanomder.nextLong();
            set_seed(seed);
        }
        
        random.setSeed(get_seed());
        
        for(int i = 0; i < targets.size(); i++){
            
            RollData rollData = new RollData(this);
            
            rollData.set_target(targets.get(i).GetString());
            
            sucessfullRolls = 0;
            
            for(long j = 0; j < numberOfDice; j++){
                
                int randomInt = random.nextInt(get_sides()) + 1;
                long prevSuccessfulRolls = sucessfullRolls;
                
                switch (targets.get(i).get_targetEnum()){
                    case ABOVE:
                        
                        if(randomInt >= targets.get(i).get_target()){
                            sucessfullRolls++;
                        }
                        
                        break;
                    
                    case BELOW:
                        
                        if(randomInt <= targets.get(i).get_target()){
                            sucessfullRolls++;
                        }
                        
                        break;
                    
                    case EQUAL:
                        
                        if(randomInt == targets.get(i).get_target()){
                            sucessfullRolls++;
                        }
                        
                        break;
                }
                
                if(prevSuccessfulRolls <= sucessfullRolls && randomInt == targets.get(i).get_reroll()){
                    rollData.AddToReroll();
                    j--;
                }else{
                    rollData.AddRoll(randomInt);
                }
            }
            
            rollData.set_successfulRolls(sucessfullRolls);
            
            numberOfDice = sucessfullRolls;
            
            addRollData(rollData);
        }
        
        calcauteAndStore();
    }
    
    private double calculateAverageRoll(){
        
        Double sum = 0.0;
        Double length = 0.0;
        
        for (RollData rolldata : _rollData) {
            
            sum += UtilsClass.getSum(rolldata.get_rolls());
            length += rolldata.get_totalRolls();
        }
        
        return sum / length;
    }
    
    public int compareTo(StatsData n) {
        return (int)(n.get_timeCreation() - get_timeCreation());
    }
    
    protected StatsData(Parcel in) {
        _name = in.readString();
        _sides = in.readInt();
        _numberOfDice = in.readLong();
        if (in.readByte() == 0x01) {
            _rollData = new ArrayList<RollData>();
            in.readList(_rollData, RollData.class.getClassLoader());
        } else {
            _rollData = null;
        }
        _creationDate = in.readLong();
        _averageRoll = in.readByte() == 0x00 ? null : in.readDouble();
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_name);
        dest.writeLong(_sides);
        dest.writeLong(_numberOfDice);
        if (_rollData == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(_rollData);
        }
        dest.writeLong(_creationDate);
        if (_averageRoll == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(_averageRoll);
        }
    }
    
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StatsData> CREATOR = new Parcelable.Creator<StatsData>() {
        @Override
        public StatsData createFromParcel(Parcel in) {
            return new StatsData(in);
        }
        
        @Override
        public StatsData[] newArray(int size) {
            return new StatsData[size];
        }
    };
}