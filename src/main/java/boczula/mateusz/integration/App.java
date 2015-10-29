package boczula.mateusz.integration;

import com.flexionmobile.codingchallenge.integration.IntegrationTestRunner;

public class App 
{
    public static void main( String[] args )
    {
    	IntegrationImpl integration = new IntegrationImpl("mboczula");
    	IntegrationTestRunner runner = new IntegrationTestRunner();
    	runner.runTests(integration);
    }
}
