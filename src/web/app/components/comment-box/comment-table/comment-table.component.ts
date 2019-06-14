import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbModal, NgbModalRef } from "@ng-bootstrap/ng-bootstrap";
import { ConfirmDeleteCommentModalComponent } from "../confirm-delete-comment-modal/confirm-delete-comment-modal.component";
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

  constructor(private modalService: NgbModal) { }

  ngOnInit() {
  }

  triggerCloseCommentEditFormEvent() {
    //TODO either close the whole table OR revert back to original comment
  }

  triggerDeleteCommentEvent(index: number) {
    const modalRef: NgbModalRef = this.modalService.open(ConfirmDeleteCommentModalComponent);

    modalRef.result.then( () => {
        // TODO: parent handling of event
        this.deleteCommentEvent.emit(index);

        this.comments.splice(index, 1);
      }
    , () => {});
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
