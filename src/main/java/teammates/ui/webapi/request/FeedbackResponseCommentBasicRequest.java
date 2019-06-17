package teammates.ui.webapi.request;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Assumption;
import teammates.ui.webapi.output.FeedbackVisibilityType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The basic request of modifying a feedback response comment.
 */
public class FeedbackResponseCommentBasicRequest extends BasicRequest {

    private String commentText;
    private String commentGiver;

    private List<FeedbackVisibilityType> showCommentTo;
    private List<FeedbackVisibilityType> showGiverNameTo;

    public FeedbackResponseCommentBasicRequest(String commentText,
                                               String commentGiver,
                                               List<FeedbackVisibilityType> showCommentTo,
                                               List<FeedbackVisibilityType> showGiverNameTo) {
        this.commentText = commentText;
        this.commentGiver = commentGiver;
        this.showCommentTo = showCommentTo;
        this.showGiverNameTo = showGiverNameTo;
    }

    @Override
    public void validate() {
        assertTrue( commentGiver != null, "Comment Giver can't be null");
        assertTrue(commentText != null, "Comment Text can't be null");
        assertTrue(showCommentTo != null, "showCommentTo can't be null");
        assertTrue( showGiverNameTo != null, "showGiverNameTo can't be null");
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCommentGiver() {
        return commentGiver;
    }

    public List<FeedbackParticipantType> getShowCommentTo() {
        return convertToFeedbackParticipantType(showCommentTo);
    }

    public List<FeedbackParticipantType> getShowGiverNameTo() {
        return convertToFeedbackParticipantType(showGiverNameTo);
    }

    /**
     * Converts a list of feedback visibility type to a list of feedback participant type.
     */
    private List<FeedbackParticipantType> convertToFeedbackParticipantType(
            List<FeedbackVisibilityType> feedbackVisibilityTypes) {
        return feedbackVisibilityTypes.stream().map(feedbackVisibilityType -> {
            switch (feedbackVisibilityType) {
                case STUDENTS:
                    return FeedbackParticipantType.STUDENTS;
                case INSTRUCTORS:
                    return FeedbackParticipantType.INSTRUCTORS;
                case RECIPIENT:
                    return FeedbackParticipantType.RECEIVER;
                case GIVER_TEAM_MEMBERS:
                    return FeedbackParticipantType.OWN_TEAM_MEMBERS;
                case RECIPIENT_TEAM_MEMBERS:
                    return FeedbackParticipantType.RECEIVER_TEAM_MEMBERS;
                default:
                    Assumption.fail("Unknown feedbackVisibilityType" + feedbackVisibilityType);
                    break;
            }
            return null;
        }).collect(Collectors.toList());
    }
}
