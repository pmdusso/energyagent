package monitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import common.Symbols;
import common.UnityType;
import common.UsageType;
import common.Utils;

import parser.ProcParser;
import parser.SensorParser;
import usage.CpuData;
import usage.DiskData;
import usage.MemData;
import usage.MonitoredData;
import usage.NetworkData;
import usage.SensorChannel;
import usage.SensorData;

/**
 * @author pmdusso
 */
class NodeInfoGather
{

	private final ProcParser pp;

	private SensorParser sp;
	private final int uuid;
	private String sensorAddress;

	private final Boolean isSensored;

	public NodeInfoGather(int _uuid, String _sensorAddress)
	{
		uuid = _uuid;
		if (Utils.stringNotEmpty(_sensorAddress))
		{
			sensorAddress = _sensorAddress;
			isSensored = true;
		} else
			isSensored = false;

		pp = new ProcParser(Utils.getPid());

		if (isSensored)
			sp = new SensorParser(sensorAddress);

	}

	/**
	 * Creates a list of CPU objects; Each objects of this list correspond to a
	 * CPU core. The first is the sum of all cores.
	 */

	private Map<Integer, CpuData> fillCpuData(ArrayList<String> gatheredData)
	{
		final Map<Integer, CpuData> c = new HashMap<Integer, CpuData>();
		final int offset = 10;
		for (int base = 0; base < gatheredData.size(); base += offset)
			c.put(Integer.parseInt(gatheredData.get(base)),
					new CpuData(Integer.parseInt(gatheredData.get(base)),
							Integer.parseInt(gatheredData.get(base + 1)),
							Long.parseLong(gatheredData.get(base + 2)),
							Long.parseLong(gatheredData.get(base + 3)),
							Long.parseLong(gatheredData.get(base + 4)),
							Long.parseLong(gatheredData.get(base + 5)),
							Long.parseLong(gatheredData.get(base + 6)),
							Integer.parseInt(gatheredData.get(base + 7)),
							Long.parseLong(gatheredData.get(base + 8)),
							Long.parseLong(gatheredData.get(base + 9))));
		return c;
	}

	/**
	 * Creates a list of Disk objects; Each object of this list correspond to a
	 * disk physical partition.
	 */

	private Map<String, DiskData> fillDiskData(List<String> gatheredData)
	{
		final Map<String, DiskData> d = new HashMap<String, DiskData>();
		final int offset = 14;
		for (int base = 0; base < gatheredData.size(); base += offset)
			d.put(gatheredData.get(base + 2),
					new DiskData(gatheredData.get(base),
							gatheredData.get(base + 1),
							gatheredData.get(base + 2),
							Long.parseLong(gatheredData.get(base + 3)),
							Long.parseLong(gatheredData.get(base + 4)),
							Long.parseLong(gatheredData.get(base + 5)),
							Long.parseLong(gatheredData.get(base + 6)),
							Long.parseLong(gatheredData.get(base + 7)),
							Long.parseLong(gatheredData.get(base + 8)),
							Long.parseLong(gatheredData.get(base + 9)),
							Long.parseLong(gatheredData.get(base + 10)),
							Long.parseLong(gatheredData.get(base + 11)),
							Long.parseLong(gatheredData.get(base + 12)),
							Long.parseLong(gatheredData.get(base + 13))));
		return d;
	}

	/**
	 * Creates a single Memory object corresponding to all values read from the
	 * system.
	 */

	private MemData fillMemData(List<String> gatheredData)
	{
		final MemData m = new MemData(
				Integer.parseInt(gatheredData.get(0)),
				Integer.parseInt(gatheredData.get(1)),
				Integer.parseInt(gatheredData.get(2)),
				Integer.parseInt(gatheredData.get(3)),
				// Integer.parseInt(gatheredData.get(4)), not used
				Integer.parseInt(gatheredData.get(5)),
				// Integer.parseInt(gatheredData.get(6)), not used
				gatheredData.get(7),
				Integer.parseInt(gatheredData.get(8)),
				gatheredData.get(9),
				Integer.parseInt(gatheredData.get(10)),
				// used memory = total memory - free memory
				(Integer.parseInt(gatheredData.get(10)) - Integer.parseInt(gatheredData.get(11))),
				Integer.parseInt(gatheredData.get(11)),
				Integer.parseInt(gatheredData.get(12)),
				Integer.parseInt(gatheredData.get(13)));
		return m;
	}

