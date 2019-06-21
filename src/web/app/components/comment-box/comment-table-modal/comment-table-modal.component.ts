import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal for the comments table
 */
@Component({
  selector: 'tm-comment-table-modal',
  templateUrl: './comment-table-modal.component.html',
  styleUrls: ['./comment-table-modal.component.scss'],
})
export class CommentTableModalComponent implements OnInit {

  @Input() response: any = '';
  @Input() questionDetails: any = '';

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
