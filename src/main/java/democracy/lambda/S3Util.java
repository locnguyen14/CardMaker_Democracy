package democracy.lambda;

public class S3Util 
{
	public static String getFolderName()
	{
		String testing = System.getenv("TESTING");
		if (testing != null && testing.equals("1"))
		{
			System.out.println("Using test S3 folder.");
			return "test_images/";
		}
		else
		{
			return "images/";
		}
	}
}
