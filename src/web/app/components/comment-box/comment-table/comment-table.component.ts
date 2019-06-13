import { Component, Input, OnInit } from '@angular/core';
import { FeedbackResponseCommentModel } from "./comment-table-model";

@Component({
  selector: 'tm-comment-table',
  templateUrl: './comment-table.component.html',
  styleUrls: ['./comment-table.component.scss']
})
export class CommentTableComponent implements OnInit {

  @Input()
  comments: FeedbackResponseCommentModel[] = [];
  isTableHidden: boolean = true;

  constructor() { }

  ngOnInit() {
  }

  triggerCloseCommentEditForm() {
    this.isTableHidden = true;
    //TODO either close the whole table OR revert back to original comment
  }
}
