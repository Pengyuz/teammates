import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackResponseCommentModel } from './comment-table-model';

/**
 * Comment row component to be used in a comment table
 */
@Component({
  selector: 'tm-comment-row',
  templateUrl: './comment-row.component.html',
  styleUrls: ['./comment-row.component.scss'],
})
export class CommentRowComponent implements OnInit {
  @Input() isInEditMode: boolean = false;
  @Input() isVisibilityOptionEnabled: boolean = true;
  @Input() isDiscardButtonEnabled: boolean = true;

  @Input()
  commentModel: FeedbackResponseCommentModel = {
    commentText: '',
    commentGiver: '',
    createdAt: '',
    editedAt: '',
    responseGiver: '',
    responseRecipient: '',
  };

  @Output()
  closeCommentEditFormEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  editCommentEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  deleteCommentEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  saveCommentEvent: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Disable edit mode.
   */
  triggerCloseCommentEditForm(): void {
    this.closeCommentEditFormEvent.emit();
  }

  /**
   * Change to edit mode.
   */
  triggerEditCommentEvent(): void {
    this.editCommentEvent.emit();
  }

  /**
   * Triggers the save comment event.
   */
  triggerSaveCommentEvent(data: any): void {
    this.saveCommentEvent.emit(data);
  }

  /**
   * Triggers the delete comment event
   */
  triggerDeleteCommentEvent(): void {
    this.deleteCommentEvent.emit();
  }
}
