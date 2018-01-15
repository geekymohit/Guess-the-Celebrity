package com.example.mohitagarwal.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {



    ArrayList<String> celebUrls=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();
    int choosenceleb=0;
    int locationofCorrectAnswers=0;
    String[] answers = new String[4];

    ImageView imageView;
    Button button;
    Button button2;
    Button button3;
    Button button4;


    public void celebChosen(View view){

        if (view.getTag().toString().equals(Integer.toString(locationofCorrectAnswers))){

            Toast.makeText(getApplicationContext(),"Correct",Toast.LENGTH_LONG).show();
        }else{

            Toast.makeText(getApplicationContext(),"Incorrect ! It was "+celebNames.get(choosenceleb),Toast.LENGTH_LONG).show();
        }
        generateQuestion();

    }

    public class downloadimage extends AsyncTask<String,Void,Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {

            try{

                URL url=new URL(urls[0]);
                HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream inputStream=urlConnection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                return bitmap;


            }catch (MalformedURLException e){

                e.printStackTrace();
            }
            catch (IOException e){

                e.printStackTrace();
            }


            return null;
        }
    }


    public class downloadtask extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{

                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream inputStream=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1){

                    char current=(char) data;
                    result +=current;
                    data=reader.read();
                }


            }catch (Exception e){

                e.printStackTrace();



            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView)findViewById(R.id.imageView);
        button=(Button)findViewById(R.id.button);
        button2=(Button)findViewById(R.id.button2);
        button3=(Button)findViewById(R.id.button3);
        button4=(Button)findViewById(R.id.button4);
        downloadtask task=new downloadtask();
        String result= null;


        try {

            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult=result.split("<div class=\"sidebarContainer\">");
            Pattern p=Pattern.compile("<img src=\"(.*?)\"");
            Matcher m =p.matcher(splitResult[0]);
            while (m.find()){

                celebUrls.add(m.group(1));
            }
            p=Pattern.compile("alt=\"(.*?)\"");
            m =p.matcher(splitResult[0]);
            while (m.find()){

                celebNames.add(m.group(1));
            }

        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        catch (ExecutionException e) {

            e.printStackTrace();

        }
        generateQuestion();
    }
    public void generateQuestion(){

        Random random=new Random();
        choosenceleb=random.nextInt(celebUrls.size());

        downloadimage imageTask=new downloadimage();
        Bitmap celebImage;
        try {

            celebImage=imageTask.execute(celebUrls.get(choosenceleb)).get();
            imageView.setImageBitmap(celebImage);
            int incorrectanswersLocation;
            locationofCorrectAnswers=random.nextInt(4);
            for (int i=0;i<4;i++)
            {
                if(i==locationofCorrectAnswers ){

                    answers[i]=celebNames.get(choosenceleb);
                }else {

                    incorrectanswersLocation=random.nextInt(celebUrls.size());
                    while (incorrectanswersLocation==choosenceleb){

                        incorrectanswersLocation=random.nextInt(celebUrls.size());
                    }
                    answers[i]=celebNames.get( incorrectanswersLocation );

                }

            }

            button.setText(answers[0]);
            button2.setText(answers[1]);
            button3.setText(answers[2]);
            button4.setText(answers[3]);

        } catch (Exception e) {

            e.printStackTrace();

        }


    }
}

