package teammates.test.cases.webapi;

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
import teammates.common.util.Const;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.ui.webapi.action.GetFeedbackResponseCommentsAction;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackResponseCommentData;
import teammates.ui.webapi.output.FeedbackResponseCommentsData;

/**
 * SUT: {@link GetFeedbackResponseCommentsAction}.
 */
public class GetFeedbackResponseCommentsActionTest extends BaseActionTest<GetFeedbackResponseCommentsAction> {

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
    private FeedbackResponseCommentAttributes comment1FromT1C1ToR1Q6S1C1;
    private FeedbackResponseCommentAttributes comment1FromT1C1ToR2Q6S1C1;

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
    public void testExecute() {
        // ses individual test cases
    }

    @Test
    protected void testExecute_typicalSuccessCase_shouldPass() {

        ______TS("typical successful case as student_submission");
        loginAsStudent(student1InCourse1.getGoogleId());
        createMcqQuestion();
        createMcqResponseAsStudent();
        createCommentForStudentResponse();

        String[] submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response1ForQ6S1C1.getId(),
        };

        FeedbackResponseCommentsData actualComments = getFeedbackResponseComments(submissionParams);
        List<FeedbackResponseCommentData> comments = actualComments.getComments();
        assertEquals(comments.size(), 1);
        FeedbackResponseCommentData actual = comments.get(0);
        FeedbackResponseCommentAttributes expected = logic.getFeedbackResponseCommentsForResponseFromParticipant(
                response1ForQ6S1C1.getId(), true).get(0);
        assertNotNull(actual.getFeedbackResponseCommentId());
        assertEquals(actual.getFeedbackCommentText(), expected.getCommentText());
        assertEquals(actual.getCommentGiver(), expected.getCommentGiver());

        ______TS("typical successful case as instructor_submission");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        createMcqResponseAsInstructor();
        createCommentForInstructorResponse();
        submissionParams = new String[] {
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, response2ForQ6S1C1.getId(),
        };
        actualComments = getFeedbackResponseComments(submissionParams);
        comments = actualComments.getComments();
        assertEquals(comments.size(), 1);
        actual = comments.get(0);
        expected = logic.getFeedbackResponseCommentsForResponseFromParticipant(
                response2ForQ6S1C1.getId(), true).get(0);
        assertNotNull(actual.getFeedbackResponseCommentId());
        assertEquals(actual.getFeedbackCommentText(), expected.getCommentText());
        assertEquals(actual.getCommentGiver(), expected.getCommentGiver());

        ______TS("typical successful as student_result");

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        // see individual test cases
    }

    private FeedbackResponseCommentsData getFeedbackResponseComments(String[] params) {
        GetFeedbackResponseCommentsAction action = getAction(params);
        JsonResult actualResult = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, actualResult.getStatusCode());
        return (FeedbackResponseCommentsData) actualResult.getOutput();
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
