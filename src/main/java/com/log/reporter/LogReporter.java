package com.log.reporter;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.monte.media.FormatKeys.MediaType;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class LogReporter {

	public static ArrayList<ArrayList<String>> detailTests = new ArrayList<ArrayList<String>>();
	public static HashMap<Long,String> hMap = new HashMap<Long,String>(); 
	public static HashMap<Long,String> hMapVid = new HashMap<Long,String>(); 
	
	static String pathReport = "";
	static String pathScreenshots = "";
	static String pathlogFolder = "";
	static String pathDownloads = "";
	static String pathRecordings = "";
	static String nameLogFile = "";
	Writer writer = null;//
	WebDriver driver;
	private SpecializedScreenRecorder screenRecorder;
	 
	private static String HtmlHead= "<head><meta http-equiv=\"Content-type\" content=\"text/html;charset=UTF-8\">" +
            "<h1 align=\"center\">Log Report</h1><style type='text/css'>" +
            ".logTable {font-size:12px;color:#333333;width:100%;border-width: 1px;border-color: #729ea5;border-collapse: collapse;}"+
            ".logTable th {font-size:12px;background-color:#00aeef;border-width: 1px;padding: 8px;border-style: solid;border-color:" +"#729ea5;text-align:left;}" +
            ".logTable tr:nth-child(odd) {background-color:#cfd5d6;}" +
            ".logTable tr:nth-child(even) {background-color:#ffffff;}" +
            ".logTable td {font-size:12px;border-width: 1px;padding: 8px;border-style: solid;border-color: #cfd5d6;}" +
            ".logTable tr:hover {background-color:#5cb3c4;}" +
            ".txtColor {color:red;}" +
            ".warningText {color:yellow;}" +
            "</style></head><body>" +
            "<table class='logTable' border='1'>" +
            "<tr><th>Index</th><th>Screenshot</th><th>DateTime</th><th>File:Line</th><th>LogLevel</th><th>Message</th></tr>";
	
	private static String htmlExecutionSummary = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\"> "
			+ "<html xmlns=\"http://www.w3.org/1999/xhtml\"> <head> <title>Test(s) Execution Summary Report</title> <style type=\"text/css\"> "
			+ "table { margin-bottom: 10px; border-collapse: collapse; empty-cells: show } th,td { border: 1px solid #009; padding: .25em .5em } "
			+ "th { vertical-align: bottom } td { vertical-align: top } table a { font-weight: bold } .stripe td { background-color: #E6EBF9 } .num "
			+ "{ text-align: right } .failureSectionHide { display:none } .failureSectionShow { display:block } tr.skipped td.status { background-color: #CCC } "
			+ "tr.passed td.status { background-color: #0A0 } tr.failed td.status { background-color: #D00 } tr.passed td.passed { background-color: #0A0 } "
			+ "tr.skipped td.skipped { background-color: #CCC } tr.failed td.failed { background-color: #D00 } .stacktrace { white-space: pre; font-family: monospace "
			+ "} .totop { font-size: 85%; text-align: center; border-bottom: 2px solid #000 } .hidden { display: none; } </style> </head> <body> <center> "
			+ "<h1>Test(s) Execution Report</h1> </center> <br/> <h3>Test(s) Execution Summary:</h3> <table> <tr> <th>Number of tests</th> <th>Passed</th> "
			+ "<th>Skipped</th> <th>Failed</th> <th>Total Execution Time (Minutes, Seconds)</th> <th>Browser</th> </tr> <!--##TestSummaryData##--> </table> "
			+ "<h3>Test(s) Execution Details:</h3> <table> <tr> <th>Name</th> <th>QA Owner</th> <th>Language</th> <th>Status</th> "
			+ "<th>Execution Time (Minutes, Seconds)</th> <th>Details</th> <th>VideoLink</th> </tr> <!--##ExecutionDynamicData##--> "
			+ "</table> <br /> <div class=\"failureSectionHide\"> <h2>Cause(s) of Failure, if any</h2> <table> <thead> <tr> <th>S.No</th> "
			+ "<th>Test Name</th> <th>QA Owner</th> <th>Language</th> <th>Exception/Reason</th> <th>Message</th> <th class=\"hidden\">Stack Trace</th> "
			+ "<th>Screenshot/Video</th> <th>Defect(s), If Any</th> </tr> </thead> <tbody> ##FailureDynamicData## </tbody> </table> </div> </body> </html>";
	
	
	public LogReporter(String pathProject,WebDriver argDriver){
		try {
			driver = argDriver;
			createReportFolder(pathProject);
			createlogFile();
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nameLogFile),"UTF8")); 
	        writer.append(HtmlHead);
	        writer.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createReportFolder(String pathProject) {
		try {
			String currentDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			if(pathReport.equals("")) {
				pathReport = pathProject+"\\Report_"+currentDateTime;
				pathScreenshots = pathReport+"\\Screenshots";
				pathlogFolder = pathReport+"\\logs";
				pathDownloads = pathReport+"\\Downloads";
				pathRecordings = pathReport+"\\Recording";
				File file = new File(pathReport);
				file.mkdir();
				file = new File(pathScreenshots);
				file.mkdir();
				file = new File(pathlogFolder);
				file.mkdir();
				file = new File(pathDownloads);
				file.mkdir();
				file = new File(pathRecordings);
				file.mkdir();
			}				
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String takeScreenShot(WebDriver driver,boolean takeScreenShotFlag) {
		try {
			if(takeScreenShotFlag==true)
			{
				try 
				{
					String screenshotName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
					FileOutputStream out = new FileOutputStream(pathScreenshots +"\\"+screenshotName+".jpg");
					out.write(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
					out.close();
					return pathScreenshots +"\\"+screenshotName+".jpg";
				} catch (Exception e){
					e.printStackTrace();
					return null;
				}
			}
			return null;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized void createlogFile() {
		try {
			String nameTestMethod = new Throwable().fillInStackTrace().getStackTrace()[2].getMethodName();
			String nameClass = Thread.currentThread().getStackTrace()[3].getClassName();
			nameLogFile = pathlogFolder+"\\"+nameClass+"_"+nameTestMethod+"_"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())+".html";
			File file = new File(nameLogFile);
			file.createNewFile();
			
			hMap.put(Thread.currentThread().getId(),nameLogFile);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void logInfo(String message,boolean screenshotFlag) {
		try {
			String nameClass = Thread.currentThread().getStackTrace()[2].getClassName();
			String nameMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
			String lineNumber = Integer.toString(Thread.currentThread().getStackTrace()[2].getLineNumber());
			String stack = "ClassName:-"+nameClass+" , MethodName:->"+nameMethod+" , LineNumber:->"+lineNumber;
	    	writer.append(getHtml(message,stack,screenshotFlag,driver,"INFO")); 
	    	writer.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void logError(String message,boolean screenshotFlag) {
		try {
			String nameClass = Thread.currentThread().getStackTrace()[2].getClassName();
			String nameMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
			String lineNumber = Integer.toString(Thread.currentThread().getStackTrace()[2].getLineNumber());
			String stack = "ClassName:-"+nameClass+" , MethodName:->"+nameMethod+" , LineNumber:->"+lineNumber;
	    	writer.append(getHtml(message,stack,screenshotFlag,driver,"ERROR")); 
	    	writer.flush();
	    	Assert.fail();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void commitReport() {
		try {
			writer.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	    public String getHtml(String message, String stack, Boolean isScreenshot,WebDriver driver,String loglevel){ 
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String level = "";
        String screenshotLink = "";
        String name= "Screenshot";
        if(isScreenshot==true)
        	screenshotLink=  "<td><b><a href="+takeScreenShot(driver,isScreenshot)+">"+name+"</a></b></td>" ;
        else
            screenshotLink = "<td>Screenshot Option Not Opted</td>" ;
        if(loglevel.equalsIgnoreCase("error")) {
        	level = "<td><font color=\"red\">" + loglevel + "</font></td>";
        }else {
        	level = "<td>" + loglevel + "</td>";
        }
        return  "<tr>" + 
                    "<td></td>" +
                    screenshotLink+
                    "<td>" + dateFormat.format(date) + "</td>"+
                    "<td>" + stack + "</td>" +
                    level +
                    "<td>" + message + "</td>" +
                    
                "</tr>";
        }
	    
	    public void attachFileToReport(String defaultDownloadPath){
	    	try {
	    		String nameLatestFile = getLatestFile(defaultDownloadPath);
				String pathTo = pathDownloads+"\\";
				String updatedName = renameFileName(defaultDownloadPath,nameLatestFile,"");
				copyAndDeleteFile(defaultDownloadPath,pathTo,updatedName);		
				String link = "<wysiwyg><a href='" +(pathTo+updatedName)+"' target='_blank'>Download_File</a></wysiwyg>";
				logInfo("<wysiwyg><h4>Please click on link to download file "+link+"</h4></wysiwyg>",true);
	    	}catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    
	    public static String getLatestFile(String downloadPath) {
			String lastFileName = null;

			try {
				Thread.sleep(4000);
				File dir = new File(downloadPath);
				System.out.println("url on Path is: " + dir);
				FileFilter fileFilter = new WildcardFileFilter("*");
				File[] files = dir.listFiles(fileFilter);

				/** The newest file comes first **/
				int length = files.length;

				System.out.println("The size of array is: " + length);
				Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);

				lastFileName = files[length-1].getName();

				System.out.println("The latest file is: " + lastFileName);
			} catch (Exception e) {
				System.out.println("Error encountered: " + e.getLocalizedMessage());
				e.printStackTrace();
			}
			return lastFileName;
		}
	    
	    public String renameFileName(String path, String fileName, String type) {
			String updatedName = null;
			File newfile = null;
			String currentDate = new SimpleDateFormat("ddMMyyHHmmss").format(Calendar.getInstance().getTime());
			try {
				updatedName = fileName.replace(" ", "");
				updatedName = currentDate + "_" + type + "_" + updatedName;
				File oldfile = new File(path + "" + fileName);
				newfile = new File(path + "" + updatedName);

				if (oldfile.renameTo(newfile)) {
					System.out.println("Rename succesful");
					} else {
					System.out.println("Rename failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return updatedName;
		}
	    
	    public String copyAndDeleteFile(String copyFrom, String copyTo, String fileName) {
			InputStream inStream = null;
			OutputStream outStream = null;
			File destinationPath = null;

			try {

				File file = new File(copyTo);
				if (!file.exists()) {
					if (file.mkdir()) {
						System.out.println(file + " Directory is created!");
					} else {
						System.out.println("Failed to create directory!");
					}
				}

				File sourcePath = new File(copyFrom + "" + fileName);
				destinationPath = new File(copyTo + "\\" + fileName);

				inStream = new FileInputStream(sourcePath);
				outStream = new FileOutputStream(destinationPath);

				byte[] buffer = new byte[1024];

				int length;
				// copy the file content in bytes
				while ((length = inStream.read(buffer)) > 0) {
					outStream.write(buffer, 0, length);
				}

				inStream.close();
				outStream.close();

				// delete the original file
				sourcePath.delete();

				System.out.println("File is copied successful!");

			} catch (IOException e) {
				e.printStackTrace();
			}
			return destinationPath + "";
		}
	    
	    public synchronized void startRecording(Boolean videoFlag){
			try {
				if (videoFlag == true)
				{
					String nameTestMethod = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
					String nameClass = Thread.currentThread().getStackTrace()[2].getClassName();
					String nameVideo = nameClass+"_"+nameTestMethod+"_"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
					
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					int width = screenSize.width;
					int height = screenSize.height;

					Rectangle captureSize = new Rectangle(0, 0, width, height);

					GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
							.getDefaultConfiguration();

					this.screenRecorder = new SpecializedScreenRecorder(gc, captureSize,
							new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
							new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
									CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
									Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
							new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
							null, new File(pathRecordings),nameVideo);
					this.screenRecorder.start();
					
					hMapVid.put(Thread.currentThread().getId(),pathReport+"\\Recording\\"+nameVideo+".avi");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    public synchronized void stopRecording(boolean videoFlag){
			try {
				if (videoFlag == true) {
					try{
						this.screenRecorder.stop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	    
	    public synchronized void isEqual(boolean Expected,boolean Actual,String message,boolean screenShotFlag) {
	    	try {
	    		if(Expected == Actual) {
	    			logInfo(message,screenShotFlag);
	    		}else {
	    			logError(message,screenShotFlag);
	    		}
	    	}catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    
	    public static void prepareSummaryReport(ITestContext context) {
	    	try {
	    		String nameSummaryReport = pathReport+"\\SummaryReport_"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())+".html";
				File file = new File(nameLogFile);
				file.createNewFile();
	    		
				Writer objWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nameSummaryReport),"UTF8"));
				
	    		String QAOwner = context.getSuite().getXmlSuite().getAllParameters().get("QAOwner");
				String Language = context.getSuite().getXmlSuite().getAllParameters().get("Language");
	    		String browser = context.getSuite().getXmlSuite().getAllParameters().get("browserName");
	    			
	    		
	    		String numPassed = Integer.toString(context.getPassedTests().size());
	    		String numSkipped = Integer.toString(context.getSkippedTests().size());
	    		String numFailed = Integer.toString(context.getFailedTests().size());
	    		String numTests = Integer.toString(context.getPassedTests().size()+context.getSkippedTests().size()+context.getFailedTests().size());
	    		String totalExecutionTime = duration(context.getEndDate().getTime()-context.getStartDate().getTime());
	    	
	    		String replaceA = "<tr> <th>"+numTests+"</th> <th>"+numPassed+"</th> <th>"+numSkipped+"</th> <th>"+numFailed+"</th> <th>"+totalExecutionTime+"</th> "
	    				+ "<th>"+browser+"</th> </tr><!--##TestSummaryData##-->";
	    		htmlExecutionSummary = htmlExecutionSummary.replaceAll("<!--##TestSummaryData##-->", replaceA);
	    		
	    		String nameTC,Status,execTime,details,videoLink;
	    		for(ArrayList<String> str : detailTests) {
	    			nameTC = str.get(0)+"_"+str.get(1);
	    			Status = str.get(2);
	    			execTime = str.get(3);
	    			details = str.get(4).replaceAll("\\\\","/");
	    			String replaceB="";
	    			if(str.get(5)==null) {
	    				replaceB = "<tr> <td>"+nameTC+"</td> <td>"+QAOwner+"</td> <td>"+Language+"</td> <td>"+Status+"</td> <td>"+execTime+"</td> "
			    				+ "<td><b><a href="+details+">DETAIL REPORT</a></b></td> <td>VIDEO NOT OPTED</td> </tr><!--##ExecutionDynamicData##-->";
	    			}else {
	    				videoLink = str.get(5).replaceAll("\\\\","/");
	    				replaceB = "<tr> <td>"+nameTC+"</td> <td>"+QAOwner+"</td> <td>"+Language+"</td> <td>"+Status+"</td> <td>"+execTime+"</td> "
			    				+ "<td><b><a href="+details+">DETAIL REPORT</a></b></td> <td><b><a href="+videoLink+">VIDEO LINK</a></b></td> </tr><!--##ExecutionDynamicData##-->";
	    			}
	    			
	    			
	    			htmlExecutionSummary = htmlExecutionSummary.replaceAll("<!--##ExecutionDynamicData##-->",replaceB);
	    		}	
	    		logsIndexing();
	    		objWriter.append(htmlExecutionSummary);
	    		objWriter.flush();
	    	}catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    
	    public static String duration(long milliSeconds) {
			long millis = milliSeconds;
		    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
		            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
		            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		    return hms;
		}
	
	    public static void logsIndexing(){
	    	try {
	    		List<String> lines = new ArrayList<String>();
	    	    String line = "";
	    	    File objFile = null;
	    	    Reader fr = null;
	    	    BufferedReader br = null;
	    	    
	    	    Writer fw = null;
	    	    BufferedWriter out = null;
	    	    
	    	    File folder = new File(pathlogFolder);
	    	    File[] listOfFiles = folder.listFiles();
	    	    
	    	    for(int i=0;i<listOfFiles.length;i++) {
	    	    	objFile = new File(listOfFiles[i].getAbsolutePath());
	    	    	fr = new InputStreamReader(new FileInputStream(objFile),"UTF-8");
	    	    	br = new BufferedReader(fr);
	    	    	while ((line = br.readLine()) != null) {
	    	    		String[] tmp = null;
	    	    		String strTmp = "";
	    	            if (line.contains("<td></td>")) {
	    	            	tmp = line.split("<td></td>");
	    	            	strTmp = tmp[0];
	    	                for(int j=1;j<tmp.length;j++) {
	    	                	strTmp += "<td>"+j+"</td>"+tmp[j];
	    	                }
	    	            }
	    	                
	    	            lines.add(strTmp);
	    	        }
	    	    	fr.close();
	    	        br.close();
	    	        
	    	        fw = new OutputStreamWriter(new FileOutputStream(objFile),StandardCharsets.UTF_8);
	    	        out = new BufferedWriter(fw);
	    	        for(String s : lines)
	    	             out.write(s);
	    	        out.flush();
	    	        out.close();
	    	        lines.clear();
	    	    }
	    	}catch (Exception e) {
				e.printStackTrace();
			}
	    }
}

