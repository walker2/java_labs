package lab8.src.product;

import lab8.src.matrix.*;
import lab8.src.utils.*;

import java.util.Random;

public class ThreadedMatrixProduct {

    class MultiplicationThread extends Thread {
        private final IMatrix first, second, result;
        private final int firstIndex, lastIndex;

        public MultiplicationThread(final IMatrix first, final IMatrix second,
                                    final IMatrix result, final int firstIndex,
                                    final int lastIndex) {
            this.first = first;
            this.second = second;
            this.result = result;
            this.firstIndex = firstIndex;
            this.lastIndex = lastIndex;
        }

        private void calcValue(final int row, final int col) {
            // Calculate a value in one cell
            int sum = 0;
            for (int i = 0; i < second.rowNum(); ++i) {
                sum += first.getElement(row, i) * second.getElement(i, col);
            }
            result.setElement(row, col, sum);
        }

        @Override
        public void run() {
            int colCount = second.columnNum();
            for (int index = firstIndex; index < lastIndex; ++index){
                calcValue(index / colCount, index % colCount);
            }
        }
    }

    private IMatrix threadedProduct(final IMatrix first,
                                    final IMatrix second,
                                    int threadCount) {

        int rows = first.rowNum();
        int cols = second.columnNum();
        IMatrix result = new UsualMatrix(first.rowNum(), second.columnNum());

        int cellsForThread = (rows * cols) / threadCount; // Number of cells the thread should calculate
        int firstIndex = 0;
        MultiplicationThread[] mulThreads = new MultiplicationThread[threadCount];

        // Create threads and run them
        for (int threadIndex = threadCount - 1; threadIndex >= 0; --threadIndex) {
            int lastIndex = firstIndex + cellsForThread;

            if (threadIndex == 0) {
                lastIndex = rows * cols; // Calculate what is left, if any
            }

            mulThreads[threadIndex] = new MultiplicationThread(first, second, result, firstIndex, lastIndex);
            mulThreads[threadIndex].start();
            firstIndex = lastIndex;
        }

        // Join all the threads
        try {
            for (MultiplicationThread multiplierThread : mulThreads)
                multiplierThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean areMatricesEqual(IMatrix matrix1, IMatrix matrix2)
    {
        // Check if they are the same
        for (int row = 0; row < matrix1.rowNum(); ++row) {
            for (int col = 0; col < matrix2.columnNum(); ++col) {
                if (matrix1.getElement(row, col) != matrix2.getElement(row, col)) {
                    System.out.println("Error in multithreaded calculation!");
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        UsualMatrix matrix1 = new UsualMatrix(10, 10);
        UsualMatrix matrix2 = new UsualMatrix(10, 10);

        Random random = new Random(System.currentTimeMillis());
        matrix1.randomize(random);
        matrix2.randomize(random);

        System.out.println("Matrix A: \n" + matrix1);
        System.out.println("Matrix B: \n" + matrix2);

        ThreadedMatrixProduct tmp = new ThreadedMatrixProduct();

        TimeWatch timeWatch;
        long passedTime;

        timeWatch = TimeWatch.start();
        // Calculate multithreaded matrix product
        UsualMatrix resultMult1 = (UsualMatrix) tmp.threadedProduct(matrix1, matrix2, 42);
        passedTime = timeWatch.time();

        System.out.println("Multithreaded A x B \n" + resultMult1);
        System.out.println("Multithreading product time on 42 threads: " + passedTime + " ms");

        UsualMatrix matrix3 = new UsualMatrix(10, 10);
        UsualMatrix matrix4 = new UsualMatrix(10, 10);

        for (int i = 0; i < matrix3.rowNum(); i++) {
            for (int j = 0; j < matrix4.columnNum(); j++) {
                matrix3.setElement(i, j, 14);
                matrix4.setElement(i, j, 17);
            }
        }
        timeWatch = TimeWatch.start();
        // Calculate multithreaded matrix product
        UsualMatrix resultMult2 = (UsualMatrix) tmp.threadedProduct(matrix3, matrix4, 4);
        passedTime = timeWatch.time();

        System.out.println("Multithreading product time on 4 threads: " + passedTime + " ms");

        timeWatch = TimeWatch.start();
        // Calculate regular product
        IMatrix result = matrix1.product(matrix2);
        passedTime = timeWatch.time();



        if (!tmp.areMatricesEqual(resultMult1, result))
            return;

        System.out.println("DONE");
    }
}
