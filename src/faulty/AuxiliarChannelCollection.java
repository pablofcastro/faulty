package faulty;
import java.util.*;


public class AuxiliarChannelCollection {
	
	
	    LinkedList<IntChannel> intChannels;
		LinkedList<BoolChannel> boolChannels;
		int maxLengthChannels;
		
		public AuxiliarChannelCollection(){
			
			intChannels = new LinkedList<IntChannel>();
			boolChannels = new LinkedList<BoolChannel>();
			maxLengthChannels = 0;		
		
		}
		
		
		 /**
	     * Adds a Int channel to the list of channel in the process
	     * @param	chan 	the new channel
	     */
	    public void addIntChannel(IntChannel chan){
	    	intChannels.add(chan);
	    	if (chan.getSize() > maxLengthChannels){
	    		maxLengthChannels = chan.getSize();
	    		
	    	}
	    }
	    
	    
	    /**
	     * Adds a Bool channel to the list of channel in the process
	     * @param	chan 	the new channel
	     */
	    public void addBoolChannel(BoolChannel chan){
	    	boolChannels.add(chan);
	    	if (chan.getSize() > maxLengthChannels){
	    		maxLengthChannels = chan.getSize();
	    		
	    	}
	    }
	    
	    
	    public LinkedList<BoolChannel> getBoolChannels(){
	    	return boolChannels;
	    }

	    public LinkedList<IntChannel> getIntChannels(){
	    	return intChannels;
	    }

	    public int getNumBoolChannels(){
	    	return boolChannels.size();
	    }
	    
	    public int getNumIntChannels(){
	    	return intChannels.size();
	    }
	    

       public int getMaxLengthChannels(){
    	   return maxLengthChannels;
       }
	       		
	


}
