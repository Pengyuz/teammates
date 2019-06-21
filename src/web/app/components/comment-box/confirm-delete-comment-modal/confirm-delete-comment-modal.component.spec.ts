import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmDeleteCommentModalComponent } from './confirm-delete-comment-modal.component';

describe('ConfirmDeleteCommentModalComponent', () => {
  let component: ConfirmDeleteCommentModalComponent;
  let fixture: ComponentFixture<ConfirmDeleteCommentModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConfirmDeleteCommentModalComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmDeleteCommentModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
