import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmDeleteCommentModalComponent } from '../confirm-delete-comment-modal/confirm-delete-comment-modal.component';
import { CommentTableMode, FeedbackResponseCommentModel } from './comment-table-model';

/**
 * Component for the comments table
 */
@Component({
  selector: 'tm-comment-table',
  templateUrl: './comment-table.component.html',
  styleUrls: ['./comment-table.component.scss'],
})
export class CommentTableComponent implements OnInit {

  // enum
  CommentTableMode: typeof CommentTableMode = CommentTableMode;

  // Determines the mode of the comments table.
  // In SESSION_RESULT mode, the table shows multiple comments.
  // In SESSION_SUBMISSION mode, the table only shows a single comment row.
  @Input() commentTableMode: CommentTableMode = CommentTableMode.SESSION_RESULT;

  @Input()
  comments: FeedbackResponseCommentModel[] = [
    { commentText: 'this is a comment',
      commentGiver: 'someone',
      createdAt: 'time',
      editedAt: 'time',
      responseGiver: 'giver',
      responseRecipient: 'recipient',
      isInEditMode: false,
      isEditable: true,
    },
  ];

  @Output()
  saveNewCommentEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  deleteCommentEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  updateCommentEvent: EventEmitter<any> = new EventEmitter();

  constructor(private modalService: NgbModal) { }

  ngOnInit(): void {
  }

  /**
   * Triggers the close comment edit form event.
   */
  triggerCloseCommentEditFormEvent(comment: FeedbackResponseCommentModel): void {
    comment.isInEditMode = false;
  }

  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(index: number): void {
    const modalRef: NgbModalRef = this.modalService.open(ConfirmDeleteCommentModalComponent);

    modalRef.result.then(() => {
      // TODO: parent handling of event
      this.deleteCommentEvent.emit(index);

      // TODO: remove this
      this.comments.splice(index, 1);
    }
    , () => {});
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent(index: number, data: any): void {
    // TODO: parent handling of event
    const comments: FeedbackResponseCommentModel[] = this.comments.slice();
    comments[index] = { ...comments[index], commentText: data , isInEditMode: false};
    this.updateCommentEvent.emit(comments);
  }

  /**
   * Toggles the comment model to edit mode.
   */
  triggerEditCommentEvent(comment: FeedbackResponseCommentModel): void {
    comment.isInEditMode = true;
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(data: any): void {
    // TODO parent handling of new comment

    this.saveNewCommentEvent.emit(data);
  }
}
