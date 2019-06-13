import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'tm-comment-edit-form',
  templateUrl: './comment-edit-form.component.html',
  styleUrls: ['./comment-edit-form.component.scss']
})
export class CommentEditFormComponent implements OnInit {

  @Input()
  isHidden: boolean = true;

  @Output()
  closeCommentBox: EventEmitter<any> = new EventEmitter();

  @Output()
  commentDetailsChange: EventEmitter<any> = new EventEmitter();

  commentText: string = '';

  isAddCommentButtonShown: boolean = true;

  constructor() { }

  ngOnInit() {
    this.isAddCommentButtonShown = this.isHidden;
  }

  triggerCommentDetailsChange(data: any) {
    this.commentDetailsChange.emit(data);
  }

  triggerCloseCommentBox(data: any) {
    this.isHidden = true;
    this.isAddCommentButtonShown = true;

    this.triggerCommentDetailsChange('');
    // TODO this should revert back to original comment which might not be empty
    this.commentText = '';

    this.closeCommentBox.emit(data);
  }

}
