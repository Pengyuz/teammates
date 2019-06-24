package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Assumption;

/**
 * The API output format of {@link teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes}.
 */
public class FeedbackResponseCommentData extends ApiOutput {

    private long feedbackResponseCommentId;
    private String commentText;
    private String commentGiver;
    private String createdAt;
    private String updatedAt;

    private List<FeedbackVisibilityType> showGiverNameTo;
    private List<FeedbackVisibilityType> showCommentTo;

    public FeedbackResponseCommentData(FeedbackResponseCommentAttributes frc) {
        this.feedbackResponseCommentId = frc.getId();
        this.commentText = frc.getCommentText();
        this.commentGiver = frc.getCommentGiver();
        this.showGiverNameTo = convertToFeedbackVisibilityType(frc.getShowGiverNameTo());
        this.showCommentTo = convertToFeedbackVisibilityType(frc.getShowCommentTo());
        this.createdAt = frc.getCreatedAt().toString();
        this.updatedAt = frc.getLastEditedAt().toString();
    }

    /**
     * Converts a list of feedback participant type to a list of visibility type.
     */
    private List<FeedbackVisibilityType> convertToFeedbackVisibilityType(
            List<FeedbackParticipantType> feedbackParticipantTypeList) {
        return feedbackParticipantTypeList.stream().map(feedbackParticipantType -> {
            switch (feedbackParticipantType) {
            case INSTRUCTORS:
                return FeedbackVisibilityType.INSTRUCTORS;
            case STUDENTS:
                return FeedbackVisibilityType.STUDENTS;
            case GIVER:
                return FeedbackVisibilityType.GIVER;
            case OWN_TEAM_MEMBERS:
                return FeedbackVisibilityType.GIVER_TEAM_MEMBERS;
            case RECEIVER:
                return FeedbackVisibilityType.RECIPIENT;
            case RECEIVER_TEAM_MEMBERS:
                return FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS;
            default:
                Assumption.fail("Unknown feedbackParticipantType" + feedbackParticipantType);
                break;
            }
            return null;
        }).collect(Collectors.toList());
    }

    public long getFeedbackResponseCommentId() {
        return feedbackResponseCommentId;
    }

    public String getFeedbackCommentText() {
        return commentText;
    }

    public String getCommentGiver() {
        return commentGiver;
    }

    public List<FeedbackVisibilityType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public List<FeedbackVisibilityType> getShowCommentTo() {
        return showCommentTo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
