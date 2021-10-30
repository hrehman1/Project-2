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
				System.out.println(e.getMessage());
			}
			
			//initialize trng arrays
			if (trng != null)
			{
				for (int iterator1 = 0; iterator1 < 100; iterator1++)
				{
					trngOne[iterator1] = trng.nextInt();
					trngTwo[iterator1] = trng.nextInt();
				}
			}
			
			//initialize gcd counters
			int prngGCD = 0, csprngGCD = 0, trngGCD = 0;
			
			//get primepairs
			for (int iterator1 = 0; iterator1 < 100; iterator1++)
			{
				if (prng != null)
				{
					prngGCD = euclideanAlg(prngOne[iterator1], prngTwo[iterator1]);
				}
				if (csprng != null)
				{
					csprngGCD = euclideanAlg(csprngOne[iterator1], csprngTwo[iterator1]);
				}
				if (trng != null)
				{
					trngGCD = euclideanAlg(trngOne[iterator1], trngTwo[iterator1]);
				}
				
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
		System.out.println("PRNG Frequency Table");
		generateTable(prngFrequencyGraph);
		System.out.println("CSPRNG Frequency Table");
		generateTable(csprngFrequencyGraph);
		System.out.println("TRNG Frequency Table");
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
		double min = getMin(frequencies);
		double max = getMax(frequencies);
		double[] classBoundaries = getClassBoundaries(min, max, classWidth);
		double[] classFrequencies = new double[5];
		
		//generate class frequencies
		for (int frequencyIterator = 0; frequencyIterator < classFrequencies.length; frequencyIterator++)
		{
			classFrequencies[frequencyIterator] = getFrequency(classBoundaries[frequencyIterator], classBoundaries[frequencyIterator + 1], frequencies);
		}
		
		//print mean and standard deviation for given dataset
		System.out.println("mean = " + mean);
		System.out.println("standardDeviation = " + standardDeviation);
		
		//format and generate actual table
		System.out.printf("%10s%20s\n", "Class Boundaries", "Frequency");
		for (int iterator = 0; iterator < 5; iterator ++)
		{
			System.out.printf("%1.7f%s%1.7f%10.1f\n\n", classBoundaries[iterator], " - ", classBoundaries[iterator + 1], classFrequencies[iterator]);
		}
	}
	
	public static int getFrequency(double lowerClassBoundary, double upperClassBoundary, double[] frequencies)
	{
		//each index of array classFrequencies represents the frequency of data values in the given class
		int frequency = 0;
		
		for (int iterator = 0; iterator < frequencies.length; iterator++)	//iterates through array of pi generations and checks if they fall under class boundaries or not
		{
			if (frequencies[iterator] > lowerClassBoundary - 0.0000005 && frequencies[iterator] < upperClassBoundary + 0.0000005) frequency++;
		}
		
		return frequency;
	}
	
	public static double[] getClassBoundaries(double min, double max, double classWidth)
	{
		double[] classBoundaries = new double[6];
		double currentClassMin = min;
		
		for (int iterator = 0; iterator < classBoundaries.length; iterator++)
		{
			classBoundaries[iterator] = currentClassMin;
			currentClassMin += classWidth;
		}
		
		return classBoundaries;
	}
	
	public static double getMin(double[] frequencies)
	{
		double min = 10;
		
		for (int iterator = 0; iterator < frequencies.length; iterator++)
		{
			if (frequencies[iterator] < min) min = frequencies[iterator];
		}
		
		return min;
	}
	
	public static double getMax(double[] frequencies)
	{
		double max = 0;
		
		for (int iterator = 0; iterator < frequencies.length; iterator++)
		{
			if (frequencies[iterator] > max) max = frequencies[iterator];
		}
		
		return max;
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
