package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.webapi.output.FeedbackResponseCommentData;
import teammates.ui.webapi.request.FeedbackResponseCommentCreateRequest;

/**
 * Creates a new feedback response comment.
 */
public class CreateFeedbackResponseCommentAction extends BasicFeedbackSubmissionAction {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String feedbackResponseId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        String courseId = response.courseId;
        String feedbackSessionName = response.feedbackSessionName;
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        String questionId = response.feedbackQuestionId;
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(questionId);
        FeedbackQuestionType questionType = question.getQuestionType();
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));

        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes studentAttributes = logic.getStudentForGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(studentAttributes, session);
            validQuestionTypeForCommentInSubmission(questionType);
            verifyCommentNotExist(feedbackResponseId);
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor, session, response.giverSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor, session, response.recipientSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            validQuestionTypeForCommentInSubmission(questionType);
            verifyCommentNotExist(feedbackResponseId);
            break;
        case INSTRUCTOR_RESULT:
            InstructorAttributes instructor1 = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor1, session, response.giverSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor1, session, response.recipientSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public ActionResult execute() {
        String feedbackResponseId = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_ID);

        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        FeedbackResponseCommentCreateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentCreateRequest.class);

        String commentText = comment.getCommentText();
        if (commentText.trim().isEmpty()) {
            return new JsonResult(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, HttpStatus.SC_BAD_REQUEST);
        }

        String courseId = response.courseId;
        String email;

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        boolean isFromParticipant;
        boolean isFollowingQuestionVisibility;
        FeedbackParticipantType commentGiverType;
        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.getId());
            email = student.getEmail();
            isFromParticipant = true;
            isFollowingQuestionVisibility = true;
            commentGiverType = FeedbackParticipantType.STUDENTS;
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            email = instructor.getEmail();
            isFromParticipant = true;
            isFollowingQuestionVisibility = true;
            commentGiverType = FeedbackParticipantType.INSTRUCTORS;
            break;
        case INSTRUCTOR_RESULT:
            InstructorAttributes instructor1 = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            email = instructor1.getEmail();
            isFromParticipant = false;
            isFollowingQuestionVisibility = false;
            commentGiverType = FeedbackParticipantType.INSTRUCTORS;
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        String feedbackQuestionId = response.feedbackQuestionId;
        String feedbackSessionName = response.feedbackSessionName;

        FeedbackResponseCommentAttributes feedbackResponseComment = FeedbackResponseCommentAttributes
                .builder()
                .withCourseId(courseId)
                .withFeedbackSessionName(feedbackSessionName)
                .withCommentGiver(email)
                .withCommentText(commentText)
                .withFeedbackQuestionId(feedbackQuestionId)
                .withFeedbackResponseId(feedbackResponseId)
                .withGiverSection(response.giverSection)
                .withReceiverSection(response.recipientSection)
                .withCommentFromFeedbackParticipant(isFromParticipant)
                .withCommentGiverType(commentGiverType)
                .withVisibilityFollowingFeedbackQuestion(isFollowingQuestionVisibility)
                .withShowCommentTo(comment.getShowCommentTo())
                .withShowGiverNameTo(comment.getShowGiverNameTo())
                .build();

        FeedbackResponseCommentAttributes createdComment = null;
        try {
            createdComment = logic.createFeedbackResponseComment(feedbackResponseComment);
            logic.putDocument(createdComment);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_NOT_FOUND);
        } catch (EntityAlreadyExistsException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_CONFLICT);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        return new JsonResult(new FeedbackResponseCommentData(createdComment));
    }

}
