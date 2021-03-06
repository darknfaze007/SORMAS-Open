package de.symeda.sormas.app.backend.hospitalization;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.symeda.sormas.app.backend.common.AbstractAdoDao;
import de.symeda.sormas.app.backend.epidata.EpiDataTravel;

/**
 * Created by Mate Strysewske on 22.02.2017.
 */

public class PreviousHospitalizationDao extends AbstractAdoDao<PreviousHospitalization> {

    public PreviousHospitalizationDao(Dao<PreviousHospitalization, Long> innerDao) throws SQLException {
        super(innerDao);
    }

    @Override
    protected Class<PreviousHospitalization> getAdoClass() {
        return PreviousHospitalization.class;
    }

    @Override
    public String getTableName() {
        return PreviousHospitalization.TABLE_NAME;
    }

    public List<PreviousHospitalization> getByHospitalization(Hospitalization hospitalization) {
        if (hospitalization.isSnapshot()) {
            return querySnapshotsForEq(PreviousHospitalization.HOSPITALIZATION + "_id", hospitalization, PreviousHospitalization.CHANGE_DATE, false);
        }
        return querySnapshotsForEq(PreviousHospitalization.HOSPITALIZATION + "_id", hospitalization, PreviousHospitalization.CHANGE_DATE, false);
    }
}
