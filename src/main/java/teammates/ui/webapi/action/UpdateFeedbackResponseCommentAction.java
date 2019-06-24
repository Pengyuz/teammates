package teammates.ui.webapi.action;

import java.time.Instant;
import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.webapi.output.FeedbackResponseCommentData;
import teammates.ui.webapi.request.FeedbackResponseCommentUpdateRequest;

/**
 * Updates a feedback response comment.
 */
public class UpdateFeedbackResponseCommentAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        if (frc == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Feedback response comment is not found"));
        }

        String courseId = frc.courseId;
        String feedbackResponseId = frc.feedbackResponseId;
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        String feedbackSessionName = frc.feedbackSessionName;
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        Assumption.assertNotNull(response);
        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.id);
            Assumption.assertNotNull(student);
            gateKeeper.verifyOwnership(frc, student.email);
            break;
        case INSTRUCTOR_SUBMISSION:
            InstructorAttributes instructor1 = logic.getInstructorForGoogleId(courseId, userInfo.id);
            Assumption.assertNotNull(instructor1);
            gateKeeper.verifyOwnership(frc, instructor1.email);
            break;
        case INSTRUCTOR_RESULT:
            InstructorAttributes instructor2 = logic.getInstructorForGoogleId(courseId, userInfo.id);
            if (instructor2 != null && frc.commentGiver.equals(instructor2.email)) { // giver, allowed by default
                return;
            }
            gateKeeper.verifyAccessible(instructor2, session, response.giverSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            gateKeeper.verifyAccessible(instructor2, session, response.recipientSection,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }
    }

    @Override
    public ActionResult execute() {
        long feedbackResponseCommentId = getLongRequestParamValue(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID);

        FeedbackResponseCommentAttributes frc = logic.getFeedbackResponseComment(feedbackResponseCommentId);
        if (frc == null) {
            throw new EntityNotFoundException(
                    new EntityDoesNotExistException("Feedback response comment is not found"));
        }

        String feedbackResponseId = frc.feedbackResponseId;
        String courseId = frc.courseId;
        FeedbackResponseAttributes response = logic.getFeedbackResponse(feedbackResponseId);
        Assumption.assertNotNull(response);

        Intent intent = Intent.valueOf(getNonNullRequestParamValue(Const.ParamsNames.INTENT));
        String email;

        switch (intent) {
        case STUDENT_SUBMISSION:
            StudentAttributes student = logic.getStudentForGoogleId(courseId, userInfo.id);
            email = student.getEmail();
            break;
        case INSTRUCTOR_SUBMISSION:
        case INSTRUCTOR_RESULT:
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            email = instructor.getEmail();
            break;
        default:
            throw new InvalidHttpParameterException("Unknown intent " + intent);
        }

        FeedbackResponseCommentUpdateRequest comment = getAndValidateRequestBody(FeedbackResponseCommentUpdateRequest.class);

        // Edit comment text
        String commentText = comment.getCommentText();
        if (commentText.trim().isEmpty()) {
            return new JsonResult(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, HttpStatus.SC_BAD_REQUEST);
        }

        List<FeedbackParticipantType> showCommentTo = comment.getShowCommentTo();
        List<FeedbackParticipantType> showGiverNameTo = comment.getShowGiverNameTo();

        FeedbackResponseCommentAttributes.UpdateOptions.Builder commentUpdateOptions =
                FeedbackResponseCommentAttributes.updateOptionsBuilder(feedbackResponseCommentId)
                        .withCommentText(commentText)
                        .withShowCommentTo(showCommentTo)
                        .withShowGiverNameTo(showGiverNameTo)
                        .withLastEditorEmail(email)
                        .withLastEditorAt(Instant.now());

        // edit visibility settings

        FeedbackResponseCommentAttributes updatedComment = null;
        try {
            updatedComment = logic.updateFeedbackResponseComment(commentUpdateOptions.build());
            logic.putDocument(updatedComment);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_NOT_FOUND);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        return new JsonResult(new FeedbackResponseCommentData(updatedComment));
    }

}
