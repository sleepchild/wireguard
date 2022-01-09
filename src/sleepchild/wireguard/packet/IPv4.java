package sleepchild.wireguard.packet;
import java.net.*;
import java.nio.*;
import java.io.*;
import java.util.*;
import sleepchild.wireguard.*;

// This is not a complete implementation; but it serves my purposes.
// see:
//    https://en.m.wikipedia.org/wiki/IPv4
//    
public class IPv4
{
    static String TAG ="IPv4";
    byte version;
    byte IHL;// internet header length
    byte DSCP; // Differentiated Services Code Point
    byte ECN; // Explicit Congestion Notification
    int totalLength; // Total Length of this packet
    int identification;
    
    // fragment flags
    // todo: re-implement this
    byte reserved; // ?? must be zero
    byte DF;// Dont Fragment 
    byte MF;// Must Fragment
    
    
    int fragmentOffset;
    int TTL; // Time To Live
    int protocol;
    int headerChecksum;
    
    InetAddress sourceAddress;
    InetAddress destinationAddress;
    
    byte[] options; // ? this field may not exist
    
    ByteBuffer packet;
    
    TCP tcp=null;
    UDP udp=null;
    
    // https://en.m.wikipedia.org/wiki/List_of_IP_protocol_numbers
    public static final int PROTOCOL_TCP = 6;
    public static final int PROTOCOL_UDP = 17;
    // other aint supported yet; no need to list em here
    
    //int pos0;
    
    public IPv4(ByteBuffer buffer) throws NotVersion4Exception, IOException{
        this.packet = buffer;        
        //
        initHeaderData(buffer);
        
        if(protocol==PROTOCOL_TCP){
            this.tcp = new TCP(buffer, this.sourceAddress, this.destinationAddress);
        }else if(protocol==PROTOCOL_UDP){
            this.udp = new UDP(buffer);
            // no support for now;
            // throw new IOExecption("UDP currently not supported");
        }
    }
    
    private void initHeaderData(ByteBuffer buffer) throws NotVersion4Exception, IOException{
        int pos = buffer.position();
        
        int b = buffer.get();
        this.version = (byte) (b >> 4);
        this.IHL = (byte) (b & 0xF);
        
        if(version != 4){
            throw new NotVersion4Exception("IP: ivalid version: "+version);
        }
        
        b = buffer.get();
        this.DSCP = (byte) (b >> 4);
        this.ECN = (byte) (b & 0xF);
        
        this.totalLength = buffer.getShort() & 0xFFFF;
        this.identification = buffer.getShort() & 0xFFFF;
        
        b = buffer.getShort();
        int flags = (byte)(b >> 13);
        this.reserved = (byte) (flags & 1);
        this.DF = (byte)(flags & 2); // to boolean
        this.MF = (byte) (flags & 3);// to boolean
        this.fragmentOffset = b & 0x1FFF;
       
        this.TTL = buffer.get() & 0xFF;
        this.protocol = buffer.get() & 0xFF;
        this.headerChecksum= buffer.getShort() & 0xFFFF;
        
        byte[] addr = new byte[4];
        buffer.get(addr,0,4);
        this.sourceAddress= InetAddress.getByAddress(addr);
        
        buffer.get(addr,0,4);
        this.destinationAddress =  InetAddress.getByAddress(addr);
        
        int optsLength = this.IHL * 4 - 20;
        this.options = new byte[optsLength];
        if(optsLength > 0){
            buffer.get(this.options);
        }
        
        buffer.putShort(pos + 10, (short) 0);
        this.headerChecksum = IPUtils.getChecksum(buffer, pos, buffer.position() - pos);
        
    }
    
    public int getVersion(){
        return version;
    }
    
    public InetAddress getSourceAddr(){
        return sourceAddress;
    }
    
    public InetAddress getDestAddr(){
        return destinationAddress;
    }
    
    public int getProtocol(){
        return protocol;
    }
    
    public TCP getTCP(){
        return tcp;
    }
    
    
    public int getUid4() {
        String addr = "";
        byte[] b = this.sourceAddress.getAddress();
        for (int i = b.length - 1; i >= 0; i--)
            addr += String.format("%02X", b[i]);
        addr += ":" + String.format("%04X", this.tcp.sourcePort);

        int uid = scanUid("0000000000000000FFFF0000" + addr, "/proc/net/tcp6");
        if (uid < 0)
            uid = scanUid(addr, "/proc/net/tcp");
        return uid;
    }

    private static int scanUid(String addr, String name) {
        File file = new File(name);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("sl"))
                    continue;

                String[] field = line.split("\\s+");
                if (addr.equals(field[1]))
                    return Integer.parseInt(field[7]);
            }
        } catch (FileNotFoundException ex) {
            LogTool.get().f(TAG, ex.toString() + "\n" + android.util.Log.getStackTraceString(ex));
        } finally {
            if (scanner != null)
                scanner.close();
        }

        return -1;
    }
    
    /*
    private void encode(ByteBuffer buffer) {
        this.IPv4.encode(buffer);
        if (this.tcp != null)
            this.tcp.encode(this.sourceAddress, this.destinationAddress, buffer);
        buffer.position(0);
    }

    public void send(FileOutputStream out) throws IOException {
        this.packet = ByteBuffer.allocate(32767);
        encode(this.packet);
        byte[] r = new byte[this.packet.limit()];
        this.packet.get(r);
        out.write(r);
    }
    //*/
    
    public static class NotVersion4Exception extends IOException{
        public NotVersion4Exception(String message){
            super(message);
        }
    }
    
    
    
}
