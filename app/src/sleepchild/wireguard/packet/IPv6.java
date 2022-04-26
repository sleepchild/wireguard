package sleepchild.wireguard.packet;
import java.nio.*;
import java.net.*;

// see: https://en.m.wikipedia.org/wiki/IPv6_packet
public class IPv6
{
    byte version;
    byte trafficClass;
    int flowLabel;
    short payloadLength;
    byte nextHeader; // ?? protocol
    byte hopLimit;
    
    InetAddress sourceAddress;
    InetAddress destinationAddress;
    
    int protocol; // int value of nextHeader
    TCP tcp=null;
    UDP udp=null;
    
    // https://en.m.wikipedia.org/wiki/List_of_IP_protocol_numbers
    public static final int PROTOCOL_TCP = 6;
    public static final int PROTOCOL_UDP = 17;
    //
    
    public IPv6(ByteBuffer buffer) throws NotVersion6Exception, UnknownHostException{
        
        initHeader6Data(buffer);
        
        if(this.protocol==PROTOCOL_TCP){
            this.tcp = new TCP(buffer, this.sourceAddress, this.destinationAddress);
        }else if (this.protocol == PROTOCOL_UDP){
            //this.udp = new UDP(buffer);
        }
        
    }
    
    
    private void initHeader6Data(ByteBuffer buffer) throws NotVersion6Exception, UnknownHostException{
        int pos = buffer.position();
        
        int b1 = buffer.get();
        int b2 = buffer.get();
        
        this.version = (byte) ((b1 & 0xF0) >>> 4);
        if(version != 6){
            throw new NotVersion6Exception("IP: invalid verson: "+version);
        }
        
        this.trafficClass = (byte) ( ((b1 & 0xF) << 4) | (b2 & 0xF0) >> 4);
        
        this.flowLabel = ((b2 & 0xF) << 16) | (buffer.getShort() & 0xFFFF);
        this.payloadLength = buffer.getShort();
        this.nextHeader = buffer.get();
        this.protocol = nextHeader & 0xF;
        
        this.hopLimit= buffer.get();
        
        byte[] addr = new byte[16];
        buffer.get(addr, 0,16);
        this.sourceAddress = InetAddress.getByAddress(addr);
        
        buffer.get(addr, 0, 16);
        this.destinationAddress = InetAddress.getByAddress(addr);
        
        
        
    }
    
    public int getProtocol(){
        return protocol;
    }
    
    public InetAddress getSourceAddress()
    {
        return sourceAddress;
    }
    
    public InetAddress getDestAddress()
    {
        return destinationAddress;
    }


    public int getVersion()
    {
        return version;
    }

    public UDP getUDP()
    {
        return udp;
    }

    public TCP getTCP()
    {
        return tcp;
    }
    
    
    
    public static class NotVersion6Exception extends Exception{
        public NotVersion6Exception(String message){
            super(message);
        }
    }
}
