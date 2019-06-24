package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.webapi.action.GetFeedbackResponseCommentsAction;

/**
 * SUT: {@link GetFeedbackResponseCommentsAction}.
 */
public class GetFeedbackResponseCommentsActionTest extends BaseActionTest<GetFeedbackResponseCommentsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    public void testExecute() {
        //TODO
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        //TODO
    }
}
