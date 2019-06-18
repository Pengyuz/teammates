import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { VisibilityStateMachine } from "../../../services/visibility-state-machine";
import { FeedbackParticipantType } from "../../../types/api-output";
import { CommentVisibilityControl } from '../../../types/comment-visibility-control';

@Component({
  selector: 'tm-comment-edit-form',
  templateUrl: './comment-edit-form.component.html',
  styleUrls: ['./comment-edit-form.component.scss']
})
export class CommentEditFormComponent implements OnInit {

  //enum
  CommentVisibilityControl: typeof CommentVisibilityControl = CommentVisibilityControl;
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;

  @Input() commentText: string = '';
  @Input() isDiscardButtonEnabled: boolean = true;
  @Input() isVisibilityOptionEnabled: boolean = true;
  @Input() placeholderText: string = 'Enter your comment here';

  @Output() closeCommentBoxEvent: EventEmitter<any> = new EventEmitter();
  @Output() commentDetailsChangeEvent: EventEmitter<any> = new EventEmitter();
  @Output() saveCommentEvent: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  triggerCommentDetailsChange(data: any) {
    this.commentDetailsChangeEvent.emit(data);
  }

  triggerCloseCommentBox(data: any) {
    this.closeCommentBoxEvent.emit(data);
  }

  triggerSaveComment(){
    if (this.commentText.trim() == '') {
      return;
    }

    this.saveCommentEvent.emit(this.commentText);
    this.commentText = '';
  }

}
