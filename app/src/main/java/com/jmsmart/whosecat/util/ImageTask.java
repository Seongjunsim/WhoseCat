package com.jmsmart.whosecat.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.jmsmart.whosecat.Database.ImageTable;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.view.com.MainActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageTask extends AsyncTask<String, Long, Boolean> {

    ImageTable imageTable;
    CircleImageView imageView;
    List<ImageTable> byId;
    String cmd;
    Context context;

    public final static String INSERT = "INSERT";
    public final static String UPDATE = "UPDATE";
    public final static String SELECT = "SELECT";
    public final static String DELETE = "DELETE";



    public ImageTask(ImageTable imageTable, CircleImageView imageView, Context context){
        this.imageTable = imageTable;
        this.imageView = imageView;
        this.context = context;
    }

    @Override
    protected void onPreExecute(){ }

    @Override
    protected Boolean doInBackground(String... strings) {
        cmd = strings[0];
        try{
            switch(cmd){
                case INSERT:
                    MainActivity.imageDao.insertImage(imageTable);
                    break;
                case UPDATE:
                    MainActivity.imageDao.updateImageData(imageTable.getPetID(), imageTable.getPetImage());
                    break;
                case SELECT:
                    byId = MainActivity.imageDao
                            .loadAllById(imageTable.getPetID());
                    break;
                case DELETE:
                    MainActivity.imageDao.deleteImageData(imageTable.getPetID());
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPostExecute(Boolean result) {
        switch(cmd){
            case INSERT:
               System.out.println("inserted");
                break;
            case UPDATE:
                System.out.println("updated");
                break;
            case SELECT:
                if(byId.size() == 0){
                    imageView.setImageDrawable(context.getDrawable(R.drawable.default_icon));
                }
                else{
                    imageView.setImageBitmap(byId.get(0).getPetImageInBitmap());
                }
                break;
            case DELETE:
                System.out.println("deleted");
                break;
        }
    }
}