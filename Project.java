/** Project 2 : TRNG vs PRNG vs CSPRNG
 * Project : Uses 3 diff rng methods and checks randomness based on Cesaro's theorem
 * 
 * @author Hudhaifah Rehman and Nicolas Swank
 * @version 10/21/2021
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Project 
{
	public static void main(String[] args)
	{
		//tests
		System.out.println(euclideanAlg(34, 17));
		System.out.println(euclideanAlg(193, 12));
		System.out.println(estimatePi(56));
		System.out.println(estimatePi(61));
	}
	
	public static int euclideanAlg(int x, int y)
	{
		if (x < y)
		{
			int temp = x;
			x = y;
			y = temp;
		}
		
		int remainder = x % y;
		
		while (remainder != 0)
		{
			x = y;
			y = remainder;
			remainder = x % y;
		}
		
		return y;
	}
	
	public static double estimatePi(double primeGCDCount)
	{
		return Math.sqrt(6 / (primeGCDCount / 100));
	}
}
