import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'tm-comment-edit-form',
  templateUrl: './comment-edit-form.component.html',
  styleUrls: ['./comment-edit-form.component.scss']
})
export class CommentEditFormComponent implements OnInit {

  @Input()
  commentText: string = 'placeholder text';

  @Output()
  closeCommentBox: EventEmitter<any> = new EventEmitter();

  @Output()
  commentDetailsChange: EventEmitter<any> = new EventEmitter();

  @Output()
  saveComment: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  triggerCommentDetailsChange(data: any) {
    this.commentDetailsChange.emit(data);
  }

  triggerCloseCommentBox(data: any) {
    // TODO this should revert back to original comment which might not be empty

    this.closeCommentBox.emit(data);
  }

  triggerSaveComment(){
    this.saveComment.emit();
  }

}
