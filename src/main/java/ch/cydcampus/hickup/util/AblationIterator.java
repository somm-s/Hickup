package ch.cydcampus.hickup.util;

import java.util.ArrayList;
import java.util.List;

public class AblationIterator {

    public static List<List<Integer>> ablationIndices(int[]... arrays) {
        List<List<Integer>> indices = new ArrayList<>();

        // Add base experiment
        List<Integer> base = new ArrayList<>();
        for (int i = 0; i < arrays.length; i++) {
            base.add(0); // Add the index 0 for each array
        }
        indices.add(base);

        // Add experiments with one index adapted per experiment
        for (int i = 0; i < arrays.length; i++) {
            for (int j = 1; j < arrays[i].length; j++) {
                List<Integer> experiment = new ArrayList<>(base);
                experiment.set(i, j); // Set the index instead of the element
                indices.add(experiment);
            }
        }

        return indices;
    }


    public static void main(String[] args) {
        int[] arr1 = {0, 1};
        int[] arr2 = {0, 1};
        int[] arr3 = {0, 3};
        int[] arr4 = {0, 2};

        List<List<Integer>> indices = ablationIndices(arr1, arr2, arr3, arr4);

        for (List<Integer> tuple : indices) {
            System.out.println(tuple);
        }
    }
}
