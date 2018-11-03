
public class Interval<T extends Comparable<T>> implements IntervalADT<T> {

	private T start;
	private T end;
	private String label;

    public Interval(T start, T end, String label) {
    	this.start = start;
    	this.end = end;
    	this.label = label;
    }

    @Override
    public T getStart() {
    	return start;
    }

    @Override
    public T getEnd() {
    	return end;
    }

    @Override
    public String getLabel() {
    	return label;
    }

    @Override
    public boolean overlaps(IntervalADT<T> other) {
        // TODO Auto-generated method stub
    	//why int and not double
    	if(this.getStart().compareTo(other.getEnd()) > 0 || 
    			this.getEnd().compareTo(other.getStart()) < 0) 
    		return false;
    	else 
    		return true;
    }

    @Override
    public boolean contains(T point) {
    	return (this.getStart().compareTo(point) <= 0 &&
    			this.getEnd().compareTo(point) >= 0);
    }

    @Override
    public int compareTo(IntervalADT<T> other) {
    	if(this.getStart().compareTo(other.getStart()) == 0){
    		return this.getEnd().compareTo(other.getEnd());
    	}
    	else{
    		return this.getStart().compareTo(other.getStart());
    	}
    }
    
    public String toString(){
    	return label + "[" + start + ", " + end + "]";
    }
    public static void main(String args[]){
    	Interval I1 = new Interval (41,50,"p1");
    	Interval I2 = new Interval (21,40,"p2");
    	System.out.println(I1.compareTo(I2));
    }

}
