package com.example.abhinav.blue_internship.Tasks;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.abhinav.blue_internship.MetaData;
import com.example.abhinav.blue_internship.SocketHolder;
import com.example.abhinav.blue_internship.interfaces.TaskUpdate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;

/**
 * Created by ash on 21/2/18.
 * Make sure to instantiate only one instance of this class.
 * Assume Socket connection is established over bluetooth.
 */

public class FileReceiverTask extends AsyncTask {

    private static final String TAG = "===FileReceiverTask";

    InputStream inputStream;
    TaskUpdate taskUpdate;
    MetaData metaData;
    //String filePath;
    public FileReceiverTask(TaskUpdate taskUpdate) {
        Log.d(FileReceiverTask.TAG,"Object created");
        this.taskUpdate = taskUpdate;
        try {

            Log.d(FileReceiverTask.TAG,"trying to get inputStream");
            inputStream = SocketHolder.getBluetoothSocket().getInputStream();
            Log.d(FileReceiverTask.TAG,"obtained inputStream");

        } catch (IOException e) {
            Log.d(FileReceiverTask.TAG,"Failed to obtain input stream");
            Log.d(FileReceiverTask.TAG,e.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected Object doInBackground(Object[] objects) {
        taskUpdate.TaskStarted();
        if(!SocketHolder.getBluetoothSocket().isConnected()) {
            Log.d(FileReceiverTask.TAG,"Socket is closed... Can't perform file receiving Task...");
            return null;
        }

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            metaData = (MetaData)objectInputStream.readObject();
            Log.d(FileReceiverTask.TAG," Variable value received : " + metaData.toString() );
            //objectInputStream.close();

            String receivePath = String.valueOf(Environment.getExternalStorageDirectory()) +"/"+ metaData.getFname();

            File file = new File(receivePath);
            if(!file.exists()) {
                Log.d(FileReceiverTask.TAG, "File does not exist : " + receivePath);
            }  else {
                Log.d(FileReceiverTask.TAG,"File already exists : " + receivePath);
            }

            OutputStream outputStreamWriteToFile = new FileOutputStream(file);
            Log.d(FileReceiverTask.TAG,"outputStreamWriteToFile created");

            byte[] buffer = new byte[1024];
            int read;
            long totalRead = 0;
            long toRead = metaData.getDataSize();
            int loop = 0;
            if(! SocketHolder.getBluetoothSocket().isConnected()) {
                Log.d(FileReceiverTask.TAG,"Socket is closed... Can't perform file receiving from stream...");
                return null;
            }
            while ((read = inputStream.read(buffer)) != -1) {

                totalRead = totalRead + read;
                outputStreamWriteToFile.write(buffer,0,read);
                loop++;
                //Log.d(FileReceiverTask.TAG,"loop iterations : " + loop + " bytes read: " + totalRead);
                if(totalRead % 1024 == 0) {
                    taskUpdate.TaskProgressPublish(metaData.getFname() + String.valueOf((float)totalRead/toRead*100) + "%");
                }

                //This the most important part...
                if(totalRead == metaData.getDataSize()) {
                    Log.d(FileReceiverTask.TAG,"breaking from loop");
                    break;
                }
            }

            Log.d(FileReceiverTask.TAG,"loop iterations : " + loop + " bytes read: " + totalRead);
            Log.d(FileReceiverTask.TAG,"outputStreamWriteToFile closing");
            outputStreamWriteToFile.close();
            Log.d(FileReceiverTask.TAG,"outputStreamWriteToFile closed");

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(FileReceiverTask.TAG,e.toString() );
            taskUpdate.TaskError(e.toString());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d(FileReceiverTask.TAG,e.toString() );
            taskUpdate.TaskError(e.toString());
        }

        return null;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        taskUpdate.TaskCompleted(metaData.getFname());
        if(SocketHolder.getBluetoothSocket().isConnected()) {
            Log.d(FileReceiverTask.TAG,"BluetoothSocket is connected");

        } else {
            Log.d(FileReceiverTask.TAG,"BluetoothSocket is ****NOT*** connected");
        }
        Log.d(FileReceiverTask.TAG,"Task execution completed");

    }
}
