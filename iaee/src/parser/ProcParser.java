package parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usage.UsageType;
import utils.Symbols;
import utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extract information from the process information pseudo-file system called
 * "/proc".
 *
 * @author pmdusso
 * @version 1.0 @created 24-abr-2012 15:22:37
 */
public class ProcParser {

    /*
      * The pid of the process
      */
    private int processPid = -1;

    /*
      * Constant access path string. Those with 'pid' before are in /proc/[pid]/;
      * Those with 'net' are in /proc/net/. Those without are directly in /proc/.
      */
    private static final String pidStatmPath = "/proc/#/statm";
    private static final String pidStatPath = "/proc/#/stat";
    private static final String statPath = "/proc/stat";
    private static final String cpuinfoPath = "/proc/cpuinfo";
    private static final String meminfoPath = "/proc/meminfo";
    private static final String netdevPath = "/proc/net/dev";
    private static final String partitionsPath = "/proc/partitions";
    private static final String diskstatsPath = "/proc/diskstats";

    /**
     * @param usageType
     */
    public ProcParser(int processPid) {
        this.processPid = processPid;
    }

    /**
     * Gathers the usage statistic from the /proc file system for CPU, Memory,
     * Disk and Network
     */
    @Nullable
    public ArrayList<String> gatherUsage(@Nullable UsageType uType) {
        if ((uType == null) || (processPid < 0))
            throw new IllegalArgumentException();
        ArrayList<String> usageData = null;

        switch (uType) {
            case CPU:
                usageData = gatherCpuUsage();
                break;
            case MEMORY:
                usageData = gatherMemoryUsage(processPid);
                break;
            case DISK:
                usageData = gatherDiskUsage();
                break;
            case NETWORK:
                usageData = gatherNetworkUsage();
                break;
            default:
                break;
        }
        return usageData;
    }

