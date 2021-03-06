package by.epam.jwd.testingApp.controller.command.commandImpl;

import by.epam.jwd.testingApp.controller.command.Command;
import by.epam.jwd.testingApp.controller.mapping.AttributeNames;
import by.epam.jwd.testingApp.controller.mapping.PageMapping;
import by.epam.jwd.testingApp.controller.transitionManager.TransitionManager;
import by.epam.jwd.testingApp.entity.Test;
import by.epam.jwd.testingApp.exception.ServiceException;
import by.epam.jwd.testingApp.service.entity.factory.EntitiesServiceFactory;
import by.epam.jwd.testingApp.service.errorMsg.ErrorMsgProvider;
import by.epam.jwd.testingApp.service.errorMsg.ErrorMsgSupplier;
import by.epam.jwd.testingApp.service.pagination.DirectPagination;
import by.epam.jwd.testingApp.service.parameterParser.ParserProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class ViewMyTests implements Command {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final String UNDEFINED_USER = "test.undefinedUser";
    public static final int LIMIT_ON_PAGE = 4;
    public static final int PAGINATION_MAX_SIZE = 7;

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        ParserProvider parserProvider = ParserProvider.getInstance();
        EntitiesServiceFactory factory = EntitiesServiceFactory.getInstance();

        Integer userId = parserProvider.getUserIdParser().parsing(request);
        if(userId==null){
            String language = ParserProvider.getInstance().getLanguageParser().parsing(request);
            ErrorMsgSupplier errorMsg = ErrorMsgProvider.getInstance().getManagerByLocale(language);
            request.setAttribute(AttributeNames.ERROR_MSG, errorMsg.getValueByName(UNDEFINED_USER));

            TransitionManager.getInstance().getTransitionByForward().
                    doTransition(request, response, PageMapping.AUTHORIZATION_PAGE);
            return;
        }

        try {
            int pageNumber = parserProvider.getPageNumberParser().parsing(request);
            int testsNumber = factory.getTestService().calculateUsersTotalTestsNumber(userId,false);
            if(pageNumber > (testsNumber-1)/LIMIT_ON_PAGE){
                pageNumber = (testsNumber-1)/LIMIT_ON_PAGE;
            }else if(pageNumber < 0){
                pageNumber = 0;
            }
            session.setAttribute(AttributeNames.PAGE_NUMBER, pageNumber);

            List<Test> testList = factory.getTestService().
                    selectByCreatorId(userId,pageNumber*LIMIT_ON_PAGE,true,LIMIT_ON_PAGE );

            request.setAttribute(AttributeNames.TEST_LIST,
                    testList);
            request.setAttribute(AttributeNames.PASSED_USERS,
                    factory.getResultService().calculateRowsNumberByTestId(testList));
            request.setAttribute(AttributeNames.TEST_RESULTS,
                    factory.getResultService().calculateAvgResultsByTestId(testList));
            request.setAttribute(AttributeNames.PAGINATION,
                    DirectPagination.getInstance().
                            calculatePagination(pageNumber,testsNumber,LIMIT_ON_PAGE,PAGINATION_MAX_SIZE));

        } catch (ServiceException e) {
            LOGGER.error(e);
            throw new ServletException(e);
        }

        TransitionManager.getInstance().getTransitionByForward().
                doTransition(request, response, PageMapping.VIEW_MY_TESTS);
    }
}
