package com.jmsmart.whosecat.Database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;

@Entity(tableName = "ImageTable")
public class ImageTable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "PetID")
    private int PetID;
    @ColumnInfo(name = "PetImage")
    private byte[] PetImage;



    public void setPetID(int PetID){this.PetID=PetID;}
    public int getPetID(){return PetID;}

    public void setPetImageByBitmap(Bitmap PetImage){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PetImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        this.PetImage = data;
    }
    public Bitmap getPetImageInBitmap(){
        Bitmap bitmap = BitmapFactory.decodeByteArray(this.PetImage,0,this.PetImage.length);
        return bitmap;
    }

    public void setPetImage(byte[] PetImage){
        this.PetImage = PetImage;
    }
    public byte[] getPetImage(){
        return PetImage;
    }
}
