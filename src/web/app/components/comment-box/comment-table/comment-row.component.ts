import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackResponseCommentModel } from "./comment-table-model";

@Component({
  selector: 'tm-comment-row',
  templateUrl: './comment-row.component.html',
  styleUrls: ['./comment-row.component.scss']
})
export class CommentRowComponent implements OnInit {
  @Input()
  isInEditMode: boolean = false;

  @Input()
  isDiscardButtonEnabled: boolean = true;

  @Input()
  commentModel: FeedbackResponseCommentModel = {
    commentText: '',
    commentGiver: '',
    createdAt: '',
    editedAt: '',
    responseGiver: '',
    responseReceipient: '',
  };

  @Output()
  closeCommentEditFormEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  deleteCommentEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  saveCommentEvent: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  triggerCloseCommentEditForm() {
    this.isInEditMode = false;
  }

  triggerSaveComment(data: any) {
    this.commentModel.commentText = '';
    this.saveCommentEvent.emit(data);
  }

  triggerEditCommentEvent() {
    this.isInEditMode = true;
  }

  triggerDeleteCommentEvent() {
    this.deleteCommentEvent.emit();
  }
}
