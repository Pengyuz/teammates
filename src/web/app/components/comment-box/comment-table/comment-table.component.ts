import { Component, Input, OnInit } from '@angular/core';
import { FeedbackResponseComment } from "../../../../types/api-output";

@Component({
  selector: 'tm-comment-table',
  templateUrl: './comment-table.component.html',
  styleUrls: ['./comment-table.component.scss']
})
export class CommentTableComponent implements OnInit {

  @Input()
  commentsData: FeedbackResponseComment[] = [];
  isTableHidden: boolean = true;

  constructor() { }

  ngOnInit() {
  }

  triggerCloseCommentEditForm() {
    this.isTableHidden = true;
    //TODO either close the whole table OR revert back to original comment
  }
}
