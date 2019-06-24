package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;

/**
 * The API output format for a list of {@link FeedbackResponseCommentData}.
 */
public class FeedbackResponseCommentsData extends ApiOutput {
    private List<FeedbackResponseCommentData> comments;

    public FeedbackResponseCommentsData(List<FeedbackResponseCommentAttributes> commentAttributesList) {
        comments = commentAttributesList.stream().map(FeedbackResponseCommentData::new).collect(Collectors.toList());
    }

    public List<FeedbackResponseCommentData> getComments() {
        return comments;
    }
}
