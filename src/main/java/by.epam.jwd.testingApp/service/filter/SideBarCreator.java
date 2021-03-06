package by.epam.jwd.testingApp.service.filter;

import by.epam.jwd.testingApp.controller.mapping.AttributeNames;
import by.epam.jwd.testingApp.exception.ServiceException;
import by.epam.jwd.testingApp.service.entity.abstractService.AbstractTestService;
import by.epam.jwd.testingApp.service.entity.factory.EntitiesServiceFactory;

import javax.servlet.*;
import java.io.IOException;

public class SideBarCreator implements Filter {

    private static AbstractTestService testService;

    public static final int LIMIT_ON_SIDE_BAR = 3;
    public static final int DEFAULT_OFFSET = 0;
    public static final boolean DEFAULT_DIRECTION = true;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        testService =  EntitiesServiceFactory.getInstance().getTestService();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            servletRequest.setAttribute(AttributeNames.NEW_TESTS,
                    testService.sortByCreationDate(DEFAULT_OFFSET, DEFAULT_DIRECTION, LIMIT_ON_SIDE_BAR));
            servletRequest.setAttribute(AttributeNames.POPULAR_TESTS,
                    testService.sortByUsersPassedNumber(DEFAULT_OFFSET, DEFAULT_DIRECTION, LIMIT_ON_SIDE_BAR));
        } catch (ServiceException e) {
            throw new ServletException(e);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