    /**
     * Parse /proc/stat file and fill the member values list We gonna parse the
     * first line (total) and each line corresponding to one core Line example:
     * cpu0 311689 2102 654770 6755602 32431 38 4127 0 0 0
     *
     * @param _processPid
     * @param _memberValues
     * @throws IOException
     */
    @NotNull
    private ArrayList<String> gatherCpuUsage() {
        BufferedReader br;
        final ArrayList<String> data = new ArrayList<String>();
        String[] tempData;
        try {
            final int numberOfCores = getNumberofCores();

            br = getStream(statPath);
            // Read from 0 until the number of cores because the first line
            // is the total times for all cores.
            for (int core = 0; core <= numberOfCores; core++) {
                data.add(String.valueOf(core));
                tempData = Utils.removeEmptyStringsFromArray(br.readLine()
                        .split(Symbols.SPACE));
                // Adds the first 9 fields.
                data.addAll(Arrays.asList(tempData).subList(1, 10));
            }
            br.close();
        } catch (@NotNull final IOException ex) {
            Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return data;
    }

    private int getNumberofCores() throws NumberFormatException {
        String[] tempData = null;
        String[] tempFile;

        // Parse /proc/cpuinfo to obtain how many cores the CPU has.
        tempFile = getContents(cpuinfoPath).split(
                System.getProperty(Symbols.LINE_SEPARATOR));
        for (final String line : tempFile)
            if (line.contains("cpu cores")) {
                tempData = line.split(Symbols.COLON);
                break;
            }
        return Integer.parseInt(tempData[1].trim());
    }

    /**
     * Get memory usage information. Files: /proc/[pid]/statm /proc/[pid]/stat
     *
     * @param _processPid
     * @param _memberValues
     */
    @NotNull
    private ArrayList<String> gatherMemoryUsage(int _processPid) {
        BufferedReader br;
        final ArrayList<String> data = new ArrayList<String>();
        String[] tempData;
        try {
            // Parse /proc/[pid]/statm file and fill the member values list with
            // its contents (all)
            tempData = getContents(
                    pidStatmPath.replace(Symbols.SHARP,
                            String.valueOf(_processPid))).trim().split(
                    Symbols.SPACE);
            data.addAll(Arrays.asList(tempData));
            // Parse /proc/[pid]/stat file and fill the member values list just
            // with values 22, 23 and 24 (vsize, resident set size and resident
            // set size limit).
            tempData = getContents(
                    pidStatPath.replace(Symbols.SHARP,
                            String.valueOf(_processPid))).trim().split(
                    Symbols.SPACE);
            data.add(tempData[22]);
            data.add(tempData[23]);
            data.add(tempData[24]);
            // Parse /proc/meminfo file for the system memory information.
            br = getStream(meminfoPath);
            for (int i = 0; i < 4; i++) {
                tempData = br.readLine().trim().split(Symbols.SPACE);
                for (final String s : tempData)
                    if (!s.isEmpty() && Utils.tryParseInt(s))
                        data.add(s);
            }
            br.close();
        } catch (@NotNull final FileNotFoundException ex) {
            Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE,
                    null, ex);
        } catch (@NotNull final IOException ex) {
            Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return data;
    }

    /**
     * @param _memberValues
     */
    @NotNull
    private ArrayList<String> gatherNetworkUsage() {
        final ArrayList<String> data = new ArrayList<String>();
        String[] tempData;
        String[] tempFile;

        tempFile = getContents(netdevPath).split(
                System.getProperty(Symbols.LINE_SEPARATOR));
        // Skip the first two lines (headers)
        for (int i = 2; i < tempFile.length; i++) {
            // Parse /proc/net/dev to obtain network statistics.
            // Line e.g.:
            // lo: 4852 43 0 0 0 0 0 0 4852 43 0 0 0 0 0 0
            tempData = tempFile[i].replace(Symbols.COLON, Symbols.SPACE).split(
                    Symbols.SPACE);
            data.addAll(Arrays.asList(tempData));
            data.removeAll(Collections.singleton(Symbols.EMPTY));
        }
        return data;
    }

    /**
     * @param _memberValues
     */
    @NotNull
    private ArrayList<String> gatherPartitionUsage() {
        final ArrayList<String> data = new ArrayList<String>();
        String[] tempData;
        String[] tempFile;

        tempFile = getContents(partitionsPath).split(
                System.getProperty(Symbols.LINE_SEPARATOR));

        // parse the disk partitions
        for (int i = 2; i < tempFile.length; i++) {
            tempData = tempFile[i].split(Symbols.SPACE);
            data.addAll(Arrays.asList(tempData));
            data.removeAll(Collections.singleton(Symbols.EMPTY));
        }
        return data;
    }

    /*
      * Create a list with the partitions name to be used to find their
      * statistics in /proc/diskstats file
      */
    @NotNull
    private ArrayList<String> getPartitionNames(@NotNull ArrayList<String> data) {

        final ArrayList<String> partitionsName = new ArrayList<String>();
        for (final String string : data)
            if (!Utils.tryParseInt(string))
                partitionsName.add(string);
        return partitionsName;
    }

    /**
     * @param _memberValues
     */
    @NotNull
    private ArrayList<String> gatherDiskUsage() {
        final ArrayList<String> partitionData = gatherPartitionUsage();
        final ArrayList<String> data = new ArrayList<String>();
        String[] tempData;
        String[] tempFile;

        tempFile = getContents(diskstatsPath).split(
                System.getProperty(Symbols.LINE_SEPARATOR));
        final ArrayList<String> tempPart = getPartitionNames(partitionData);
        final ArrayList<String> tempPartClean = new ArrayList<String>();
        for (final String part : tempPart)
            if (part.contains("sda"))
                tempPartClean.add(part);
        // Parse /proc/diskstats to obtain disk statistics
        for (final String line : tempFile)
            for (final String partition : tempPartClean)
                if (line.contains(Symbols.SPACE + partition + Symbols.SPACE)) {
                    // split(SPACE);
                    tempData = Utils.removeEmptyStringsFromArray(line
                            .split(Symbols.SPACE));
                    // adds the rest of the disk statistics
                    data.addAll(Arrays.asList(tempData));
                    data.removeAll(Collections.singleton(Symbols.EMPTY));
                }

        return data;
    }

    /**
     * Fetch the entire contents of a text file, and return it in a String. This
     * style of implementation does not throw Exceptions to the caller.
     *
     * @param path is a file which already exists and can be read.
     * @throws IOException
     */
    static private synchronized String getContents(String path) {
        // ...checks on aFile are elided
        final StringBuilder contents = new StringBuilder();

        try {
            // use buffering, reading one line at a time
            // FileReader always assumes default encoding is OK!
            final BufferedReader input = new BufferedReader(new FileReader(
                    new File(path)));
            try {
                String line; // not declared within while loop
                /*
                     * readLine is a bit quirky : it returns the content of a line
                     * MINUS the newline. it returns null only for the END of the
                     * stream. it returns an empty String if two newlines appear in
                     * a row.
                     */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty(Symbols.LINE_SEPARATOR));
                }
            } finally {
                input.close();

            }
        } catch (@NotNull final IOException ex) {
            Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE,
                    null, ex);
        }

        return contents.toString();
    }

    /**
     * Opens a stream from a existing file and return it. This style of
     * implementation does not throw Exceptions to the caller.
     *
     * @param path is a file which already exists and can be read.
     * @throws IOException
     */
    @Nullable
    private synchronized BufferedReader getStream(String _path) {
        BufferedReader br = null;
        final File file = new File(_path);
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
            br = new BufferedReader(fileReader);

        } catch (@NotNull final FileNotFoundException ex) {
            Logger.getLogger(ProcParser.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return br;
    }
}// end ProcInfoParser

