import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CommentTableMode, FeedbackResponseCommentModel } from '../comment-table/comment-table-model';

/**
 * Modal for the comments table
 */
@Component({
  selector: 'tm-comment-table-modal',
  templateUrl: './comment-table-modal.component.html',
  styleUrls: ['./comment-table-modal.component.scss'],
})
export class CommentTableModalComponent implements OnInit {

  @Input() response: any = '';
  @Input() questionDetails: any = '';
  @Input() comments: FeedbackResponseCommentModel[] = [];

  @Output() deleteCommentEvent: EventEmitter<any> = new EventEmitter();
  @Output() updateCommentEvent: EventEmitter<any> = new EventEmitter();
  @Output() saveCommentEvent: EventEmitter<any> = new EventEmitter();

  commentTableMode: CommentTableMode = CommentTableMode.INSTRUCTOR_RESULT;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(commentId: number): void {
    this.deleteCommentEvent.emit(commentId);
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent(commentData: any): void {
    this.updateCommentEvent.emit(commentData);
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(commentText: string): void {
    this.saveCommentEvent.emit(commentText);
  }
}
