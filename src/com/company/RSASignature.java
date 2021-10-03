package com.company;

import java.util.Random;

public class RSASignature {

    private final int n;
    private final int fi;
    private final Random random;

    public RSASignature(int p, int q){
        n = p*q;
        fi = (p-1)*(q-1);
        random = new Random();
    }

    private int generateRandomNumber(int min, int max){
        return random.nextInt(max - min + 1) + min;
    }

    private EuclidRow generalizedEuclidAlgorithm(int a, int b){
        if (b>a){
            a = a + b;
            b = a - b;
            a = a - b;
        }
        EuclidRow u = new EuclidRow(a, 1, 0);
        EuclidRow v = new EuclidRow(b, 0, 1);
        EuclidRow t = new EuclidRow(0,0,0);

        while (v.gcd!=0){
            int q = u.gcd / v.gcd;
            t.gcd = u.gcd % v.gcd;
            t.a = u.a - q * v.a;
            t.b = u.b - q * v.b;
            u.set(v);
            v.set(t);
        }
        return u;
    }

    private int getD(){
        int d = generateRandomNumber(2, fi-1);
        while (generalizedEuclidAlgorithm(fi, d).gcd!=1){
            d = generateRandomNumber(2, fi-1);
        }
        return d;
    }

    private int getC(int d){
        int c = generalizedEuclidAlgorithm(fi, d).b;
        if (c<0)
            return c + fi;
        return c;
    }

    private int calculatePowerByMod(int base, int power) {
        int result = 1;
        while (power > 0) {
            if ((power & 1) == 1)
                result = (result * base) % n;
            base = (base * base) % n;
            power = power >> 1;
        }
        return result;
    }

    public void sign(){
        int d = getD();
        System.out.println("Alice picked d = "+d+" which is coprime with fi number");
        int c = getC(d);
        System.out.println("Alice picked c = "+c+" such that c*d mod fi = "+c*d % fi);
        System.out.println("Alice published N = "+n+" and d = "+d+". So anyone can access those numbers to check authenticity of her signature.");
        String message = "Hi, Bob!";
        System.out.println("Alice wants to send Bob message: "+message);
        int y = message.hashCode() % n;
        System.out.println("Alice hashed her message: y = hash(message) = "+y);
        int s = calculatePowerByMod(y, c);
        System.out.println("Alice signs her message with s = "+s+", and sends Bob pair (message, s) = "+"("+message+", "+s+")");
        System.out.println("Bob knows open parameters of Alice N = "+n+" d = "+d+". He can check authenticity of Alice's signature.");
        int w = calculatePowerByMod(s, d);
        System.out.println("Bob calculates w = s^d mod N = "+s+"^"+d+" mod "+n+" = "+w);
        System.out.println("If w = hash(message) then the signature is authentic. w = "+w+". hash(message) = "+message.hashCode()%n+".");
        if (w==message.hashCode() % n){
            System.out.println("The signature is authentic");
        } else
            System.out.println("The signature is not authentic");

    }
}
