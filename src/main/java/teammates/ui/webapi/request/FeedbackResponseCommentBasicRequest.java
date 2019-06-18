package teammates.ui.webapi.request;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Assumption;
import teammates.ui.webapi.output.FeedbackVisibilityType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The basic request of modifying a feedback response comment.
 */
public class FeedbackResponseCommentBasicRequest extends BasicRequest {

    private String commentText;

    private List<FeedbackVisibilityType> showCommentTo;
    private List<FeedbackVisibilityType> showGiverNameTo;

    public FeedbackResponseCommentBasicRequest(String commentText,
                                               List<FeedbackVisibilityType> showCommentTo,
                                               List<FeedbackVisibilityType> showGiverNameTo) {
        this.commentText = commentText;
        this.showCommentTo = showCommentTo;
        this.showGiverNameTo = showGiverNameTo;
    }

    @Override
    public void validate() {
        assertTrue(commentText != null, "Comment Text can't be null");
//        assertTrue(showCommentTo != null, "showCommentTo can't be null");
//        assertTrue( showGiverNameTo != null, "showGiverNameTo can't be null");
    }

    public String getCommentText() {
        return commentText;
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
                case INSTRUCTORS:
                    return FeedbackParticipantType.INSTRUCTORS;
                case GIVER:
                    return FeedbackParticipantType.GIVER;
                default:
                    Assumption.fail("Unknown feedbackVisibilityType" + feedbackVisibilityType);
                    break;
            }
            return null;
        }).collect(Collectors.toList());
    }
}
