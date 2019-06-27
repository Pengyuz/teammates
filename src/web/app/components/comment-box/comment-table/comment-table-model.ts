/**
 * Model for a comment to be displayed in the comment table
 */
export interface FeedbackResponseCommentModel {
  responseGiver: string;
  responseRecipient: string;
  createdAt: string;
  editedAt: string;
  commentGiver: string;
  commentText: string;
  isInEditMode: boolean;
  isEditable: boolean;
}
