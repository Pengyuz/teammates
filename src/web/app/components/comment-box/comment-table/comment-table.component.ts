import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackResponseCommentModel } from "./comment-table-model";

@Component({
  selector: 'tm-comment-table',
  templateUrl: './comment-table.component.html',
  styleUrls: ['./comment-table.component.scss']
})
export class CommentTableComponent implements OnInit {

  @Input()
  comments: FeedbackResponseCommentModel[] = [
      { commentText: "this is a comment",
        commentGiver: "Someone",
        createdAt: "time",
        editedAt: "time",
        responseGiver: "responseGiver",
        responseReceipient: "receipient",
      },
      { commentText: "this is another comment",
        commentGiver: "Someone",
        createdAt: "time",
        editedAt: "time",
        responseGiver: "responseGiver",
        responseReceipient: "receipient",
      },
    ];

  @Output()
  saveCommentEvent: EventEmitter<any> = new EventEmitter();

  @Output()
  deleteCommentEvent: EventEmitter<any> = new EventEmitter();

  isTableHidden: boolean = true;

  constructor() { }

  ngOnInit() {
  }

  triggerCloseCommentEditFormEvent() {
    this.isTableHidden = true;
    //TODO either close the whole table OR revert back to original comment
  }

  triggerDeleteCommentEvent(index: number) {
    // TODO: parent handling of event
    this.deleteCommentEvent.emit(index);

    this.comments.splice(index, 1);

    if (this.isTableEmpty()) {
      this.isTableHidden = true;
    }
  }

  triggerSaveCommentEvent(index: number, data: any) {
    // TODO: parent handling of event and what data to pass through
    this.saveCommentEvent.emit(data);

    if (index < this.comments.length) {
      this.comments[index] = {...this.comments[index], commentText: data};
    } else {
      // TODO handle new comments
      // properties other that commentGiver should be handled by parent
      this.comments.push({ commentText: data,
        commentGiver: "Someone",
        createdAt: "time",
        editedAt: "time",
        responseGiver: "responseGiver",
        responseReceipient: "receipient"
      });
    }
  }

  isTableEmpty(): boolean {
    return this.comments === undefined || this.comments.length == 0;
  }
}
