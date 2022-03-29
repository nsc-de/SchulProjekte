package schule.projects.e2e_chat;

import java.util.Random;

public class Util {

    public static final Random random = new Random();

    public static class ByteTags {

        public static final byte TAG_ERROR = 0x00;
        public static final byte TAG_GET_P = 0x01;
        public static final byte TAG_GET_S = 0x02;
        public static final byte TAG_GET_PS = 0x03;

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
        return Math.abs(random.nextLong()) % n;
    }

    public static long customPow(long base, long exp, long mod) {
        long result = 1;
        for(long i = 0; i < exp; i++) {
            result = (result * base) % mod;
        }
        return result;
    }

    public static long powerMod(long base, long exp, long mod) {
        if(exp == 0) return 1;
        if(exp == 1) return base % mod;
        long result = 1;
        if((exp % 2) == 1) {
            result *= base;
            result %= mod;
            exp -= 1;
        }
        long tmp = powerMod(base, exp >> 1, mod);
        result *= (tmp * tmp) % mod;
        return result % mod;
    }
}
