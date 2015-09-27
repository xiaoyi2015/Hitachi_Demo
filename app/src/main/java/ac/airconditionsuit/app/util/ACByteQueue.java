package ac.airconditionsuit.app.util;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ac on 9/24/15.
 * 没有考虑总数据大于512的情况，因为
 */
public class ACByteQueue {

    Queue<Byte> queue = new LinkedBlockingQueue<>();

    public void offer(byte[] content, int offset, int length){
        for (int i = offset; i < offset + length; ++i) {
            queue.offer(content[i]);
        }
    }

    public byte[] poll(int length){
        byte[] res = new byte[length];
        for (int i = 0; i < length; ++i) {
            res[i] = queue.poll();
        }
        return res;
    }

    public int size() {
        return queue.size();
    }
}
