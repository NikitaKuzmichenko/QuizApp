package by.epam.jwd.testingApp.model.dao.abstractDao.entitiesDao;

import by.epam.jwd.testingApp.entities.Test;
import by.epam.jwd.testingApp.exceptions.DaoException;
import by.epam.jwd.testingApp.model.dao.abstractDao.genericDao.AbstractGenericDao;

import java.util.List;

public interface AbstractTestDao extends AbstractGenericDao<Test, Integer> {

    List<Test> selectSortedTestsByIntRow(int limit, int offset, boolean desc, String sortByRow, String rowName, int rowValue) throws DaoException;

    List<Test> sortTestsByRow(int limit, int offset, boolean desc ,String sortedRow) throws DaoException;

    boolean removeTest(int testId)throws DaoException;

    int calculateTestsNumber(int rowValue,String rowName,boolean onlyAvailable)throws DaoException;

    Integer createAndGetId(Test entity) throws DaoException;
}
