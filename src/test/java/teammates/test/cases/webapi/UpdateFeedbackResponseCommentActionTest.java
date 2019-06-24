package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.UpdateFeedbackResponseCommentAction;
import teammates.ui.webapi.output.FeedbackVisibilityType;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.FeedbackResponseCommentUpdateRequest;

/**
 * SUT: {@link UpdateFeedbackResponseCommentAction}.
 */
public class UpdateFeedbackResponseCommentActionTest extends BaseActionTest<UpdateFeedbackResponseCommentAction> {

    private InstructorAttributes instructor1OfCourse1;
    private FeedbackResponseAttributes response1ForQ1S1C1;
    private FeedbackResponseCommentAttributes comment1FromT1C1ToR1Q1S1C1;
    private InstructorAttributes instructor2OfCourse1;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSE_COMMENT;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    protected void prepareTestData() {
        removeAndRestoreTypicalDataBundle();

        FeedbackSessionAttributes session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        response1ForQ1S1C1 = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail());
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        comment1FromT1C1ToR1Q1S1C1 = typicalBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
    }

    @Override
    @Test
    public void testExecute() throws Exception {

        comment1FromT1C1ToR1Q1S1C1 = logic.getFeedbackResponseComment(response1ForQ1S1C1.getId(),
                comment1FromT1C1ToR1Q1S1C1.commentGiver, comment1FromT1C1ToR1Q1S1C1.createdAt);
        assertNotNull("response comment not found", comment1FromT1C1ToR1Q1S1C1);

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Unsuccessful case: not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical successful case for unpublished session");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS));
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc =
                logic.getFeedbackResponseComment(comment1FromT1C1ToR1Q1S1C1.getId());
        assertEquals(comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);

        ______TS("Null show comments and show giver permissions");

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)",
                new ArrayList<>(), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("Typical successful case for unpublished session public to various recipients");

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)",
                new ArrayList<>(), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)",
                 Arrays.asList(FeedbackVisibilityType.GIVER), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.RECIPIENT), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.GIVER_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.STUDENTS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        ______TS("Non-existent feedback response comment id");

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        UpdateFeedbackResponseCommentAction action0 = action;
        assertThrows(EntityNotFoundException.class, () -> getJsonResult(action0));

        ______TS("Instructor is not feedback response comment giver");

        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(comment1FromT1C1ToR1Q1S1C1.getId());
        assertEquals(comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertEquals(instructor2OfCourse1.getEmail(), frc.lastEditorEmail);
        assertFalse(frc.isCommentFromFeedbackParticipant);

        ______TS("Typical successful case for published session");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        FeedbackSessionsLogic.inst().publishFeedbackSession(
                comment1FromT1C1ToR1Q1S1C1.feedbackSessionName, comment1FromT1C1ToR1Q1S1C1.courseId);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited for published session)",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(comment1FromT1C1ToR1Q1S1C1.getId());
        assertEquals(comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited for published session)",
                frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);

        ______TS("Unsuccessful case: empty comment text");

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                "", new ArrayList<>(), new ArrayList<>());
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
        FeedbackQuestionAttributes feedbackQuestion = logic.getFeedbackQuestion(
                "First feedback session", "idOfTypicalCourse1", questionNumber);

        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes feedbackResponse = logic.getFeedbackResponse(feedbackQuestion.getId(),
                giverEmail, receiverEmail);

        FeedbackResponseCommentAttributes feedbackResponseComment = typicalBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q1S1C1");

        feedbackResponseComment = logic.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.commentGiver, feedbackResponseComment.createdAt);

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
        };
        // this person is not the giver. so not accessible
        verifyInaccessibleWithoutModifySessionCommentInSectionsPrivilege(submissionParams);
        verifyOnlyInstructorsCanAccess(submissionParams);
    }
}
