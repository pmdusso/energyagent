package common;

public enum Config
{
	INSTANCE;
	public static final String CONFIG_FILE = "config.properties";
	public static final String PORT = "PORT";
	public static final String SERVER_ADDRESS = "SERVER_ADDRESS";
	public static final String IS_SENSORED = "IS_SENSORED";
	public static final String SENSOR_ADDRESS = "SENSOR_ADDRESS";
	public static final String GATHER_INTERVAL = "GATHER_INTERVAL";
	public static final String HDB_NAME = "HDB_NAME";
	public static final String MACHINE_UUID = "MACHINE_UUID";
	public static final String CPU_CORE = "CPU_CORE";
	public static final String DISK_PARTITION = "DISK_PARTITION";
	public static final String TIME_INTERVAL_BEGIN = "TIME_INTERVAL_BEGIN";
	public static final String TIME_INTERVAL_END = "TIME_INTERVAL_END";
	public static final String DATE_TIME_FORMAT = "DATE_TIME_FORMAT";

	public static final String ENERGY_FILE = "energy.properties";
	public static final String MAX_FACTORY_TDP = "MAX_FACTORY_TDP";
	public static final String MAX_MEASURED_TDP = "MAX_MEASURED_TDP";
	public static final String AVG_DISK_FACTORY_PCP = "AVG_FACTORY_PCP";
	public static final String AVG_DISK_MEASURED_PCP = "AVG_MEASURED_PCP";
	public static final String AVG_MEM_FACTORY_PCP = "AVG_FACTORY_PCP";
	public static final String AVG_MEM_MEASURED_PCP = "AVG_MEASURED_PCP";
	public static final String MAX_NET_FACTORY_PCP = "MAX_NET_FACTORY_PCP";
	public static final String MAX_NET_MEASURED_PCP = "MAX_NET_MEASURED_PCP";
	public static final String MISC_PCP = "MISC_PCP";

}
