package org.westminster.api.model;

/**
 * POJO representing a single sensor measurement event.
 */
public class SensorReading {
    private String id;        // Unique reading event ID (UUID recommended)
    private long timestamp;   // Epoch time (ms) when reading was captured
    private double value;      // The actual metric value recorded by the hardware

    // Default constructor for Jackson
    public SensorReading() {}

    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
