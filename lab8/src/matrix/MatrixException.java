package lab8.src.matrix;

public class MatrixException extends RuntimeException {
    private String message = "";

    MatrixException(String message) { this.message = message; }

    public String getMessage() {   return message; }

}
