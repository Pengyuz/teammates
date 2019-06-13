import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'tm-comment-row',
  templateUrl: './comment-row.component.html',
  styleUrls: ['./comment-row.component.scss']
})
export class CommentRowComponent implements OnInit {
  @Input()
  isInEditMode: boolean = false;

  @Output()
  closeCommentEditForm: EventEmitter<any> = new EventEmitter<any>();

  commentText: string = 'COMMENT PLACEHOLDER';
  constructor() { }

  ngOnInit() {
  }

  triggerCloseCommentEditForm() {
    this.closeCommentEditForm.emit();
  }

  triggerEditComment() {
    this.isInEditMode = true;
  }

  triggerSaveComment() {
    this.isInEditMode = false;
  }

  triggerDeleteComment() {
    this.closeCommentEditForm.emit();
  }
}
