package util;

import algorithm.DecompositionDegree;
import algorithm.DecompositionNeighbor;
import model.Hypergraph;
import model.Result;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PrintOutput {
    private static final Logger LOGGER = Logger.getLogger(PrintOutput.class);

    public static void printHypergraphStatistic(Hypergraph hypergraph) {
        long startTime = System.nanoTime();

        ArrayList<Integer> nodeList = hypergraph.getNodeList();
        HashMap<Integer, ArrayList<Integer>> edgeMap = hypergraph.getEdgeMap();

        int m = edgeMap.size();
        System.out.println("node size:" + nodeList.size());
        System.out.println("edge size:" + m);

        int maxCardinality = Integer.MIN_VALUE;
        int minCardinality = Integer.MAX_VALUE;
        int sumCardinality = 0;
        for (ArrayList<Integer> e : edgeMap.values()) {
            int cardi = e.size();

            maxCardinality = Math.max(maxCardinality, cardi);
            minCardinality = Math.min(minCardinality, cardi);
            sumCardinality += cardi;
        }

        System.out.println("minCardinality:" + minCardinality);
        System.out.println("maxCardinality:" + maxCardinality);
        System.out.println("avgCardinality:" + sumCardinality * 1.0 / m);


        long endTime = System.nanoTime();
        LOGGER.info("DONE: " + (double) (endTime - startTime) / 1.0E9D);
    }

    public static void printCumulativeCardinalityDistribution(Hypergraph hypergraph) {
        HashMap<Integer, ArrayList<Integer>> edgeMap = hypergraph.getEdgeMap();
        HashMap<Integer, Integer> cardiNumMap = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<Integer>> entry : edgeMap.entrySet()) {
            int cardi =entry.getValue().size();

            int num = cardiNumMap.get(cardi) == null ? 1 : cardiNumMap.get(cardi) + 1;
            cardiNumMap.put(cardi, num);
        }
        cardiNumMap = (HashMap<Integer, Integer>) GetUtils.getSortMapByKey(cardiNumMap, 1);

        HashMap<Integer, Double> cardiCumulativeProportion = new HashMap<>();
        int allSum = 0;
        for (Integer key : cardiNumMap.keySet()) {
            allSum += cardiNumMap.get(key);
        }
        int curSum = 0;
        for (Map.Entry<Integer, Integer> entry : cardiNumMap.entrySet()) {
            curSum += entry.getValue();
            double precent = 1.0 * curSum / allSum;
            cardiCumulativeProportion.put(entry.getKey(), (new BigDecimal(precent * 100).setScale(3, RoundingMode.FLOOR)).doubleValue());
        }

        System.out.println("x=np.array(" + cardiCumulativeProportion.keySet().toString() + ")");
        System.out.println("y=np.array(" + cardiCumulativeProportion.values().toString() + ")");
    }

    public static void printDegreeDistribution(Hypergraph hypergraph) {
        long startTime = System.nanoTime();

        HashMap<Integer, Integer> degreeMap = hypergraph.getDegreeMap();
        HashMap<Integer, Integer> degreeDistribution = ComputeUtils.computeDistribution(degreeMap);
        System.out.println("x=np.array(" + degreeDistribution.keySet().toString() + ")");
        System.out.println("y=np.array(" + degreeDistribution.values().toString() + ")");

        long endTime = System.nanoTime();
        LOGGER.info("DONE!: " + (double) (endTime - startTime) / 1.0E9D);
    }

    public static void printNeighborNumsDistribution(Hypergraph hypergraph) {
        long startTime = System.nanoTime();

        HashMap<Integer, Integer> neighborNumMap = hypergraph.getNeighborNumMap();
        HashMap<Integer, Integer> neighborNumDistribution = ComputeUtils.computeDistribution(neighborNumMap);
        System.out.println("x=np.array(" + neighborNumDistribution.keySet().toString() + ")");
        System.out.println("y=np.array(" + neighborNumDistribution.values().toString() + ")");

        long endTime = System.nanoTime();
        LOGGER.info("DONE!: " + (double) (endTime - startTime) / 1.0E9D);
    }

    public static void printDegreeCoreDistribution(Hypergraph hypergraph) {
        long startTime = System.nanoTime();

        DecompositionDegree decompositionDegree = new DecompositionDegree(hypergraph);
        Result result_decompositionDegree = decompositionDegree.run();
        HashMap<Integer, Integer> CoreDegreeDistribution = ComputeUtils.computeDistribution(result_decompositionDegree.getCoreVMap());
        System.out.println("x=np.array(" + CoreDegreeDistribution.keySet().toString() + ")");
        System.out.println("y=np.array(" + CoreDegreeDistribution.values().toString() + ")");

        long endTime = System.nanoTime();
        LOGGER.info("DONE!: " + (double) (endTime - startTime) / 1.0E9D);
    }


    public static void printNeighborCoreDistribution(Hypergraph hypergraph) {
        long startTime = System.nanoTime();

        DecompositionNeighbor decompositionNeighbor = new DecompositionNeighbor(hypergraph);
        Result result_decompositionNeighbor = decompositionNeighbor.run();
        HashMap<Integer, Integer> CoreNeighborDistribution = ComputeUtils.computeDistribution(result_decompositionNeighbor.getCoreVMap());
        System.out.println("x=np.array(" + CoreNeighborDistribution.keySet().toString() + ")");
        System.out.println("y=np.array(" + CoreNeighborDistribution.values().toString() + ")");

        long endTime = System.nanoTime();
        LOGGER.info("DONE!: " + (double) (endTime - startTime) / 1.0E9D);
    }
}
