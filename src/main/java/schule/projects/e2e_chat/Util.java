package schule.projects.e2e_chat;

import java.util.Random;

public class Util {

    public static final Random random = new Random();

    public static class ByteTags {

        public static final byte TAG_GET_P = 0x00;
        public static final byte TAG_GET_Q = 0x01;
        public static final byte TAG_GET_PQ = 0x02;

    }

    public static long getRandomPrime() {
        long base = Math.abs(random.nextInt());
        while (!isPrime(base)) {
            base = Math.abs(random.nextInt());
        }
        return base;
    }

    public static boolean isPrime(long n) {
        if (n <= 1) {
            return false;
        }
        if (n <= 3) {
            return true;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        for (long i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    public static long getRandomBelow(long n) {
        return random.nextLong() % n;
    }

    public static long customPow(long base, long exp, long mod) {
        long result = 1;
        for(long i = 0; i < exp; i++) {
            result = (result * base) % mod;
        }
        return result;
    }

}
