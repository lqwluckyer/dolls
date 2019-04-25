package com.game.sdk.dolls.bean;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2019-01-14.
 */
@Component
public class SnowflakeWorkerId {

    //*bits allocations for timeStamp, datacenterId, workerId and sequence

    private final long unusedBits = 1L;
    /**
     * 'time stamp' here is defined as the number of millisecond that have
     * elapsed since the {@link #epoch} given by users on  Snowflake}
     * instance initialization
     */
    private final long timestampBits = 41L;
    private final long datacenterIdBits = 5L;
    private final long workerIdBits = 5L;
    private final long sequenceBits = 12L;

    /*
     * max values of timeStamp, workerId, datacenterId and sequence
     */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits); // 2^5-1
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits); // 2^5-1
    private final long maxSequence = -1L ^ (-1L << sequenceBits); // 2^12-1

    /**
     * left shift bits of timeStamp, workerId and datacenterId
     */
    private final long timestampShift = sequenceBits + datacenterIdBits + workerIdBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long workerIdShift = sequenceBits;

	/*
     * object status variables
	 */

    /**
     * reference material of 'time stamp' is '2019-01-01'. its value can't be
     * modified after initialization.
     */
    private final long epoch = 1514736000000L;

    /**
     * data center number the process running on, its value can't be modified
     * after initialization.
     * <p>
     * max: 2^5-1 range: [0,31]
     */
    private final long dataCenterId;

    /**
     * machine or process number, its value can't be modified after
     * initialization.
     * <p>
     * max: 2^5-1 range: [0,31]
     */
    private final long workerId;

    /**
     * the unique and incrementing sequence number scoped in only one
     * period/unit (here is ONE millisecond). its value will be increased by 1
     * in the same specified period and then reset to 0 for next period.
     * <p>
     * max: 2^12-1 range: [0,4095]
     */
    private long sequence = 0L;

    /**
     * the time stamp last snowflake ID generated
     */
    private long lastTimestamp = -1L;

    /**
     *
     */
    public SnowflakeWorkerId(@Value("${data.center.id}") String dataCenterId, @Value("${worker.id}") String workerId) {
        if(StringUtils.isBlank(dataCenterId) || StringUtils.isBlank(workerId)){
            throw new IllegalArgumentException("dataCenterId is null  or workerId is null");
        }
        long dataCenterIdLong = Long.parseLong(dataCenterId);
        if (dataCenterIdLong > maxDatacenterId || dataCenterIdLong <= 0) {
            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        long workerIdLong =  Long.parseLong(workerId);
        if (workerIdLong > maxWorkerId || workerIdLong <= 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.dataCenterId = dataCenterIdLong;
        this.workerId = workerIdLong;
    }

    /**
     * generate an unique and incrementing id
     *
     * @return id
     */
    public synchronized long nextId() {
        long currTimestamp = timestampGen();

        if (currTimestamp < lastTimestamp) {
            throw new IllegalStateException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - currTimestamp));
        }

        if (currTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) { // overflow: greater than max sequence
                currTimestamp = waitNextMillis(currTimestamp);
            }

        } else { // reset to 0 for next period/millisecond
            sequence = 0L;
        }

        // track and memo the time stamp last snowflake ID generated
        lastTimestamp = currTimestamp;

        return ((currTimestamp - epoch) << timestampShift) |
                (dataCenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    protected long waitNextMillis(long currTimestamp) {
        while (currTimestamp <= lastTimestamp) {
            currTimestamp = timestampGen();
        }
        return currTimestamp;
    }

    protected long timestampGen() {
        return System.currentTimeMillis();
    }
}
