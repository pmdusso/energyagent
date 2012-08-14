package usage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

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
	 * 
	 * @param _channelNumber
	 */
	public SensorData(String _sensorAddress, DateTime _date,
			List<SensorChannel> _values) {
		this.sensorUUID = Math.abs(_sensorAddress.hashCode());
		this.date = _date;
		this.channels = new ArrayList<SensorChannel>(_values);
	}

	private int sensorUUID;
	private DateTime date;
	private ArrayList<SensorChannel> channels;

	@Override
	public String toString() {
		return "SensorData [sensorUUID=" + sensorUUID + ", date=" + date
				+ ", channels=" + channelsToString() + "]";
	}

	private String channelsToString() {
		return "";
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
