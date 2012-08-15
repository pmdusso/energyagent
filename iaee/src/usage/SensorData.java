package usage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import utils.Symbols;
import utils.Utils;

/**
 * @author pmdusso
 * @version 1.0 @created 07-ago-2012
 */
public class SensorData implements Serializable {

    /**
     * This is necessary to serialize the object before sending through sockets
     */
    private static final long serialVersionUID = -5576168050062983937L;

    /**
     * Construct a new Sensor Data object.
     */
    public SensorData( String _sensorAddress, DateTime _date,
                      List<SensorChannel> _values) {
        this.sensorUUID = Math.abs(_sensorAddress.hashCode());
        this.date = _date;
        this.channels = new ArrayList<SensorChannel>(_values);
    }

    private final int sensorUUID;
    private final DateTime date;
    
    private final ArrayList<SensorChannel> channels;

    
    @Override
    public String toString() {
        return "SensorData [sensorUUID=" + sensorUUID + ", date=" + date
                + ", channels=" + channelsToString() + "]";
    }

    
    private String channelsToString() {
        String s = Symbols.EMPTY;
        for (SensorChannel c : this.channels) {
            s = "Channel: " + c.getNummer() + " Unity: " + Utils.convertUnityToString(c.getUnity()) + " Value: " + String.valueOf(c.getValue()) + "\n";
        }
        return s;
    }

    @Override
    public void finalize() throws Throwable {

    }

    public int getSensorUUID() {
        return sensorUUID;
    }

    
    public ArrayList<SensorChannel> getChannels() {
        return channels;
    }

    public DateTime getDate() {
        return date;
    }
}
