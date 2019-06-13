import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'tm-comment-row',
  templateUrl: './comment-row.component.html',
  styleUrls: ['./comment-row.component.scss']
})
export class CommentRowComponent implements OnInit {

  commentText: string = 'COMMENT PLACEHOLDER';
  constructor() { }

  ngOnInit() {
  }

}
