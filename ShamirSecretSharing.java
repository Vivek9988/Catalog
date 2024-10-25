import org.json.JSONObject;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ShamirSecretSharing {

    // Function to decode a value from a specific base to decimal (base 10)
    public static BigInteger decodeBase(String base, String value) {
        int baseInt = Integer.parseInt(base);
        return new BigInteger(value, baseInt);
    }

    // Function to apply Lagrange Interpolation and find the constant term (secret
    // c)
    public static BigInteger lagrangeInterpolation(Map<Integer, BigInteger> points, int k) {
        BigInteger result = BigInteger.ZERO;

        for (int i : points.keySet()) {
            BigInteger xi = BigInteger.valueOf(i);
            BigInteger yi = points.get(i);
            BigInteger term = yi;

            for (int j : points.keySet()) {
                if (i != j) {
                    BigInteger xj = BigInteger.valueOf(j);
                    BigInteger denominator = xi.subtract(xj);
                    if (!denominator.equals(BigInteger.ZERO)) {
                        term = term.multiply(xj.negate()).divide(denominator);
                    } else {
                        throw new ArithmeticException("Division by zero in Lagrange interpolation.");
                    }
                }
            }

            result = result.add(term);
        }

        return result;
    }

    // Main function
    public static void main(String[] args) {
        try {
            // Read JSON from file
            Scanner scanner = new Scanner(new FileReader("input.json"));
            StringBuilder jsonData = new StringBuilder();
            while (scanner.hasNext()) {
                jsonData.append(scanner.nextLine());
            }
            scanner.close();

            // Parse JSON
            JSONObject jsonObject = new JSONObject(jsonData.toString());
            JSONObject keys = jsonObject.getJSONObject("keys");
            int n = keys.getInt("n"); // Total number of points
            int k = keys.getInt("k"); // Minimum points required to determine polynomial

            // Map to store decoded points (x, y)
            Map<Integer, BigInteger> points = new HashMap<>();

            // Loop through the JSON and extract base and value, then decode y
            for (String key : jsonObject.keySet()) {
                if (!key.equals("keys")) {
                    JSONObject point = jsonObject.getJSONObject(key);
                    String base = point.getString("base");
                    String value = point.getString("value");

                    // Decode y based on the base
                    BigInteger y = decodeBase(base, value);
                    int x = Integer.parseInt(key); // x is the key in the JSON

                    // Store the (x, y) point
                    points.put(x, y);
                }
            }

            // Ensure we have at least k points for solving the polynomial
            if (points.size() < k) {
                System.out.println("Not enough points to solve the polynomial");
                return;
            }

            // Find the constant term using Lagrange Interpolation
            BigInteger secret = lagrangeInterpolation(points, k);

            // Output the result (secret constant term)
            System.out.println("The secret constant term (c) is: " + secret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
