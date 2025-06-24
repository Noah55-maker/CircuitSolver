package algebra;

import java.util.Arrays;
import java.util.Stack;

public class SystemOfEquationSolver {
    private static final double EPSILON = 1e-12;

    private final double[][] lhs;
    private final double[] rhs;
    private final Stack<int[]> stack;

    private SystemOfEquationSolver(double[][] lhs, double[] rhs) {
        int n = lhs.length;

        this.lhs = new double[n][n];
        for (int i = 0; i < n; i++) {
            this.lhs[i] = Arrays.copyOf(lhs[i], n);
        }

        this.rhs = Arrays.copyOf(rhs, n);
        stack = new Stack<>();
    }

    public static double[] solve(double[][] lhs, double[] rhs) {
        int n = lhs.length;
        if (n != rhs.length) throw new IllegalArgumentException("LHS length must be the same as RHS length");
        for (double[] a : lhs) {
            if (a.length != n) throw new IllegalArgumentException("LHS sub-array length must be the same as LHS length");
        }

        SystemOfEquationSolver soe = new SystemOfEquationSolver(lhs, rhs);
        soe.evaluate();

        return soe.rhs;
    }

    private void evaluate() {
        int n = lhs.length;
        System.out.println("Given system of equations:");
        print();

        ref(0);
        rref(n-1);

        while (!stack.isEmpty()) {
            int[] swaps = stack.pop();
            swapRows(swaps[0], swaps[1]);
        }

        System.out.println("Solution:");
        print();
    }

    private void ref(int subIndex) {
        int n = lhs.length;
        if (subIndex == n) return;

        for (int i = subIndex; i < n; i++) {
            if (Math.abs(lhs[i][subIndex]) > EPSILON) {
                if (swapRows(i, subIndex))
                    stack.push(new int[]{i, subIndex});
                break;
            }
        }

        divRow(subIndex, lhs[subIndex][subIndex]);

        for (int i = subIndex+1; i < n; i++) {
            subRow(i, subIndex, lhs[i][subIndex]);
        }

        ref(subIndex+1);
    }

    private void rref(int subIndex) {
        if (subIndex == 0) return;

        for (int i = 0; i < subIndex; i++)
            subRow(i, subIndex, lhs[i][subIndex]);

        rref(subIndex-1);
    }

    private boolean swapRows(int a, int b) {
        if (a == b) return false;

        double[] tmp = lhs[a];
        lhs[a] = lhs[b];
        lhs[b] = tmp;

        double t = rhs[a];
        rhs[a] = rhs[b];
        rhs[b] = t;

        return true;
    }

    // equation[a] -= equation[b] * num
    private void subRow(int a, int b, double num) {
        for (int i = 0; i < lhs.length; i++)
            lhs[a][i] -= lhs[b][i] * num;

        rhs[a] -= rhs[b] * num;
    }

    // equation[r] /= num
    private void divRow(int r, double num) {
        for (int i = 0; i < lhs.length; i++)
            lhs[r][i] /= num;

        rhs[r] /= num;
    }

    private void print() {
        int n = lhs.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n-1; j++) {
                System.out.printf("%+.5f\t+ ", lhs[i][j]);
            }
            System.out.printf("%+.5f\t= %+.5f\n", lhs[i][n-1], rhs[i]);
        }
        System.out.println();
    }
}
