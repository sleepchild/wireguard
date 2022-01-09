package sleepchild.wireguard.packet;

import java.nio.*;
import java.net.*;

// see: 
//     https://en.m.wikipedia.org/wiki/Transmission_Control_Protocol
public class TCP
{
    int sourcePort;
    int destinationPort;
    long sequenceNumber;
    long acknowledgeNumber;
    
    public TCP(ByteBuffer buffer, InetAddress sourceAddr, InetAddress destAddr){
        int pos = buffer.position();
        
        this.sourcePort = buffer.getShort() & 0xFFFF;
        this.destinationPort = buffer.getShort() & 0xFFFF;
        this.sequenceNumber = buffer.getShort() & 0xFFFFFFFFL;
        this.acknowledgeNumber = buffer.getShort() & 0xFFFFFFFFL;
        //
    }
    
    public int getSourcePort(){
        return sourcePort;
    }
    
    public int getDestPort(){
        return destinationPort;
    }
    
}