	/**
	 * Creates a list of Network objects; Each object of this list correspond to
	 * a network interface.
	 */

	private Map<String, NetworkData> fillNetworkData(List<String> gatheredData)
	{
		final Map<String, NetworkData> n = new HashMap<String, NetworkData>();
		final int offset = 17;
		for (int base = 0; base < gatheredData.size(); base += offset)
			n.put(gatheredData.get(base),
					new NetworkData(gatheredData.get(base),
							Long.parseLong(gatheredData.get(base + 1)),
							Integer.parseInt(gatheredData.get(base + 2)),
							Integer.parseInt(gatheredData.get(base + 3)),
							Integer.parseInt(gatheredData.get(base + 4)),
							Integer.parseInt(gatheredData.get(base + 5)),
							Integer.parseInt(gatheredData.get(base + 6)),
							Integer.parseInt(gatheredData.get(base + 7)),
							Integer.parseInt(gatheredData.get(base + 8)),
							Long.parseLong(gatheredData.get(base + 9)),
							Integer.parseInt(gatheredData.get(base + 10)),
							Integer.parseInt(gatheredData.get(base + 11)),
							Integer.parseInt(gatheredData.get(base + 12)),
							Integer.parseInt(gatheredData.get(base + 13)),
							Integer.parseInt(gatheredData.get(base + 14)),
							Integer.parseInt(gatheredData.get(base + 15)),
							Integer.parseInt(gatheredData.get(base + 16))));
		return n;
	}

	/**
	 * Create a sensor data object, with the information polled from the sensor
	 * 
	 * @param _sensorAddress
	 *            : the IP address
	 * @param gatheredData
	 *            : list with the data collected from the sensor
	 */

	private SensorData fillSensorData(String _sensorAddress,
			ArrayList<String> gatheredData)
	{
		final DateTime dateComplete = new DateTime();
		final List<SensorChannel> values = new ArrayList<SensorChannel>();

		for (final String s : gatheredData)
			if (s.contains("N"))
			{
				// E.g. N 001 ^C +00219E-01
				final String[] tempValues = Utils.removeEmptyStringsFromArray(s.split(Symbols.SPACE));

				if (tempValues.length == 4)
					values.add(new SensorChannel(
							Integer.parseInt(tempValues[1]),
							Utils.convertStringToUnity(tempValues[2]),
							Double.parseDouble(tempValues[3])));
				else
					values.add(new SensorChannel(
							Integer.parseInt(tempValues[1]), UnityType.NULL,
							Double.parseDouble(tempValues[2])));

			} else if (s.contains("DATE"))
			{
				// E.g. DATE 12/08/07 (YY-MM-DD)
				final String[] date = s.split(Symbols.SPACE)[1].split(Symbols.SLASH);
				dateComplete.plusYears(Integer.parseInt(date[0]) + 2000);
				dateComplete.plusMonths(Integer.parseInt(date[1]));
				dateComplete.plusDays(Integer.parseInt(date[2]));
			} else if (s.contains("TIME"))
			{
				// E.g. TIME 17:27:38
				final String[] time = s.split(Symbols.SPACE)[1].split(Symbols.COLON);
				dateComplete.plusHours(Integer.parseInt(time[0]));
				dateComplete.plusMinutes(Integer.parseInt(time[1]));
				dateComplete.plusSeconds(Integer.parseInt(time[2]));
			}

		return new SensorData(_sensorAddress, dateComplete, values);
	}

	/**
	 * Collect process system information from /proc file system. Collect
	 * machine physical information from a sensor (if installed on it).
	 * 
	 * @param: uuid: universally unique identifier of the node in the cluster.
	 * @throws IOException
	 */

	public synchronized MonitoredData getSystemUsage() throws IOException
	{

		SensorData sData = null;

		if (isSensored)
			sData = fillSensorData(sensorAddress, sp.gatherSensor());

		System.out.println("IP Address of the client: " + String.valueOf(uuid));

		return new MonitoredData(uuid,
				fillCpuData(pp.gatherUsage(UsageType.CPU)),
				fillMemData(pp.gatherUsage(UsageType.MEMORY)),
				fillDiskData(pp.gatherUsage(UsageType.DISK)),
				fillNetworkData(pp.gatherUsage(UsageType.NETWORK)), sData);
	}
}
