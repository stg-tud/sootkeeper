package test;

/**
 * Created by floriankuebler on 09/11/16.
 */
public class Test {
    public static void main(String[] args) {
        int i = 3;
        int b = 2;
        i = 5;
        if (args.length == 0) {
            i = 5;
        } else {
            i = 6;
        }
        System.out.println(i);
    }
}
