package com.jmsmart.whosecat.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImageDao {
    @Query("SELECT * FROM ImageTable WHERE PetID IS :petID")
    List<ImageTable> loadAllById(int petID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImage(ImageTable imageTable);

    @Query("UPDATE ImageTable SET PetImage = :petImage WHERE PetID = :petID")
    int updateImageData(int petID, byte[] petImage);

    @Query("DELETE FROM ImageTable WHERE PetID = :petID")
    void deleteImageData(int petID);
}
