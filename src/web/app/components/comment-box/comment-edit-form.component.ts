import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackParticipantType } from '../../../types/api-output';
import { CommentVisibilityControl } from '../../../types/comment-visibility-control';

/**
 * Comment edit form component
 */
@Component({
  selector: 'tm-comment-edit-form',
  templateUrl: './comment-edit-form.component.html',
  styleUrls: ['./comment-edit-form.component.scss'],
})
export class CommentEditFormComponent implements OnInit {

  // enum
  CommentVisibilityControl: typeof CommentVisibilityControl = CommentVisibilityControl;
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;

  @Input() commentText: string = '';
  @Input() isDiscardButtonEnabled: boolean = true;
  @Input() isVisibilityOptionEnabled: boolean = true;
  @Input() isSaveButtonEnabled: boolean = true;
  @Input() placeholderText: string = 'Enter your comment here';

  @Output() closeCommentBoxEvent: EventEmitter<any> = new EventEmitter();
  @Output() commentDetailsChangeEvent: EventEmitter<any> = new EventEmitter();
  @Output() saveCommentEvent: EventEmitter<any> = new EventEmitter();
  @Output() commentFormChangeEvent: EventEmitter<any> = new EventEmitter();
  updatedCommentText: string = '';

  constructor() { }

  ngOnInit(): void {
    this.updatedCommentText = this.commentText;
  }

  /**
   * Trigger comment form change event.
   */
  triggerCommentFormChangeEvent(commentText: any): void {
    this.commentFormChangeEvent.emit(commentText);
  }

  /**
   * Triggers close comment box event.
   */
  triggerCloseCommentBoxEvent(data: any): void {
    this.closeCommentBoxEvent.emit(data);
  }

  /**
   * Triggers save comment event.
   */
  triggerSaveCommentEvent(): void {
    if (this.updatedCommentText.trim() === '') {
      return;
    }

    this.saveCommentEvent.emit(this.updatedCommentText);
    this.updatedCommentText = '';
  }

}
