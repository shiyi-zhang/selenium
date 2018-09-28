package com.se.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author 张世一
 * @version 1.0
 */
public class Selenium {

    private Map<String, Integer> getMap() {
        Map<String, Integer> drugMap = new HashMap<String, Integer>();
        drugMap.put("凡德他尼", 130);
        drugMap.put("帕博西尼", 120);
        drugMap.put("阿西替尼", 94);
        drugMap.put("马法兰", 112);
        drugMap.put("艾乐替尼", 76);
        drugMap.put("威罗菲尼", 124);
        drugMap.put("帕托珠单抗", 119);
        drugMap.put("尼达尼布", 74);
        drugMap.put("达雷木单抗", 134);
        drugMap.put("雷莫芦单抗", 97);
        drugMap.put("苏金单抗", 115);
        drugMap.put("尼拉帕尼", 111);
        return drugMap;
    }

    private Map<String, String> getLinkMap() {
        Map<String, String> drugMap = new HashMap<String, String>();
        drugMap.put("凡德他尼", "www.headkonhc.com/jiazhuangxianai/337.html");
        drugMap.put("帕博西尼", "www.headkonhc.com/ruxianai/83.html");
        drugMap.put("阿西替尼", "www.headkonhc.com/shenai/55.html");
        drugMap.put("马法兰", "www.headkonhc.com/duofaxinggusuiai/97.html");
        drugMap.put("艾乐替尼", "www.headkonhc.com/feiai/284.html");
        drugMap.put("威罗菲尼", "www.headkonhc.com/heisesuliu/342.html");
        drugMap.put("帕托珠单抗", "www.headkonhc.com/ruxianai/84.html");
        drugMap.put("尼达尼布", "www.headkonhc.com/feiai/340.html");
        drugMap.put("达雷木单抗", "www.headkonhc.com/duofaxinggusuiai/320.html");
        drugMap.put("雷莫芦单抗", "www.headkonhc.com/weiai/62.html");
        drugMap.put("苏金单抗", "www.headkonhc.com/yinxiebing/329.html");
        drugMap.put("尼拉帕尼", "www.headkonhc.com/luanchaoai/319.html");
        return drugMap;
    }

