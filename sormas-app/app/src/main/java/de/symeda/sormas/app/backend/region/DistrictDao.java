package de.symeda.sormas.app.backend.region;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.common.DaoException;

/**
 * Created by Martin Wahnschaffe on 22.07.2016.
 */
public class DistrictDao extends AbstractAdoDao<District> {

    public DistrictDao(Dao<District,Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<District> getAdoClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTableName() {
        return District.TABLE_NAME;
    }

    public List<District> getByRegion(Region region) {
        return queryForEq(District.REGION+"_id", region, District.NAME, true);
    }
}
