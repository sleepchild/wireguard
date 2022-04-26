package sleepchild.wireguard.packet;

import java.nio.*;
import java.net.*;
import sleepchild.wireguard.*;

// see: 
//     https://en.m.wikipedia.org/wiki/Transmission_Control_Protocol
public class TCP
{
    private int sourcePort;
    private int destinationPort;
    private long sequenceNumber;
    private long acknowledgeNumber;
    
    public TCP(ByteBuffer buffer, InetAddress sourceAddr, InetAddress destAddr){
        int pos = buffer.position();
        
        this.sourcePort = buffer.getShort() & 0xFFFF;
        this.destinationPort = buffer.getShort() & 0xFFFF;
        this.sequenceNumber = buffer.getShort() & 0xFFFFFFFFL;
        this.acknowledgeNumber = buffer.getShort() & 0xFFFFFFFFL;
        //
        LogTool.get().f("TCP.java", "srcp: "+sourcePort);
    }
    
    public int getSourcePort(){
        return sourcePort;
    }
    
    public int getDestPorts(){
        return destinationPort;
    }
    
}
