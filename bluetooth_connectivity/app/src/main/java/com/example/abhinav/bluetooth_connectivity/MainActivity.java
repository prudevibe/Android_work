package com.example.abhinav.bluetooth_connectivity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        Thread2 t = new Thread2();
        t.start();
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            textView.setText(String.valueOf(msg.arg1));
            return false;
        }
    });
    private  class Thread2 extends Thread
    {
        public void run()
        {
            for(int i=0;i<50;i++)
            {
                Message message = Message.obtain();
                message.arg1 = i;
                handler.sendMessage(message);

                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
