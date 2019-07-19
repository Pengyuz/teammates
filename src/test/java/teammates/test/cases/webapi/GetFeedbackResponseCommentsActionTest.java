package teammates.test.cases.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetFeedbackResponseCommentsAction;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackResponseCommentData;
import teammates.ui.webapi.output.FeedbackResponseCommentsData;

/**
 * SUT: {@link GetFeedbackResponseCommentsAction}.
 */
public class GetFeedbackResponseCommentsActionTest extends BaseActionTest<GetFeedbackResponseCommentsAction> {

    private InstructorAttributes instructor1OfCourse1;
    private InstructorAttributes instructor1OfCourse2;
    private FeedbackResponseAttributes response1ForQ1;
    private FeedbackResponseAttributes response1ForQ3;
    private StudentAttributes student1InCourse1;
    private StudentAttributes student1InCourse2;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = loadDataBundle("/FeedbackResponseCommentTest.json");
        removeAndRestoreDataBundle(dataBundle);

        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        FeedbackQuestionAttributes qn3InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 3);

        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        student1InCourse2 = dataBundle.students.get("student1InCourse2");
        instructor1OfCourse1 = dataBundle.instructors.get("instructor1InCourse1");
        instructor1OfCourse2 = dataBundle.instructors.get("instructor1InCourse2");
        response1ForQ1 = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail());
        response1ForQ3 = logic.getFeedbackResponse(qn3InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail());
    }

    @Override
    @Test
    protected void testExecute() {
        // ses individual test cases
    }

    @Test
    protected void testExecute_notEnoughParameters_shouldFail() {
        loginAsStudent(student1InCourse1.getGoogleId());

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId());
        verifyHttpParameterFailure(Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString());
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {

        ______TS("invalid intent as instructor_result");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };
        verifyHttpParameterFailure(submissionParams);

        ______TS("invalid intent as student_result");
        loginAsStudent(student1InCourse1.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ3.getId(),
        };
        verifyHttpParameterFailure(submissionParams);
    }

    @Test
    protected void testExecute_typicalSuccessCase_shouldPass() {

        ______TS("typical successful case as student_submission");
        loginAsStudent(student1InCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ3.getId(),
        };

        FeedbackResponseCommentsData actualComments = getFeedbackResponseComments(submissionParams);
        List<FeedbackResponseCommentData> comments = actualComments.getComments();
        assertEquals(comments.size(), 1);
        FeedbackResponseCommentData actual = comments.get(0);
        FeedbackResponseCommentAttributes expected = logic.getFeedbackResponseCommentsForResponseFromParticipant(
                response1ForQ3.getId(), true).get(0);
        assertNotNull(actual.getFeedbackResponseCommentId());
        assertEquals(actual.getFeedbackCommentText(), expected.getCommentText());
        assertEquals(actual.getCommentGiver(), expected.getCommentGiver());

        ______TS("typical successful case as instructor_submission");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };
        actualComments = getFeedbackResponseComments(submissionParams);
        comments = actualComments.getComments();
        assertEquals(comments.size(), 1);
        actual = comments.get(0);
        expected = logic.getFeedbackResponseCommentsForResponseFromParticipant(
                response1ForQ1.getId(), true).get(0);
        assertNotNull(actual.getFeedbackResponseCommentId());
        assertEquals(actual.getFeedbackCommentText(), expected.getCommentText());
        assertEquals(actual.getCommentGiver(), expected.getCommentGiver());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        // see individual test cases
    }

    @Test
    protected void testAccessControl_typicalSuccessCase_shouldPass() {

        ______TS("typical success case as student_submission");
        loginAsStudent(student1InCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ3.getId(),
        };

        verifyCanAccess(submissionParams);

        ______TS("typical success case as instructor_submission");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };
        verifyCanAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_invalidIntent_shouldFail() {

        ______TS("invalid intent as student_result");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] studentInvalidIntentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ3.getId(),
        };
        assertThrows(InvalidHttpParameterException.class,
                () -> getAction(studentInvalidIntentParams).checkSpecificAccessControl());

        ______TS("invalid intent as instructor_result");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] instructorInvalidIntentParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };
        assertThrows(InvalidHttpParameterException.class,
                () -> getAction(instructorInvalidIntentParams).checkSpecificAccessControl());
    }

    @Test
    protected void testAccessControl_responseNotExisting_shouldFail() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, "responseIdOfNonExistingResponse",
        };

        assertThrows(EntityNotFoundException.class, () -> getAction(submissionParams).checkSpecificAccessControl());
    }

    @Test
    protected void testAccessControl_accessAcrossCourses_shouldFail() {

        ______TS("instructor access other instructor's response from different course");
        loginAsInstructor(instructor1OfCourse2.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };

        verifyCannotAccess(submissionParams);

        ______TS("students access other students' response from different course");
        loginAsStudent(student1InCourse2.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ3.getId(),
        };

        verifyCannotAccess(submissionParams);

    }

    private FeedbackResponseCommentsData getFeedbackResponseComments(String[] params) {
        GetFeedbackResponseCommentsAction action = getAction(params);
        JsonResult actualResult = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, actualResult.getStatusCode());
        return (FeedbackResponseCommentsData) actualResult.getOutput();
    }

}
