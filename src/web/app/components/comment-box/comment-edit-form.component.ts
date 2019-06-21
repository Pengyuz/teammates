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
  @Input() placeholderText: string = 'Enter your comment here';

  @Output() closeCommentBoxEvent: EventEmitter<any> = new EventEmitter();
  @Output() commentDetailsChangeEvent: EventEmitter<any> = new EventEmitter();
  @Output() saveCommentEvent: EventEmitter<any> = new EventEmitter();

  updatedCommentText: string = '';

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Updates the comment text
   */
  triggerCommentTextChange(data: any): void {
    this.updatedCommentText = data;
  }

  /**
   * Triggers close comment box event.
   */
  triggerCloseCommentBox(data: any): void {
    this.closeCommentBoxEvent.emit(data);
  }

  /**
   * Triggers save comment event.
   */
  triggerSaveComment(): void {
    if (this.updatedCommentText.trim() === '') {
      return;
    }

    this.saveCommentEvent.emit(this.updatedCommentText);
  }

}
