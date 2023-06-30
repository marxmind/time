package com.marxmind.bean;
//would like to thanks this site my project had been recovered : http://www.javadecompilers.com/
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.stream.FileImageOutputStream;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;

import com.marxmind.controller.AppSetting;
import com.marxmind.controller.Employee;
import com.marxmind.controller.TimeRecord;
import com.marxmind.enm.TimeAmPmType;
import com.marxmind.enm.TimeType;
import com.marxmind.serial.SerialPortReader;
import com.marxmind.utils.AppConf;
import com.marxmind.utils.DateUtils;
import com.marxmind.utils.GlobalVar;
import com.marxmind.utils.Numbers;
import com.marxmind.utils.PopupMessage;
import com.marxmind.utils.ReadConfig;
import com.marxmind.utils.TimeUtils;

import jakarta.annotation.PostConstruct;
import jakarta.faces.FacesException;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import lombok.Data;

@Data
@Named("main")
@ViewScoped
public class MainBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4565736454541L;
	private String fingerPrintData;
	private String fullName;
	private String message;
	private List<TimeRecord> timeRcs;
	private String morningTotalTime;
	private String afternoonTotalTime;
	private String renderedTotalTime;
	private String late;
	private String totalLateCost;
	private String filename;
	private List<TimeRecord> timeChecks;
	private String timeVal;
	
	
	
	public void checkTime() {
		PrimeFaces pf = PrimeFaces.current();
		timeChecks = new ArrayList<TimeRecord>();
		if(timeVal!=null && !timeVal.isEmpty()) {
			
			String sql = " AND tm.isactivetime=1 ";
			int day = DateUtils.getCurrentDay();
			int month = DateUtils.getCurrentMonth();
			int year = DateUtils.getCurrentYear();
			
			sql = " AND (emp.eid="+ timeVal + " OR cctsid='"+ timeVal +"' )";
			sql += " AND tm.monthrec=" + month;
			sql += " AND tm.dayrec=" + day;
			sql += " AND tm.yearrec=" + year;
			timeChecks = TimeRecord.retrieve(sql + " ORDER BY tm.tid", new String[0]);
			
			pf.executeScript("PF('panelCheck').show(1000);PF('panelWgr').hide(1000);");
		}
	}
	
	private String getRandomImageName() {
	        int i = (int) (Math.random() * 10000000);

	        return String.valueOf(i);
    }
	
	private void saveLocalImage(String fromFilePath, String fileName) {
			System.out.println("TIME_IMG:"+GlobalVar.TIME_IMAGE_FOLDER);
			File fileFolder = new File(GlobalVar.TIME_IMAGE_FOLDER);
			fileFolder.mkdir();
			File file = new File(fromFilePath);
			System.out.println("From file : " + fromFilePath);
			System.out.println("file to new save: " + fileName);
			 try{
	    			Files.copy(file.toPath(), (new File(GlobalVar.TIME_IMAGE_FOLDER + fileName)).toPath(),
	    			        StandardCopyOption.REPLACE_EXISTING);
	    			}catch(IOException e){e.printStackTrace();}
	}
	
	public void oncapture(CaptureEvent captureEvent) {
        filename = getRandomImageName();
        byte[] data = captureEvent.getData();
        //C:\workspace-june-2022\test\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\time\\resources\time\images\photocam\
        ExternalContext EXTERNAL_CONTEXT = FacesContext.getCurrentInstance().getExternalContext();
        String newFileName = EXTERNAL_CONTEXT.getRealPath("") + File.separator + "resources" + File.separator + "time"
                + File.separator + "images" + File.separator + "photocam" + File.separator + filename + ".jpeg";
        String newCopiedFile = EXTERNAL_CONTEXT.getRealPath("") + "resources" + File.separator + "time" + File.separator + "images" + File.separator + "photocam" + File.separator + filename + ".jpeg";
        System.out.println("EXTERNAL : " + newFileName);
        System.out.println("COPIED LOCATION: " + newCopiedFile);
        FileImageOutputStream imageOutput;
        try {
            imageOutput = new FileImageOutputStream(new File(newFileName));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            System.out.println("saving picture file to : " + newFileName);
            saveLocalImage(newCopiedFile,filename + ".jpeg");
        }
        catch (IOException e) {
            throw new FacesException("Error in writing captured image.", e);
        }
    }
	
	@PostConstruct
	public void init() {
		String val = ReadConfig.value(AppConf.SERVER_LOCAL);
        HttpSession session = SessionBean.getSession();
		session.setAttribute("server-local", val);
		setMessage("Your name will display here...");
		
	}
	
	/*
	 * private void load() { long device = 0; device =
	 * FingerprintSensorEx.OpenDevice(0);
	 * System.out.println("check devide if open... : " + device); }
	 */
	
	private String keyPress="subId";
	
	public String convertTime(String val) {
		return TimeUtils.time12Format(val);
	}
	
	public void submitTime() {
		
		//clear();
		
		System.out.println("submitting....");
		System.out.println("Checking image : " + filename);
		String sql = "";
		filename += ".jpeg";
		String timeNow = TimeUtils.getTime24FormatPlain();
		
		boolean isExist = TimeRecord.checkiFDoubleEntry(getFingerPrintData(), timeNow);
		
		if(getFingerPrintData()!=null && !getFingerPrintData().isEmpty() && !isExist) {
			timeRcs = new ArrayList<TimeRecord>();
			sql = " AND (eid="+getFingerPrintData() +" OR cctsid='"+ getFingerPrintData() +"' ) ";
			List<Employee> emps = Employee.retrieve(sql, new String[0]);
			System.out.println("size: " + emps.size());
			if(emps!=null && emps.size()>0) {
				Employee e = emps.get(0);
				setMessage("Hi " + e.getFirstName() + " you are successfully recorded your time.");
				setFingerPrintData(null);
				setFullName(e.getFullName());
				int day = DateUtils.getCurrentDay();
				int month = DateUtils.getCurrentMonth();
				int year = DateUtils.getCurrentYear();
				
				sql = " AND emp.eid="+ e.getId();
				sql += " AND tm.dayrec="+day;
				sql += " AND tm.monthrec=" + month;
				sql += " AND tm.yearrec=" + year;
				List<TimeRecord> times = TimeRecord.retrieve(sql + " ORDER BY tm.tid", new String[0]);
				
				if(times!=null && times.size()>0) {
					boolean isForUpdate=false;
					int size = times.size();
					if(size==4) {isForUpdate=true;}
					
					TimeRecord tmpTime = times.get(size-1);
					int timeInOut = tmpTime.getTimeInOutType();
					System.out.println("Checking : " + timeInOut);
					if(timeInOut<3) {
						timeInOut+=1;
					}
					System.out.println("After Checking : " + timeInOut);
				TimeRecord data = TimeRecord.builder()
						.timeInOutType(timeInOut)
						.day(day)
						.month(month)
						.year(year)
						.type(tmpTime.getType())
						.timeRecord(timeNow)
						.employee(e)
						.isActive(1)
						.photoid(filename)
						.build();
				
				if(isForUpdate) {
				 data = tmpTime;
				 data.setPhotoid(filename);
				 data.setTimeRecord(timeNow);
				 data = TimeRecord.save(data);
				 times.get(3).setTimeRecord(timeNow);
				 timeRcs.addAll(times);
				}else {
					data = TimeRecord.save(data);
					timeRcs.addAll(times);
					data.setTypeName(TimeType.nameId(data.getType()));
					data.setInOutTypeName(TimeAmPmType.nameId(data.getTimeInOutType()));
					timeRcs.add(data);
				}
				
				
				
				}else {
					
					double hh = Integer.valueOf(timeNow.split(":")[0]);
					if(hh>12 && hh<14) {
						
						TimeRecord data = TimeRecord.builder()
								.timeInOutType(TimeAmPmType.AM_IN.getId())
								.day(day)
								.month(month)
								.year(year)
								.type(TimeType.REGULAR.getId())
								.timeRecord("12:00")
								.employee(e)
								.isActive(1)
								.remarks("No Morning")
								.photoid(filename)
								.build();
						
						data = TimeRecord.save(data);
						data.setTypeName(TimeType.nameId(data.getType()));
						data.setInOutTypeName(TimeAmPmType.nameId(data.getTimeInOutType()));
						timeRcs.add(data);
						
							data = TimeRecord.builder()
								.timeInOutType(TimeAmPmType.AM_OUT.getId())
								.day(day)
								.month(month)
								.year(year)
								.type(TimeType.REGULAR.getId())
								.timeRecord("12:00")
								.employee(e)
								.isActive(1)
								.remarks("No Morning")
								.photoid(filename)
								.build();
						
						data = TimeRecord.save(data);
						data.setTypeName(TimeType.nameId(data.getType()));
						data.setInOutTypeName(TimeAmPmType.nameId(data.getTimeInOutType()));
						timeRcs.add(data);
						
						data = TimeRecord.builder()
								.timeInOutType(TimeAmPmType.PM_IN.getId())
								.day(day)
								.month(month)
								.year(year)
								.type(TimeType.REGULAR.getId())
								.timeRecord(timeNow)
								.employee(e)
								.isActive(1)
								.photoid(filename)
								.build();
						
						data = TimeRecord.save(data);
						data.setTypeName(TimeType.nameId(data.getType()));
						data.setInOutTypeName(TimeAmPmType.nameId(data.getTimeInOutType()));
						timeRcs.add(data);
						
					}else if(hh>17) {
						
						TimeRecord data = TimeRecord.builder()
								.timeInOutType(TimeAmPmType.AM_IN.getId())
								.day(day)
								.month(month)
								.year(year)
								.type(TimeType.REGULAR.getId())
								.timeRecord("12:00")
								.employee(e)
								.isActive(1)
								.remarks("No Morning")
								.photoid(filename)
								.build();
						
						data = TimeRecord.save(data);
						data.setTypeName(TimeType.nameId(data.getType()));
						data.setInOutTypeName(TimeAmPmType.nameId(data.getTimeInOutType()));
						timeRcs.add(data);
						
							data = TimeRecord.builder()
								.timeInOutType(TimeAmPmType.AM_OUT.getId())
								.day(day)
								.month(month)
								.year(year)
								.type(TimeType.REGULAR.getId())
								.timeRecord("12:00")
								.employee(e)
								.isActive(1)
								.remarks("No Morning")
								.photoid(filename)
								.build();
						
						data = TimeRecord.save(data);
						data.setTypeName(TimeType.nameId(data.getType()));
						data.setInOutTypeName(TimeAmPmType.nameId(data.getTimeInOutType()));
						timeRcs.add(data);
						
						data = TimeRecord.builder()
								.timeInOutType(TimeAmPmType.PM_IN.getId())
								.day(day)
								.month(month)
								.year(year)
								.type(TimeType.REGULAR.getId())
								.timeRecord("17:00")
								.employee(e)
								.isActive(1)
								.remarks("No Afternoon")
								.photoid(filename)
								.build();
						
						data = TimeRecord.save(data);
						data.setTypeName(TimeType.nameId(data.getType()));
						data.setInOutTypeName(TimeAmPmType.nameId(data.getTimeInOutType()));
						timeRcs.add(data);
						
						data = TimeRecord.builder()
								.timeInOutType(TimeAmPmType.PM_OUT.getId())
								.day(day)
								.month(month)
								.year(year)
								.type(TimeType.REGULAR.getId())
								.timeRecord(timeNow)
								.employee(e)
								.isActive(1)
								.remarks("No Afternoon")
								.photoid(filename)
								.build();
						
						data = TimeRecord.save(data);
						data.setTypeName(TimeType.nameId(data.getType()));
						data.setInOutTypeName(TimeAmPmType.nameId(data.getTimeInOutType()));
						timeRcs.add(data);
						
						
					}else {
						TimeRecord data = TimeRecord.builder()
								.timeInOutType(TimeAmPmType.AM_IN.getId())
								.day(day)
								.month(month)
								.year(year)
								.type(TimeType.REGULAR.getId())
								.timeRecord(timeNow)
								.employee(e)
								.isActive(1)
								.photoid(filename)
								.build();
						
						data = TimeRecord.save(data);
						data.setTypeName(TimeType.nameId(data.getType()));
						data.setInOutTypeName(TimeAmPmType.nameId(data.getTimeInOutType()));
						timeRcs.add(data);
					}
					
					
					
				}
				calcTime(timeRcs);
				//PrimeFaces pf = PrimeFaces.current();
				//pf.executeScript("PF('panelWgr').show(1000);PF('idTime').focus();PF('panelCheck').hide(1000)");
				
			}else {
				PopupMessage.addMessage(3,"Error", "Finger print scanner cannot recognize your finger print.");
				//PrimeFaces pf = PrimeFaces.current();
				//pf.executeScript("PF('panelWgr').hide(1000);PF('panelCheck').hide(1000)");
				setFingerPrintData(null);
			}
		}else {
			//PrimeFaces pf = PrimeFaces.current();
			if(isExist) {
				PopupMessage.addMessage(2,"Warning Double input time. This time: " + timeNow +" is already recorded.", "Double input time.");
			}else {
				PopupMessage.addMessage(2,"Warning", "No Data to retrieve.");
			}
			//pf.executeScript("PF('panelWgr').hide(1000);PF('panelCheck').hide(1000)");
			setFingerPrintData(null);
		}
		
		//clear();	
		//PrimeFaces pf = PrimeFaces.current();
		//pf.executeScript("PF('dlg').show(1000)");
	}
	
	private void clear() {
		setMorningTotalTime(null);
		setAfternoonTotalTime(null);
		setRenderedTotalTime(null);
		setLate(null);
		setTotalLateCost(null);
		setFilename(null);
	}
	
	private void calcTime(List<TimeRecord> times) {
		int size = times.size();
		setMorningTotalTime("");
		setAfternoonTotalTime("");
		setRenderedTotalTime("");
		
		List<AppSetting> apps = AppSetting.retrieveTime("TIME-RECORD");
		double timeInAM = Integer.valueOf(apps.get(0).getValue().split(":")[0]);
		double timeOutAM = Integer.valueOf(apps.get(1).getValue().split(":")[0]);
		
		double timeInPM = Integer.valueOf(apps.get(2).getValue().split(":")[0]);
		double timeOutPM = Integer.valueOf(apps.get(3).getValue().split(":")[0]);
		double lateCharge = Double.valueOf(apps.get(4).getValue());
		
		if(size==1) {
			String[] time1 = times.get(0).getTimeRecord().split(":");
			double hh1 = Integer.valueOf(time1[0]);
			double mm1 = Integer.valueOf(time1[1]);
			double late = 0d;
			double start = hh1 + (mm1/60);
			if(start<=timeInAM) {
				start = timeInAM;
			}else {//late calculation
				late = start-timeInAM;
				setLate(Numbers.roundOf(late, 2)+"");
				late = late*60;
				late *= lateCharge;
				setTotalLateCost(Numbers.roundOf(late, 2)+"");
			}
			
			
		}else if(size==2 || size==3) {
			String[] time1 = times.get(0).getTimeRecord().split(":");
			double hh1 = Integer.valueOf(time1[0]);
			double mm1 = Integer.valueOf(time1[1]);
			
			String[] time2 = times.get(1).getTimeRecord().split(":");
			double hh2 = Integer.valueOf(time2[0]);
			double mm2 = Integer.valueOf(time2[1]);
			
			double start = hh1 + (mm1/60);
			double late = 0d;
			if(start<=timeInAM) {
				start = timeInAM;
			}else {//get late
				late = start-timeInAM;
				setLate(Numbers.roundOf(late, 2)+"");
				late = late*60;
				late *= lateCharge;
				setTotalLateCost(Numbers.roundOf(late, 2)+"");
			}
			
			double end = hh2 + (mm2/60);
			if(end>=timeOutAM) {
				end = timeOutAM;
			}
			
			double total = end - start;
			if(total<0) {total=0;}
			setMorningTotalTime(Numbers.roundOf(total,2)+"");
			
			
			
			
		}else if(size==4) {
			
			String[] time1 = times.get(0).getTimeRecord().split(":");
			double hh1 = Integer.valueOf(time1[0]);
			double mm1 = Integer.valueOf(time1[1]);
			
			String[] time2 = times.get(1).getTimeRecord().split(":");
			double hh2 = Integer.valueOf(time2[0]);
			double mm2 = Integer.valueOf(time2[1]);
			
			String[] time3 = times.get(2).getTimeRecord().split(":");
			double hh3 = Integer.valueOf(time3[0]);
			double mm3 = Integer.valueOf(time3[1]);
			
			String[] time4 = times.get(3).getTimeRecord().split(":");
			double hh4 = Integer.valueOf(time4[0]);
			double mm4 = Integer.valueOf(time4[1]);
			
			double startM = hh1 + (mm1/60);
			double late = 0d;
			if(startM<=timeInAM) {
				startM=timeInAM;
			}else {//get late calculation
				late = startM-timeInAM;
				System.out.println("late: " + late);
				setLate(Numbers.roundOf(late, 2)+"");
				late = late*60;
				late *= lateCharge;
				setTotalLateCost(Numbers.roundOf(late, 2)+"");
			}
			double endM = hh2 + (mm2/60);
			if(endM>=timeOutAM) {
				endM=timeOutAM;
			}
			double totalM = endM - startM;
			if(totalM<0) {totalM=0;}
			setMorningTotalTime(Numbers.roundOf(totalM,2)+"");
			
			double startP = hh3 + (mm3/60);
			if(startP<=timeInPM) {
				startP = timeInPM;
			}
			double endP = hh4 + (mm4/60);
			if(endP>=timeOutPM) {
				endP=timeOutPM;
			}
			double totalP = endP - startP;
			if(totalP<0) {totalP=0;}
			setAfternoonTotalTime(Numbers.roundOf(totalP,2)+"");
			
			double totalRecorded = totalM + totalP;
			setRenderedTotalTime(Numbers.roundOf(totalRecorded,2)+"");
			
		}
		
	}
	
}
