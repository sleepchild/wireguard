package sleepchild.wireguard.packet;
import java.nio.*;

//see: 
//    https://en.m.wikipedia.org/wiki/User_Datagram_Protocol
public class UDP
{
    int sorcePort;
    int destinationPort;
    
    int length;
    int checksum;
    
    public UDP(ByteBuffer buffer){
        int pos = buffer.position();
        
        
    }
}
