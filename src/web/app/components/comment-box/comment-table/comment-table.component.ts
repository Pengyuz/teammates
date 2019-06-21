import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmDeleteCommentModalComponent,
} from '../confirm-delete-comment-modal/confirm-delete-comment-modal.component';
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

  // The comments to be displayed in SESSION_RESULT mode.
  @Input()
  comments: FeedbackResponseCommentModel[] = [
    { commentText: 'this is a comment',
      commentGiver: 'someone',
      createdAt: 'time',
      editedAt: 'time',
      responseGiver: 'giver',
      responseRecipient: 'recipient',
    },
    { commentText: 'another comment',
      commentGiver: 'someone',
      createdAt: 'created at',
      editedAt: 'time',
      responseGiver: 'giver',
      responseRecipient: 'recipient',
    },
  ];

  // The comment to be displayed in SESSION_SUBMISSION mode.
  @Input()
  comment: FeedbackResponseCommentModel = {
    commentText: '',
    commentGiver: '',
    createdAt: '',
    editedAt: '',
    responseGiver: '',
    responseRecipient: '',
  };

  @Output()
  saveCommentEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  deleteCommentEvent: EventEmitter<any> = new EventEmitter();

  newComment: FeedbackResponseCommentModel = {
    commentText: '',
    commentGiver: '',
    createdAt: '',
    editedAt: '',
    responseGiver: '',
    responseRecipient: '',
  };

  isInEditMode: boolean[] = [];

  constructor(private modalService: NgbModal) { }

  ngOnInit(): void {
  }

  /**
   * Triggers the close comment edit form event.
   */
  triggerCloseCommentEditFormEvent(index: number): void {
    // TODO either close the whole table OR revert back to original comment
    this.isInEditMode[index] = false;
  }

  /**
   * Triggers the edit form event
   */
  triggerEditFormEvent(index: number): void {
    this.isInEditMode[index] = true;
  }

  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(index: number): void {
    const modalRef: NgbModalRef = this.modalService.open(ConfirmDeleteCommentModalComponent);

    modalRef.result.then(() => {
      // TODO: parent handling of event
      this.deleteCommentEvent.emit(index);

      this.comments.splice(index, 1);
    }
    , () => {});
  }

  /**
   * Triggers the save comment event.
   */
  triggerSaveCommentEvent(index: number, data: any): void {
    // TODO: parent handling of event and what data to pass through
    const comments: FeedbackResponseCommentModel[] = this.comments.slice();
    comments[index] = { ...comments[index], commentText: data };
    this.saveCommentEvent.emit(comments);
  }

  /**
   * Triggers the add new comment event.
   */
  triggerAddNewComment(data: any): void {
    // TODO parent handling of new comment
    this.saveCommentEvent.emit(data);
  }
}
