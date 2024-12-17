package com.whats2watch.w2w.model.dao.entities.media.tvseries;

import com.google.gson.reflect.TypeToken;
import com.whats2watch.w2w.exceptions.DAOException;
import com.whats2watch.w2w.model.TVSeries;
import com.whats2watch.w2w.model.dao.entities.media.DAOFileSystemMedia;

public class DAOFileSystemTVSeries extends DAOFileSystemMedia<TVSeries> {

    public DAOFileSystemTVSeries(String filePath) throws DAOException {
        super(filePath, new TypeToken<TVSeries[]>() {}.getType());
    }

    @Override
    protected String getEntityName() {
        return "TVSeries";
    }
}
