/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package usage;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Map;

/**
 * @author pmdusso
 */
public class MonitoredData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7909922150038205539L;
    private int nodeUUID;
    @Nullable
    private Map<Integer, CpuData> cpu;
    @Nullable
    private MemData mem;
    @Nullable
    private Map<String, DiskData> disk;
    @Nullable
    private Map<String, NetworkData> net;
    @Nullable
    private SensorData sensor;

    public MonitoredData(int _uuid, @Nullable Map<Integer, CpuData> _cpu, @Nullable MemData _mem,
                         @Nullable Map<String, DiskData> _disk, @Nullable Map<String, NetworkData> _net,
                         @Nullable SensorData _sensor) {
        if (_cpu == null)
            throw new ExceptionInInitializerError(
                    "CPU monitored data object is null");
        else if (_mem == null)
            throw new ExceptionInInitializerError(
                    "Memory monitored data object is null");
        else if (_disk == null)
            throw new ExceptionInInitializerError(
                    "Disk monitored data object is null");
        else if (_net == null)
            throw new ExceptionInInitializerError(
                    "Network monitored data object is null");
        else if (_sensor == null)
            throw new ExceptionInInitializerError(
                    "Sensor monitored data object is null");

        this.nodeUUID = _uuid;

        this.cpu = _cpu;
        this.mem = _mem;
        this.disk = _disk;
        this.net = _net;
        this.sensor = _sensor;
    }

    public int getNodeUUID() {
        return this.nodeUUID;
    }

    /**
     * @return the cpu
     */
    @Nullable
    public Map<Integer, CpuData> getCpu() {
        return this.cpu;
    }

    /**
     * @return the mem
     */
    @Nullable
    public MemData getMem() {
        return this.mem;
    }

    /**
     * @return the disk
     */
    @Nullable
    public Map<String, DiskData> getDisk() {
        return this.disk;
    }

    /**
     * @return the net
     */
    @Nullable
    public Map<String, NetworkData> getNet() {
        return this.net;
    }

    /**
     * @return the sensor
     */
    @Nullable
    public SensorData getSensor() {
        return sensor;
    }
}
