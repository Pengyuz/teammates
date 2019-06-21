/**
 * Model for comments to be displayed in the comment table
 */
export interface FeedbackResponseCommentModel {
  responseGiver: string;
  responseRecipient: string;
  createdAt: string;
  editedAt: string;
  commentGiver: string;
  commentText: string;
}

/**
 * The display mode of the comments table
 */
export enum CommentTableMode {
  /**
   * Session submission mode.
   */
  SESSION_SUBMISSION,
  /**
   * Session result mode.
   */
  SESSION_RESULT,
}
