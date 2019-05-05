package test.jkk;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Unit test for simple App.
 */
public class AppTest {
	private static final String url1 = "https://jhomes.to-kousya.or.jp/search/jkknet/service/akiyaJyoukenStartInit";

	public interface WebDriverFactory {
		public WebDriver create();
	}

	public static Iterable<WebDriverFactory> getDriverFactories() {
		ArrayList<WebDriverFactory> factories = new ArrayList<WebDriverFactory>();
		factories.add(new WebDriverFactory() {
			public WebDriver create() {
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--headless", "--disable-gpu","--disable-dev-shm-usage","--no-sandbox");
				return new ChromeDriver(options);
			}
		});
		return factories;
	}



	@Test
	public void testXXX() throws Exception {
		for (WebDriverFactory factory : getDriverFactories()) {
			WebDriver driver = factory.create();
			try {
				driver.get(url1);
				String currentWindow = driver.getWindowHandle();
				for (String popUpHandle : driver.getWindowHandles()) {
					if(popUpHandle.equalsIgnoreCase(currentWindow)) continue;
					driver.switchTo().window(popUpHandle);
				}

				List<WebElement> checkboxs = driver.findElements(By.name("akiyaInitRM.akiyaRefM.checks"));
				for(WebElement checkbox : checkboxs){
					if(checkbox.getAttribute("value").equals("23")){
						checkbox.click();
						break;
					}
				}
				
				List<WebElement> as = driver.findElements(By.tagName("a"));
				for(WebElement a : as){
					if(a.getAttribute("onclick") != null && a.getAttribute("onclick").equals("javascript:submitPage('akiyaJyoukenRef'); return false")){
						a.click();
						break;
					}
				}
				ArrayList<String> namels = new ArrayList<String>();
				
				
				if(driver.getTitle().equals("JKKねっと > あき家検索・申込 > 先着順あき家募集")){
					List<WebElement> tdDataCells = driver.findElements(By.xpath("//td[@class='Data_cell' and @colspan='3']"));
					for(WebElement tdDataCell : tdDataCells){
						namels.add(tdDataCell.getText());
						break;
					}
					
				}else{
					List<WebElement> selects = driver.findElements(By.tagName("select"));
					for(WebElement select : selects){
						if(select.getAttribute("name").equals("akiyaRefRM.showCount")){
							Select showCountSelect = new Select(select);
							showCountSelect.selectByValue("50");
							break;
						}
					}
					

					List<WebElement> tdListTXT1s = driver.findElements(By.xpath("//td[@class='ListTXT1' and @align='center']"));
					for(WebElement tdListTXT1 : tdListTXT1s){
						if(tdListTXT1.getAttribute("width").equals("")){
							//System.out.println(tdListTXT1.getText());
							namels.add(tdListTXT1.getText());
						}
					}
					
					List<WebElement> tdListTXT2s = driver.findElements(By.xpath("//td[@class='ListTXT2' and @align='center']"));
					for(WebElement tdListTXT2 : tdListTXT2s){
						if(tdListTXT2.getAttribute("width").equals("")){
							//System.out.println(tdListTXT2.getText());
							namels.add(tdListTXT2.getText());
						}
					}
				}

				
	            String dispString = "";
	            
				for(String name : namels){
					dispString =   dispString + name + " , ";
				}
				
				try {
					
					FileWriter file = new FileWriter("/var/www/html/jkk.html", true);
					PrintWriter pw = new PrintWriter(new BufferedWriter(file));
					pw.println("<tr><td>"+new Date().toString()+" : " + dispString+"</td></tr>");
					
					pw.close();
									


				} catch (IOException e) {
					e.printStackTrace();
				}
				
				MailSender sender = new MailSender();
				if(dispString.contains("清新")) {
					sender.sendMail(MailSender.FROM, MailSender.FROMNAME, MailSender.TO, "jkk", dispString);
				}
	            //displayTray(dispString);
	            
	            Thread.sleep(10000);
				
			} finally {
				driver.quit();
			}
		}
	}
	
    public void displayTray(String dispString) throws AWTException, MalformedURLException {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);

        trayIcon.displayMessage("", dispString, MessageType.INFO);
    }
    
    
    
}
