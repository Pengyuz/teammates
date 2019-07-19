package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.ui.webapi.action.CreateFeedbackResponseCommentAction;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackResponseCommentData;
import teammates.ui.webapi.output.FeedbackVisibilityType;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.FeedbackResponseCommentCreateRequest;

/**
 * SUT: {@link CreateFeedbackResponseCommentAction}.
 */
public class CreateFeedbackResponseCommentActionTest extends BaseActionTest<CreateFeedbackResponseCommentAction> {
    private FeedbackSessionAttributes session1InCourse1;
    private InstructorAttributes instructor1OfCourse1;
    private InstructorAttributes instructor2OfCourse1;
    private InstructorAttributes helperOfCourse1;
    private FeedbackResponseAttributes response1ForQ1;
    private FeedbackResponseAttributes response1ForQ3;
    private FeedbackResponseAttributes response2ForQ3;
    private FeedbackResponseAttributes response1ForQ5;
    private StudentAttributes student1InCourse1;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    protected void prepareTestData() {
        DataBundle dataBundle = loadDataBundle("/FeedbackResponseCommentTest.json");
        removeAndRestoreDataBundle(dataBundle);

        instructor1OfCourse1 = dataBundle.instructors.get("instructor1InCourse1");
        instructor2OfCourse1 = dataBundle.instructors.get("instructor2InCourse1");
        helperOfCourse1 = dataBundle.instructors.get("helperOfCourse1");
        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        StudentAttributes student2InCourse1 = dataBundle.students.get("student2InCourse1");
        session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        FeedbackQuestionAttributes qn3InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 3);
        FeedbackQuestionAttributes qn5InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 5);
        response1ForQ1 = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail());
        response1ForQ3 = logic.getFeedbackResponse(qn3InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail());
        response2ForQ3 = logic.getFeedbackResponse(qn3InSession1InCourse1.getId(),
                student2InCourse1.getEmail(), student2InCourse1.getEmail());
        response1ForQ5 = logic.getFeedbackResponse(qn5InSession1InCourse1.getId(),
                instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail());

    }

    @Override
    @Test
    public void testExecute() throws Exception {
        //see individual test cases.
    }

    @Test
    public void testExecute_invalidHttpParameters_shouldFail() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("not enough parameters");
        verifyHttpParameterFailure();
    }

    @Test
    public void testExecute_unpublishedSessionForInstructorResult_shouldPass() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("successful case for unpublished session for INSTRUCTOR_RESULT");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };
        FeedbackResponseCommentCreateRequest requestBody =
                new FeedbackResponseCommentCreateRequest("Comment to first response",
                        Arrays.asList(FeedbackVisibilityType.INSTRUCTORS),
                        Arrays.asList(FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.GIVER));
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        JsonResult r = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        FeedbackResponseCommentData commentData = (FeedbackResponseCommentData) r.getOutput();
        assertEquals("Comment to first response", commentData.getFeedbackCommentText());

        List<FeedbackResponseCommentAttributes> frcList =
                getInstructorComments(response1ForQ1.getId(), "Comment to first response");
        assertEquals(1, frcList.size());
        FeedbackResponseCommentAttributes frc = frcList.get(0);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);
        assertFalse(frc.isVisibilityFollowingFeedbackQuestion);
    }

    @Test
    public void testExecute_unpublishedSessionEmptyGiverPermission_shouldPass() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("typical successful case for unpublished session empty giver permissions");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest(
                "Empty giver permissions", new ArrayList<>(), new ArrayList<>());
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);
    }

    @Test
    public void testExecute_unpublishedSessionValidVisibilitySettings_shouldPass() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("typical successful case for unpublished session shown to various recipients");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest(
                "Null comment permissions", new ArrayList<>(), new ArrayList<>());
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };

        requestBody = new FeedbackResponseCommentCreateRequest("Comment shown to giver",
                Arrays.asList(FeedbackVisibilityType.GIVER), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };

        requestBody = new FeedbackResponseCommentCreateRequest("Comment shown to receiver",
                Arrays.asList(FeedbackVisibilityType.RECIPIENT), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };

        requestBody =
                new FeedbackResponseCommentCreateRequest("Comment shown to own team members",
                        Arrays.asList(FeedbackVisibilityType.GIVER_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };

        requestBody = new FeedbackResponseCommentCreateRequest(
                "Comment shown to receiver team members",
                Arrays.asList(FeedbackVisibilityType.GIVER_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };

        requestBody = new FeedbackResponseCommentCreateRequest("Comment shown to students",
                Arrays.asList(FeedbackVisibilityType.STUDENTS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

    }

    @Test
    public void testExecute_publishedSessionForInstructorResult_shouldPass() throws Exception {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        FeedbackSessionsLogic.inst().publishFeedbackSession(session1InCourse1.getFeedbackSessionName(),
                session1InCourse1.getCourseId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest(
                "Comment to first response, published session",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS));
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        List<FeedbackResponseCommentAttributes> frcList = getInstructorComments(response1ForQ1.getId(),
                "Comment to first response, published session");
        assertEquals(1, frcList.size());
        FeedbackResponseCommentAttributes frc = frcList.get(0);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals("instructor1@course1.tmt", frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);
        assertFalse(frc.isVisibilityFollowingFeedbackQuestion);
    }

    @Test
    public void testExecute_emptyCommentText_shouldFail() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Unsuccessful case: empty comment text");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest("",
                new ArrayList<>(), new ArrayList<>());
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, output.getMessage());
    }

    @Test
    protected void testExecute_typicalCaseForSubmission_shouldPass() {

        ______TS("Successful case: student submission");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ3.getId(),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest(
                "Student submission comment", Arrays.asList(FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        List<FeedbackResponseCommentAttributes> comments = logic.getFeedbackResponseCommentsForResponseFromParticipant(
                response1ForQ3.getId(), true);
        assertEquals(comments.size(), 2);
        FeedbackResponseCommentAttributes comment = comments.get(1);
        assertEquals(comment.getCommentText(), "Student submission comment");

        ______TS("Successful case: instructor submission");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };

        requestBody = new FeedbackResponseCommentCreateRequest(
                "Instructor submission comment", Arrays.asList(FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        comments = logic.getFeedbackResponseCommentsForResponseFromParticipant(
                response1ForQ1.getId(), true);
        assertEquals(comments.size(), 2);
        comment = comments.get(1);
        assertEquals(comment.getCommentText(), "Instructor submission comment");
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {

        ______TS("invalid intent STUDENT_RESULT");
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ3.getId(),
        };
        verifyHttpParameterFailure(invalidIntent1);

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ3.getId(),
        };
        verifyHttpParameterFailure(invalidIntent2);
    }

    @Test
    protected void testAccessControl_submitCommentForOthersResponse_shouldFail() {

        ______TS("students access other students session and give comments");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParamsStudentToStudents = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response2ForQ3.getId(),
        };
        verifyCannotAccess(submissionParamsStudentToStudents);

        ______TS("instructors access other instructor's session and give comments");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        String[] submissionParamsInstructorToInstructor = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ5.getId(),
        };
        verifyCannotAccess(submissionParamsInstructorToInstructor);
    }

    @Test
    protected void testAccessControl_invalidIntent_shouldFail() {

        ______TS("invalid intent STUDENT_RESULT");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ3.getId(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent1).checkAccessControl());

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent2).checkAccessControl());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        // see individual test cases
    }

    @Test
    protected void testAccessControl_instructorWithoutSubmitSessionInSectionsPrivilege_shouldFail() {

        loginAsInstructor(helperOfCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ3.getId(),
        };
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_logOut_shouldFail() {

        gaeSimulation.logoutUser();
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ3.getId(),
        };
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_studentAccessInstructorResponse_shouldFail() {

        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_accessibleForInstructorInSameCourse_shouldPass() {

        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };
        verifyCanAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_accessibleForAdminToMasqueradeAsInstructor_shouldPass() {

        loginAsAdmin();
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1.getId(),
        };
        verifyCanMasquerade(instructor1OfCourse1.getGoogleId(), submissionParams);
    }

    /**
     * Filters instructor comments according to comment text from all comments on a response.
     *
     * @param responseId response id of response
     * @param commentText comment text
     * @return instructor comments
     */
    private List<FeedbackResponseCommentAttributes> getInstructorComments(String responseId, String commentText) {
        FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
        return frcDb.getFeedbackResponseCommentsForResponse(responseId)
                .stream()
                .filter(comment -> comment.commentText.equals(commentText))
                .collect(Collectors.toList());
    }

}
