package storage;

class SqlScripts {

    /**
     * Insert into TIME table
     */
    public static final String INSERT_TIME = "INSERT INTO Time (SECOND,MINUTE,HOUR,M_DAY,MONTH,YEAR,W_DAY,Y_DAY,IS_DST) VALUES (%s); ";
    /**
     * Insert into NODE table
     */
    public static final String INSERT_NODE = "INSERT OR REPLACE INTO Node (NODE_ID) VALUES (?);";

    public static final String SELECT_NODES = "SELECT * FROM Node;";

    public static final String SELECT_SENSORS = "SELECT * FROM Sensor;";

    public static final String SELECT_UNITY = "SELECT * FROM UnityType";
    /**
     * Insert into Sensor table
     */
    public static final String INSERT_SENSOR = "INSERT OR REPLACE INTO Sensor (SENSOR_ID) VALUES(?);";
    /**
     * Insert into UnityType table
     */
    public static final String INSERT_UNITY_TYPE = "INSERT OR REPLACE INTO UnityType (UNITY) VALUES ('CELCIUS'); INSERT OR REPLACE INTO UnityType (UNITY) VALUES ('WATT'); INSERT OR REPLACE INTO UnityType (UNITY) VALUES ('PERCENT'); INSERT OR REPLACE INTO UnityType (UNITY) VALUES ('EMPTY'); INSERT OR REPLACE INTO UnityType (UNITY) VALUES ('MBITS'); INSERT OR REPLACE INTO UnityType (UNITY) VALUES ('GBITS');";


    /**
     * Insert into CPU table
     */
    public static final String INSERT_CPU_USAGE = "INSERT INTO CpuUsage (NODE_ID, TIME_ID, CORE_ID, USER, NICE, SYSMODE, IDLE, IOWAIT, IRQ, SOFTIRQ, STEAL, GUEST) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
    /**
     * Insert into Memory table
     */
    public static final String INSERT_MEMORY_USAGE = "INSERT INTO MemoryUsage (NODE_ID, TIME_ID, SIZE, RESIDENT, SHARE, TEXT, DATA, VIRTUALSIZE, RSS, RSSLIM, MEM_TOTAL, MEM_USED, MEM_FREE, MEM_BUFFERS, MEM_CACHED) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
    /**
     * Insert into Network table
     */
    public static final String INSERT_NETWORK_USAGE = "INSERT INTO NetworkUsage (NODE_ID, TIME_ID, INTERFACE, R_BYTES, R_PACKETS, R_ERRORS, R_DROP, R_FIFO, R_FRAME, R_COMPRESSED, R_MULTICAST, T_BYTES, T_PACKETS, T_ERRORS, T_DROP, T_FIFO, T_COLLS, T_CARRIER, T_COMPRESSED) VALUES (%s, %s, '%s', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
    /**
     * Insert into Disk table
     */
    public static final String INSERT_DISK_USAGE = "INSERT INTO DiskUsage (NODE_ID, TIME_ID, PARTITION_NAME, READS_COMPLETED, READS_MERGED, WRITES_MERGED, SECTORS_READ, MILLISECONDS_READING, WRITES_COMPLETED, SECTORS_WRITTEN, MILLISECONDS_WRITING, IO_IN_PROGRESS, MILLISECONDS_SPENT_IN_IO, WEIGHTED_MILLISECONDS_DOING_IO) VALUES (%s, %s, '%s', %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";
    /**
     * Insert into Sensor table
     */
    public static final String INSERT_SENSOR_USAGE = "INSERT INTO SensorUsage(NODE_ID, TIME_ID, SENSOR_ID, CHANNEL_NUMBER, UNITY_TYPE_ID, VALUE) VALUES (%s,%s,%s,%s,%s,%s);";

