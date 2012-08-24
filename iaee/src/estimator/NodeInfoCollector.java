package estimator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.Interval;

import storage.HistoricalDatabase;
import storage.SqlScripts;
import usage.EstimationData;

import common.UsageType;

public class NodeInfoCollector
{
	private final Interval interval;
	private HistoricalDatabase hdb;

	public NodeInfoCollector(Interval _interval, String _hdbFile)
	{
		this.interval = _interval;
		hdb = new HistoricalDatabase(_hdbFile);

	}

	public EstimationData collect(String _uuid, String _core, String _partition)
	{
		EstimationData eData = new EstimationData(_uuid);

		eData.addData(UsageType.CPU, queryCpu(_uuid, _core));

		eData.addData(UsageType.DISK, queryDisk(_uuid, _partition));

		eData.addData(UsageType.MEMORY, queryMemory(_uuid));

		return eData;

	}

	private ArrayList<Integer> queryMemory(String _uuid)
	{
		String query = String.format(SqlScripts.SELECT_MEM_DATA, _uuid,
				this.interval.getStart().getHourOfDay(),
				this.interval.getStart().getMinuteOfHour(),
				this.interval.getStart().getSecondOfMinute(),
				this.interval.getEnd().getHourOfDay(),
				this.interval.getEnd().getMinuteOfDay(),
				this.interval.getEnd().getSecondOfMinute());

		ResultSet tempResult = this.hdb.readFromDatabase(query.toString());

		ArrayList<Integer> result = new ArrayList<Integer>();
		try
		{
			while (tempResult.next())
			{
				result.add(tempResult.getInt(1));
			}
		} catch (SQLException e)
		{
			Logger.getLogger(NodeInfoCollector.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}

	private ArrayList<Integer> queryDisk(String _uuid, String _partition)
	{
		String query = String.format(SqlScripts.SELECT_DISK_DATA, _uuid,
				_partition, this.interval.getStart().getHourOfDay(),
				this.interval.getStart().getMinuteOfHour(),
				this.interval.getStart().getSecondOfMinute(),
				this.interval.getEnd().getHourOfDay(),
				this.interval.getEnd().getMinuteOfDay(),
				this.interval.getEnd().getSecondOfMinute());

		ResultSet tempResult = this.hdb.readFromDatabase(query.toString());

		ArrayList<Integer> result = new ArrayList<Integer>();
		try
		{
			while (tempResult.next())
			{
				result.add(tempResult.getInt(1));
			}
		} catch (SQLException e)
		{
			Logger.getLogger(NodeInfoCollector.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}

	private ArrayList<Integer> queryCpu(String _uuid, String _core)
	{
		String query = String.format(SqlScripts.SELECT_CPU_DATA, _uuid, _core,
				this.interval.getStart().getHourOfDay(),
				this.interval.getStart().getMinuteOfHour(),
				this.interval.getStart().getSecondOfMinute(),
				this.interval.getEnd().getHourOfDay(),
				this.interval.getEnd().getMinuteOfHour(),
				this.interval.getEnd().getSecondOfMinute());

		ResultSet tempResult = this.hdb.readFromDatabase(query.toString());

		ArrayList<Integer> result = new ArrayList<Integer>();
		try
		{
			while (tempResult.next())
			{
				result.addAll(Arrays.asList(tempResult.getInt(1),
						tempResult.getInt(2), tempResult.getInt(3),
						tempResult.getInt(4), tempResult.getInt(5),
						tempResult.getInt(5), tempResult.getInt(7),
						tempResult.getInt(5), tempResult.getInt(9)));
			}
		} catch (SQLException e)
		{
			Logger.getLogger(NodeInfoCollector.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}

		return result;
	}

}
