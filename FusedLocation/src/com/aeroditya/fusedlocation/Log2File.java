package com.aeroditya.fusedlocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import android.os.Environment;
import android.util.Log;

public class Log2File {
        private static File file;

        /**
         * Init with default settings. Log file will be named "log_file.txt" and will be stored in directory named "Log2File" in root of SDCard.
         */
        public static void init() {
                File sdCard = Environment.getRootDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/Log2File/");
                dir.mkdirs();
                file = new File(dir, "log_file.txt");
        }
        
        public static void init(String fileName) {
                File sdCard = Environment.getRootDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/Log2File/");
                dir.mkdirs();
                file = new File(dir, fileName);
        }
        public static void init(String directory, String fileName) {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/"+directory+"/");
                //File dir = new File(directory + "/");
                dir.mkdirs();
                file = new File(dir, fileName);
        }
        
        
        private static void writeStringToAFile(String tag, final String data, File file) {
                long time = System.currentTimeMillis();
                SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");

                BufferedWriter out=null;
                try {
                	Log.d("Log2File",file.getAbsolutePath());
                    file.createNewFile();

                        out = new BufferedWriter(new FileWriter(file,true));
                        
                        String lineToWriteToFile = formater.format(time) + "," + data+"\n\r";
                        
                        out.write(lineToWriteToFile);
                        
                        //Log.d(tag,data);
                        
                } catch (IOException e) {
                        e.printStackTrace();
                }finally{
                        try {
                            	 out.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
        }
        
        public static void log(String data){
                writeStringToAFile(file.getName(), data, file);
        }
        public static void log(String tag, String data){
                writeStringToAFile(tag, data, file);
        }
}