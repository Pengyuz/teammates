import { Component, Input, OnInit } from '@angular/core';
import { FeedbackResponseCommentModel } from "./comment-table-model";

@Component({
  selector: 'tm-comment-table',
  templateUrl: './comment-table.component.html',
  styleUrls: ['./comment-table.component.scss']
})
export class CommentTableComponent implements OnInit {

  @Input()
  comments: FeedbackResponseCommentModel[] = [
      {commentText: "this is a comment"},
      {commentText: "this is another comment"},
      {commentText: "comment"},
    ];

  isTableHidden: boolean = true;

  constructor() { }

  ngOnInit() {
  }

  triggerCloseCommentEditFormEvent() {
    this.isTableHidden = true;
    //TODO either close the whole table OR revert back to original comment
  }

  triggerDeleteCommentEvent(index: number) {
    this.comments.splice(index, 1);

    if (this.isTableEmpty()) {
      this.isTableHidden = true;
    }
  }
  triggerSaveCommentEvent(index: number, data: any) {
    // TODO if new comment add to comments list else update current comment
    console.log("saving comment");
  }

  isTableEmpty(): boolean {
    return this.comments.length == 0;
  }
}
