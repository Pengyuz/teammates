package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.Arrays;

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
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
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
    private InstructorAttributes instructor2OfCourse1;
    private InstructorAttributes helperOfCourse1;
    private StudentAttributes student1InCourse1;
    private StudentAttributes student2InCourse1;
    private FeedbackResponseAttributes response1ForQn1;
    private FeedbackResponseCommentAttributes comment1ForQn1;
    private FeedbackResponseCommentAttributes comment1ForQn2;
    private FeedbackResponseCommentAttributes comment1ForQn3;

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
        DataBundle dataBundle = loadDataBundle("/FeedbackResponseCommentTest.json");
        removeAndRestoreDataBundle(dataBundle);

        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        student2InCourse1 = dataBundle.students.get("student2InCourse1");
        instructor1OfCourse1 = dataBundle.instructors.get("instructor1InCourse1");
        instructor2OfCourse1 = dataBundle.instructors.get("instructor2InCourse1");
        helperOfCourse1 = dataBundle.instructors.get("helperOfCourse1");
        FeedbackSessionAttributes session1InCourse1 = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        FeedbackQuestionAttributes qn2InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 2);
        FeedbackQuestionAttributes qn3InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 3);
        response1ForQn1 = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail());
        FeedbackResponseAttributes response1ForQn2 = logic.getFeedbackResponse(qn2InSession1InCourse1.getId(),
                instructor1OfCourse1.getEmail(), student1InCourse1.getEmail());
        FeedbackResponseAttributes response1ForQn3 = logic.getFeedbackResponse(qn3InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail());
        comment1ForQn1 = logic.getFeedbackResponseCommentsForResponseFromParticipant(
                response1ForQn1.getId(), true).get(0);
        comment1ForQn2 = logic.getFeedbackResponseCommentsForResponseFromParticipant(
                response1ForQn2.getId(), false).get(0);
        comment1ForQn3 = logic.getFeedbackResponseCommentsForResponseFromParticipant(
                response1ForQn3.getId(), true).get(0);
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        // see individual test cases.
    }

    @Test
    protected void testExecute_notEnoughParams_shouldFail() {

        ______TS("Unsuccessful case: not enough parameters");

        verifyHttpParameterFailure();
    }

    @Test
    protected void testExecute_typicalSuccessfulCases_shouldPass() {
        comment1ForQn1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1ForQn1.commentGiver, comment1ForQn1.createdAt);
        assertNotNull("response comment not found", comment1ForQn1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for INSTRUCTOR_RESULT");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS));
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc =
                logic.getFeedbackResponseComment(comment1ForQn1.getId());
        assertEquals(comment1ForQn1.commentText + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);

        ______TS("Typical successful case for STUDENT_SUBMISSION");
        loginAsStudent(student1InCourse1.getGoogleId());

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn3.getId().toString(),
        };
        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn3.getCommentText() + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.INSTRUCTORS), Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(comment1ForQn3.getId());
        assertEquals(comment1ForQn3.getCommentText() + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.STUDENTS, frc.commentGiverType);
        assertEquals(student1InCourse1.getEmail(), frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);

        ______TS("Typical successful case for INSTRUCTOR_SUBMISSION");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };
        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn1.getCommentText() + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.INSTRUCTORS), Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(comment1ForQn1.getId());
        assertEquals(comment1ForQn1.getCommentText() + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);
    }

    @Test
    protected void testExecute_emptyVisibilitySettings_shouldPass() {
        comment1ForQn1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1ForQn1.commentGiver, comment1ForQn1.createdAt);
        assertNotNull("response comment not found", comment1ForQn1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Null show comments and show giver permissions");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn1.commentText + " (Edited)", new ArrayList<>(), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);
    }

    @Test
    protected void testExecute_variousVisibilitySettings_shouldPass() {
        comment1ForQn1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1ForQn1.commentGiver, comment1ForQn1.createdAt);
        assertNotNull("response comment not found", comment1ForQn1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for unpublished session public to various recipients");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn1.commentText + " (Edited)", new ArrayList<>(), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.GIVER), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.RECIPIENT), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.GIVER_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };

        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.STUDENTS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);
    }

    @Test
    protected void testExecute_nonExistingFeedbackResponse_shouldFail() {

        ______TS("Non-existent feedback response comment id");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS));
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        UpdateFeedbackResponseCommentAction action0 = action;
        assertThrows(EntityNotFoundException.class, () -> getJsonResult(action0));
    }

    @Test
    protected void testExecute_instructorIsNotCommentGiver_shouldPass() {
        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        ______TS("Instructor is not feedback response comment giver");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn2.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn2.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS));
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(comment1ForQn2.getId());
        assertEquals(comment1ForQn2.commentText + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertEquals(instructor2OfCourse1.getEmail(), frc.lastEditorEmail);
        assertFalse(frc.isCommentFromFeedbackParticipant);
    }

    @Test
    protected void testExecute_typicalCasePublishedSession_shouldPass() throws Exception {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for published session");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        FeedbackSessionsLogic.inst().publishFeedbackSession(
                comment1ForQn2.feedbackSessionName, comment1ForQn2.courseId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn2.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1ForQn2.commentText + " (Edited for published session)",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(comment1ForQn2.getId());
        assertEquals(comment1ForQn2.commentText + " (Edited for published session)",
                frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);
    }

    @Test
    protected void testExecute_emptyCommentText_shouldFail() {
        comment1ForQn1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1ForQn1.commentGiver, comment1ForQn1.createdAt);
        assertNotNull("response comment not found", comment1ForQn1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Unsuccessful case: empty comment text");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                "", new ArrayList<>(), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput output = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, output.getMessage());
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {
        ______TS("invalid intent STUDENT_RESULT");
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn3.getId().toString(),
        };
        verifyHttpParameterFailure(invalidIntent1);

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, comment1ForQn1.getId().toString(),
        };
        verifyHttpParameterFailure(invalidIntent2);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        // see individual test cases
    }

    @Test
    protected void testAccessControl_accessibleWithPrivilege_shouldPass() {

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn2.getId().toString(),
        };
        ______TS("accessible for instructors of the same course");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        verifyCanAccess(submissionParams);
        loginAsAdmin();
        verifyCanMasquerade(instructor2OfCourse1.getGoogleId(), submissionParams);
        ______TS("inaccessible for helper instructors");
        loginAsInstructor(helperOfCourse1.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

    @Test
    protected void testAccessControl_typicalSuccessfulCase_shouldPass() {

        ______TS("successful case for student submission");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn3.getId().toString(),
        };
        verifyCanAccess(submissionParams);

        ______TS("successful case for instructor submission");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };
        verifyCanAccess(submissionParams);

        ______TS("successful case for instructor result");
        comment1ForQn1 = logic.getFeedbackResponseComment(response1ForQn1.getId(),
                comment1ForQn1.commentGiver, comment1ForQn1.createdAt);
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };
        verifyCanAccess(submissionParams);

        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        verifyCanAccess(submissionParams);

    }

    @Test
    protected void testAccessControl_invalidIntent_shouldFail() {

        ______TS("invalid intent STUDENT_RESULT");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn3.getId().toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent1).checkAccessControl());

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn3.getId().toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent2).checkAccessControl());
    }

    @Test
    protected void testAccessControl_updateCommentForOthersResponse_shouldFail() {

        ______TS("students access other students session and give comments");
        loginAsStudent(student2InCourse1.getGoogleId());
        String[] submissionParamsStudentToStudents = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn3.getId().toString(),
        };
        verifyCannotAccess(submissionParamsStudentToStudents);

        ______TS("instructors access other instructor's session and give comments");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        String[] submissionParamsInstructorToInstructor = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1ForQn1.getId().toString(),
        };
        verifyCannotAccess(submissionParamsInstructorToInstructor);
    }

    @Test
    protected void testAccessControl_nonExistingResponseComment_shouldFail() {

        ______TS("Response comment doesn't exist");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };
        assertThrows(EntityNotFoundException.class, () -> getAction(submissionParams).checkSpecificAccessControl());
    }
}
