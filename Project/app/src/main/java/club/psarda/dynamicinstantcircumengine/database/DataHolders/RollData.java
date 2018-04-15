package club.psarda.dynamicinstantcircumengine.database.DataHolders;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.LongSparseArray;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.psarda.dynamicinstantcircumengine.Utils.UtilsClass;

/**
 * Created by pfsar on 8/09/2017.
 */

public class RollData implements Serializable, Parcelable {
    
    private StatsData _owner;
    private TargetData _target;
    private long _rerolls;
    private long _successfulRolls;
    private List<Integer> _rolls = new ArrayList<>();
    private SparseArray<Long> _histogram = new SparseArray<>();
    
    public RollData(StatsData owner){
        _owner = owner;
    }
    
    public String get_target(){
        return _target.get_target() + UtilsClass.toString(_target.get_targetEnum());
    }
    
    public TargetData GetRawTargetData(){
        return _target;
    }
    
    public long get_successfulRolls(){
        return _successfulRolls;
    }
    
    public List<Integer> get_rolls(){
        return _rolls;
    }
    
    public long get_totalRolls(){
        return _rolls.size() + 1;
    }
    
    public long get_rerolls(){
        return _rerolls;
    }
    
    public double GetPercentageOfHits(){
        return  ((double)_successfulRolls / (double)get_totalRolls()) * 100;
    }
    
    public double GetAverageDouble(){
        return  UtilsClass.averageInteger(get_rolls());
    }
    
    public SparseArray<Long> GetHistorgram(){
        return _histogram;
    }
    
    public void set_target(String target){
        _target = new TargetData(target);
    }
    
    public void set_successfulRolls(long sucessfullRolls){
        _successfulRolls = sucessfullRolls;
    }
    
    public void AddToReroll(){
        _rerolls++;
    }
    
    public void AddRoll(int toAdd){
        _rolls.add(toAdd);
        _histogram.append(toAdd, _histogram.get(toAdd, 0L) + 1L);
    }
    
    
    protected RollData(Parcel in) {
        _target = (TargetData) in.readValue(TargetData.class.getClassLoader());
        _rerolls = in.readLong();
        _successfulRolls = in.readLong();
        if (in.readByte() == 0x01) {
            _rolls = new ArrayList<Integer>();
            in.readList(_rolls, Integer.class.getClassLoader());
        } else {
            _rolls = null;
        }
        _histogram = (SparseArray) in.readValue(SparseArray.class.getClassLoader());
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(_target);
        dest.writeLong(_rerolls);
        dest.writeLong(_successfulRolls);
        if (_rolls == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(_rolls);
        }
        dest.writeValue(_histogram);
    }
    
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RollData> CREATOR = new Parcelable.Creator<RollData>() {
        @Override
        public RollData createFromParcel(Parcel in) {
            return new RollData(in);
        }
        
        @Override
        public RollData[] newArray(int size) {
            return new RollData[size];
        }
    };
}