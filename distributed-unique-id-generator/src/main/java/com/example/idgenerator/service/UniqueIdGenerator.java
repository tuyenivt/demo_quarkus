package com.example.idgenerator.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class UniqueIdGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(UniqueIdGenerator.class);
    
    private final long nodeId;
    private final long epoch;
    private final long sequenceBits;
    private final long nodeIdBits;
    private final long timestampBits;
    
    private final long maxSequence;
    private final long maxNodeId;
    private final long maxTimestamp;
    
    private final AtomicLong lastTimestamp = new AtomicLong(-1L);
    private final AtomicLong sequence = new AtomicLong(0L);
    
    @Inject
    public UniqueIdGenerator(
            @ConfigProperty(name = "id-generator.node-id") long nodeId,
            @ConfigProperty(name = "id-generator.epoch") long epoch,
            @ConfigProperty(name = "id-generator.sequence-bits") long sequenceBits,
            @ConfigProperty(name = "id-generator.node-id-bits") long nodeIdBits,
            @ConfigProperty(name = "id-generator.timestamp-bits") long timestampBits) {
        
        this.nodeId = nodeId;
        this.epoch = epoch;
        this.sequenceBits = sequenceBits;
        this.nodeIdBits = nodeIdBits;
        this.timestampBits = timestampBits;
        
        this.maxSequence = ~(-1L << sequenceBits);
        this.maxNodeId = ~(-1L << nodeIdBits);
        this.maxTimestamp = ~(-1L << timestampBits);
        
        if (nodeId > maxNodeId) {
            throw new IllegalArgumentException("Node ID cannot be greater than " + maxNodeId);
        }
        
        LOG.info("Initialized ID Generator with nodeId: {}, epoch: {}", nodeId, epoch);
    }
    
    public synchronized long generateId() {
        long currentTimestamp = getCurrentTimestamp();
        
        if (currentTimestamp < lastTimestamp.get()) {
            LOG.warn("Clock moved backwards. Rejecting requests until {} ms", lastTimestamp.get());
            throw new IllegalStateException("Clock moved backwards. Rejecting requests");
        }
        
        if (currentTimestamp == lastTimestamp.get()) {
            sequence.set((sequence.get() + 1) & maxSequence);
            if (sequence.get() == 0) {
                // Sequence overflow, wait for next millisecond
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence.set(0L);
        }
        
        lastTimestamp.set(currentTimestamp);
        
        return ((currentTimestamp - epoch) << (nodeIdBits + sequenceBits))
                | (nodeId << sequenceBits)
                | sequence.get();
    }
    
    private long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }
    
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
    
    public long[] generateBatch(int count) {
        if (count <= 0 || count > 1000) {
            throw new IllegalArgumentException("Batch size must be between 1 and 1000");
        }
        
        long[] ids = new long[count];
        for (int i = 0; i < count; i++) {
            ids[i] = generateId();
        }
        return ids;
    }
} 