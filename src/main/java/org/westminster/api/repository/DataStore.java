package org.westminster.api.repository;

import org.westminster.api.model.Room;
import org.westminster.api.model.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory data store for the Smart Campus API.
 * Uses thread-safe collections as per project requirements (no external DB allowed).
 */
public class DataStore {
    private static DataStore instance;
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();

    private DataStore() {}

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // Room Operations
    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public boolean deleteRoom(String id) {
        // Business Rule: Check if room has sensors
        Room room = rooms.get(id);
        if (room != null && !room.getSensorIds().isEmpty()) {
            return false; // Cannot delete if sensors are assigned
        }
        return rooms.remove(id) != null;
    }

    // Sensor Operations
    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        // Link to room if exists
        Room room = rooms.get(sensor.getRoomId());
        if (room != null) {
            room.addSensorId(sensor.getId());
        }
    }

    public Sensor getSensor(String id) {
        return sensors.get(id);
    }

    public List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors.values());
    }
}