    /**
     * Create table
     */
    public static final String CREATE_TABLES = "CREATE TABLE IF NOT EXISTS Sensor ( ID  INTEGER, SENSOR_ID INTEGER, CONSTRAINT PK_Sensor PRIMARY KEY ( ID ) ); CREATE TABLE IF NOT EXISTS Time ( ID INTEGER, SECOND INTEGER, MINUTE INTEGER, HOUR  INTEGER, M_DAY INTEGER, MONTH INTEGER, YEAR  INTEGER, W_DAY INTEGER, Y_DAY INTEGER, IS_DST INTEGER, CONSTRAINT PK_Time PRIMARY KEY ( ID ) ); CREATE TABLE IF NOT EXISTS Node ( ID INTEGER, NODE_ID INTEGER, CONSTRAINT PK_Node PRIMARY KEY ( ID ) ); CREATE TABLE IF NOT EXISTS UnityType ( ID  INTEGER, UNITY TEXT, CONSTRAINT PK_UNITY_TYPE PRIMARY KEY ( ID ) ); CREATE TABLE IF NOT EXISTS CpuUsage ( ID INTEGER, NODE_ID INTEGER, TIME_ID INTEGER, CORE_ID INTEGER, USER  INTEGER, NICE  INTEGER, SYSMODE INTEGER, IDLE  INTEGER, IOWAIT INTEGER, IRQ INTEGER, SOFTIRQ INTEGER, STEAL  INTEGER, GUEST  INTEGER, CONSTRAINT 'PK_CpuUsage' PRIMARY KEY ( ID ), CONSTRAINT 'FK_CpuUsage_Node' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE ON UPDATE CASCADE ); CREATE TABLE IF NOT EXISTS DiskUsage ( ID INTEGER, NODE_ID INTEGER, TIME_ID INTEGER, PARTITION_NAME INTEGER, READS_COMPLETED INTEGER, READS_MERGED  INTEGER, WRITES_MERGED INTEGER, SECTORS_READ  INTEGER, MILLISECONDS_READING INTEGER, WRITES_COMPLETED INTEGER, SECTORS_WRITTEN INTEGER, MILLISECONDS_WRITING INTEGER, IO_IN_PROGRESS INTEGER, MILLISECONDS_SPENT_IN_IO INTEGER, WEIGHTED_MILLISECONDS_DOING_IO INTEGER, CONSTRAINT 'PK_DiskUsage' PRIMARY KEY ( ID ), CONSTRAINT 'FK_DiskUsage_Node' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE ON UPDATE CASCADE ); CREATE TABLE IF NOT EXISTS MemoryUsage ( ID INTEGER, NODE_ID INTEGER, TIME_ID INTEGER, SIZE  INTEGER, RESIDENT  INTEGER, SHARE INTEGER, TEXT  INTEGER, DATA  INTEGER, VIRTUALSIZE INTEGER, RSS  INTEGER, RSSLIM INTEGER, MEM_TOTAL  INTEGER, MEM_USED  INTEGER, MEM_FREE  INTEGER, MEM_BUFFERS INTEGER, MEM_CACHED INTEGER, CONSTRAINT 'PK_MemoryUsage' PRIMARY KEY ( ID ), CONSTRAINT 'FK_MemoryUsage_Node' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE  ON UPDATE CASCADE );  CREATE TABLE IF NOT EXISTS NetworkUsage ( ID INTEGER, NODE_ID INTEGER, TIME_ID INTEGER, INTERFACE  TEXT, R_BYTES INTEGER, R_PACKETS  INTEGER, R_ERRORS INTEGER, R_DROP INTEGER, R_FIFO INTEGER, R_FRAME INTEGER, R_COMPRESSED INTEGER, R_MULTICAST INTEGER, T_BYTES INTEGER, T_PACKETS  INTEGER, T_ERRORS INTEGER, T_DROP INTEGER, T_FIFO INTEGER, T_COLLS INTEGER, T_CARRIER  INTEGER, T_COMPRESSED INTEGER, CONSTRAINT 'PK_NetworkUsage' PRIMARY KEY ( ID ), CONSTRAINT 'FK_NetworkUsage_Node' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE ON UPDATE CASCADE ); CREATE TABLE IF NOT EXISTS SensorUsage ( ID  INTEGER, NODE_ID  INTEGER, TIME_ID  INTEGER, SENSOR_ID INTEGER, CHANNEL_NUMBER INTEGER, UNITY_TYPE_ID INTEGER, VALUE NUMERIC, CONSTRAINT 'PK_Sensor' PRIMARY KEY ( ID ), CONSTRAINT 'FK_Sensor_Node' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE ON UPDATE CASCADE );";
    /**
     * Create index
     */
    public static final String CREATE_INDEXES = "CREATE UNIQUE INDEX IF NOT EXISTS idx_CpuUsage ON CpuUsage ( TIME_ID, NODE_ID, CORE_ID ); CREATE UNIQUE INDEX IF NOT EXISTS idx_DiskUsage ON DiskUsage ( PARTITION_NAME, NODE_ID, TIME_ID ); CREATE UNIQUE INDEX IF NOT EXISTS idx_MemoryUsage ON MemoryUsage ( NODE_ID, TIME_ID ); CREATE UNIQUE INDEX IF NOT EXISTS idx_NetworkUsage ON NetworkUsage ( NODE_ID, TIME_ID, INTERFACE ); CREATE UNIQUE INDEX IF NOT EXISTS idx_SensorUsage ON SensorUsage ( CHANNEL_NUMBER, TIME_ID, NODE_ID, SENSOR_ID );";

}
