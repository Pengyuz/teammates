/**
 * Model for a comment to be displayed in the comment table
 */
export interface FeedbackResponseCommentModel {
  commentId: number;
  responseGiver: string;
  responseRecipient: string;
  createdAt: string;
  editedAt: string;
  commentGiver: string;
  commentText: string;
  isInEditMode: boolean;
  isEditable: boolean;
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