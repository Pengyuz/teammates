import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'tm-comment-box',
  templateUrl: './comment-box.component.html',
  styleUrls: ['./comment-box.component.scss']
})
export class CommentBoxComponent implements OnInit {

  commentText:string = '';

  @Output()
  closeCommentBox: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  triggerFormChange(data: any) {
    console.log(data);
  }

  triggerCloseCommentBox(data: any) {
    this.closeCommentBox.emit();
    console.log(data);
  }

}
