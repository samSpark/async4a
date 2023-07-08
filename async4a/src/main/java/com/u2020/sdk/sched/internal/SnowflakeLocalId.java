package com.u2020.sdk.sched.internal;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SnowflakeLocalId {
    /** 开始时间戳 */
    private final int twepoch = 1676536153;

    /** 序列在id中占的位数 */
    private final long sequenceBits = 7L;

    /** 时间截向左移7位 */
    private final long timestampLeftShift = sequenceBits;

    /** 生成序列的掩码，这里为127 */
    private final int sequenceMask = ~(-1 << sequenceBits);

    /** 分钟内序列(0~127) */
    private int sequence = 0;
    private int laterSequence = 0;

    /** 上次生成ID的时间戳 */
    private int lastTimestamp = -1;

    private static final int MASK = 0x7FFFFFFF;
    private final AtomicInteger counter  = new AtomicInteger(0);;

    /** 预支时间标志位 */
    private volatile boolean tilNextGen = false;

    // ==============================Constructors=====================================
    public SnowflakeLocalId() {

    }

    // ==============================Methods==========================================
    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized int nextId() {
        int timestamp = timeGen();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过
        if (timestamp < lastTimestamp) {
           return Math.abs(UUID.randomUUID().toString().hashCode());
        }

        if(timestamp > (counter.get() & MASK)) {
            counter.set(timestamp & MASK);
            tilNextGen = false;
        }

        // 如果是同一时间生成的，则进行时间戳单位内序列
        if (lastTimestamp == timestamp || tilNextGen) {
            if(!tilNextGen) {
                sequence = (sequence + 1) & sequenceMask;
            }

            // 内序列溢出
            if (sequence == 0) {
                // 预支下一新的时间戳
                tilNextGen = true;
                int laterTimestamp = counter.get() & MASK;
                if (laterSequence == 0) {
                    laterTimestamp = counter.incrementAndGet() & MASK;
                }

                int nextId = ((laterTimestamp - twepoch) << timestampLeftShift)
                        | laterSequence;
                laterSequence = (laterSequence + 1) & sequenceMask;
                return nextId;
            }
        }
        // 时间戳改变，序列重置
        else {
            sequence = 0;
            laterSequence = 0;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成32位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | sequence;
    }

    /**
     * 返回以秒为单位的当前时间
     *
     * @return 当前时间戳
     */
    protected synchronized int timeGen() {
        return (int) (System.currentTimeMillis() / 1000);
    }
}
