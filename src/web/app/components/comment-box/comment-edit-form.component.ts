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

  isAddCommentButtonShown: boolean = this.isHidden;

  constructor() { }

  ngOnInit() {
  }

  triggerCommentDetailsChange(data: any) {
    console.log(data);
  }

  triggerCloseCommentBox(data: any) {
    this.isHidden = true;
    this.isAddCommentButtonShown = true;
    this.closeCommentBox.emit(data);
  }

}
