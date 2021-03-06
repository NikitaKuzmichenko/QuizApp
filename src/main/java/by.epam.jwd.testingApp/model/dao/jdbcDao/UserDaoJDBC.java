package by.epam.jwd.testingApp.model.dao.jdbcDao;
import by.epam.jwd.testingApp.entity.User;
import by.epam.jwd.testingApp.exception.DaoException;
import by.epam.jwd.testingApp.model.connectionPool.ConnectionPool;
import by.epam.jwd.testingApp.model.dao.abstractDao.entitiesDao.AbstractUserDao;
import by.epam.jwd.testingApp.model.dataBaseMapping.UserMapping;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBC implements AbstractUserDao {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final String SELECT_BY_LOGIN_SQL = "SELECT * FROM " + UserMapping.TABLE_NAME
            + " WHERE " + UserMapping.EMAIL + " = ?;";

    public static final String SELECT_BY_ID_SQL = "SELECT * FROM " + UserMapping.TABLE_NAME
            + " WHERE " + UserMapping.ID + " = ?;";

    public static final String UPDATE_SQL = "UPDATE " + UserMapping.TABLE_NAME
            + " SET " + UserMapping.NAME + " = ?"
            + ", " + UserMapping.PASSWORD + " = ?"
            + ", " + UserMapping.EMAIL + " = ?"
            + ", " + UserMapping.ROLE_ID + " = ?"
            + "WHERE " + UserMapping.ID +" = ?;";

    public static final String DELETE_SQL = "DELETE FROM " + UserMapping.TABLE_NAME
            + " WHERE " + UserMapping.ID +" = ?;";

    public static final String CREATE_SQL = "INSERT INTO " + UserMapping.TABLE_NAME
            + " (" + UserMapping.NAME + ", "
            + UserMapping.EMAIL + ", "
            + UserMapping.PASSWORD + ", "
            + UserMapping.ROLE_ID + ")"
            +  "VALUES(?,?,?,?);";

    public static final String IS_ROW_EXIST_SQL = "SELECT * FROM " + UserMapping.TABLE_NAME
            + " WHERE " + UserMapping.NAME + " = ?"
            + " OR " + UserMapping.EMAIL + " = ?;";


    @Override
    public User selectEntityByLogin(String email) throws DaoException {
        if(email == null) return null;

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.takeConnection();

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<User> result;

        try {
            statement = connection.prepareStatement(SELECT_BY_LOGIN_SQL);
            statement.setString(1,email);
            resultSet = statement.executeQuery();
            result = parsFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {LOGGER.error(e);}
            pool.returnConnection(connection);
        }
        return (result.size() == 1) ? result.get(0) : null;
    }

    @Override
    public User selectEntityById(Integer id) throws DaoException {
        if(id==null) return null;

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.takeConnection();

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<User> result;

        try {
            statement = connection.prepareStatement(SELECT_BY_ID_SQL);
            statement.setInt(1,id);
            resultSet = statement.executeQuery();
            result = parsFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {LOGGER.error(e);}
            pool.returnConnection(connection);
        }
        return (result.size() == 1) ? result.get(0) : null;
    }

    @Override
    public boolean update(User entity) throws DaoException {
        if(entity == null) return false;

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.takeConnection();

        PreparedStatement statement = null;
        int result;
        try {
            if(isRowExist(entity,connection))return false;

            statement = connection.prepareStatement(UPDATE_SQL);
            statement.setString(1,entity.getName());
            statement.setString(2,entity.getPassword());
            statement.setString(3,entity.getEmail());
            statement.setInt(4,entity.getRoleId());
            result = statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {LOGGER.error(e);}
            pool.returnConnection(connection);
        }
        return result==1;
    }

    @Override
    public boolean delete(Integer id) throws DaoException {
        if(id==null) return false;

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.takeConnection();

        PreparedStatement statement = null;

        int result;
        try {
            statement = connection.prepareStatement(DELETE_SQL);
            statement.setInt(1,id);
            result = statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {LOGGER.error(e);}
            pool.returnConnection(connection);
        }
        return result==1;
    }

    @Override
    public boolean create(User entity) throws DaoException {
        if(entity == null) return false;

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.takeConnection();

        PreparedStatement statement = null;
        int result;
        try {
            if(isRowExist(entity,connection))return false;

            statement = connection.prepareStatement(CREATE_SQL);
            statement.setString(1,entity.getName());
            statement.setString(2,entity.getEmail());
            statement.setString(3,entity.getPassword());
            statement.setInt(4,entity.getRoleId());
            result = statement.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {LOGGER.error(e);}
            pool.returnConnection(connection);
        }
        return result==1;
    }

    private boolean isRowExist(User entity, Connection connection) throws SQLException {

        PreparedStatement statement = connection.prepareStatement(IS_ROW_EXIST_SQL);
        statement.setString(1,entity.getName());
        statement.setString(2,entity.getEmail());
        ResultSet resultSet = statement.executeQuery();

        boolean result = resultSet.next();
        resultSet.close();
        statement.close();

        return  result;
    }

    private List<User> parsFromResultSet(ResultSet set) throws SQLException {
        List<User> result = new ArrayList<>();
        if(set==null) return result;
        while (set.next()){
            result.add(new User(
                    set.getInt(UserMapping.ID),
                    set.getInt(UserMapping.ROLE_ID),
                    set.getString(UserMapping.NAME),
                    set.getString(UserMapping.EMAIL),
                    set.getString(UserMapping.PASSWORD))
            );
        }
        return result;
    }
}
