import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'tm-comment-table-modal',
  templateUrl: './comment-table-modal.component.html',
  styleUrls: ['./comment-table-modal.component.scss']
})
export class CommentTableModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

}
