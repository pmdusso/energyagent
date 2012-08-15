package storage;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import usage.MonitoredData;
import usage.SensorChannel;
import utils.Utils;

import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HistoricalDatabase {

    private Connection conn;
    private Statement stm;
    private DateTime date;
    private Map<Integer, Integer> nodes;
    private Map<Integer, Integer> sensors;
    private Map<String, Integer> unities;

    /**
     * O construtor cria uma nova conexão com o banco de dados sqlite contido no
     * arquivo passado como parâmetro. A conexão é possibilitada pelo driver
     * JDBC, fornecido por SQLiteJDBC.
     */
    public HistoricalDatabase(String file) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + file);
            initDB();
        } catch (@NotNull final SQLException ex) {
            Logger.getLogger(HistoricalDatabase.class.getName()).log(
                    Level.SEVERE, null, ex);
        } catch (@NotNull final ClassNotFoundException ex) {
            Logger.getLogger(HistoricalDatabase.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    private void loadSensorsFromDB() throws SQLException {
        Statement select = conn.createStatement();
        ResultSet result = select.executeQuery(SqlScripts.SELECT_SENSORS);

        /* NOTICE THE ORDE OF THE PARAMETERS! */
        /*
           * The key in the map have to be the sensor UUID, not the ID from the DB
           */
        while (result.next()) { // process results one row at a time
            if (!this.sensors.containsKey(result.getInt(1)))
                this.sensors.put(result.getInt(2), result.getInt(1));
        }
    }

    private void loadNodesFromDB() throws SQLException {
        Statement select = conn.createStatement();
        ResultSet result = select.executeQuery(SqlScripts.SELECT_NODES);

        /* NOTICE THE ORDE OF THE PARAMETERS! */
        /*
           * The key in the map have to be the node UUID, not the ID from the DB
           */
        while (result.next()) { // process results one row at a time
            if (!this.nodes.containsKey(result.getInt(1)))
                this.nodes.put(result.getInt(2), result.getInt(1));
        }
    }

    /**
     * Cria uma nova tabela de recordes, apagando tudo que houvesse na base
     * anteriormente.
     */
    private void initDB() {
        try {
            date = new DateTime();

            stm = conn.createStatement();
            String[] tables = SqlScripts.CREATE_TABLES.split(";");
            String[] indexes = SqlScripts.CREATE_INDEXES.split(";");

            for (final String query : tables)
                stm.executeUpdate(query);
            for (final String query : indexes)
                stm.executeUpdate(query);

            nodes = new HashMap<Integer, Integer>();
            loadNodesFromDB();
            sensors = new HashMap<Integer, Integer>();
            loadSensorsFromDB();
            unities = new HashMap<String, Integer>();
            loadUnityFromDB();

            if (unities.size() < 1) {
                String[] unityType = SqlScripts.INSERT_UNITY_TYPE.split(";");
                for (final String query : unityType)
                    stm.executeUpdate(query);
            }

        } catch (@NotNull final SQLException ex) {
            Logger.getLogger(HistoricalDatabase.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    private void loadUnityFromDB() throws SQLException {
        Statement select = conn.createStatement();
        ResultSet result = select.executeQuery(SqlScripts.SELECT_UNITY);

        /* NOTICE THE ORDE OF THE PARAMETERS! */
        while (result.next()) { // process results one row at a time
            if (!this.unities.containsKey(result.getString(2))) {
                this.unities.put(result.getString(2), result.getInt(1));
            }
        }
    }

    /**
     * Adiciona uma nova linha na tabela de recordes.
     */
    public void saveOrUpdate(@NotNull MonitoredData mData) {

        final String timeID = getTimeLastRowID();
        int nodeID;
        int sensorID;
        try {
            if (!this.nodes.containsKey(mData.getNodeUUID())) {
                saveOrUpdateToDatabase(SqlScripts.INSERT_NODE.replace("?",
                        String.valueOf(mData.getNodeUUID())));

                loadNodesFromDB();
            }
            if (!this.sensors.containsKey(mData.getSensor().getSensorUUID())) {
                saveOrUpdateToDatabase(SqlScripts.INSERT_SENSOR.replace("?",
                        String.valueOf(mData.getSensor().getSensorUUID())));
                loadSensorsFromDB();
            }

        } catch (SQLException e) {
            Logger.getLogger(HistoricalDatabase.class.getName()).log(
                    Level.SEVERE, null, e);
        }

        nodeID = this.nodes.get(mData.getNodeUUID());
        sensorID = this.sensors.get(mData.getSensor().getSensorUUID());

        // statements prepared and executed here
        // Insere na tabela de nodos este cliente, usando como primary key o
        // hashCode do IP address da máquina

        try {
            saveOrUpdateToDatabase(String.format(SqlScripts.INSERT_MEMORY_USAGE,
                    nodeID, timeID, mData.getMem().getSize(), mData.getMem()
                    .getResident(), mData.getMem().getShare(), mData
                    .getMem().getText(), mData.getMem().getData(), mData
                    .getMem().getVsize(), mData.getMem().getRss(), mData
                    .getMem().getRsslim(), mData.getMem().getMemTotal(),
                    mData.getMem().getMemUsed(), mData.getMem().getMemFree(), mData
                    .getMem().getMemBuffers(), mData.getMem()
                    .getMemCached()));
            for (int core = 0; core < mData.getCpu().keySet().size(); core++)
                saveOrUpdateToDatabase(String.format(SqlScripts.INSERT_CPU_USAGE,
                        nodeID, timeID, mData.getCpu().get(core).getCoreId(), mData
                        .getCpu().get(core).getUser(),
                        mData.getCpu().get(core).getNice(), mData.getCpu()
                        .get(core).getSysmode(), mData.getCpu().get(core)
                        .getIdle(), mData.getCpu().get(core).getIowait(),
                        mData.getCpu().get(core).getIrq(), mData.getCpu().get(core)
                        .getSoftirq(), mData.getCpu().get(core).getSteal(),
                        mData.getCpu().get(core).getGuest()));
            for (final String o : mData.getDisk().keySet())
                saveOrUpdateToDatabase(String.format(SqlScripts.INSERT_DISK_USAGE,
                        nodeID, timeID, mData.getDisk().get(o).getName(), mData
                        .getDisk().get(o).getReadsCompleted(), mData
                        .getDisk().get(o).getReadsMerged(), mData.getDisk()
                        .get(o).getWritesMerged(), mData.getDisk().get(o)
                        .getSectorsRead(), mData.getDisk().get(o)
                        .getMillisecondsReading(), mData.getDisk().get(o)
                        .getWritesCompleted(), mData.getDisk().get(o)
                        .getSectorsWritten(), mData.getDisk().get(o)
                        .getMillisecondsWriting(), mData.getDisk().get(o)
                        .getIosInProgress(), mData.getDisk().get(o)
                        .getMillisecondsSpentInIO(), mData.getDisk().get(o)
                        .getWeightedMillisecondsDoingIO()));
            for (final String o : mData.getNet().keySet())
                saveOrUpdateToDatabase(String.format(
                        SqlScripts.INSERT_NETWORK_USAGE, nodeID, timeID, mData
                        .getNet().get(o).getInterfaceName(), mData.getNet()
                        .get(o).getReceive().getRX_Bytes(), mData.getNet()
                        .get(o).getReceive().getRX_Packets(), mData
                        .getNet().get(o).getReceive().getRX_Erros(), mData
                        .getNet().get(o).getReceive().getRX_Dropped(),
                        mData.getNet().get(o).getReceive().getRX_Fifo(), mData
                        .getNet().get(o).getReceive().getRX_Frame(), mData
                        .getNet().get(o).getReceive().getRX_Compressed(),
                        mData.getNet().get(o).getReceive().getRX_Multicast(), mData
                        .getNet().get(o).getTransmit().getTX_Bytes(), mData
                        .getNet().get(o).getTransmit().getTX_Packets(),
                        mData.getNet().get(o).getTransmit().getTX_Erros(), mData
                        .getNet().get(o).getTransmit().getTX_Dropped(),
                        mData.getNet().get(o).getTransmit().getTX_Fifo(), mData
                        .getNet().get(o).getTransmit().getTX_Collisions(),
                        mData.getNet().get(o).getTransmit().getTX_CarrierErrors(),
                        mData.getNet().get(o).getTransmit().getTX_Compressed()));

            for (SensorChannel o : mData.getSensor().getChannels()) {

                saveOrUpdateToDatabase(String.format(
                        SqlScripts.INSERT_SENSOR_USAGE, nodeID, timeID, sensorID,
                        o.getNummer(),
                        this.unities.get(Utils.convertUnityToString(o.getUnity())),
                        o.getValue()));
            }
        } catch (Exception e) {
            Logger.getLogger(HistoricalDatabase.class.getName()).log(
                    Level.SEVERE, null, e);
        }

    }

    /**
     * Adiciona uma nova linha na tabela e retorna o ID da linha inserida
     *
     * @param insert : the insert statement
     * @return o ID da linha inserida
     */
    private synchronized int saveOrUpdateToDatabase(String insert) {
        try {
            stm = conn.createStatement();
            stm.executeUpdate(insert);
            return getLastInsertRowId();
        } catch (@NotNull final SQLException ex) {
            Logger.getLogger(HistoricalDatabase.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        return -1;
    }

    /**
     * @return The ID of the last row inserted in the database
     */
    private int getLastInsertRowId() {
        int rowID = 0;
        ResultSet rs;
        try {
            rs = stm.getGeneratedKeys();
            while (rs.next())
                rowID = rs.getInt(1);
        } catch (@NotNull final SQLException ex) {
            Logger.getLogger(HistoricalDatabase.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        return rowID;
    }

    /**
     * Insert a time row in the time table and return the last row id generated.
     */
    private String getTimeLastRowID() {
        return String.valueOf(saveOrUpdateToDatabase(String.format(
                SqlScripts.INSERT_TIME, getTime())));
    }

    /**
     * Get the current time in the following format: SECOND,MINUTE,HOUR,Month
     * DAY,MONTH,YEAR,Week DAY,Year DAY, is Daylight Savings Time
     */
    private String getTime() {
        return String.format("%d, %d, %d, %d, %d, %d, %d, %d, %d", date
                .getSecondOfMinute(), date.getMinuteOfHour(), date
                .getHourOfDay(), date.getDayOfMonth(), date.getMonthOfYear(),
                date.getYear(), date.getDayOfWeek(), date.getDayOfYear(),
                (TimeZone.getDefault().inDaylightTime(new Date()) == true ? 1
                        : 0));
    }

}
