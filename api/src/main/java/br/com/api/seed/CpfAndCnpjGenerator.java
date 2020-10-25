package br.com.api.seed;

public class CpfAndCnpjGenerator {

    private static int RANDOM_MULTIPLIER = 9;

    public static String cpf() {
        int n1 = randomInRange();
        int n2 = randomInRange();
        int n3 = randomInRange();
        int n4 = randomInRange();
        int n5 = randomInRange();
        int n6 = randomInRange();
        int n7 = randomInRange();
        int n8 = randomInRange();
        int n9 = randomInRange();
        int d1 = n9 * 2 + n8 * 3 + n7 * 4 + n6 * 5 + n5 * 6 + n4 * 7 + n3 * 8 + n2 * 9 + n1 * 10;

        d1 = 11 - (mod(d1, 11));

        if (d1 >= 10)
            d1 = 0;

        int d2 = d1 * 2 + n9 * 3 + n8 * 4 + n7 * 5 + n6 * 6 + n5 * 7 + n4 * 8 + n3 * 9 + n2 * 10 + n1 * 11;

        d2 = 11 - (mod(d2, 11));

        if (d2 >= 10)
            d2 = 0;

        return "" + n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + d1 + d2;

    }

    public static String cnpj() {
        int n1 = randomInRange();
        int n2 = randomInRange();
        int n3 = randomInRange();
        int n4 = randomInRange();
        int n5 = randomInRange();
        int n6 = randomInRange();
        int n7 = randomInRange();
        int n8 = randomInRange();
        int n9 = 0;
        int n10 = 0;
        int n11 = 0;
        int n12 = 1;
        int d1 = n12 * 2 + n11 * 3 + n10 * 4 + n9 * 5 + n8 * 6 + n7 * 7 + n6 * 8 + n5 * 9 + n4 * 2 + n3 * 3 + n2 * 4 + n1 * 5;

        d1 = 11 - (mod(d1, 11));

        if (d1 >= 10)
            d1 = 0;

        int d2 = d1 * 2 + n12 * 3 + n11 * 4 + n10 * 5 + n9 * 6 + n8 * 7 + n7 * 8 + n6 * 9 + n5 * 2 + n4 * 3 + n3 * 4 + n2 * 5 + n1 * 6;

        d2 = 11 - (mod(d2, 11));

        if (d2 >= 10)
            d2 = 0;

        return "" + n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + d1 + d2;
    }

    private static int randomInRange() {
        return (int) (Math.random() * RANDOM_MULTIPLIER);
    }

    private static int mod(int dividendo, int divisor) {
        return (int) Math.round(dividendo - (Math.floor(dividendo / divisor) * divisor));
    }

}
