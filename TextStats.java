

import java.util.HashMap;

public class TextStats
{
	//set margin rules for "abusive text" or "acceptable text"
	public static final double ABUSIVE_MARGIN = 5.0d;
	public static final double LOUD_MARGIN = 10.0d;
	
	private volatile HashMap<Integer, SentenceStats> sentenceStats;
	
	private int wordCount;
	private int sentenceCount;
	
	// Initialise the counters
	public TextStats()
	{
		sentenceStats = new HashMap<>();
		wordCount = 0;
		sentenceCount = 0;
	}
	// Reset the counters
	public void reset()
	{
		sentenceCount = 0;
		wordCount = 0;
		sentenceStats.clear();
	}
	// Count and output 
	public void addSentenceStats(SentenceStats stats)
	{
		wordCount += stats.wordCount;
		
		sentenceStats.put(sentenceCount++, stats);
	}
	
	public SentenceStats getSentenceStats(int sentence)
	{
		if(sentence < 0 || sentence >= sentenceStats.size())
			return null;
		
		return sentenceStats.get(sentence);
	}
	// Check if language deemed bad or "abusive"
	public double getBadPercentage()
	{
		int badCount = 0;
		
		for(int idx : sentenceStats.keySet())
		{
			SentenceStats stats = sentenceStats.get(idx);
			badCount += stats.badWordCount;
		}
		
		System.out.println(badCount + " / " + wordCount);
		
		return ((double)badCount / (double)wordCount) * 100;
	}
	
	// Checker for use of caps lock in text 
	public double getLoudPercentage()
	{
		int loudCount = 0;
		for(int sentence : sentenceStats.keySet())
			loudCount += sentenceStats.get(sentence).allCapsWordCount;
		
		return ((double)loudCount / (double)wordCount) * 100;
	}
	// Returns number of words
	public int getWordCount()
	{
		return wordCount;
	}
	
	public int getSentenceCount()
	{
		return sentenceCount;
	}
	
	public String getTextInformation()
	{
		StringBuilder builder = new StringBuilder();
		
		if(getBadPercentage() >= ABUSIVE_MARGIN)
			builder.append("abusive ");
		else
			builder.append("acceptable ");
		
		if(getLoudPercentage() >= LOUD_MARGIN)
			builder.append("loud ");
		else
			builder.append("");
		
		builder.append("text");
		
		return builder.toString();
	}
}
