package estimator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import storage.HistoricalDatabase;
import usage.EstimationData;

import common.Config;

class NodeInfoAnalyzer
{
	private HistoricalDatabase hdb = null;
	private Properties energyFile;

	public NodeInfoAnalyzer(String _hdbFile)
	{
		try
		{
			hdb = new HistoricalDatabase(_hdbFile);
			energyFile = new Properties();
			energyFile.load(new FileInputStream(Config.ENERGY_FILE));
		} catch (FileNotFoundException e)
		{
			Logger.getLogger(NodeInfoAnalyzer.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		} catch (IOException e)
		{
			Logger.getLogger(NodeInfoAnalyzer.class.getName()).log(
					Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Analyze the consulted data.
	 * 
	 * @param _core
	 * @param _partition
	 */
	public void analyze(EstimationData _eData)
	{
		
	}
}