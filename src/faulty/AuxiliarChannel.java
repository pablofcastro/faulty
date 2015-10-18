package faulty;


import java.util.*;

public class AuxiliarChannel {

	Channel channel;
	Type typeChannel;
	
	public AuxiliarChannel(Channel ch, Type t){
		this.channel = ch;
		this.typeChannel = t;
	}
	
	public Type getType(){
		
		return typeChannel;
	}
	
	
    public Channel getChannel(){
		
		return channel;
	}
	
}
