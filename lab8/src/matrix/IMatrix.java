package lab8.src.matrix;

public interface IMatrix {

    int rowNum();

    int columnNum();

    IMatrix sum(IMatrix matrix);

    IMatrix product(IMatrix matrix);

    String toString();

    boolean equals(IMatrix matrix);

    void setElement(int row, int column, int value);

    int getElement(int row, int column);

}
