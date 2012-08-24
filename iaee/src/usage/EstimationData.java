package usage;

import java.util.ArrayList;

import common.UsageType;

public class EstimationData
{
	private final int nodeUUID;
	ArrayList<Integer> cpuData;
	ArrayList<Integer> diskData;
	ArrayList<Integer> memData;

	public EstimationData(String _nodeUUID)
	{
		this.nodeUUID = Integer.parseInt(_nodeUUID);
		this.cpuData = new ArrayList<Integer>();
		this.diskData = new ArrayList<Integer>();
		this.memData = new ArrayList<Integer>();

	};

	public void addData(UsageType _type, ArrayList<Integer> _list)
	{
		switch (_type)
		{
		case CPU:
			this.cpuData.addAll(_list);

		case DISK:
			this.diskData.addAll(_list);

		case MEMORY:
			this.memData.addAll(_list);

		default:
			break;
		}
	}

	public Integer getNodeUUID()
	{
		return this.nodeUUID;
	}
}
