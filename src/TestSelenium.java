import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestSelenium {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("useAutomationExtension",false);
		
		//Chrome Driver Location and instance
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\ShresthaA1\\MGCS\\QA automation Project\\Selenium Chrome Driver\\Selenium Chrome Driver\\chromedriver.exe");
		WebDriver driver = new ChromeDriver(options);
		
		//For UAT
		String URL = "https://10.200.14.88:9443/spcp2/logon";
		//String CaseNum = "2020-09-1-1478404344";

		WebDriverWait wait = new WebDriverWait(driver, 20);
		
		//Actions action = new Actions(driver);

		//calls loadPage method to load the URL
		loadPage(driver, wait, URL);
		//sleepTime(9000);
		Properties prop = new Properties();
	    InputStream input = null;
	    try {

	        input = new FileInputStream("config.properties");
	        // load a properties file
	        prop.load(input);
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    } finally {
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    //get userid from properties file
        String userid = prop.getProperty("userid");
        //enters the user id and logs in
		sendKeysMethod(driver, wait, "//input[@id='userName']", userid);
		clickMethod(driver, wait, "//form[@class='loginForm']/button");
		sleepTime(2000);

		//creates new template if the flag for createNewTemplate in the properties file is Yes
		String createNewTemplate = prop.getProperty("createNewTemplate");
		//System.out.println(createNewTemplate);
		if(createNewTemplate.equalsIgnoreCase("Yes")) {
			listTemplates(driver, wait, prop);
			createNewTemplate(driver, wait, prop);		

			//openTemplate(driver, wait, prop);
			addPageToTemplate(driver, wait);
			
			addSectionToPage(driver, wait);
			
			publishTemplate(driver, wait);
		}
		else {
			//lists the existing templates by program and template type
			listTemplates(driver, wait, prop);

			//searches for the existing templates
			openTemplate(driver, wait, prop);
			
			//clones the template
			cloneTemplate(driver, wait, prop);
			
			//publishes the new template
			publishTemplate(driver, wait);
		}

	}
	public static void listTemplates(WebDriver driver, WebDriverWait wait, Properties prop) {
		//clicks 'List Templates'
		clickMethod(driver, wait, "(//a[@class='spc_link'])[2]");
		//enters template type to the dropdown
		sendKeysMethod(driver, wait, "//select[@id='templateTypeCode']", prop.getProperty("templateType"));
		//enters program name to the dropdown
		sendKeysMethod(driver, wait, "//select[@id='programCode']", prop.getProperty("program"));
		//clicks search
		clickMethod(driver, wait, "//button[@type='submit']");
	}
	
	public static void openTemplate(WebDriver driver, WebDriverWait wait, Properties prop) {
		//if the template number is not provided in config.properties file, system displays a message and exists the program
		if(prop.getProperty("templateNumber").isEmpty()){
			System.out.println("Please provide the template number.");
			System.exit(1);
		}
		//clicks on the template number provided in config file 'templateNumber'
		driver.findElement(By.linkText(prop.getProperty("templateNumber"))).click();	
	}
	
	public static void cloneTemplate(WebDriver driver, WebDriverWait wait, Properties prop) {

		
		//clicks on 'Clone' button
		clickMethod(driver, wait, "//button[@id='cloneBtn']");

		//sets the program if it is different than the existing, leaves as it is by default
		if(!prop.getProperty("program_clone").isEmpty()){
			sendKeysMethod(driver, wait, "//select[@id='programCode']", prop.getProperty("program_clone"));
		} 
		
		//sets the template name, if new name is not provided in config.properties file, leaves as it is
		if(!prop.getProperty("templateName_clone").isEmpty()){
			//clears the previous template name
			driver.findElement(By.xpath("//input[@id='templateName']")).sendKeys(Keys.CONTROL + "a");
			driver.findElement(By.xpath("//input[@id='templateName']")).sendKeys(Keys.DELETE);
			
			//enters the new template name for the cloned template
			sendKeysMethod(driver, wait, "//input[@id='templateName']", prop.getProperty("templateName_clone"));
		} else {
			driver.findElement(By.xpath("//input[@id='templateName']")).sendKeys("_clone");
		}
		
		//sets the effective date if provided in config.properties file, leaves as it is if not
		if(!prop.getProperty("effectiveDate_clone").isEmpty()){
			sendKeysMethod(driver, wait, "//input[@id='template_effectiveDate']", prop.getProperty("effectiveDate_clone"));
		}
		
		//sets the end date if provided in config.properties file, leaves as it is if not
		if(!prop.getProperty("endDate_clone").isEmpty()){
			sendKeysMethod(driver, wait, "//input[@id='template_endDate']", prop.getProperty("endDate_clone"));
		}
		
		//sets the description if provided in config.properties file, appends "This is a cloned business plan template" in the existing description" if not
		if(!prop.getProperty("description_clone").isEmpty()){
			//clears the description field
			driver.findElement(By.xpath("//textarea[@id='description']")).sendKeys(Keys.CONTROL + "a");
			driver.findElement(By.xpath("//textarea[@id='description']")).sendKeys(Keys.DELETE);
			
			//adds the new description
			sendKeysMethod(driver, wait, "//textarea[@id='description']", prop.getProperty("description_clone"));
		} else {
			driver.findElement(By.xpath("//textarea[@id='description']")).sendKeys(" This is a cloned business plan template");
		}
		
		//clicks "Save" button
		clickMethod(driver, wait, "//button[@id='saveBtn']");
	}
	public static void createNewTemplate(WebDriver driver, WebDriverWait wait, Properties prop) {
		//clicks 'New' button to create new template
		clickMethod(driver, wait, "//a[@id='newTemplate']");
		if(prop.getProperty("templateType").isEmpty()){
			sendKeysMethod(driver, wait, "//select[@id='templateType']", "Business Plan");
		} else {
			sendKeysMethod(driver, wait, "//select[@id='templateType']", prop.getProperty("templateType"));
		}
		if(prop.getProperty("program").isEmpty()){
			sendKeysMethod(driver, wait, "//select[@id='programCode']", "Integrated Employment Services");
		} else {
			sendKeysMethod(driver, wait, "//select[@id='programCode']", prop.getProperty("program"));
		}
		if(prop.getProperty("templateName").isEmpty()){
			sendKeysMethod(driver, wait, "//input[@id='templateName']", "Test_BP");
		} else {
			sendKeysMethod(driver, wait, "//input[@id='templateName']", prop.getProperty("templateName"));
		}
		if(prop.getProperty("effectiveDate").isEmpty()){
			sendKeysMethod(driver, wait, "//input[@id='template_effectiveDate']", "01/04/2020");
		} else {
			sendKeysMethod(driver, wait, "//input[@id='template_effectiveDate']", prop.getProperty("effectiveDate"));
		}
		if(prop.getProperty("endDate").isEmpty()){
			sendKeysMethod(driver, wait, "//input[@id='template_endDate']", "31/03/2021");
		} else {
			sendKeysMethod(driver, wait, "//input[@id='template_endDate']", prop.getProperty("endDate"));
		}
		if(prop.getProperty("description").isEmpty()){
			sendKeysMethod(driver, wait, "//textarea[@id='description']", "test description");
		} else {
			sendKeysMethod(driver, wait, "//textarea[@id='description']", prop.getProperty("description"));
		}
		clickMethod(driver, wait, "//input[@title='Central Region']");
		clickMethod(driver, wait, "//input[@title='Eastern Region']");
		clickMethod(driver, wait, "//input[@title='Northern Region']");
		clickMethod(driver, wait, "//input[@title='Western Region']");
		
		clickMethod(driver, wait, "//button[@id='saveBtn']");
	}
	public static void addPageToTemplate(WebDriver driver, WebDriverWait wait) {
		clickMethod(driver, wait, "(//a[@id='addPage'])[1]");
		//newFrameTemplatePage(driver, wait);
		sendKeysMethod(driver, wait, "//input[@id='templatePageName']", "Test Template Page");
		sendKeysMethod(driver, wait, "//input[@id='pageEnglishLabel']", "test english");
		sendKeysMethod(driver, wait, "//input[@id='pageFrenchLabel']", "test french");
		sendKeysMethod(driver, wait, "//select[@id='visibilityCode']", "Edit by Ministry users only");
		sendKeysMethod(driver, wait, "//select[@id='pageType']", "Business Plan");
		clickMethod(driver, wait, "//button[@id='saveBtn']");
		//clickMethod(driver, wait, "(//a[@tabindex=\"16\"])[4]");
	    
	}
	public static void addSectionToPage(WebDriver driver, WebDriverWait wait) {
		clickMethod(driver, wait, "//a[@id='addSection']");
		//newFrameTemplatePage(driver, wait);
		sendKeysMethod(driver, wait, "//input[@id='templatePageName']", "Test Section");
		sendKeysMethod(driver, wait, "//input[@id='pageEnglishLabel']", "test english");
		sendKeysMethod(driver, wait, "//input[@id='pageFrenchLabel']", "test french");
		sendKeysMethod(driver, wait, "//select[@id='visibilityCode']", "Edit by Ministry users only");
		//sendKeysMethod(driver, wait, "//select[@id='pageType']", "Business Plan");
		clickMethod(driver, wait, "//button[@id='saveBtn']");
		clickMethod(driver, wait, "(//a[@tabindex=\"16\"])[4]");
	    
	}
	public static void publishTemplate(WebDriver driver, WebDriverWait wait) {
		//clicks "Publish" button
		clickMethod(driver, wait, "//button[@id='publishBtn']");
		clickMethod(driver, wait, "(//div[@class='ui-dialog-buttonset'])[3]/button[1]/span");
	}
	public static void loadPage(WebDriver driver, WebDriverWait wait, String url) {
		driver.manage().window().maximize();
		
		driver.get(url);
	}
	public static void clickMethod(WebDriver driver, WebDriverWait wait, String by){
		
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(by)));
		driver.findElement(By.xpath(by)).click();
	
	}
	public static void sendKeysMethod(WebDriver driver, WebDriverWait wait, String by, String keys){
		
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(by)));
		driver.findElement(By.xpath(by)).sendKeys(keys);
		
	}

	public static void sleepTime(int time){
		
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void ctrlSave(WebDriver driver) {

		Actions action = new Actions(driver);

		action.keyDown(Keys.CONTROL)
		.sendKeys("s")
		.build()
		.perform();

		action.keyUp(Keys.CONTROL)
		.build()
		.perform();

	}
}
