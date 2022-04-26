package sleepchild.wireguard.packet;

import java.nio.ByteBuffer;

public class IPUtils
{
    public static int getChecksum(ByteBuffer buffer, int position, int length) {
        int i = 0;
        long sum = 0;
        long data;
        while (length > 1) {
            data = (((buffer.get(position + i) << 8) & 0xFF00) | (buffer.get(position + i + 1) & 0xFF));
            sum += data;
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum += 1;
            }
            i += 2;
            length -= 2;
        }

        if (length > 0) {
            sum += (buffer.get(position + i) << 8 & 0xFF00);
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum += 1;
            }
        }

        sum = ~sum;
        sum = sum & 0xFFFF;
        return (int) sum;
    }
}
