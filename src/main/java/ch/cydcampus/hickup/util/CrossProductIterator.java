package ch.cydcampus.hickup.util;

import java.util.ArrayList;
import java.util.List;

public class CrossProductIterator {

    public static List<List<Integer>> crossProductIndices(int[]... arrays) {

        int[] lengths = new int[arrays.length];
        for (int i = 0; i < arrays.length; i++) {
            lengths[i] = arrays[i].length;
        }

        int[][] indexArrays = new int[arrays.length][];
        for (int i = 0; i < arrays.length; i++) {
            indexArrays[i] = new int[arrays[i].length];
            for (int j = 0; j < arrays[i].length; j++) {
                indexArrays[i][j] = j;
            }
        }

        List<List<Integer>> result = new ArrayList<>();
        crossProductHelper(result, new ArrayList<>(), indexArrays, 0);
        return result;
    }


    private static void crossProductHelper(List<List<Integer>> result, List<Integer> current, int[][] arrays, int depth) {
        if (depth == arrays.length) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < arrays[depth].length; i++) {
            current.add(arrays[depth][i]);
            crossProductHelper(result, current, arrays, depth + 1);
            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {
        int[] arr1 = {2, 1, 2, 3, 4};
        int[] arr2 = {0, 1, 2};

        List<List<Integer>> indices = crossProductIndices(arr1, arr2);

        for (List<Integer> tuple : indices) {
            System.out.println(tuple);
        }
    }
}
