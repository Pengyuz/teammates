package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
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
    private FeedbackResponseAttributes response1ForQ1S1C1;
    private FeedbackResponseAttributes response1ForQ6S1C1;
    private FeedbackResponseAttributes response2ForQ6S1C1;
    private StudentAttributes student1InCourse1;
    private StudentAttributes student2InCourse1;
    private FeedbackQuestionAttributes qn1InSession1InCourse1;
    private FeedbackQuestionAttributes qn6InSession1InCourse1;

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
        student2InCourse1 = typicalBundle.students.get("student2InCourse1");
        session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), 1);
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        response1ForQ1S1C1 = logic.getFeedbackResponse(qn1InSession1InCourse1.getId(),
                student1InCourse1.getEmail(), student1InCourse1.getEmail());
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        instructor2OfCourse1 = typicalBundle.instructors.get("instructor2OfCourse1");
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
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
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
                getInstructorComments(response1ForQ1S1C1.getId(), "Comment to first response");
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
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest("Empty giver permissions",
                new ArrayList<>(), new ArrayList<>());
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);
    }

    @Test
    public void testExecute_unpublishedSessionValidVisibilitySettings_shouldPass() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());

        ______TS("typical successful case for unpublished session shown to various recipients");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest("Null comment permissions",
                new ArrayList<>(), new ArrayList<>());
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        requestBody = new FeedbackResponseCommentCreateRequest("Comment shown to giver",
                Arrays.asList(FeedbackVisibilityType.GIVER), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        requestBody = new FeedbackResponseCommentCreateRequest("Comment shown to receiver",
                Arrays.asList(FeedbackVisibilityType.RECIPIENT), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        requestBody =
                new FeedbackResponseCommentCreateRequest("Comment shown to own team members",
                        Arrays.asList(FeedbackVisibilityType.GIVER_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        requestBody = new FeedbackResponseCommentCreateRequest(
                "Comment shown to receiver team members",
                Arrays.asList(FeedbackVisibilityType.GIVER_TEAM_MEMBERS), new ArrayList<>());
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
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
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest(
                "Comment to first response, published session",
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.GIVER, FeedbackVisibilityType.INSTRUCTORS));
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        List<FeedbackResponseCommentAttributes> frcList = getInstructorComments(response1ForQ1S1C1.getId(),
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
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ1S1C1.getId(),
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
        createMcqQuestion();
        createMcqResponseAsStudent();
        String[] submissionParams = new String[] {
          Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
          Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ6S1C1.getId(),
        };

        FeedbackResponseCommentCreateRequest requestBody = new FeedbackResponseCommentCreateRequest(
                "Student submission comment", Arrays.asList(FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        CreateFeedbackResponseCommentAction action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        List<FeedbackResponseCommentAttributes> comments = logic.getFeedbackResponseCommentsForResponseFromParticipant(
                response1ForQ6S1C1.getId(), true);
        assertEquals(comments.size(), 1);
        FeedbackResponseCommentAttributes comment = comments.get(0);
        assertEquals(comment.getCommentText(), "Student submission comment");

        ______TS("Successful case: instructor submission");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        createMcqResponseAsInstructor();
        submissionParams = new String[] {
          Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
          Const.ParamsNames.FEEDBACK_RESPONSE_ID, response2ForQ6S1C1.getId(),
        };

        requestBody = new FeedbackResponseCommentCreateRequest(
                "Instructor submission comment", Arrays.asList(FeedbackVisibilityType.INSTRUCTORS),
                Arrays.asList(FeedbackVisibilityType.INSTRUCTORS));
        action = getAction(requestBody, submissionParams);
        getJsonResult(action);

        comments = logic.getFeedbackResponseCommentsForResponseFromParticipant(
                response2ForQ6S1C1.getId(), true);
        assertEquals(comments.size(), 1);
        comment = comments.get(0);
        assertEquals(comment.getCommentText(), "Instructor submission comment");
    }

    @Test
    protected void testExecute_invalidIntent_shouldFail() {

        ______TS("invalid intent STUDENT_RESULT");
        createMcqQuestion();
        createMcqResponseAsStudent();
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ6S1C1.getId(),
        };
        verifyHttpParameterFailure(invalidIntent1);

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ6S1C1.getId(),
        };
        verifyHttpParameterFailure(invalidIntent2);
    }

    @Test
    protected void testAccessControl_submitCommentForOthersResponse_shouldFail() {

        ______TS("students access other students session and give comments");
        loginAsStudent(student2InCourse1.getGoogleId());
        createMcqQuestion();
        createMcqResponseAsStudent();
        String[] submissionParamsStudentToStudents = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ6S1C1.getId(),
        };
        verifyCannotAccess(submissionParamsStudentToStudents);

        ______TS("students access instructor's session and give comments");
        createMcqResponseAsInstructor();
        String[] submissionParamsStudentToInstructor = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response2ForQ6S1C1.getId(),
        };

        verifyCannotAccess(submissionParamsStudentToInstructor);

        ______TS("instructors access other instructor's session and give comments");
        loginAsInstructor(instructor2OfCourse1.getGoogleId());
        String[] submissionParamsInstructorToInstructor = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response2ForQ6S1C1.getId(),
        };
        verifyCannotAccess(submissionParamsInstructorToInstructor);

        ______TS("instructor access student's session and give comments");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] submissionParamsInstructorToStudent = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ6S1C1.getId(),
        };
        verifyCannotAccess(submissionParamsInstructorToStudent);

    }

    @Test
    protected void testAccessControl_invalidIntent_shouldFail() {

        ______TS("invalid intent STUDENT_RESULT");
        createMcqQuestion();
        createMcqResponseAsStudent();
        String[] invalidIntent1 = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ6S1C1.getId(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent1).checkAccessControl());

        ______TS("invalid intent FULL_DETAIL");
        String[] invalidIntent2 = new String[] {
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ6S1C1.getId(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(invalidIntent2).checkAccessControl());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackResponseCommentAttributes comment = FeedbackResponseCommentAttributes
                .builder()
                .withCourseId(session1InCourse1.getCourseId())
                .withFeedbackSessionName(session1InCourse1.getFeedbackSessionName())
                .withCommentGiver(student1InCourse1.getEmail())
                .withCommentText("")
                .withFeedbackQuestionId(qn1InSession1InCourse1.getId())
                .withFeedbackResponseId(response1ForQ1S1C1.getId())
                .build();

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
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
        try{
            FeedbackQuestionsLogic.inst().createFeedbackQuestion(qn6InSession1InCourse1);
            qn6InSession1InCourse1 =  FeedbackQuestionsLogic.inst().getFeedbackQuestion(session1InCourse1.getFeedbackSessionName(),
                    session1InCourse1.getCourseId(), 6);
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
        try{
            FeedbackResponsesLogic.inst().createFeedbackResponse(response1ForQ6S1C1);
            response1ForQ6S1C1 = FeedbackResponsesLogic.inst().getFeedbackResponse(qn6InSession1InCourse1.getId(), student1InCourse1.getEmail(),
                    student1InCourse1.getEmail());
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
            response2ForQ6S1C1 = FeedbackResponsesLogic.inst().getFeedbackResponse(qn6InSession1InCourse1.getId(), instructor1OfCourse1.getEmail(),
                    instructor1OfCourse1.getEmail());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
