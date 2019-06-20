package teammates.test.cases.webapi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.*;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.ui.webapi.action.CreateFeedbackResponseCommentAction;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackVisibilityType;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.FeedbackResponseCommentUpdateRequest;

/**
 * SUT: {@link CreateFeedbackResponseCommentAction}.
 */
public class CreateFeedbackResponseCommentActionTest extends BaseActionTest<CreateFeedbackResponseCommentAction> {
    private FeedbackSessionAttributes session1InCourse1;
    private FeedbackQuestionAttributes qn1InSession1InCourse1;
    private StudentAttributes student1InCourse1;
    private InstructorAttributes instructor1OfCourse1;
    private FeedbackResponseAttributes response1ForQ1S1C1;

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
        removeAndRestoreTypicalDataBundle();
        session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        qn1InSession1InCourse1 = logic.getFeedbackQuestion(session1InCourse1.getFeedbackSessionName(),
                session1InCourse1.getCourseId(), 1);
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        response1ForQ1S1C1 = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail());
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

    }

    @Override
    @Test
    public void testExecute() throws Exception {

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Unsuccessful case: not enough parameters");

        verifyHttpParameterFailure();

        ______TS("typical successful case for unpublished session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        FeedbackResponseCommentUpdateRequest requestBody =
                new FeedbackResponseCommentUpdateRequest("Comment to first response",
                        Arrays.asList(FeedbackVisibilityType.INSTRUCTORS),
                        Arrays.asList(FeedbackVisibilityType.INSTRUCTORS, FeedbackVisibilityType.GIVER));
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        List<FeedbackResponseCommentAttributes> frcList =
                getInstructorComments(response1ForQ1S1C1.getId(), "Comment to first response");
        assertEquals(1, frcList.size());
        FeedbackResponseCommentAttributes frc = frcList.get(0);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);
        assertFalse(frc.isVisibilityFollowingFeedbackQuestion);

        ______TS("typical successful case for unpublished session empty giver permissions");

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("Empty giver permissions", null, null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("typical successful case for unpublished session shown to various recipients");

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("Null comment permissions",
                null, null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("Comment shown to giver",
                Arrays.asList(FeedbackVisibilityType.GIVER), null);//giver
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("Comment shown to receiver",
                Arrays.asList(FeedbackVisibilityType.RECIPIENT), null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        requestBody =
                new FeedbackResponseCommentUpdateRequest("Comment shown to own team members",
                        Arrays.asList(FeedbackVisibilityType.GIVER_TEAM_MEMBERS), null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                "Comment shown to receiver team members",
                Arrays.asList(FeedbackVisibilityType.GIVER_TEAM_MEMBERS), null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("Comment shown to students",
                Arrays.asList(FeedbackVisibilityType.STUDENTS), null);
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("typical successful case for published session");

        FeedbackSessionsLogic.inst().publishFeedbackSession(session1InCourse1.getFeedbackSessionName(),
                session1InCourse1.getCourseId());
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                "Comment to first response, published session", "GIVER,INSTRUCTORS", "GIVER,INSTRUCTORS");
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frcList = getInstructorComments(response.getId(), "Comment to first response, published session");
        assertEquals(1, frcList.size());
        frc = frcList.get(0);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals("instructor1@course1.tmt", frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);
        assertFalse(frc.isVisibilityFollowingFeedbackQuestion);

        ______TS("Unsuccessful case: empty comment text");

        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response.getId(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest("", null, null);
        action = getAction(requestBody, submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, output.getMessage());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        int questionNumber = 1;
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(
                fs.getFeedbackSessionName(), fs.getCourseId(), questionNumber);
        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes response = logic.getFeedbackResponse(question.getId(),
                giverEmail, receiverEmail);
        FeedbackResponseCommentAttributes comment = FeedbackResponseCommentAttributes
                .builder()
                .withCourseId(fs.getCourseId())
                .withFeedbackSessionName(fs.getFeedbackSessionName())
                .withCommentGiver(giverEmail)
                .withCommentText("")
                .withFeedbackQuestionId(question.getId())
                .withFeedbackResponseId(response.getId())
                .build();

        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, comment.feedbackResponseId,
        };

        verifyInaccessibleWithoutSubmitSessionInSectionsPrivilege(submissionParams);
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
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
