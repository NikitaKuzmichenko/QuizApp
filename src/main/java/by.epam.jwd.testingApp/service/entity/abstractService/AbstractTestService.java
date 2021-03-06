package by.epam.jwd.testingApp.service.entity.abstractService;

import by.epam.jwd.testingApp.entity.Test;
import by.epam.jwd.testingApp.exception.ServiceException;

import by.epam.jwd.testingApp.service.entity.abstractService.genericAbstractService.AbstractEntitiesService;

import java.util.List;

public interface AbstractTestService extends AbstractEntitiesService<Test,Integer> {
    List<Test> selectByCreatorId(int creatorId, int offset, boolean desc,int limit)throws ServiceException;
    List<Test> selectByCategory(int categoryId, int offset, boolean desc,int limit)throws ServiceException;

    List<Test> sortByUsersPassedNumber(int offset, boolean desc,int limit)throws ServiceException;
    List<Test> sortByName(int offset, boolean desc,int limit)throws ServiceException;
    List<Test> sortByCreationDate(int offset, boolean desc,int limit)throws ServiceException;

    List<Test> sortByUsersPassedNumber(int categoryId,int offset, boolean desc,int limit)throws ServiceException;
    List<Test> sortByName(int categoryId,int offset, boolean desc,int limit)throws ServiceException;
    List<Test> sortByCreationDate(int categoryId,int offset, boolean desc,int limit)throws ServiceException;

    int calculateTotalTestsNumber(Integer categoryId) throws ServiceException;
    int calculateUsersTotalTestsNumber(Integer userId,boolean onlyAvailable) throws ServiceException;
    boolean remove(Integer entityId) throws ServiceException;
    Integer createAndGetId(Test entity) throws ServiceException;
}
