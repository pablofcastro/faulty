package faulty.auxiliar;


import java.util.*;


public class AuxiliarChannelCollection extends AuxiliarProgramNode{
	
	
	    LinkedList<AuxiliarChannel> intChannels;
		LinkedList<AuxiliarChannel> boolChannels;
        LinkedList<AuxiliarChannel> enumChannels;
		int maxLengthChannels;
		
		public AuxiliarChannelCollection(){
			
			intChannels = new LinkedList<AuxiliarChannel>();
			boolChannels = new LinkedList<AuxiliarChannel>();
            enumChannels= new LinkedList<AuxiliarChannel>();
			maxLengthChannels = 0;		
		
		}
		
		
		 /**
	     * Adds a Int channel to the list of channel in the process
	     * @param	chan 	the new channel
	     */
	    public void addIntChannel(AuxiliarChannel chan){
	    	intChannels.add(chan);
	    	if (chan.getSize() > maxLengthChannels){
	    		maxLengthChannels = chan.getSize();
	    	}
	    }
	    
	    
		public void accept(AuxiliarFaultyVisitor v){
		     v.visit(this);			
		}
	    
	    
	    /**
	     * Adds a Bool channel to the list of channel in the process
	     * @param	chan 	the new channel
	     */
	    public void addBoolChannel(AuxiliarChannel chan){
	    	boolChannels.add(chan);
	    	if (chan.getSize() > maxLengthChannels){
	    		maxLengthChannels = chan.getSize();
	    	}
	    }
	    
        /**
        * Adds a Enumerated channel to the list of channel in the process
        * @param	chan 	the new channel
        */
        public void addEnumChannel(AuxiliarChannel chan){
           enumChannels.add(chan);
           if (chan.getSize() > maxLengthChannels){
              maxLengthChannels = chan.getSize();
           }
        }
    
    
    
	    public LinkedList<AuxiliarChannel> getBoolChannels(){
	    	return boolChannels;
	    }

	    public LinkedList<AuxiliarChannel> getIntChannels(){
	    	return intChannels;
	    }
    
        public LinkedList<AuxiliarChannel> getEnumChannels(){
           return enumChannels;
        }


	    public int getNumBoolChannels(){
	    	return boolChannels.size();
	    }
	    
	    public int getNumIntChannels(){
	    	return intChannels.size();
	    }
	    
        public int getNumEnumChannels(){
            return enumChannels.size();
        }
    
       public int getMaxLengthChannels(){
    	   return maxLengthChannels;
       }
	       		
	


}
