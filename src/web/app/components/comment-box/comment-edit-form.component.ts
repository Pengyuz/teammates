import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'tm-comment-edit-form',
  templateUrl: './comment-edit-form.component.html',
  styleUrls: ['./comment-edit-form.component.scss']
})
export class CommentEditFormComponent implements OnInit {

  @Input()
  commentText: string = '';

  @Input()
  isDiscardButtonEnabled: boolean = true;

  @Input()
  isVisibilityOptionEnabled: boolean = true;

  @Input()
  placeholderText: string = 'Enter your comment here';

  @Output()
  closeCommentBoxEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  commentDetailsChangeEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  saveCommentEvent: EventEmitter<any> = new EventEmitter();

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
    this.saveCommentEvent.emit(this.commentText);
    this.commentText = '';
  }

}
