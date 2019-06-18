package teammates.ui.webapi.request;

import teammates.ui.webapi.output.FeedbackVisibilityType;

import java.util.List;

/**
 * The create request of a feedback response comment.
 */
public class FeedbackResponseCommentCreateRequest extends FeedbackResponseCommentBasicRequest {

    public FeedbackResponseCommentCreateRequest(String commentText,
                                                List<FeedbackVisibilityType> showCommentTo,
                                                List<FeedbackVisibilityType> showGiverNameTo) {
        super(commentText, showCommentTo, showGiverNameTo);
    }
}
