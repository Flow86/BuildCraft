package buildcraft.lib.misc.data;

import gnu.trove.list.array.TByteArrayList;

public class CompactingBitSet {
    public final int bits;
    private final TByteArrayList bytes = new TByteArrayList();
    private int bitIndex = 0;

    public CompactingBitSet(int bits) {
        this.bits = bits;
    }

    private void appendBit(int bit) {
        if (bitIndex == 0) {
            bytes.add((byte) 0);
        }
        int offset = bytes.size() - 1;
        byte current = bytes.get(offset);
        if ((bit & 1) == 1) {
            current |= 1 << bitIndex;
            bytes.set(offset, current);
        }
        bitIndex++;
        if (bitIndex == 8) {
            bitIndex = 0;
        }
    }

    public void append(int value) {
        for (int i = bits - 1; i >= 0; i--) {
            int bit = (value >> i) & 1;
            appendBit(bit);
        }
    }

    public byte[] getBytes() {
        return bytes.toArray();
    }
}
