package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqResponseDetails;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
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
    private StudentAttributes student1InCourse1;
    private StudentAttributes student2InCourse1;
    private FeedbackSessionAttributes session1InCourse1;
    private FeedbackQuestionAttributes qn6InSession1InCourse1;
    private FeedbackResponseAttributes response1ForQ6S1C1;
    private FeedbackResponseAttributes response2ForQ6S1C1;
    private FeedbackResponseAttributes response1ForQ1S1C1;
    private FeedbackResponseCommentAttributes comment1FromT1C1ToR1Q1S1C1;
    private FeedbackResponseCommentAttributes comment1FromT1C1ToR1Q6S1C1;
    private FeedbackResponseCommentAttributes comment1FromT1C1ToR2Q6S1C1;

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

        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
        session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        response1ForQ1S1C1 = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail());
        comment1FromT1C1ToR1Q1S1C1 = typicalBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
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
        comment1FromT1C1ToR1Q1S1C1 = logic.getFeedbackResponseComment(response1ForQ1S1C1.getId(),
                comment1FromT1C1ToR1Q1S1C1.commentGiver, comment1FromT1C1ToR1Q1S1C1.createdAt);
        assertNotNull("response comment not found", comment1FromT1C1ToR1Q1S1C1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for INSTRUCTOR_RESULT");

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

        ______TS("Typical successful case for STUDENT_SUBMISSION");
        loginAsStudent(student1InCourse1.getGoogleId());
        createMcqQuestion();
        createMcqResponseAsStudent();
        createCommentForStudentResponse();

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q6S1C1.getId().toString(),
        };
        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q6S1C1.getCommentText() + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.INSTRUCTORS), Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(comment1FromT1C1ToR1Q6S1C1.getId());
        assertEquals(comment1FromT1C1ToR1Q6S1C1.getCommentText() + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.STUDENTS, frc.commentGiverType);
        assertEquals(student1InCourse1.getEmail(), frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);

        ______TS("Typical successful case for INSTRUCTOR_SUBMISSION");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        createMcqResponseAsInstructor();
        createCommentForInstructorResponse();
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR2Q6S1C1.getId().toString(),
        };
        requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR2Q6S1C1.getCommentText() + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.INSTRUCTORS), Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        frc = logic.getFeedbackResponseComment(comment1FromT1C1ToR2Q6S1C1.getId());
        assertEquals(comment1FromT1C1ToR2Q6S1C1.getCommentText() + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertTrue(frc.isCommentFromFeedbackParticipant);
    }

    @Test
    protected void testExecute_emptyVisibilitySettings_shouldPass() {
        comment1FromT1C1ToR1Q1S1C1 = logic.getFeedbackResponseComment(response1ForQ1S1C1.getId(),
                comment1FromT1C1ToR1Q1S1C1.commentGiver, comment1FromT1C1ToR1Q1S1C1.createdAt);
        assertNotNull("response comment not found", comment1FromT1C1ToR1Q1S1C1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Null show comments and show giver permissions");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)", new ArrayList<>(), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);
    }

    @Test
    protected void testExecute_variousVisibilitySettings_shouldPass() {
        comment1FromT1C1ToR1Q1S1C1 = logic.getFeedbackResponseComment(response1ForQ1S1C1.getId(),
                comment1FromT1C1ToR1Q1S1C1.commentGiver, comment1FromT1C1ToR1Q1S1C1.createdAt);
        assertNotNull("response comment not found", comment1FromT1C1ToR1Q1S1C1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for unpublished session public to various recipients");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)", new ArrayList<>(), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
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
    }

    @Test
    protected void testExecute_nonExistingFeedbackResponse_shouldFail() {

        ______TS("Non-existent feedback response comment id");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS));
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        UpdateFeedbackResponseCommentAction action0 = action;
        assertThrows(EntityNotFoundException.class, () -> getJsonResult(action0));
    }

    @Test
    protected void testExecute_instructorIsNotCommentGiver_shouldPass() {
        comment1FromT1C1ToR1Q1S1C1 = logic.getFeedbackResponseComment(response1ForQ1S1C1.getId(),
                comment1FromT1C1ToR1Q1S1C1.commentGiver, comment1FromT1C1ToR1Q1S1C1.createdAt);
        assertNotNull("response comment not found", comment1FromT1C1ToR1Q1S1C1);
        loginAsInstructor(instructor2OfCourse1.getGoogleId());

        ______TS("Instructor is not feedback response comment giver");

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

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(comment1FromT1C1ToR1Q1S1C1.getId());
        assertEquals(comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited)", frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertEquals(instructor2OfCourse1.getEmail(), frc.lastEditorEmail);
        assertFalse(frc.isCommentFromFeedbackParticipant);
    }

    @Test
    protected void testExecute_typicalCasePublishedSession_shouldPass() throws Exception {
        comment1FromT1C1ToR1Q1S1C1 = logic.getFeedbackResponseComment(response1ForQ1S1C1.getId(),
                comment1FromT1C1ToR1Q1S1C1.commentGiver, comment1FromT1C1ToR1Q1S1C1.createdAt);
        assertNotNull("response comment not found", comment1FromT1C1ToR1Q1S1C1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Typical successful case for published session");

        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        FeedbackSessionsLogic.inst().publishFeedbackSession(
                comment1FromT1C1ToR1Q1S1C1.feedbackSessionName, comment1FromT1C1ToR1Q1S1C1.courseId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };

        FeedbackResponseCommentUpdateRequest requestBody = new FeedbackResponseCommentUpdateRequest(
                comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited for published session)",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS), new ArrayList<>());
        UpdateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(comment1FromT1C1ToR1Q1S1C1.getId());
        assertEquals(comment1FromT1C1ToR1Q1S1C1.commentText + " (Edited for published session)",
                frc.commentText);
        assertEquals(FeedbackParticipantType.INSTRUCTORS, frc.commentGiverType);
        assertEquals(instructor1OfCourse1.getEmail(), frc.commentGiver);
        assertFalse(frc.isCommentFromFeedbackParticipant);
    }

    @Test
    protected void testExecute_emptyCommentText_shouldFail() {
        comment1FromT1C1ToR1Q1S1C1 = logic.getFeedbackResponseComment(response1ForQ1S1C1.getId(),
                comment1FromT1C1ToR1Q1S1C1.commentGiver, comment1FromT1C1ToR1Q1S1C1.createdAt);
        assertNotNull("response comment not found", comment1FromT1C1ToR1Q1S1C1);
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("Unsuccessful case: empty comment text");
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
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
        createMcqQuestion();
        createMcqResponseAsStudent();
        createCommentForStudentResponse();
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q6S1C1.getId().toString(),
        };
        verifyHttpParameterFailure(invalidIntent1);

        ______TS("invalid intent FULL_DETAIL");
        createMcqResponseAsInstructor();
        createCommentForInstructorResponse();
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, comment1FromT1C1ToR2Q6S1C1.getId().toString(),
        };
        verifyHttpParameterFailure(invalidIntent2);
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

    @Test
    protected void testAccessControl_typicalSuccessfulCase_shouldPass() {

        ______TS("successful case for student submission");
        loginAsStudent(student1InCourse1.getGoogleId());
        createMcqQuestion();
        createMcqResponseAsStudent();
        createCommentForStudentResponse();
        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q6S1C1.getId().toString(),
        };
        verifyCanAccess(submissionParams);

        ______TS("successful case for instructor submission");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        createMcqResponseAsInstructor();
        createCommentForInstructorResponse();
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR2Q6S1C1.getId().toString(),
        };
        verifyCanAccess(submissionParams);

        ______TS("successful case for instructor result");
        comment1FromT1C1ToR1Q1S1C1 = logic.getFeedbackResponseComment(response1ForQ1S1C1.getId(),
                comment1FromT1C1ToR1Q1S1C1.commentGiver, comment1FromT1C1ToR1Q1S1C1.createdAt);
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q1S1C1.getId().toString(),
        };
        verifyCanAccess(submissionParams);

        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        verifyCanAccess(submissionParams);

    }

    @Test
    protected void testAccessControl_invalidIntent_shouldFail() {

        ______TS("invalid intent STUDENT_RESULT");
        loginAsStudent(student1InCourse1.getGoogleId());
        createMcqQuestion();
        createMcqResponseAsStudent();
        createCommentForStudentResponse();
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q6S1C1.getId().toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent1).checkAccessControl());

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q6S1C1.getId().toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent2).checkAccessControl());
    }

    @Test
    protected void testAccessControl_updateCommentForOthersResponse_shouldFail() {

        ______TS("students access other students session and give comments");
        loginAsStudent(student2InCourse1.getGoogleId());
        createMcqQuestion();
        createMcqResponseAsStudent();
        createCommentForStudentResponse();
        String[] submissionParamsStudentToStudents = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR1Q6S1C1.getId().toString(),
        };
        verifyCannotAccess(submissionParamsStudentToStudents);

        ______TS("instructors access other instructor's session and give comments");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        createMcqResponseAsInstructor();
        createCommentForInstructorResponse();
        String[] submissionParamsInstructorToInstructor = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment1FromT1C1ToR2Q6S1C1.getId().toString(),
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

    private void createMcqQuestion() {
        FeedbackMcqQuestionDetails questionDetails = new FeedbackMcqQuestionDetails();
        qn6InSession1InCourse1 = FeedbackQuestionAttributes.builder()
                .withCourseId(session1InCourse1.getCourseId())
                .withFeedbackSessionName(session1InCourse1.getFeedbackSessionName())
                .withGiverType(FeedbackParticipantType.SELF)
                .withRecipientType(FeedbackParticipantType.NONE)
                .withNumberOfEntitiesToGiveFeedbackTo(-100)
                .withQuestionNumber(6)
                .withShowGiverNameTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS))
                .withShowRecipientNameTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS))
                .withShowResponsesTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS))
                .withQuestionDetails(questionDetails)
                .build();
        try {
            FeedbackQuestionsLogic.inst().createFeedbackQuestion(qn6InSession1InCourse1);
            qn6InSession1InCourse1 = FeedbackQuestionsLogic.inst().getFeedbackQuestion(
                    session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 6);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void createMcqResponseAsStudent() {
        FeedbackMcqResponseDetails responseDetails = new FeedbackMcqResponseDetails();
        response1ForQ6S1C1 = FeedbackResponseAttributes.builder(qn6InSession1InCourse1.getFeedbackQuestionId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail())
                .withCourseId(session1InCourse1.getCourseId())
                .withFeedbackSessionName(session1InCourse1.getFeedbackSessionName())
                .withResponseDetails(responseDetails)
                .withGiverSection(student1InCourse1.getSection())
                .withRecipientSection(student1InCourse1.getSection())
                .build();
        try {
            FeedbackResponsesLogic.inst().createFeedbackResponse(response1ForQ6S1C1);
            response1ForQ6S1C1 = FeedbackResponsesLogic.inst().getFeedbackResponse(
                    qn6InSession1InCourse1.getId(), student1InCourse1.getEmail(), student1InCourse1.getEmail());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void createMcqResponseAsInstructor() {
        FeedbackMcqResponseDetails responseDetails = new FeedbackMcqResponseDetails();
        response2ForQ6S1C1 = FeedbackResponseAttributes.builder(qn6InSession1InCourse1.getFeedbackQuestionId(),
                instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail())
                .withCourseId(session1InCourse1.getCourseId())
                .withFeedbackSessionName(session1InCourse1.getFeedbackSessionName())
                .withResponseDetails(responseDetails)
                .build();
        try {
            FeedbackResponsesLogic.inst().createFeedbackResponse(response2ForQ6S1C1);
            response2ForQ6S1C1 = FeedbackResponsesLogic.inst().getFeedbackResponse(
                    qn6InSession1InCourse1.getId(), instructor1OfCourse1.getEmail(), instructor1OfCourse1.getEmail());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void createCommentForStudentResponse() {
        comment1FromT1C1ToR1Q6S1C1 = FeedbackResponseCommentAttributes.builder()
                .withCommentGiver(student1InCourse1.getEmail())
                .withCourseId(session1InCourse1.getCourseId())
                .withFeedbackQuestionId(qn6InSession1InCourse1.getFeedbackQuestionId())
                .withFeedbackSessionName(session1InCourse1.getFeedbackSessionName())
                .withCommentFromFeedbackParticipant(true)
                .withCommentGiverType(FeedbackParticipantType.STUDENTS)
                .withVisibilityFollowingFeedbackQuestion(true)
                .withFeedbackResponseId(response1ForQ6S1C1.getId())
                .withCommentText("Comment from students")
                .withShowCommentTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS))
                .withShowGiverNameTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS))
                .withGiverSection(student1InCourse1.getSection())
                .withReceiverSection(student1InCourse1.getSection())
                .build();
        try {
            FeedbackResponseCommentsLogic.inst().createFeedbackResponseComment(comment1FromT1C1ToR1Q6S1C1);
            List<FeedbackResponseCommentAttributes> comments = FeedbackResponseCommentsLogic.inst()
                    .getFeedbackResponseCommentForResponseFromParticipant(response1ForQ6S1C1.getId(), true);
            comment1FromT1C1ToR1Q6S1C1 = comments.get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void createCommentForInstructorResponse() {
        comment1FromT1C1ToR2Q6S1C1 = FeedbackResponseCommentAttributes.builder()
                .withCommentGiver(instructor1OfCourse1.getEmail())
                .withCourseId(session1InCourse1.getCourseId())
                .withFeedbackQuestionId(qn6InSession1InCourse1.getFeedbackQuestionId())
                .withFeedbackSessionName(session1InCourse1.getFeedbackSessionName())
                .withCommentFromFeedbackParticipant(true)
                .withCommentGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withVisibilityFollowingFeedbackQuestion(true)
                .withFeedbackResponseId(response2ForQ6S1C1.getId())
                .withCommentText("Comment from instructors")
                .withShowCommentTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS))
                .withShowGiverNameTo(Arrays.asList(FeedbackParticipantType.INSTRUCTORS))
                .build();
        try {
            FeedbackResponseCommentsLogic.inst().createFeedbackResponseComment(comment1FromT1C1ToR2Q6S1C1);
            List<FeedbackResponseCommentAttributes> comments = FeedbackResponseCommentsLogic.inst()
                    .getFeedbackResponseCommentForResponseFromParticipant(response2ForQ6S1C1.getId(), true);
            comment1FromT1C1ToR2Q6S1C1 = comments.get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
