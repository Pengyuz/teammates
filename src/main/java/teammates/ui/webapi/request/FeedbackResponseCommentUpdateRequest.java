package teammates.ui.webapi.request;

import teammates.ui.webapi.output.FeedbackVisibilityType;

import java.util.List;

/**
 * The update request of a feedback response comment.
 */
public class FeedbackResponseCommentUpdateRequest extends FeedbackResponseCommentBasicRequest {

    public FeedbackResponseCommentUpdateRequest(String commentText,
                                                List<FeedbackVisibilityType> showCommentTo,
                                                List<FeedbackVisibilityType> showGiverNameTo) {
        super(commentText, showCommentTo, showGiverNameTo);
    }
}
