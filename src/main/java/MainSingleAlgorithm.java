import algorithm.DecompositionConstraintCore;
import model.Hypergraph;
import org.apache.log4j.Logger;
import util.FileIOUtils;
import util.GetUtils;
import util.PrintOutput;
import util.SetOpt;
import util.SetOpt.Option;

import java.io.IOException;

public class MainSingleAlgorithm {
    private static final Logger LOGGER = Logger.getLogger(MainSingleAlgorithm.class);

    @Option(abbr = 'd', usage = "Separate delimiter,0:tab,1:space,2:comma")
    public static int delimType = 1;

    @Option(abbr = 'p', usage = "whether print the core number in result, 0:no,1:yes")
    public static int printResult = 1;

    @Option(abbr = 'c', usage = "whether to constructe nodeToEdgesMap, false:no, true:yes")
    public static boolean constructStructure = true;


    public static void main(String[] args) throws IOException {
        //read parameters
        MainSingleAlgorithm main = new MainSingleAlgorithm();
        args = SetOpt.setOpt(main, args);
        String datasetName = args[0];

        //graph information
        Hypergraph hypergraph = FileIOUtils.loadGraph(datasetName, GetUtils.getDelim(delimType), constructStructure);

//        PrintOutput.printHypergraphStatistic(hypergraph); //basic statistic
//        PrintOutput.printCumulativeCardinalityDistribution(hypergraph); //cardinality cumulative distribution
//        PrintOutput.printDegreeDistribution(hypergraph);//degree distribution
//        PrintOutput.printNeighborNumsDistribution(hypergraph);//neighbor nums distribution
//        PrintOutput.printDegreeCoreDistribution(hypergraph);//degree core distribution
//        PrintOutput.printNeighborCoreDistribution(hypergraph);//neighbor core distribution

        //naive (k,c)-core computation
        DecompositionConstraintCore decompositionConstraintCore = new DecompositionConstraintCore(hypergraph);
//        Hypergraph kcCore = decompositionConstraintCore.run(3, 3);
//        System.out.println(kcCore.toString());

        System.out.println(decompositionConstraintCore.run().getCoreVMap());

    }
}
