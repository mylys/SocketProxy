package com.sand;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
	private String DirName;
	private String App;
	private String FileName;
	private String ErrLog;
	private boolean err = false;
	private int ilevel = 5;
	public LogUtil(){
		  SimpleDateFormat format  =  new SimpleDateFormat("yyyyMMdd"); 
	      String sysDate = format.format(new Date(System.currentTimeMillis())); 
	      FileName = "debug_"+ sysDate+".log";
	      App="debug";
	      ErrLog="err_"+sysDate+".log";
	}
	
	public LogUtil(String app){
		SimpleDateFormat format  =  new SimpleDateFormat("yyyyMMdd"); 
	    String sysDate = format.format(new Date(System.currentTimeMillis())); 
		FileName = app+"_"+sysDate+".log";
		App = app;
		ErrLog="err_"+sysDate+".log";
	}

	public String getDirName() {
		return DirName;
	}

	public void setDirName(String dirName) {
		DirName = dirName;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}
	
	
	 public int getIlevel() {
		return ilevel;
	}

	public void setIlevel(int ilevel) {
		this.ilevel = ilevel;
	}

	public String getErrLog() {
		return ErrLog;
	}

	public void setErrLog(String errLog) {
		ErrLog = errLog;
	}

	/**
     * �½�Ŀ¼
     * @param folderPath String �� c:/fqf
     * @return boolean
     */
   public void newFolder(String folderPath) {
       try {
           String filePath = folderPath;
           filePath = filePath.toString();
          File myFilePath = new File(filePath);
           if (!myFilePath.exists()) {
               myFilePath.mkdir();
           }
       }
       catch (Exception e) {
           System.out.println("�½�Ŀ¼��������");
           e.printStackTrace();
       }
   }

   
   /**
    * �½��ļ�
    * @param filePathAndName String �ļ�·������� ��c:/fqf.txt
    * @param fileContent String �ļ�����
    * @return boolean
    */
  public void newFile(String filePathAndName, String fileContent) {
      try {
          String filePath = filePathAndName;
          filePath = filePath.toString();
          File myFilePath = new File(filePath);
          if (!myFilePath.exists()) {
              myFilePath.createNewFile();
          }
          FileWriter resultFile = new FileWriter(myFilePath);
          PrintWriter myFile = new PrintWriter(resultFile);
          String strContent = fileContent;
          myFile.println(strContent);
          resultFile.close();

      }
      catch (Exception e) {
          System.out.println("�½�Ŀ¼��������");
          e.printStackTrace();

      }

  }

  /**
    * ɾ���ļ�
    * @param filePathAndName String �ļ�·������� ��c:/fqf.txt
    * @param fileContent String
    * @return boolean
    */
  public void delFile(String filePathAndName) {
      try {
          String filePath = filePathAndName;
          filePath = filePath.toString();
          java.io.File myDelFile = new java.io.File(filePath);
          myDelFile.delete();

      }
      catch (Exception e) {
          System.out.println("ɾ���ļ���������");
          e.printStackTrace();

      }

  }

  
  /**
   * ɾ���ļ�������������ļ�
   * @param path String �ļ���·�� �� c:/fqf
   */
 public void delAllFile(String path) {
     File file = new File(path);
     if (!file.exists()) {
         return;
     }
     if (!file.isDirectory()) {
         return;
     }
     String[] tempList = file.list();
     File temp = null;
     for (int i = 0; i < tempList.length; i++) {
         if (path.endsWith(File.separator)) {
             temp = new File(path + tempList[i]);
         }
         else {
             temp = new File(path + File.separator + tempList[i]);
         }
         if (temp.isFile()) {
             temp.delete();
         }
         if (temp.isDirectory()) {
             delAllFile(path+"/"+ tempList[i]);//��ɾ���ļ���������ļ�
             delFolder(path+"/"+ tempList[i]);//��ɾ����ļ���
         }
     }
 }


  /**
    * ɾ���ļ���
    * @param filePathAndName String �ļ���·������� ��c:/fqf
    * @param fileContent String
    * @return boolean
    */
  public void delFolder(String folderPath) {
      try {
          delAllFile(folderPath); //ɾ����������������
          String filePath = folderPath;
          filePath = filePath.toString();
          java.io.File myFilePath = new java.io.File(filePath);
          myFilePath.delete(); //ɾ����ļ���

      }
      catch (Exception e) {
          System.out.println("ɾ���ļ��в�������");
          e.printStackTrace();

      }

  }



	
	 /**
     * ���Ƶ����ļ�
     * @param oldPath String ԭ�ļ�·�� �磺c:/fqf.txt
     * @param newPath String ���ƺ�·�� �磺f:/fqf.txt
     * @return boolean
     */
   public void copyFile(String oldPath, String newPath) {
       try {
           int bytesum = 0;
           int byteread = 0;
           File oldfile = new File(oldPath);
           if (oldfile.exists()) { //�ļ�����ʱ
               InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ�
               FileOutputStream fs = new FileOutputStream(newPath);
               byte[] buffer = new byte[1444];
               while ( (byteread = inStream.read(buffer)) != -1) {
                   bytesum += byteread; //�ֽ��� �ļ���С
                   System.out.println(bytesum);
                   fs.write(buffer, 0, byteread);
               }
               inStream.close();
           }
       }
       catch (Exception e) {
           System.out.println("���Ƶ����ļ���������");
           e.printStackTrace();
       }
   }

   /**
     * ��������ļ�������
     * @param oldPath String ԭ�ļ�·�� �磺c:/fqf
     * @param newPath String ���ƺ�·�� �磺f:/fqf/ff
     * @return boolean
     */
   public void copyFolder(String oldPath, String newPath) {

       try {
           (new File(newPath)).mkdirs(); //����ļ��в����� �������ļ���
           File a=new File(oldPath);
           String[] file=a.list();
           File temp=null;
           for (int i = 0; i < file.length; i++) {
               if(oldPath.endsWith(File.separator)){
                   temp=new File(oldPath+file[i]);
               }
               else{
                   temp=new File(oldPath+File.separator+file[i]);
               }
               if(temp.isFile()){
                   FileInputStream input = new FileInputStream(temp);
                   FileOutputStream output = new FileOutputStream(newPath + "/" +
                           (temp.getName()).toString());
                   byte[] b = new byte[1024 * 5];
                   int len;
                   while ( (len = input.read(b)) != -1) {
                       output.write(b, 0, len);
                   }
                   output.flush();
                   output.close();
                   input.close();
               }
               if(temp.isDirectory()){//��������ļ���
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
               }
           }
       }
       catch (Exception e) {
           System.out.println("��������ļ������ݲ�������");
           e.printStackTrace();
       }
   }

   /** 
   * ���ֽ�Ϊ��λ��ȡ�ļ��������ڶ��������ļ�����ͼƬ��������Ӱ����ļ��� 
   * @param fileName �ļ����� 
   */ 
   public  void readFileByBytes(String fileName){ 
	   File file = new File(fileName); 
	   InputStream in = null; 
	   try { 
		   System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�"); 
		   in = new FileInputStream(file); 
		   int tempbyte; 
		   while((tempbyte=in.read()) != -1){ 
			   System.out.write(tempbyte); 
		   } 
		   in.close(); 
	   } catch (IOException e) { 
		   e.printStackTrace(); 
		   return; 
	   } 
	   try { 
		   System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�"); 
		   byte[] tempbytes = new byte[100]; 
		   int byteread = 0; 
		   in = new FileInputStream(fileName); 
//		   ReadFromFile.showAvailableBytes(in); 
		   //�������ֽڵ��ֽ������У�bytereadΪһ�ζ�����ֽ��� 
		   while ((byteread = in.read(tempbytes)) != -1){ 
			   System.out.write(tempbytes, 0, byteread); 
		   } 
	   } catch (Exception e1) { 
		   e1.printStackTrace(); 
	   } finally { 
		   if (in != null){ 
			   try { 
				   in.close(); 
			   } catch (IOException e1) { 
			   } 
		   } 
	   } 
   } 
   
   
   /** 
   * ���ַ�Ϊ��λ��ȡ�ļ��������ڶ��ı������ֵ����͵��ļ� 
   * @param fileName �ļ��� 
   */ 
   public void readFileByChars(String fileName){ 
   File file = new File(fileName); 
   Reader reader = null; 
   try { 
	   System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");   // һ�ζ�һ���ַ� 
	   reader = new InputStreamReader(new FileInputStream(file)); 
	   int tempchar; 
	   while ((tempchar = reader.read()) != -1){ 
		   //����windows�£�rn�������ַ���һ��ʱ����ʾһ�����С� 
		   //������������ַ�ֿ���ʾʱ���ỻ�����С� 
		   //��ˣ����ε�r����������n�����򣬽������ܶ���С� 
		   if (((char)tempchar) != 'r'){ 
			   System.out.print((char)tempchar); 
		   } 
	   } 
	   reader.close(); 
   	} catch (Exception e) { 
   		e.printStackTrace(); 
   	} 
   try { 
	   System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�"); //һ�ζ�����ַ� 
	   char[] tempchars = new char[30]; 
	   int charread = 0; 
	   reader = new InputStreamReader(new FileInputStream(fileName));    //�������ַ��ַ������У�charreadΪһ�ζ�ȡ�ַ��� 
	   while ((charread = reader.read(tempchars))!=-1){    //ͬ�����ε�r����ʾ 
		   if ((charread == tempchars.length)&&(tempchars[tempchars.length-1] != 'r')){ 
			   System.out.print(tempchars); 
		   }else{ 
			   for (int i=0; i<charread; i++){ 
				   if(tempchars[i] == 'r'){ 
					   continue; 
				   }else{ 
					   System.out.print(tempchars[i]); 
				   } 
			   } 
		   } 
	   } 
   } catch (Exception e1) { 
	   e1.printStackTrace(); 
   }finally { 
   if (reader != null){ 
	   try { 
		   		reader.close(); 
	   			} catch (IOException e1) { 
	   				}
   			} 
   		} 
   }
	
   public void i(String data)
   {
	   SimpleDateFormat format  =  new SimpleDateFormat("yyyyMMdd-HH:mm:dd"); 
	   String logtime = format.format(new Date(System.currentTimeMillis())); 
	   String info ="[" +App+"]["+logtime+"] "+data+"\n";
	   writeline(info);
   }
   
   public void e(String data)
   {
	   err = true;
	   i(data);
   }
   
   public void i(String head ,byte[] data)
   {
	   int row,count,i;
	   SimpleDateFormat format  =  new SimpleDateFormat("yyyyMMdd-HH:mm:ss"); 
	   String logtime = format.format(new Date(System.currentTimeMillis())); 
	   String info ="";
	   if(head!=null)info+="[" +App+"]["+logtime+"] "+head+"\n";
	   if(data==null){
		   info+="[" +App+"]["+logtime+"] ( NULL ) \n";
	   }else{
		   row = data.length/16;
		   count = data.length%16;
		   for(i=0;i<row;i++)  info+="[" +App+"]["+logtime+"] "+ByteUtil.toHexInfo(ByteUtil.sub(data, i*16, 16))+"\n";
		   info+="[" +App+"]["+logtime+"] "+ByteUtil.toHexInfo(ByteUtil.sub(data, i*16, count))+"\n";
	   }
	   writeline(info);
   }
   
   public void e(String head ,byte[] data)
   {
	   err = true;
	   i( head, data);
   }
   
   public void i(String head ,byte[] data,int level)
   {
	   int row,count,i;
	   if(ilevel>level){
	   SimpleDateFormat format  =  new SimpleDateFormat("yyyyMMdd-HH:mm:ss"); 
	   String logtime = format.format(new Date(System.currentTimeMillis())); 
	   String info ="";
	   if(data==null){
		   info+="[" +App+"]["+logtime+"] ( NULL ) \n";
	   }else{
		   info+="[" +App+"]["+logtime+"] "+head+" \n";
		   row = data.length/16;
		   count = data.length%16;
		   for(i=0;i<row;i++)  info+="[" +App+"]["+logtime+"] "+ByteUtil.toHexInfo(ByteUtil.sub(data, i*16, 16))+"\n";
		   info+="[" +App+"]["+logtime+"] "+ByteUtil.toHexInfo(ByteUtil.sub(data, i*16, count))+"\n";
	   }
	   writeline(info);
	   }
   }
   
   public void e(String head ,byte[] data,int level)
   {
	   err = true;
	   i(head, data, level);
   }
   
   public void i(String data,int level)
   {
	   if(ilevel>level){
		   SimpleDateFormat format  =  new SimpleDateFormat("yyyyMMdd-HH:mm:ss"); 
		   String logtime = format.format(new Date(System.currentTimeMillis())); 
		   String info = "[" +App+"]["+logtime+"] "+data+"\n";
		   writeline(info);
	   }
	   
   }
   
   public void e(String data,int level)
   {
	   err = true;
	   i( data, level);
   }
   
   private void getLogFile()
   {
	   SimpleDateFormat format  =  new SimpleDateFormat("yyyyMMdd"); 
	   String logtime = format.format(new Date(System.currentTimeMillis())); 
	   FileName = App+"_"+logtime+".log";
	   ErrLog="err_"+logtime+".log";
	   if(DirName!=null){
		   newFolder(DirName);
		   FileName = DirName+"//"+FileName;
		   ErrLog=DirName+"//"+ErrLog;
		   }
	   return;
   }
   
   public void writeline(String info)
   {
	   FileWriter fw = null;
	   try {
		getLogFile();
		fw = new FileWriter(FileName,true);
		fw.write(info);
		fw.flush();
		if(err){
			err = false;
			fw = new FileWriter(ErrLog,true);
			fw.write(info);
			fw.flush();
		}
	} catch (IOException e) {
		e.printStackTrace();
	}finally{
		if(fw!=null)
		{
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	   
   }
   
   
}
