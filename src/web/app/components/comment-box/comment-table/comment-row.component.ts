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
  commentModel: FeedbackResponseCommentModel = {commentText: ''};

  @Output()
  closeCommentEditFormEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  deleteCommentEvent: EventEmitter<any> = new EventEmitter();

  commentText: string = 'COMMENT PLACEHOLDER';
  constructor() { }

  ngOnInit() {
    this.commentText = this.commentModel.commentText;
  }

  triggerCloseCommentEditForm() {
    this.isInEditMode = false;
  }

  triggerSaveComment(data: any) {
    this.commentText = data;
    this.isInEditMode = false;
  }

  triggerEditCommentEvent() {
    this.isInEditMode = true;
  }

  triggerDeleteCommentEvent() {
    this.deleteCommentEvent.emit();
  }
}