    public static void main(String[] args) throws Exception {
        Selenium selenium = new Selenium();
        String path = System.getProperty("user.dir") + File.separator + "chromedriver";
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            path = path + ".exe";
        }
        System.setProperty("webdriver.chrome.driver", path);
        List<WordEntry> text = selenium.getText();
        selenium.zhimeng(text);
    }

    public List<WordEntry> getText() throws Exception {
        List<WordEntry> entries = new ArrayList<>();
        Map<String, Integer> map = getMap();
        Map<String, String> linkMap = getLinkMap();
        List<String> keys = map.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toList());
        File allFiles = getAllFiles();
        if (null != allFiles) {
            String title = null;
            String tags = null;
            Integer typeid = null;
            WordEntry wordEntry = null;

            FileInputStream fileInputStream = new FileInputStream(allFiles);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            List<String> contentList = new ArrayList<>();
            StringBuilder stringBuilder = null;
            String line = reader.readLine();
            String trim = null;
            String mainKey = null;
            boolean tilteFlag = false;
            String reg = "[。？?!！]";
            Pattern pattern = Pattern.compile(reg);
            while (line != null) {
                stringBuilder = new StringBuilder();
                trim = line.trim();
                if (trim.length() == 0) {
                    if (contentList.size() > 0) {
                        stringBuilder.append(contentList.get(contentList.size() - 2));
                        stringBuilder.insert(stringBuilder.indexOf(";") + 1, "color:#E53333;");
                        stringBuilder.append("<br>");
                        trim = stringBuilder.toString();

                        stringBuilder = new StringBuilder("<strong>");
                        stringBuilder.append(contentList.get(contentList.size() - 1));
                        stringBuilder.append("</strong>");

                        contentList.remove(contentList.size() - 1);
                        contentList.remove(contentList.size() - 1);
                        contentList.remove(0);

                        contentList.add(trim);
                        contentList.add(stringBuilder.toString());
                        System.out.println(StringUtils.join(contentList,""));
                        wordEntry = new WordEntry();
                        wordEntry.setTitle(title);
                        wordEntry.setTags(tags);
                        wordEntry.setTypeid(typeid);
                        wordEntry.setContent(StringUtils.join(contentList, ""));
                        entries.add(wordEntry);
                        contentList.clear();
                    }
                } else {
                    if (contentList.size() == 0) {
                        tilteFlag = true;
                        title = trim;
                        contentList.add(line);
                    } else if (tilteFlag) {

                        stringBuilder.append("<p><span style=\"font-size:16px;\">");
                        if (pattern.matcher(trim).find()) {
                            for (String key : keys) {
                                if (pattern.split(trim)[0].contains(key)) {
                                    mainKey = key;
                                    tags = key;
                                    typeid = map.get(key);
                                }
                            }
                            stringBuilder.append("<strong>");
                            stringBuilder.append(trim.substring(trim.indexOf(pattern.split(trim)[0]),pattern.split
                                (trim)[0].length()+1));
                            stringBuilder.append("</strong>");
                            trim = trim.substring(pattern.split(trim)[0].length() + 1);
                        }

                        stringBuilder.append("<a href=\"http://" + linkMap.get(mainKey) + "\">");
                        stringBuilder.append(mainKey);
                        stringBuilder.append("</a>");
                        stringBuilder.append(trim.substring(trim.indexOf(mainKey) + mainKey.length()));
                        stringBuilder.append(
                            "</span></p><br /><p style=\"text-align: center;\"><span style=\"font-size:16px;\"><img "
                                + "alt=\"" + contentList.get(0) + "\" "
                                + "src=\"/uploads/allimg/180920/10-1P920224153563.jpg\" " + "style=\"width: "
                                + "240px; height: 180px;\" /></span><br /></p><p>");
                        contentList.add(stringBuilder.toString());
                        tilteFlag = false;
                    } else {
                        if (trim.equals(contentList.get(0))) {
                            stringBuilder.append("<p><span style=\"font-size:16px;\"><span style=\"color:#ff0000;\">");
                            stringBuilder.append(trim);
                            stringBuilder.append("</span></span><br />&nbsp;</p>");
                        } else {
                            stringBuilder.append("<p><span style=\"font-size:16px;\">");
                            stringBuilder.append(trim);
                            stringBuilder.append("</span><br />&nbsp;</p>");
                        }
                        contentList.add(stringBuilder.toString());
                    }
                }
                line = reader.readLine();
            }

        }
        return entries;
    }


    public File getAllFiles() throws Exception {
        String path = System.getProperty("user.dir");
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null == files) {
                return null;
            }
            for (File fName : files) {
                if (fName.getName().endsWith(".txt")) {
                    return fName;
                }
            }
        }
        return null;
    }


    public void zhimeng(List<WordEntry> entries) throws Exception {
        int timeOut = 8;

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
        options.addArguments("--headless", "--ignore-certificate-errors");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(100, TimeUnit.MICROSECONDS);
        driver.get("http://www.headkonhc.com/haidekang.com.html/login.php");
        WebElement userid = driver.findElement(By.name("userid"));
        userid.sendKeys("admin05");
        WebElement pwd = driver.findElement(By.name("pwd"));
        pwd.sendKeys("888");
//        WebElement vdimgck = driver.findElement(By.xpath("//*[@id=\"vdimgck\"]"));
//        BufferedImage bufferedImage = captureElement(vdimgck, driver);

//        Ocr.setUp();
//        Ocr ocr = new Ocr();
//        ocr.startEngine("eng", Ocr.SPEED_FASTEST);
//        String result = ocr.recognize(bufferedImage, com.asprise.ocr.Ocr.RECOGNIZE_TYPE_ALL,
//            com.asprise.ocr.Ocr.OUTPUT_FORMAT_PLAINTEXT, 0, null);
//        ocr.stopEngine();
//
//        WebElement vdcode = driver.findElement(By.xpath("//*[@id=\"vdcode\"]"));
//        vdcode.sendKeys(result);

//        WebDriverWait wait = new WebDriverWait(driver, 20);
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("updatetest")));
        (new WebDriverWait(driver, timeOut)).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("海外新特药");
            }
        });
        driver.get("http://www.headkonhc.com/haidekang.com.html/article_add.php");
        ArrayList<String> tabs = null;
        WordEntry entry = null;
        for (int i = 0; i < entries.size(); i++) {
            entry = entries.get(i);
            WebElement title = driver.findElement(By.xpath("//*[@id=\"title\"]"));
            title.sendKeys(entry.getTitle());
            WebElement tags = driver.findElement(By.xpath("//*[@id=\"tags\"]"));
            tags.sendKeys(entry.getTags());
            WebElement source = driver.findElement(By.xpath("//*[@id=\"source\"]"));
            source.sendKeys("海得康");
            WebElement writer = driver.findElement(By.xpath("//*[@id=\"writer\"]"));
            writer.sendKeys("海外新特药小编");
            Select typeid = new Select(driver.findElement(By.xpath("//*[@id=\"typeid\"]")));
            typeid.selectByValue(entry.getTypeid().toString());
            WebElement cke_8 = driver.findElement(By.xpath("//*[@id=\"cke_8\"]"));
            cke_8.click();
//            WebElement textarea = (new WebDriverWait(driver, timeOut)).until(new ExpectedCondition<WebElement>() {
//                @Override
//                public WebElement apply(WebDriver d) {
//                    return d.findElement(By.xpath("//*[@id=\"cke_contents_body\"]/textarea"));
//                }
//            });

            WebElement textarea = driver.findElement(By.xpath("//*[@id=\"cke_contents_body\"]/textarea"));
            textarea.sendKeys(entry.getContent());
            driver.findElement(By.xpath("//*[@id=\"needset\"]/tbody/tr[12]/td/div[3]/input")).click();

            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("window.open('http://www.headkonhc.com/haidekang.com.html/article_add.php')");
            tabs = new ArrayList<String>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(i + 1));

        }
//        driver.quit();
    }


    public BufferedImage captureElement(WebElement element, WebDriver driver) throws Exception {
        File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage img = ImageIO.read(screen);
        int width = element.getSize().getWidth();
        int height = element.getSize().getHeight();
        Point p = element.getLocation();
        BufferedImage dest = img.getSubimage(p.getX() * 2, p.getY() * 2, width * 2, height * 2);
//        File screenshotLocation = new File("test.png");
//        ImageIO.write(dest, "png", screenshotLocation);
        return dest;
    }

//    private String submit(File file) throws Exception {
//
//        BufferedImage img = ImageIO.read(file);
//        Ocr.setUp();
//        Ocr ocr = new Ocr();
//        ocr.startEngine("eng", Ocr.SPEED_FASTEST);
//        String result = ocr
//            .recognize(img, com.asprise.ocr.Ocr.RECOGNIZE_TYPE_ALL, com.asprise.ocr.Ocr.OUTPUT_FORMAT_PLAINTEXT, 0,
//                null);
//        ocr.stopEngine();
//        return result;
//    }
}
