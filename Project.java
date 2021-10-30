/** Project 2 : TRNG vs PRNG vs CSPRNG
 * Main : Uses 3 diff rng methods and checks randomness based on Cesaro's theorem
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
		//Initialize random number generators
		Random prng = new Random();
		SecureRandom csprng = new SecureRandom();
		Scanner trng = null; //Declare trng scanner outside of try block for accessibility
	
		//declare rng arrays
		int[] prngOne;
		int[] prngTwo;
		double[] prngFrequencyGraph = new double[30];
		int[] csprngOne;
		int[] csprngTwo;
		double[] csprngFrequencyGraph = new double[30];
		int[] trngOne = new int[100];
		int[] trngTwo = new int[100];
		double[] trngFrequencyGraph = new double[30];
		
		//initialize primepaircounters
		int prngPrimePairs = 0;
		int csprngPrimePairs = 0;
		int trngPrimePairs = 0;
		
		//Generate data 30 times
		for (int iterator = 0; iterator < 30; iterator++)
		{
			//initialize prng arrays
			IntStream prngStream = prng.ints(100, 1, 101);
			prngOne = prngStream.toArray();
			prngStream = prng.ints(100, 1, 101);
			prngTwo = prngStream.toArray();
			
			//initialize csprng arrays
			IntStream csprngStream = csprng.ints(100, 1, 101);
			csprngOne = csprngStream.toArray();
			csprngStream = csprng.ints(100, 1, 101);
			csprngTwo = csprngStream.toArray();
			
			//Initialize connection to random.org (inside loop to make sure new link is used every iteration)
			try
			{
				String address = "https://www.random.org/integers/?num=200&min=1&max=100&col=2&base=10&format=plain&rnd=new";
				URL url = new URL(address);	
				HttpURLConnection connection = (HttpURLConnection)url.openConnection(); 
				InputStream stream = connection.getInputStream();
				trng = new Scanner(stream);
			}
			catch (MalformedURLException e)
			{
				System.out.println(e.getMessage());
			}
			catch (IOException e)
			{
				e.getMessage();
			}
			
			//initialize trng arrays
			for (int iterator1 = 0; iterator1 < 100; iterator1++)
			{
				trngOne[iterator1] = trng.nextInt();
				trngTwo[iterator1] = trng.nextInt();
			}
			
			//get primepairs
			for (int iterator1 = 0; iterator1 < 100; iterator1++)
			{
				int prngGCD = euclideanAlg(prngOne[iterator1], prngTwo[iterator1]);
				int csprngGCD = euclideanAlg(csprngOne[iterator1], csprngTwo[iterator1]);
				int trngGCD = euclideanAlg(trngOne[iterator1], trngTwo[iterator1]);
				
				if (prngGCD == 1) prngPrimePairs++;
				if (csprngGCD == 1) csprngPrimePairs++;
				if (trngGCD == 1) trngPrimePairs++;
			}
			
			//Add pi estimate to frequency arrays
			prngFrequencyGraph[iterator] = estimatePi(prngPrimePairs);
			csprngFrequencyGraph[iterator] = estimatePi(csprngPrimePairs);
			trngFrequencyGraph[iterator] = estimatePi(trngPrimePairs);
			
			//Reset prime counters
			prngPrimePairs = 0;
			csprngPrimePairs = 0;
			trngPrimePairs = 0;
		}
		
		//generate and print tables
		generateTable(prngFrequencyGraph);
		generateTable(csprngFrequencyGraph);
		generateTable(trngFrequencyGraph);
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
	
	public static void generateTable(double[] frequencies)
	{
		sort(frequencies);	//sort for distribution class generation
		
		//generate data summaries
		double dataRange = frequencies[29] - frequencies[0];
		double sum = sum(frequencies);
		double mean = sum / 30;
		double standardDeviation = standardDeviation(frequencies, mean);
		double classWidth = dataRange / 5;
		
		System.out.println("dataRange = " + dataRange);
		System.out.println("sum = " + sum);
		System.out.println("mean = " + mean);
		System.out.println("standardDeviation = " + standardDeviation);
		System.out.println("classWidth = " + classWidth);
	}
	
	public static void sort(double[] frequencies)
	{
		for (int iteratorI = 0; iteratorI < frequencies.length; iteratorI++)
		{
			for (int iteratorJ = iteratorI + 1; iteratorJ < frequencies.length; iteratorJ++)
			{
				if (frequencies[iteratorI] > frequencies[iteratorJ])
				{
					double temp = frequencies[iteratorI];
					frequencies[iteratorI] = frequencies[iteratorJ];
					frequencies[iteratorJ] = temp;
				}
			}
		}
	}
	
	public static double sum(double[] frequencies)
	{
		double sum = 0;
		
		for (int iterator = 0; iterator < frequencies.length; iterator++)
		{
			sum += frequencies[iterator];
		}
		
		return sum;
	}
	
	public static double standardDeviation(double[] frequencies, double mean)
	{
		double standardDeviation = 0;
		double deviationSum = 0;
		
		for (int iterator = 0; iterator < frequencies.length; iterator++)
		{
			deviationSum += (frequencies[iterator] - mean) * (frequencies[iterator] - mean);
		}
		
		standardDeviation = Math.sqrt(deviationSum / frequencies.length);
		
		return standardDeviation;
	}
}
