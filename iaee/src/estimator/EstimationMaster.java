package estimator;

import org.joda.time.Interval;

public class EstimationMaster implements Runnable
{
	private final NodeInfoCollector collector;
	private final NodeInfoAnalyzer analyzer;

	private final String nodeUUID;
	private final String cpuCore;
	private final String diskPartition;

	public EstimationMaster(String _nodeUUID, String _cpuCore,
			String _diskPartition, Interval _timeInterval, String _hdbFile)
	{
		this.nodeUUID = _nodeUUID;
		this.cpuCore = _cpuCore;
		this.diskPartition = _diskPartition;

		this.collector = new NodeInfoCollector(_timeInterval, _hdbFile);
		this.analyzer = new NodeInfoAnalyzer(_hdbFile);
	}

	public void run()
	{
		this.analyzer.analyze(this.collector.collect(this.nodeUUID, this.cpuCore, this.diskPartition));
	}

}
