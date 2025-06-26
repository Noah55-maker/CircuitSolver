package algebra;

import java.util.Arrays;
import java.util.Stack;

public class ComplexSystemOfEquationSolver {
    private static final double EPSILON = 1e-12;

    private final ComplexNumber[][] lhs;
    private final ComplexNumber[] rhs;
    private final Stack<int[]> stack;

    private ComplexSystemOfEquationSolver(ComplexNumber[][] lhs, ComplexNumber[] rhs) {
        int n = lhs.length;

        this.lhs = new ComplexNumber[n][n];
        for (int i = 0; i < n; i++) {
            this.lhs[i] = Arrays.copyOf(lhs[i], n);
        }

        this.rhs = Arrays.copyOf(rhs, n);
        stack = new Stack<>();
    }

    public static ComplexNumber[] solve(ComplexNumber[][] lhs, ComplexNumber[] rhs) {
        int n = lhs.length;
        if (n != rhs.length) throw new IllegalArgumentException("LHS length must be the same as RHS length");
        for (ComplexNumber[] a : lhs) {
            if (a.length != n) throw new IllegalArgumentException("LHS sub-array length must be the same as LHS length");
        }

        ComplexSystemOfEquationSolver soe = new ComplexSystemOfEquationSolver(lhs, rhs);
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
            if (lhs[i][subIndex].magnitude() > EPSILON) {
                if (swapRows(i, subIndex))
                    stack.push(new int[]{i, subIndex});
                break;
            }
        }

        divRow(subIndex, new ComplexNumber(lhs[subIndex][subIndex]));

        for (int i = subIndex+1; i < n; i++) {
            subRow(i, subIndex, new ComplexNumber(lhs[i][subIndex]));
        }

        ref(subIndex+1);
    }

    private void rref(int subIndex) {
        if (subIndex == 0) return;

        for (int i = 0; i < subIndex; i++)
            subRow(i, subIndex, new ComplexNumber(lhs[i][subIndex]));

        rref(subIndex-1);
    }

    private boolean swapRows(int a, int b) {
        if (a == b) return false;

        ComplexNumber[] tmp = lhs[a];
        lhs[a] = lhs[b];
        lhs[b] = tmp;

        ComplexNumber t = rhs[a];
        rhs[a] = rhs[b];
        rhs[b] = t;

        return true;
    }

    // equation[a] -= equation[b] * num
    private void subRow(int a, int b, ComplexNumber num) {
        for (int i = 0; i < lhs.length; i++)
            lhs[a][i].subtract(ComplexNumber.multiply(lhs[b][i], num));

        rhs[a].subtract(ComplexNumber.multiply(rhs[b], num));
    }

    // equation[r] /= num
    private void divRow(int r, ComplexNumber num) {
        for (int i = 0; i < lhs.length; i++)
            lhs[r][i].divide(num);

        rhs[r].divide(num);
    }

    private void print() {
        int n = lhs.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n-1; j++) {
                System.out.printf("%s\t+ ", lhs[i][j]);
            }
            System.out.printf("%s\t= %s\n", lhs[i][n-1], rhs[i]);
        }
        System.out.println();
    }
}
