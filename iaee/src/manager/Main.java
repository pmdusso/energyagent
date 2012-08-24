package manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import monitor.MonitoringClient;
import monitor.MonitoringMaster;

import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import common.Config;
import common.Symbols;
import common.Utils;

import storage.HistoricalDatabase;
import estimator.EstimationMaster;

/**
 * @author Pedro Martins Dusso
 */
public class Main
{
	public static void main(String[] args)
	{
		Properties configFile = new Properties();
		try
		{
			configFile.load(new FileInputStream(Config.CONFIG_FILE));

			if (args.length != 1)
			{
				System.out.println("Use:");
				System.out.println("monitor -m to run the Monitoring Master");
				System.out.println("monitor -c to run the Monitoring Client");
				System.out.println("monitor -e to run the Energy Estimation");
				System.out.println("ATENTION! Configure default properties for the monitoring" + " in the config.properties file.");
			} else
			{

				if (args[0].equals("-m"))
					startMonitoringMasterService(
							getHistoricalDBName(configFile.getProperty(Config.HDB_NAME)),
							getServerPort(configFile.getProperty(Config.PORT)));
				else if (args[0].equals("-c"))
					if (Boolean.valueOf(Config.IS_SENSORED))
					{
						startMonitoringClientService(
								getGatherInterval(configFile.getProperty(Config.GATHER_INTERVAL)),
								getServerAddress(configFile.getProperty(Config.SERVER_ADDRESS)),
								getServerPort(configFile.getProperty(Config.PORT)),
								getSensorAddress(configFile.getProperty(Config.SENSOR_ADDRESS)));
					} else
					{
						startMonitoringClientService(
								getGatherInterval(configFile.getProperty(Config.GATHER_INTERVAL)),
								getServerAddress(configFile.getProperty(Config.SERVER_ADDRESS)),
								getServerPort(configFile.getProperty(Config.PORT)),
								Symbols.EMPTY);
					}
				else if (args[0].equals("-e"))
				{
					startEstimationMasterService(

							getNodeCpuCore(configFile.getProperty(Config.CPU_CORE)),
							getNodeDiskPartition(configFile.getProperty(Config.DISK_PARTITION)),
							getTimeInterval(
									configFile.getProperty(Config.TIME_INTERVAL_BEGIN),
									configFile.getProperty(Config.TIME_INTERVAL_END),
									configFile.getProperty(Config.DATE_TIME_FORMAT)),
							getHistoricalDBName(configFile.getProperty(Config.HDB_NAME)));
				}
				System.out.println("Press Control-C to stop.");
			}

		} catch (Exception ex)
		{
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE,
					ex.getMessage());
		}
	}

	private static String getNodeDiskPartition(String _diskPartition)
	{
		if (!Utils.stringNotEmpty(_diskPartition))
			throw new IllegalArgumentException(
					"Parameter: <Disk Partition> empty.");
		else
			return _diskPartition;
	}

	private static String getNodeCpuCore(String _cpuCore)
	{
		if (!Utils.stringNotEmpty(_cpuCore))
			throw new IllegalArgumentException("Parameter: <Cpu Core> empty.");
		else
			return _cpuCore;
	}

	private static Interval getTimeInterval(String timeBegin, String timeEnd,
			String _format)
	{
		Interval interval;
		try
		{
			DateTimeFormatter format = DateTimeFormat.forPattern("HH:mm:ss");
			interval = new Interval(format.parseDateTime(timeBegin),
					format.parseDateTime(timeEnd));
		} catch (Exception e)
		{
			throw new IllegalArgumentException(
					"Parameter: <Time Begin/Time End>.", e);
		}
		return interval;
	}

	/**
	 * @param _gatherInterval
	 *            : the gather interval which the monitoring service read the
	 *            proc filesystem
	 * @return The value read from configuration file OR the minimal value of 1
	 *         second
	 */
	private static int getGatherInterval(String _gatherInterval)
	{
		if (Integer.parseInt(_gatherInterval) < 1000)
			return 1000;
		else
			return Integer.parseInt(_gatherInterval);
	}

	/**
	 * @param _hdbName
	 *            : the name of the historical database file, created and filled
	 *            by the master service.
	 * @throws IllegalArgumentException
	 */
	private static String getHistoricalDBName(String _hdbName)
	{
		if (!Utils.stringNotEmpty(_hdbName))
			throw new IllegalArgumentException(
					"Parameter: <Historical Database Name> empty.");
		else
			return _hdbName;
	}

	/**
	 * @param _serverAddress
	 *            : the IP address of the master monitoring service.
	 * @return A valid IP address for the master monitoring service.
	 * @throws IllegalArgumentException
	 */
	private static String getSensorAddress(String _sensorAddress)
	{
		if (!Utils.stringNotEmpty(_sensorAddress))
		{
			throw new IllegalArgumentException(
					"Parameter: <Sensor Address> empty or wrong format.");
		} else
			return _sensorAddress;
	}

	/**
	 * @param _serverAddress
	 *            : the IP address of the master monitoring service.
	 * @return A valid IP address for the master monitoring service.
	 * @throws IllegalArgumentException
	 */
	private static String getServerAddress(String _serverAddress)
	{
		if (!Utils.stringNotEmpty(_serverAddress))
			throw new IllegalArgumentException(
					"Parameter: <Server Address> empty or wrong format.");
		else
			return _serverAddress;
	}

	/**
	 * @param _serverPort
	 *            : the port where the master serves the monitoring clients.
	 * @return A valid port value.
	 */
	private static int getServerPort(String _serverPort)
	{
		if (!Utils.stringNotEmpty(_serverPort))
			throw new IllegalArgumentException(
					"Parameter: <Server Port> empty.");
		else
			return Integer.valueOf(_serverPort);
	}

	/**
	 * Monitoring Client Service
	 * 
	 * @param _serverAddress
	 *            : The master monitoring server IP address to connect to.
	 * @param _serverPort
	 *            : The port which the socket must connect to.
	 * @param _gatherInterval
	 *            : The interval which the monitoring service will read the proc
	 *            filesystem
	 * @throws IllegalArgumentException
	 * @throws NumberFormatException
	 */
	private static void startMonitoringClientService(int _gatherInterval,
			String _serverAddress, int _serverPort, String _sensorAddress)
	{
		new MonitoringClient(_gatherInterval, _serverAddress, _serverPort,
				_sensorAddress);
	}

	/**
	 * Monitoring Master Service
	 * 
	 * @param _serverPort
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	private static void startMonitoringMasterService(String _hdbName,
			int _serverPort) throws IllegalArgumentException, IOException
	{
		Logger.getLogger(Main.class.getName()).info(
				"Creating master service...");
		final ServerSocket servSock = new ServerSocket(_serverPort);
		final HistoricalDatabase hdb = new HistoricalDatabase(_hdbName);

		int totalClientsConnected = 0;

		// Run forever, accepting and spawning a thread for each connection
		while (true)
		{
			Logger.getLogger(Main.class.getName()).info(
					String.format(
							"Waiting for connections. Right now %d clients connected. ",
							totalClientsConnected));
			// Block waiting for connection
			final Socket clntSock = servSock.accept();
			totalClientsConnected++;
			final Thread thread = new Thread(
					new MonitoringMaster(clntSock, hdb));
			thread.start();
			Logger.getLogger(Main.class.getName()).info(
					"Created and started Thread " + thread.getName());
		}
		/* NOT REACHED */
	}

	private static void startEstimationMasterService(String _cpuCore,
			String _diskPartition, Interval _timeInterval, String _hdbName)
	{
		Logger.getLogger(Main.class.getName()).info(
				"Creating estimation service...");
		HistoricalDatabase hdb = new HistoricalDatabase(_hdbName);
		ArrayList<String> nodes = hdb.GetNodeListFromDB();

		for (String nodeUUID : nodes)
		{
			new Thread(new EstimationMaster(nodeUUID, _cpuCore, _diskPartition,
					_timeInterval, _hdbName)).start();
		}
	}
}