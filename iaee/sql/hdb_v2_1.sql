-- Table: Sensor
DROP TABLE IF EXISTS Sensor;
CREATE TABLE Sensor ( 
    ID        INTEGER,
    SENSOR_ID INTEGER,
    CONSTRAINT PK_Sensor PRIMARY KEY ( ID ) 
);




-- Table: Time
DROP TABLE IF EXISTS Time;
CREATE TABLE Time ( 
    ID     INTEGER,
    SECOND INTEGER,
    MINUTE INTEGER,
    HOUR   INTEGER,
    M_DAY  INTEGER,
    MONTH  INTEGER,
    YEAR   INTEGER,
    W_DAY  INTEGER,
    Y_DAY  INTEGER,
    IS_DST INTEGER,
    CONSTRAINT PK_Time PRIMARY KEY ( ID ) 
);




-- Table: Node
DROP TABLE IF EXISTS Node;
CREATE TABLE Node ( 
    ID      INTEGER,
    NODE_ID INTEGER,
    CONSTRAINT PK_Node PRIMARY KEY ( ID ) 
);




-- Table: UnityType
DROP TABLE IF EXISTS UnityType;
CREATE TABLE UnityType ( 
    ID    INTEGER,
    UNITY TEXT,
    CONSTRAINT PK_UNITY_TYPE PRIMARY KEY ( ID ) 
);




-- Table: CpuUsage
DROP TABLE IF EXISTS CpuUsage;
CREATE TABLE CpuUsage ( 
    ID      INTEGER,
    NODE_ID INTEGER,
    TIME_ID INTEGER,
    CORE_ID INTEGER,
    USER    INTEGER,
    NICE    INTEGER,
    SYSMODE INTEGER,
    IDLE    INTEGER,
    IOWAIT  INTEGER,
    IRQ     INTEGER,
    SOFTIRQ INTEGER,
    STEAL   INTEGER,
    GUEST   INTEGER,
    CONSTRAINT 'PK_CpuUsage' PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_CpuUsage_Node' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                     ON UPDATE CASCADE
);




-- Table: DiskUsage
DROP TABLE IF EXISTS DiskUsage;
CREATE TABLE DiskUsage ( 
    ID                             INTEGER,
    NODE_ID                        INTEGER,
    TIME_ID                        INTEGER,
    PARTITION_NAME                 INTEGER,
    READS_COMPLETED                INTEGER,
    READS_MERGED                   INTEGER,
    WRITES_MERGED                  INTEGER,
    SECTORS_READ                   INTEGER,
    MILLISECONDS_READING           INTEGER,
    WRITES_COMPLETED               INTEGER,
    SECTORS_WRITTEN                INTEGER,
    MILLISECONDS_WRITING           INTEGER,
    IO_IN_PROGRESS                 INTEGER,
    MILLISECONDS_SPENT_IN_IO       INTEGER,
    WEIGHTED_MILLISECONDS_DOING_IO INTEGER,
    CONSTRAINT 'PK_DiskUsage' PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_DiskUsage_Node' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                      ON UPDATE CASCADE 
);




-- Table: MemoryUsage
DROP TABLE IF EXISTS MemoryUsage;
CREATE TABLE MemoryUsage ( 
    ID          INTEGER,
    NODE_ID     INTEGER,
    TIME_ID     INTEGER,
    SIZE        INTEGER,
    RESIDENT    INTEGER,
    SHARE       INTEGER,
    TEXT        INTEGER,
    DATA        INTEGER,
    VIRTUALSIZE INTEGER,
    RSS         INTEGER,
    RSSLIM      INTEGER,
    MEM_TOTAL   INTEGER,
    MEM_USED    INTEGER,
    MEM_FREE    INTEGER,
    MEM_BUFFERS INTEGER,
    MEM_CACHED  INTEGER,
    CONSTRAINT 'PK_MemoryUsage' PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_MemoryUsage_Node' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                        ON UPDATE CASCADE 
);




-- Table: NetworkUsage
DROP TABLE IF EXISTS NetworkUsage;
CREATE TABLE NetworkUsage ( 
    ID           INTEGER,
    NODE_ID      INTEGER,
    TIME_ID      INTEGER,
    INTERFACE    TEXT,
    R_BYTES      INTEGER,
    R_PACKETS    INTEGER,
    R_ERRORS     INTEGER,
    R_DROP       INTEGER,
    R_FIFO       INTEGER,
    R_FRAME      INTEGER,
    R_COMPRESSED INTEGER,
    R_MULTICAST  INTEGER,
    T_BYTES      INTEGER,
    T_PACKETS    INTEGER,
    T_ERRORS     INTEGER,
    T_DROP       INTEGER,
    T_FIFO       INTEGER,
    T_COLLS      INTEGER,
    T_CARRIER    INTEGER,
    T_COMPRESSED INTEGER,
    CONSTRAINT 'PK_NetworkUsage' PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_NetworkUsage_Node' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                         ON UPDATE CASCADE 
);




-- Table: SensorUsage
DROP TABLE IF EXISTS SensorUsage;
CREATE TABLE SensorUsage ( 
    ID             INTEGER,
    NODE_ID        INTEGER,
    TIME_ID        INTEGER,
    SENSOR_ID      INTEGER,
    CHANNEL_NUMBER INTEGER,
    UNITY_TYPE_ID  INTEGER,
    VALUE          NUMERIC,
    CONSTRAINT 'PK_Sensor' PRIMARY KEY ( ID ),
    CONSTRAINT 'FK_Sensor_Node' FOREIGN KEY ( NODE_ID ) REFERENCES Node ( ID ) ON DELETE CASCADE
                                                                                   ON UPDATE CASCADE 
);





