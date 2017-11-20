package matrix;

import java.util.Random;

public class UsualMatrix implements IMatrix {
    int[][] matrix;
    int rowSize = 0;
    int columnSize = 0;


    public UsualMatrix() {
    }

    public UsualMatrix(int rowSize, int columnSize) {
        this.rowSize = rowSize;
        this.columnSize = columnSize;

        this.matrix = new int[rowSize][columnSize];

        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                this.matrix[i][j] = 0;
            }
        }
    }

    @Override
    public int rowNum() {
        return rowSize;
    }

    @Override
    public int columnNum() {
        return columnSize;
    }

    @Override
    public int getElement(int row, int column) {
        return matrix[row][column];
    }

    @Override
    public void setElement(int row, int column, int value) {
        matrix[row][column] = value;
    }

    @Override
    public IMatrix sum(IMatrix matrix) throws MatrixException {
        if (this.rowNum() != matrix.rowNum() || this.columnSize != matrix.columnNum()) {
            throw new MatrixException("You can't sum not equal matrices");
        }

        for (int i = 0; i < this.rowSize; i++) {
            for (int j = 0; j < this.columnSize; j++) {
                this.matrix[i][j] += matrix.getElement(i, j);
            }
        }
        return this;
    }

    @Override
    public IMatrix product(IMatrix matrix) throws MatrixException {
        int firstRows = this.rowSize;
        int firstColumns = this.columnSize;
        int secondRows = matrix.rowNum();
        int secondColumns = matrix.columnNum();

        if (firstColumns != secondRows) {
            throw new MatrixException("Columns in first matrix and Rows in second are not equal");
        }

        IMatrix resultMatrix = new UsualMatrix(firstRows, secondColumns);

        for (int i = 0; i < firstRows; i++) {
            for (int j = 0; j < secondColumns; j++) {
                resultMatrix.setElement(0, 0, 0);
            }
        }

        for (int i = 0; i < firstRows; i++) {
            for (int j = 0; j < secondColumns; j++) {
                for (int k = 0; k < firstColumns; k++) {
                    resultMatrix.setElement(i, j, resultMatrix.getElement(i, j)
                            + this.getElement(i, k) * matrix.getElement(k, j));
                }
            }
        }
        return resultMatrix;
    }

    @Override
    public boolean equals(IMatrix matrix) {
        for (int i = 0; i < this.rowSize; i++) {
            for (int j = 0; j < this.columnSize; j++) {
                if (this.matrix[i][j] != matrix.getElement(i,j))
                    return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("");

        for (int[] aM_matrix : this.matrix) {
            for (int j = 0; j < this.matrix.length; j++) {
                stringBuilder.append(aM_matrix[j]).append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void randomize(Random random) {

        for (int i = 0; i < rowNum(); i++) {
            for (int j = 0; j < columnNum(); j++) {
                setElement(i, j, random.nextInt(10));
            }
        }

    }

}
