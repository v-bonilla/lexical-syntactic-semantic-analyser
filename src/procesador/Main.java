package procesador;

public class Main {

    // Execute with the name of file to analyze as arg
    public static void main(String[] args) {
        Scanner scanner = new Scanner(args[0]);
        Parser parser = new Parser(scanner);
        parser.Parse();
        System.out.println(parser.errors.count + " errors detected");
    }
}
