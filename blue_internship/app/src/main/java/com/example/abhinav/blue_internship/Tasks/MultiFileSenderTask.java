package com.example.abhinav.blue_internship.Tasks;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.abhinav.blue_internship.MetaData;
import com.example.abhinav.blue_internship.MetaMetaData;
import com.example.abhinav.blue_internship.SocketHolder;
import com.example.abhinav.blue_internship.interfaces.TaskUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by ash on 21/2/18.
 * Used to perform sending file in background.
 * Assume Socket is established.
 * Make sure to instantiate only one instance of this class.
 */

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class MultiFileSenderTask extends AsyncTask {

    private static final String TAG = "===MultiFileSenderTask";
    OutputStream outputStream;
    BluetoothSocket bluetoothSocket;
    TaskUpdate taskUpdate;
    ArrayList<String> arrayListFormPaths;
    MetaMetaData metaMetaData;
    MetaData metaData;
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    public MultiFileSenderTask(TaskUpdate taskUpdate, ArrayList<String> arrayListFormPaths) {

    Log.d(MultiFileSenderTask.TAG,"Object created");
    this.taskUpdate = taskUpdate;
    this.arrayListFormPaths = arrayListFormPaths;
    this.metaMetaData = new MetaMetaData(arrayListFormPaths.size());
    this.bluetoothSocket = SocketHolder.getBluetoothSocket();
        try {
            this.outputStream = bluetoothSocket.getOutputStream();
            Log.d(MultiFileSenderTask.TAG,"obtained outputStream");

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(MultiFileSenderTask.TAG,"Failed to obtain outputStream");
            Log.d(MultiFileSenderTask.TAG,e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected Object doInBackground(Object[] objects) {
        taskUpdate.TaskStarted();
        if(!SocketHolder.getBluetoothSocket().isConnected()) {
            Log.d(MultiFileSenderTask.TAG,"Socket is closed... Can't perform file sending Task...");
            return null;
        }

        //Receive metaData
        try {

            ObjectOutputStream objectOutputStream;
            objectOutputStream = new ObjectOutputStream(outputStream);

            Log.d(MultiFileSenderTask.TAG,"Trying to send : " + metaMetaData.toString());
            objectOutputStream.writeObject(metaMetaData);

            Log.d(MultiFileSenderTask.TAG,"Number of files to send :" + metaMetaData.getNumberOfFiles());

            for(int i =0 ;i <metaMetaData.getNumberOfFiles();i++) {
                String filePath = arrayListFormPaths.get(i);
                Log.d(MultiFileSenderTask.TAG,"Trying to send file number :" + i);

                Log.d(MultiFileSenderTask.TAG,"obtain file object for : "+ filePath);
                File file = new File(filePath);

                if(!file.exists()) {
                    Log.d(MultiFileSenderTask.TAG,"File does not exist");
                    taskUpdate.TaskError("File does not exist : " + filePath);

                    return null ;
                } else {
                    Log.d(MultiFileSenderTask.TAG,"File exists");
                }


                metaData = new MetaData(file.length(),file.getName());
                Log.d(MultiFileSenderTask.TAG,"Trying to send : " + metaData.toString());
                objectOutputStream.writeObject(metaData);


                if(!SocketHolder.getBluetoothSocket().isConnected()) {
                    Log.d(MultiFileSenderTask.TAG,"Socket is closed... Can't perform file sending on stream...");
                    return null;
                }

                Log.d(MultiFileSenderTask.TAG,"fileInputStream open: "+ filePath);
                FileInputStream fileInputStream = new FileInputStream(file);

                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                int loop = 0;
                long totalRead = 0;
                long toRead = metaData.getDataSize();
                while ((bytesRead = fileInputStream.read(buffer)) > 0)
                {
                    loop++;
                    outputStream.write(buffer, 0, bytesRead);
                    totalRead = totalRead + bytesRead;
                    if(totalRead % 1024 == 0) {
                        taskUpdate.TaskProgressPublish(metaData.getFname() + String.valueOf((float)totalRead/toRead*100) + "%");
                    }
                }
                Log.d(MultiFileSenderTask.TAG,"Loop iterations run : " + loop);
                Log.d(MultiFileSenderTask.TAG,"trying to close the fileInputStream...");
                fileInputStream.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
            taskUpdate.TaskError(e.toString());
            Log.d(MultiFileSenderTask.TAG,e.toString());
        } catch (Exception e) {
            taskUpdate.TaskError(e.toString());
        }

        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        taskUpdate.TaskCompleted(this.getClass().getSimpleName()+": Completed");

        if(SocketHolder.getBluetoothSocket().isConnected()) {
            Log.d(MultiFileSenderTask.TAG,"BluetoothSocket is connected");

        } else {
            Log.d(MultiFileSenderTask.TAG,"BluetoothSocket is ****NOT*** connected");
        }

        Log.d(MultiFileSenderTask.TAG,"Task execution completed");

    }
}
